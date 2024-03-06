package com.dws.challenge.repository;

import com.dws.challenge.domain.Transfer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransferRepositoryImpl implements TransferRepository{

    private final Map<Integer, Transfer> transferRecords = new ConcurrentHashMap<>();
    @Override
    public void createTransferEntry(Transfer transfer, int transferReference) {
        if(transferRecords.get(transferReference) == null) {
            transferRecords.put(transferReference, transfer);
        }
    }

    @Override
    public Transfer getTransfer(int transferReference) {
        return transferRecords.get(transferReference);
    }

    @Override
    public Transfer updateTransfer(Transfer transfer, int transferReference) {
        return transferRecords.put(transferReference, transfer);
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRecords.values().stream().toList();
    }
}
