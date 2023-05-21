package com.example.debtdomino;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;

public class Debt {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private DocumentReference documentRef;
    private String nameOf;
    private String amountOf;
    private String rate;
    private String frequency;
    private String dateOfNextPayment;
    private String uid;

    private double totalValue;

    public static Debt fromSnapshot(DocumentSnapshot snapshot) {
        return new Debt(snapshot.getReference(), snapshot.getString("nameOf"), snapshot.getString("amountOf"),
                snapshot.getString("rate"), snapshot.getString("frequency"), snapshot.getString("dateOfNextPayment"),
                snapshot.getString("uid"));
    }

    public Debt(DocumentReference documentRef, String nameOf, String amountOf, String rate, String frequency,
            String dateOfNextPayment, String uid) {
        this.documentRef = documentRef;
        this.nameOf = nameOf;
        this.amountOf = amountOf;
        this.rate = rate;
        this.frequency = frequency;
        this.dateOfNextPayment = dateOfNextPayment;
        this.uid = uid;

        this.totalValue = Double.parseDouble(amountOf);
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

    public String getDateOfNextPayment() {
        return dateOfNextPayment;
    }

    public void setDateOfNextPayment(String dateOfNextPayment) {
        this.dateOfNextPayment = dateOfNextPayment;
    }

    public Date getParsedDateOfNextPayment() {
        try {
            return sdf.parse(dateOfNextPayment);
        } catch (Exception e) {
            return new Date();
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CompletableFuture<Boolean> removeSelf() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        documentRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(true);
            } else {
                future.complete(false);
            }
        });

        return future;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void makePayment(Payment payment) {
        payment.setDebt(this);
        totalValue = Math.max(0, totalValue - payment.getPaymentAmount());
    }

    public void jumpToCurrentIntervalOf(Date currentDate) {
        try {
            while (sdf.parse(dateOfNextPayment).before(currentDate)) {
                jumpToNextInterval();
            }
        } catch (Exception e) {
        }
    }

    public void jumpToNextInterval() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(dateOfNextPayment));

        if (frequency.equals("Fortnightly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 14);
            totalValue += totalValue * Double.parseDouble(rate) / 100 / 26;
        } else if (frequency.equals("Weekly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            totalValue += totalValue * Double.parseDouble(rate) / 100 / 52;
        } else if (frequency.equals("Quarterly")) {
            calendar.add(Calendar.MONTH, 3);
            totalValue += totalValue * Double.parseDouble(rate) / 100 / 4;
        } else {
            calendar.add(Calendar.MONTH, 1);
            totalValue += totalValue * Double.parseDouble(rate) / 100 / 12;
        }

        setDateOfNextPayment(sdf.format(calendar.getTime()));
    }
}
