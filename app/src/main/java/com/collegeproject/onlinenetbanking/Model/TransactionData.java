package com.collegeproject.onlinenetbanking.Model;

public class TransactionData {

    String CustomerId;
    String CustomerName;
    String Payment;
    String Amount;
    String UserId;
    Long TimeStamp;

    public TransactionData() {
    }

    public TransactionData(String customerId, String customerName, String payment, String amount, String userId, Long timeStamp) {
        CustomerId = customerId;
        CustomerName = customerName;
        Payment = payment;
        Amount = amount;
        UserId = userId;
        TimeStamp = timeStamp;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
