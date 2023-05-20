package com.example.debtdomino;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PaymentPlanList extends AppCompatActivity implements PaymentPlanAdapter.OnRemoveButtonClickListener {

    private static final String TAG = "PaymentPlanList";

    private ListView paymentPlansListView;
    private PaymentPlanAdapter adapter;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_repayment_plan);

        db = FirebaseFirestore.getInstance();
        paymentPlansListView = findViewById(R.id.payment_plans_list);
        adapter = new PaymentPlanAdapter(this, new ArrayList<>(), this);
        paymentPlansListView.setAdapter(adapter);

        fetchPaymentPlans();
    }


    private void fetchPaymentPlans() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("paymentPlans")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> paymentPlans = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> paymentPlan = document.getData();
                                String planName = (String) paymentPlan.get("debtId");
                                Double amount = (Double) paymentPlan.get("totalAmount");
                                String startDate = ""; // Modified line
                                String finishDate = ""; // Modified line

                                // Cast Timestamp fields to String
                                if (paymentPlan.containsKey("firstDate")) {
                                    Timestamp firstDateTimestamp = (Timestamp) paymentPlan.get("firstDate");
                                    startDate = formatDate(firstDateTimestamp);
                                }

                                if (paymentPlan.containsKey("lastDate")) {
                                    Timestamp lastDateTimestamp = (Timestamp) paymentPlan.get("lastDate");
                                    finishDate = formatDate(lastDateTimestamp);
                                }

                                // Check if amount is not null before accessing its value
                                double amountValue = (amount != null) ? amount.doubleValue() : 0.0;

                                String planDetails = "Name: " + planName + "\nAmount: " + amountValue + "\nDate Range: " + startDate + " - " + finishDate;
                                paymentPlans.add(planDetails);
                            }
                            adapter.addAll(paymentPlans);
                        } else {
                            Log.d(TAG, "Error getting payment plans: ", task.getException());
                        }
                    }
                });
    }

    private String formatDate(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = timestamp.toDate();
        return formatter.format(date);
    }

    public void removePaymentPlan(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String selectedPlanDetails = adapter.getItem(position);
        String selectedPlanName = selectedPlanDetails.split("\n")[0].substring(6); // Extract the plan name from the plan details

        db.collection("paymentPlans")
                .whereEqualTo("debtId", selectedPlanName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("paymentPlans").document(document.getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    adapter.remove(selectedPlanDetails);
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(PaymentPlanList.this, "Payment plan removed successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.d(TAG, "Error deleting payment plan: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting payment plans: ", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onRemoveButtonClick(int position) {
        removePaymentPlan(position);
    }
}