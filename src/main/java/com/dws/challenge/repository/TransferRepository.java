package com.dws.challenge.repository;

import com.dws.challenge.domain.Transfer;
import java.util.List;

/**
 * This interface defines the methods for managing the transfer of funds between accounts.
 *
 * @author <NAME>
 */
public interface TransferRepository {

    /**
     * Creates a new transfer record in the system.
     *
     * @param transfer the details of the transfer
     * @param transferReference a unique reference number for the transfer
     * @throws Exception if there is an error creating the transfer
     */
    void createTransferEntry(Transfer transfer, int transferReference) throws Exception;

    /**
     * Retrieves the details of a transfer record from the system.
     *
     * @param transferReference the unique reference number for the transfer
     * @return the transfer record, or null if no record exists with the specified reference
     */
    Transfer getTransfer(int transferReference);

    /**
     * Updates the details of an existing transfer record in the system.
     *
     * @param transfer the updated details of the transfer
     * @param transferReference the unique reference number for the transfer
     * @return the updated transfer record
     * @throws Exception if there is an error updating the transfer
     */
    Transfer updateTransfer(Transfer transfer, int transferReference);

    /**
     * Retrieves all transfer records from the system.
     *
     * @return a list of transfer records
     */
    List<Transfer> getAllTransfers();

}
