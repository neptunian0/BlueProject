package com.lloydtucker.blueproject;

/**
 * Created by lloydtucker on 05/08/2016.
 */
import java.util.HashMap;
import java.util.Map;

public class Transactions {

    /**
     * System-generated Id that uniquely identifies a banking transaction
     * (Required)
     *
     */
    private String id;
    /**
     * RFC3339-compliant, system-generated timestamp for the transaction
     * (Required)
     *
     */
    private String transactionDateTime;
    /**
     * e.g. INT (Interest), S/O (Standing Order), D/D (Direct Debit), POS (Point of Sale), DPC (Direct Banking by PC), C/L (Cashline i.e. ATM)
     *
     */
    private String transactionType;
    /**
     * TODO: Schema needed
     * (Required)
     *
     */
    private String transactionDescription;
    /**
     *
     * (Required)
     *
     */
    private Double transactionAmount;
    private String transactionCurrency;
    private Double accountBalance;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * System-generated Id that uniquely identifies a banking transaction
     * (Required)
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     * System-generated Id that uniquely identifies a banking transaction
     * (Required)
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * RFC3339-compliant, system-generated timestamp for the transaction
     * (Required)
     *
     * @return
     * The transactionDateTime
     */
    public String getTransactionDateTime() {
        return transactionDateTime;
    }

    /**
     * RFC3339-compliant, system-generated timestamp for the transaction
     * (Required)
     *
     * @param transactionDateTime
     * The transactionDateTime
     */
    public void setTransactionDateTime(String transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    /**
     * e.g. INT (Interest), S/O (Standing Order), D/D (Direct Debit), POS (Point of Sale), DPC (Direct Banking by PC), C/L (Cashline i.e. ATM)
     *
     * @return
     * The transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * e.g. INT (Interest), S/O (Standing Order), D/D (Direct Debit), POS (Point of Sale), DPC (Direct Banking by PC), C/L (Cashline i.e. ATM)
     *
     * @param transactionType
     * The transactionType
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * TODO: Schema needed
     * (Required)
     *
     * @return
     * The transactionDescription
     */
    public String getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * TODO: Schema needed
     * (Required)
     *
     * @param transactionDescription
     * The transactionDescription
     */
    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The transactionAmount
     */
    public Double getTransactionAmount() {
        return transactionAmount;
    }

    /**
     *
     * (Required)
     *
     * @param transactionAmount
     * The transactionAmount
     */
    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     *
     * @return
     * The transactionCurrency
     */
    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    /**
     *
     * @param transactionCurrency
     * The transactionCurrency
     */
    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    /**
     *
     * @return
     * The accountBalance
     */
    public Double getAccountBalance() {
        return accountBalance;
    }

    /**
     *
     * @param accountBalance
     * The accountBalance
     */
    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}