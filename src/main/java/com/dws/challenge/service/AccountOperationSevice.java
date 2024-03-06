package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountException;

import java.math.BigDecimal;

public class AccountOperationSevice {

    public static void debit(Account account, BigDecimal amount) throws AccountException {
        account.setBalance(account.getBalance().subtract(amount));
    }

    public static void credit(Account account, BigDecimal amount) throws AccountException {
        account.setBalance(account.getBalance().add(amount));
    }

}
