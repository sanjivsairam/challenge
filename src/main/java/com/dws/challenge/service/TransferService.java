package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.AccountException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.repository.TransferRepository;
import com.dws.challenge.util.RandomNumberGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TransferService is a service class that provides business logic for transferring money between accounts.
 * It uses the AccountOperationService to perform the actual debit and credit operations.
 */
@Service
public class TransferService {

    private final AtomicInteger transactionCounter = new AtomicInteger();

    /**
     * Autowired dependency injection for the AccountsRepository and TransferRepository.
     */
    private final AccountsRepository accountsRepository;

    private final TransferRepository transferRepository;
    private final NotificationService notificationService;

    @Autowired
    public TransferService(AccountsRepository accountsRepository, TransferRepository transferRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.transferRepository = transferRepository;
        this.notificationService = notificationService;
    }

    /**
     * Method to transfer money between two accounts. It first validates the transaction, then creates a transfer
     * record in the database, performs the debit and credit operations, and updates the transfer record with the
     * final status. If an exception occurs, the transfer record is updated with the failure status and an exception
     * is thrown.
     *
     * @param transfer the Transfer object containing the details of the transfer
     * @throws InsufficientFundsException if the from account does not have sufficient funds
     * @throws AccountException if the from and to accounts are the same or if there is a technical error
     */
    public void transferMoney(final Transfer transfer) throws InsufficientFundsException, AccountException {
        String fromAccountId = transfer.getFromAccId();
        String toAccountId = transfer.getToAccId();
        BigDecimal amount = transfer.getTransferAmount();
        Account fromAccount;
        Account toAccount;

        synchronized (this) {
            fromAccount = accountsRepository.getAccount(fromAccountId);
            toAccount = accountsRepository.getAccount(toAccountId);
        }

        validateTransaction(fromAccount, toAccount, amount, fromAccountId);
        transactionCounter.set(RandomNumberGen.getRandomNum());
        int transactionRefNum = transactionCounter.incrementAndGet();
        processTransaction(transfer, transactionRefNum, fromAccount, amount, toAccount);
    }

    /**
     * Method to perform the actual transfer of money between the accounts. It first creates a transfer record in
     * the database with the initial status, then performs the debit and credit operations using the
     * AccountOperationService. If an exception occurs, it updates the transfer record with the failure status and
     * throws an exception.
     *
     * @param transfer the Transfer object containing the details of the transfer
     * @param transactionRefNum the reference number of the transaction
     * @param fromAccount the from account
     * @param amount the amount to be transferred
     * @param toAccount the to account
     * @throws AccountException if there is a technical error
     */
    private void processTransaction(Transfer transfer, int transactionRefNum, Account fromAccount, BigDecimal amount, Account toAccount) throws AccountException {
        try {
            transfer.setTransactionReference(transactionRefNum);
            transfer.setStatus("Initiated");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            notificationService.notifyAboutTransfer(fromAccount, String.format("Transaction initiated with transaction reference %1$s  for amount %2$s on %3$s  to account %4$s",transactionRefNum, amount, timeStamp, toAccount.getAccountId()));
            transferRepository.createTransferEntry(transfer, transactionRefNum);

            AccountOperationSevice.debit(fromAccount, amount);
            AccountOperationSevice.credit(toAccount, amount);

            transfer.setStatus("Success");
            notificationService.notifyAboutTransfer(fromAccount, String.format("Transaction is successful with transaction reference %1$s  ",transactionRefNum));
            transferRepository.updateTransfer(transfer, transactionRefNum);
        } catch (Exception e) {
            transfer.setStatus("Failed");
            transferRepository.updateTransfer(transfer, transactionRefNum);
            notificationService.notifyAboutTransfer(fromAccount, String.format("Transaction is failed with transaction reference %1$s  ",transactionRefNum));
            throw new AccountException("Transfer failure due to technical error.");
        }
    }

    /**
     * Method to validate the transaction. It checks if the from and to accounts are the same, if the from account
     * has sufficient funds, and if the from and to accounts are valid. If any of these checks fail, an exception is
     * thrown.
     *
     * @param fromAccount the from account
     * @param toAccount the to account
     * @param amount the amount to be transferred
     * @param fromAccountId the ID of the from account
     * @throws InsufficientFundsException if the from account does not have sufficient funds
     * @throws AccountException if the from and to accounts are the same or if they are not valid
     */
    private static void validateTransaction(Account fromAccount, Account toAccount, BigDecimal amount, String fromAccountId) throws InsufficientFundsException, AccountException {
        if (fromAccount == null || toAccount == null) {
            throw new AccountException("Invalid from or to Account.");
        }
        if (fromAccount.equals(toAccount)) {
            throw new AccountException("Cannot transfer money to and from the same account.");
        }
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + fromAccountId);
        }
    }

    public List<Transfer> getTransferList() {
        return transferRepository.getAllTransfers();
    }
}
