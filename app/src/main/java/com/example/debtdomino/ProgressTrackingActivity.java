package com.example.debtdomino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.view.MenuItem;
import android.content.Intent;

public class ProgressTrackingActivity extends AppCompatActivity {
    private static final String TAG = "ProgressTrackingActivity";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private Button buttonRegister;
    private TextView welcomeText;
    private TextView debtInfoTextView;
    private TextView incomeInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracking);


        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        welcomeText = findViewById(R.id.welcome_text);
        buttonRegister = findViewById(R.id.update_button);
        debtInfoTextView = findViewById(R.id.debt_info);
        incomeInfoTextView = findViewById(R.id.income_info);
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userName = document.getString("name");
                                    welcomeText.setText("Welcome, " + userName);
                                } else {
                                    Toast.makeText(ProgressTrackingActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProgressTrackingActivity.this, "get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

// Fetch user debts.
            db.collection("userDebts")
                    .whereEqualTo("uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Debt> debts = new ArrayList<>();
                                double totalDebt = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    String nameOf = document.getString("nameOf");
                                    String amountOfStr = document.getString("amountOf");
                                    double amountOf = Double.parseDouble(amountOfStr);
                                    String rate = document.getString("rate");
                                    String frequency = document.getString("frequency");
                                    String type = document.getString("type");
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Debt debt = new Debt(nameOf, amountOfStr, rate, frequency, type, dateOfNextPayment, uid);
                                    debts.add(debt);
                                    totalDebt += amountOf;
                                }

                                debtInfoTextView.setText("Total debt: " + totalDebt);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // Fetch user income.
            db.collection("userDebts")
                    .whereEqualTo("uid", currentUser.getUid())
                    .whereEqualTo("type", "income")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Income> incomes = new ArrayList<>();
                                double totalIncome = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    String nameOf = document.getString("nameOf");
                                    String amountOfStr = document.getString("amountOf");
                                    double amountOf = Double.parseDouble(amountOfStr);
                                    String frequency = document.getString("frequency");
                                    String type = document.getString("type");
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Income income = new Income(nameOf, amountOfStr, frequency, type, dateOfNextPayment, uid);
                                    incomes.add(income);
                                    totalIncome += amountOf;
                                }

                                incomeInfoTextView.setText("Total income: " + totalIncome);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


            // Set onClickListener for debtInfoTextView
            debtInfoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perform your action here
                    // For example, start a new activity
                    Intent intent = new Intent(ProgressTrackingActivity.this, DebtDetailsActivity.class);
                    startActivity(intent);
                }
            });
            incomeInfoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perform your action here
                    // For example, start a new activity
                    Intent intent = new Intent(ProgressTrackingActivity.this, IncomeDetailsActivity.class);
                    startActivity(intent);
                }
            });

        }

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgressTrackingActivity.this, DebtInventoryActivity.class);
                startActivity(intent);
            }
        });
    }

}
