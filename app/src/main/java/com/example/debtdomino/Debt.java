package com.example.debtdomino;

public class Debt {
    private String nameOf;
    private String amountOf;
    private String rate;
    private String frequency;
    private String type;
    private String dateOfNextPayment;
    private String uid;

    public Debt(String nameOf, String amountOf, String rate, String frequency, String type, String dateOfNextPayment, String uid) {
        this.nameOf = nameOf;
        this.amountOf = amountOf;
        this.rate = rate;
        this.frequency = frequency;
        this.type = type;
        this.dateOfNextPayment = dateOfNextPayment;
        this.uid = uid;
    }

    public String getNameOf() {
        return nameOf;
    }

    public void setNameOf(String nameOf) {
        this.nameOf = nameOf;
    }

    public String getAmountOf() {
        return amountOf;
    }

    public void setAmountOf(String amountOf) {
        this.amountOf = amountOf;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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
