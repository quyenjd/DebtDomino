package com.example.debtdomino;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PaymentPlanDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_plan_details);

        // Declare your TextViews (or other views) that will display the data
        TextView planNameTextView = findViewById(R.id.plan_name);
        TextView amountTextView = findViewById(R.id.total_amount);
        TextView startDateTextView = findViewById(R.id.start_date);
        TextView endDateTextView = findViewById(R.id.end_date);

        String debtId = getIntent().getStringExtra("debtId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("paymentPlans")
                .whereEqualTo("debtId", debtId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // Get the first document
                            Map<String, Object> paymentPlan = document.getData();

                            // Set the data to your TextViews
                            planNameTextView.setText((String) paymentPlan.get("debtId"));
                            amountTextView.setText((String) paymentPlan.get("totalAmount"));
                            startDateTextView.setText(formatDate((Timestamp) paymentPlan.get("firstDate")));
                            endDateTextView.setText(formatDate((Timestamp) paymentPlan.get("lastDate")));
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
}
