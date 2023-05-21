package com.example.debtdomino;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.util.concurrent.CompletableFuture;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Income {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private DocumentReference documentRef;
    private String nameOf;
    private String amountOf;
    private String frequency;
    private String dateOfNextPayment;
    private String uid;

    public static Income fromSnapshot(DocumentSnapshot snapshot) {
        return new Income(snapshot.getReference(), snapshot.getString("nameOf"), snapshot.getString("amountOf"),
                snapshot.getString("frequency"), snapshot.getString("dateOfNextPayment"),
                snapshot.getString("uid"));
    }

    public Income(DocumentReference documentRef, String nameOf, String amountOf, String frequency,
            String dateOfNextPayment, String uid) {
        this.documentRef = documentRef;
        this.nameOf = nameOf;
        this.amountOf = amountOf;
        this.frequency = frequency;
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

    public void jumpToCurrentIntervalOf(Date currentDate) {
        try {
            while (getParsedDateOfNextPayment().before(currentDate)) {
                jumpToNextInterval();
            }
        } catch (Exception e) {
        }
    }

    public void jumpToNextInterval() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getParsedDateOfNextPayment());

        if (frequency.equals("Fortnightly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 14);
        } else if (frequency.equals("Weekly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } else if (frequency.equals("Quarterly")) {
            calendar.add(Calendar.MONTH, 3);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }

        setDateOfNextPayment(sdf.format(calendar.getTime()));
    }
}
