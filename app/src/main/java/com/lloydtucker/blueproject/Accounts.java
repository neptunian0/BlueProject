package com.lloydtucker.blueproject;

/**
 * Created by lloydtucker on 05/08/2016.
 */
import java.util.HashMap;
import java.util.Map;

public class Accounts {

    /**
     * System-generated Id that uniquely identifies a single bank account
     * (Required)
     *
     */
    private String id;
    private String custId;
    private String sortCode;
    private String accountNumber;
    private String accountType;
    private String accountFriendlyName;
    private Double accountBalance;
    private String accountCurrency;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * System-generated Id that uniquely identifies a single bank account
     * (Required)
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     * System-generated Id that uniquely identifies a single bank account
     * (Required)
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * System-generated Id that uniquely identifies a single bank account
     * (Required)
     *
     * @param custId
     * The id
     */
    public void setCustId(String custId) {
        this.custId = custId;
    }

    /**
     * System-generated Id that uniquely identifies a single bank account
     * (Required)
     *
     * @return
     * The custId
     */
    public String getCustId() {
        return custId;
    }

    /**
     *
     * @return
     * The sortCode
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     *
     * @param sortCode
     * The sortCode
     */
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    /**
     *
     * @return
     * The accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     *
     * @param accountNumber
     * The accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     *
     * @return
     * The accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     *
     * @param accountType
     * The accountType
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     *
     * @return
     * The accountFriendlyName
     */
    public String getAccountFriendlyName() {
        return accountFriendlyName;
    }

    /**
     *
     * @param accountFriendlyName
     * The accountFriendlyName
     */
    public void setAccountFriendlyName(String accountFriendlyName) {
        this.accountFriendlyName = accountFriendlyName;
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

    /**
     *
     * @return
     * The accountCurrency
     */
    public String getAccountCurrency() {
        return accountCurrency;
    }

    /**
     *
     * @param accountCurrency
     * The accountCurrency
     */
    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String toString(){
        return "Account Number: " + accountNumber;
    }

}
