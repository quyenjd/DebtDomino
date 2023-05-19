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
import android.text.Html;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    private TextView nameTextView;
    private RecyclerView debtListView;
    private TextView debtInfoTextView;
    private Button debtAddButton;
    private RecyclerView incomeListView;
    private TextView incomeInfoTextView;
    private Button incomeAddButton;

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
                                    nameTextView.setText(userName);
                                } else {
                                    Toast.makeText(ProgressTrackingActivity.this, "No such document",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProgressTrackingActivity.this, "get failed with " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Fetch user debts.
            db.collection("debts")
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
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Debt debt = new Debt(document.getReference(), nameOf, amountOfStr, rate, frequency,
                                            dateOfNextPayment, uid);
                                    debts.add(debt);
                                    totalDebt += amountOf;
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
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            // Fetch user incomes.
            db.collection("incomes")
                    .whereEqualTo("uid", currentUser.getUid())
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
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Income income = new Income(document.getReference(), nameOf, amountOfStr, frequency,
                                            dateOfNextPayment, uid);
                                    incomes.add(income);
                                    totalIncome += amountOf;
                                }

                                IncomeAdapter adapter = new IncomeAdapter(ProgressTrackingActivity.this, incomes);
                                incomeListView.setAdapter(adapter);

                                String incomeInfoText = "Your total income is <b>$" + totalIncome + "</b>.";

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    incomeInfoTextView
                                            .setText(Html.fromHtml(incomeInfoText, Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    incomeInfoTextView.setText(Html.fromHtml(incomeInfoText));
                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

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
    }

}
