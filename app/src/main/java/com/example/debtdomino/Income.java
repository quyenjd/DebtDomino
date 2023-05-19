package com.example.debtdomino;

public class Income {
    private String name;
    private String amount;
    private String frequency;
    private String type;
    private String dateOfNextPayment;
    private String uid;

    public Income(String name, String amount, String frequency, String type, String dateOfNextPayment, String uid) {
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.type = type;
        this.dateOfNextPayment = dateOfNextPayment;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateOfNextPayment() {
        return dateOfNextPayment;
    }

    public void setDateOfNextPayment(String dateOfNextPayment) {
        this.dateOfNextPayment = dateOfNextPayment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
