package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.AccountException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.TransferService;
import com.dws.challenge.web.TransferController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @Mock
    private AccountsService accountsService;

    @InjectMocks
    private TransferController transferController;

    private Transfer transfer;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        transfer = new Transfer("12345678","87654321",new BigDecimal("1000.00"),"Deposit Transfer", "");
        fromAccount = new Account("12345678",new BigDecimal("10000.00"));
        toAccount = new Account("87654321",new BigDecimal("5000.00"));
    }

    @Test
    void transfer_shouldTransferMoney_whenBothAccountsExistAndHaveEnoughFunds() throws InsufficientFundsException, AccountException {
        when(accountsService.getAccount(transfer.getFromAccId())).thenReturn(fromAccount);
        when(accountsService.getAccount(transfer.getToAccId())).thenReturn(toAccount);

        transferController.transfer(transfer);

        verify(transferService, times(1)).transferMoney(transfer);
        verify(accountsService, times(1)).getAccount(transfer.getFromAccId());
        verify(accountsService, times(1)).getAccount(transfer.getToAccId());

    }
}