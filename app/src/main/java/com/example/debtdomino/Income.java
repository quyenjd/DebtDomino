package com.example.debtdomino;

import com.google.firebase.firestore.DocumentReference;
import java.util.concurrent.CompletableFuture;

public class Income {
    private DocumentReference documentRef;
    private String nameOf;
    private String amountOf;
    private String frequency;
    private String dateOfNextPayment;
    private String uid;

    public Income(DocumentReference documentRef, String nameOf, String amountOf, String frequency, String dateOfNextPayment, String uid) {
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
}
