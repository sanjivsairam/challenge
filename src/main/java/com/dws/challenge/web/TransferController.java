package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.AccountException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * The TransferController class is a REST controller that handles HTTP requests for transferring money between accounts.
 * It uses the TransferService and AccountsService beans to perform the actual transfers.
 */
@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransferController {

    private final TransferService transferService;
    private final AccountsService accountsService;

    /**
     * The constructor takes the TransferService and AccountsService beans as arguments and saves them for later use.
     */
    @Autowired
    public TransferController(TransferService transferService, AccountsService accountsService) {
        this.transferService = transferService;
        this.accountsService = accountsService;
    }

    /**
     * The transfer method is a POST endpoint that accepts a Transfer object in the request body. It uses the
     * TransferService to perform the actual transfer, and then returns a response code of 200 (OK) to indicate that
     * the transfer was successful.
     *
     * @param transfer the Transfer object containing the details of the transfer
     * @return a response with a status code of 200 (OK) if the transfer was successful, or an error code if there was an issue
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transfer(@RequestBody @Valid Transfer transfer) throws InsufficientFundsException, AccountException {
        log.info("Transfer starts: {}", transfer);
        try {
            Account fromAcc = accountsService.getAccount(transfer.getFromAccId());
            Account toAcc = accountsService.getAccount(transfer.getToAccId());
            log.info("fromAcc account balance: {}", fromAcc.getBalance().doubleValue());
            log.info("toAcc account balance: {}", toAcc.getBalance().doubleValue());
            transferService.transferMoney(transfer);
            log.info("Balance after transaction: ");
            log.info("fromAcc account balance: {}", fromAcc.getBalance().doubleValue());
            log.info("toAcc account balance: {}", toAcc.getBalance().doubleValue());
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(InsufficientFundsException | AccountException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The getTransferList method is a GET endpoint that returns a list of all Transfer objects.
     *
     * @return a list of Transfer objects
     */
    @GetMapping(path = "/getAllTransactions",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transfer> getTransferList() {
        return transferService.getTransferList();
    }

}
