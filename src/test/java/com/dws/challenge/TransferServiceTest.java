package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.AccountException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.repository.TransferRepository;
import com.dws.challenge.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    private Account fromAccount;
    private Account toAccount;
    private Transfer transfer;
    private AtomicInteger transactionCounter;

    @BeforeEach
    public void init() {
        transfer = new Transfer("12345678","87654321",new BigDecimal("1000.00"),"Deposit Transfer", "");
        fromAccount = new Account("12345678",new BigDecimal("10000.00"));
        toAccount = new Account("87654321",new BigDecimal("5000.00"));
        transactionCounter = new AtomicInteger();
    }

    @Test
    public void testProcessTransaction() throws AccountException, InsufficientFundsException {
        when(accountsRepository.getAccount(anyString())).thenReturn(fromAccount, toAccount);

        transferService.transferMoney(transfer);

        assertEquals(fromAccount.getBalance().doubleValue(), new BigDecimal("9000.00").doubleValue());
        assertEquals(toAccount.getBalance().doubleValue(), new BigDecimal("6000.00").doubleValue());
    }
    @Test
    public void testFailedTransaction() throws Exception {
        when(accountsRepository.getAccount(anyString())).thenReturn(fromAccount, toAccount);

        doThrow(new Exception()).when(transferRepository).createTransferEntry(any(),anyInt());
        AccountException exception = assertThrows(AccountException.class, () ->
                transferService.transferMoney(transfer));

        assertEquals("Transfer failure due to technical error.", exception.getMessage());
        assertEquals("Failed", transfer.getStatus());
    }

    @Test
    public void testProcessFailedSameAccTransaction() throws AccountException, InsufficientFundsException {
        when(accountsRepository.getAccount(anyString())).thenReturn(fromAccount, fromAccount);
        Exception exception = assertThrows(AccountException.class, () ->
                transferService.transferMoney(transfer));
        assertEquals("Cannot transfer money to and from the same account.", exception.getMessage());
    }

    @Test
    public void testProcessInvalidTransaction() throws AccountException, InsufficientFundsException {
        when(accountsRepository.getAccount(anyString())).thenReturn(fromAccount, null);

        Exception exception = assertThrows(AccountException.class, () ->
                transferService.transferMoney(transfer));
        assertEquals("Invalid from or to Account.", exception.getMessage());
    }

    @Test
    public void testProcessInsufficientFund() throws AccountException, InsufficientFundsException {
        Account fromAccount = new Account("12345678",new BigDecimal("0.00"));
        when(accountsRepository.getAccount(anyString())).thenReturn(fromAccount, toAccount);

        Exception exception = assertThrows(InsufficientFundsException.class, () ->
                transferService.transferMoney(transfer));
        assertEquals("Insufficient funds in account 12345678", exception.getMessage());
    }
}