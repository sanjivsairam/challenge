package com.dws.challenge.domain;

import lombok.Data;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Transfer {

    private static final Log LOG = LogFactory.getLog(Transfer.class);

    @NotNull
    private final String fromAccId;

    @NotNull
    private final String toAccId;

    @NotNull
    @Min(value = 1, message = "Transfer amount must be positive.")
    private final BigDecimal transferAmount;

    @NotNull
    private final String purpose;

    private final String comments;

    private String status;

    private int transactionReference;
}
