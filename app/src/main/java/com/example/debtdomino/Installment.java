package com.example.debtdomino;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Installment {
    private Date date;
    private Double installmentAmount;
    private Double interest;

    public Installment(Date date, Double installmentAmount, Double interest) {
        this.date = date;
        this.installmentAmount = installmentAmount;
        this.interest = interest;
    }

    // Getters
    public Date getDate() {
        return date;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public Double getInterest() {
        return interest;
    }

    // Setters
    public void setDate(Date date) {
        this.date = date;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }
}
