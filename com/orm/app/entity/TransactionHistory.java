package com.orm.app.entity;

import com.orm.app.annotations.Column;
import com.orm.app.annotations.PrimaryKey;
import com.orm.app.annotations.Table;

@Table(name="TRANSACTION_HISTORY")
public class TransactionHistory {
    @PrimaryKey(name="TRANSACTION_ID")
    public Long transactionId;
    @Column(name="ACCOUNT_NO")
    public Integer accountNumber;
    @Column(name="NAME")
    public String name;
    @Column(name="TRANSACTION_TYPE")
    public String transactionType;
    @Column(name="AMOUNT")
    public Double amount;

    public TransactionHistory(Integer accountNumber, String name, String transactionType, Double amount) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public TransactionHistory() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
