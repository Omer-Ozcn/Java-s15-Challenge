package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice implements Payable {
    private final int id;
    private final int memberId;
    private final int loanId;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime time = LocalDateTime.now();

    public Invoice(int id, int memberId, int loanId, BigDecimal amount, String description) {
        this.id = id;
        this.memberId = memberId;
        this.loanId = loanId;
        this.amount = amount;
        this.description = description;
    }

    public int getId() { return id; }
    public int getMemberId() { return memberId; }
    public int getLoanId() { return loanId; }
    public LocalDateTime getTime() { return time; }

    @Override
    public BigDecimal amount() { return amount; }

    @Override
    public String description() { return description; }
}
