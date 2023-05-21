package com.example.debtdomino;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class PaymentPlanDetailsActivity extends AppCompatActivity {

    private InstallmentAdapter mInstallmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_plan_details);

        ListView installmentListView = findViewById(R.id.installment_list);
        mInstallmentAdapter = new InstallmentAdapter(this, new ArrayList<>());
        installmentListView.setAdapter(mInstallmentAdapter);

        String debtId = getIntent().getStringExtra("debtId");
        Log.d(TAG, "Received plan ID: " + debtId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("paymentPlans").document(debtId).collection("installments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            ArrayList<Installment> installments = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                try {
                                    String dateString = (String) document.get("date");
                                    if (dateString != null) {
                                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                        Date date = format.parse(dateString);

                                        Double installmentValue = null;
                                        Double interest = null;
                                        try {
                                            installmentValue = Double.parseDouble(document.get("installment").toString());
                                            interest = Double.parseDouble(document.get("interest").toString());
                                        } catch (NumberFormatException nfe) {
                                            Log.e(TAG, "NumberFormatException: " + nfe.getMessage());
                                        }

                                        if (installmentValue != null && interest != null) {
                                            Installment installment = new Installment(date, installmentValue, interest);
                                            installments.add(installment);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Sort the installments by date
                            Collections.sort(installments, new Comparator<Installment>() {
                                public int compare(Installment o1, Installment o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });

                            // Add all the installments to the adapter
                            mInstallmentAdapter.addAll(installments);
                        } else {
                            Log.d(TAG, "Error getting installments: ", task.getException());
                        }
                    }
                });
    }
}

