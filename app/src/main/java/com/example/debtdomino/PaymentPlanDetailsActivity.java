package com.example.debtdomino;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PaymentPlanDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PaymentPlanDetailsActivity";

    private TextView noPlanTextView;
    private TextView generatedAtTextView;
    private ListView paymentPlanListView;
    private PaymentPlanAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_plan_details);

        // Set up the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        noPlanTextView = findViewById(R.id.no_plan_found_text);
        generatedAtTextView = findViewById(R.id.generated_at_text);
        paymentPlanListView = findViewById(R.id.payment_plan_list);

        generatedAtTextView.setVisibility(View.GONE);

        adapter = new PaymentPlanAdapter(this, new ArrayList<>());
        paymentPlanListView.setAdapter(adapter);

        fetchPaymentPlans();
    }

    private void fetchPaymentPlans() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("plans").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() != null) {
                                noPlanTextView.setVisibility(View.GONE);
                                generatedAtTextView
                                        .setText("Generated at: "
                                                + formatDate((Timestamp) task.getResult().getData().get("createdAt"))
                                                + ".");
                                generatedAtTextView.setVisibility(View.VISIBLE);
                                adapter.addAll((List<Map<String, Object>>) task.getResult().getData().get("plans"));
                            }
                        } else {
                            Log.d(TAG, "Error getting payment plans: ", task.getException());
                        }
                    }
                });
    }

    private String formatDate(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = timestamp.toDate();
        return formatter.format(date);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            Intent intent = new Intent(this, ProgressTrackingActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}