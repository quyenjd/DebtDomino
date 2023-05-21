package com.example.debtdomino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProgressTrackingActivity extends AppCompatActivity {
    private static final String TAG = "ProgressTrackingActivity";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView nameTextView;
    private RecyclerView debtListView;
    private TextView debtInfoTextView;
    private Button debtAddButton;
    private RecyclerView incomeListView;
    private TextView incomeInfoTextView;
    private Button incomeAddButton;
    private Button logoutButton;
    private Button updateProfileButton;
    private Button setupPaymentPlanButton;
    private Button paymentPlanListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracking);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        nameTextView = findViewById(R.id.name_text);
        debtListView = findViewById(R.id.debt_list);
        debtListView.setLayoutManager(new LinearLayoutManager(this));
        debtInfoTextView = findViewById(R.id.debt_info);
        debtAddButton = findViewById(R.id.debt_add_button);
        incomeListView = findViewById(R.id.income_list);
        incomeListView.setLayoutManager(new LinearLayoutManager(this));
        incomeInfoTextView = findViewById(R.id.income_info);
        incomeAddButton = findViewById(R.id.income_add_button);
        logoutButton = findViewById(R.id.logout_button);
        updateProfileButton = findViewById(R.id.update_profile_button);
        setupPaymentPlanButton = findViewById(R.id.setup_payment_plan_button);
        paymentPlanListButton = findViewById(R.id.view_payment_plans);

        // Fetch user data
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("name");
                                nameTextView.setText(userName);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        // Fetch user debts.
        db.collection("debts")
                .whereEqualTo("uid", currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                            @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<Debt> debts = new ArrayList<>();
                        double totalDebt = 0;

                        for (QueryDocumentSnapshot doc : snapshots) {
                            if (doc != null) {
                                String nameOf = doc.getString("nameOf");
                                String amountOfStr = doc.getString("amountOf");
                                double amountOf = Double.parseDouble(amountOfStr);
                                String rate = doc.getString("rate");
                                String frequency = doc.getString("frequency");
                                String dateOfNextPayment = doc.getString("dateOfNextPayment");
                                String uid = doc.getString("uid");

                                Debt debt = new Debt(doc.getReference(), nameOf, amountOfStr, rate, frequency,
                                        dateOfNextPayment, uid);
                                debts.add(debt);
                                totalDebt += amountOf;
                            }
                        }

                        DebtAdapter adapter = new DebtAdapter(ProgressTrackingActivity.this, debts);
                        debtListView.setAdapter(adapter);

                        String debtInfoText = "You have <b>" + debts.size() + " debt"
                                + (debts.size() != 1 ? "s" : "") + "</b> with a total of <b>$" + totalDebt
                                + "</b>.";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            debtInfoTextView.setText(Html.fromHtml(debtInfoText, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            debtInfoTextView.setText(Html.fromHtml(debtInfoText));
                        }
                    }
                });

        // Fetch user incomes.
        db.collection("incomes")
                .whereEqualTo("uid", currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                            @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<Income> incomes = new ArrayList<>();
                        double totalIncome = 0;

                        for (QueryDocumentSnapshot doc : snapshots) {
                            if (doc != null) {
                                String nameOf = doc.getString("nameOf");
                                String amountOfStr = doc.getString("amountOf");
                                double amountOf = Double.parseDouble(amountOfStr);
                                String frequency = doc.getString("frequency");
                                String dateOfNextPayment = doc.getString("dateOfNextPayment");
                                String uid = doc.getString("uid");

                                Income income = new Income(doc.getReference(), nameOf, amountOfStr, frequency,
                                        dateOfNextPayment, uid);
                                incomes.add(income);
                                totalIncome += amountOf;
                            }
                        }

                        IncomeAdapter adapter = new IncomeAdapter(ProgressTrackingActivity.this, incomes);
                        incomeListView.setAdapter(adapter);

                        String incomeInfoText = "Your total income is <b>$" + totalIncome + "</b>.";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            incomeInfoTextView.setText(Html.fromHtml(incomeInfoText, Html.FROM_HTML_MODE_LEGACY));

                        } else {
                            incomeInfoTextView.setText(Html.fromHtml(incomeInfoText));
                        }
                    }
                });

        // Click events for debt and income add buttons.
        debtAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, DebtInventoryActivity.class);
                startActivity(intent);
            }
        });

        incomeAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, IncomeInventoryActivity.class);
                startActivity(intent);
            }
        });

        // Click events for update profile and logout buttons
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProgressTrackingActivity.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure to logout?");

                // Set positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ProgressTrackingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                // Set negative button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Click event for setup payment plan button
        setupPaymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, PaymentPlanFormActivity.class);
                startActivity(intent);
            }
        });

        paymentPlanListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, PaymentPlanDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
