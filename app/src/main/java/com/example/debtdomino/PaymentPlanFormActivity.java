package com.example.debtdomino;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPlanFormActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Spinner debtRepaymentMethodSpinner;
    private Button calculateButton;
    private TextView startDateInput;
    private TextView balanceInput;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_plan_form);

        // Set up the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        debtRepaymentMethodSpinner = findViewById(R.id.debt_repayment_method_spinner);
        calculateButton = findViewById(R.id.calculate_payment_plan_button);
        startDateInput = findViewById(R.id.start_date_input);
        balanceInput = findViewById(R.id.balance_input);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateInput);
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMethod = debtRepaymentMethodSpinner.getSelectedItem().toString();
                String startDateStr = startDateInput.getText().toString();
                String balanceStr = balanceInput.getText().toString();

                if (startDateStr.isEmpty() || balanceStr.isEmpty()) {
                    Toast.makeText(PaymentPlanFormActivity.this, "Please enter all the required fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the input values
                Date startDate = null;
                double balance = 0.0;

                try {
                    startDate = dateFormatter.parse(startDateStr);
                    balance = Double.parseDouble(balanceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentPlanFormActivity.this, "Error parsing the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Let user know we are processing
                Toast.makeText(PaymentPlanFormActivity.this, "Please wait while we are processing your request",
                        Toast.LENGTH_SHORT)
                        .show();

                Date finalStartDate = startDate;
                double finalBalance = balance;

                // Fetch all user debts from Firestore
                db.collection("debts")
                        .whereEqualTo("uid", currentUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<Debt> userDebts = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        userDebts.add(Debt.fromSnapshot(document));
                                    }

                                    // Sort based on the selected method
                                    if (selectedMethod.equals("Avalanche")) {
                                        Collections.sort(userDebts,
                                                (debt1, debt2) -> Double.compare(Double.parseDouble(debt2.getRate()),
                                                        Double.parseDouble(debt1.getRate())));
                                    } else { // Assume Snowball if not Avalanche
                                        Collections.sort(userDebts,
                                                (debt1, debt2) -> Double.compare(
                                                        Double.parseDouble(debt1.getAmountOf()),
                                                        Double.parseDouble(debt2.getAmountOf())));
                                    }

                                    // Fetch all user incomes from Firestore
                                    db.collection("incomes").whereEqualTo("uid", currentUser.getUid()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<Income> userIncomes = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            userIncomes.add(Income.fromSnapshot(document));
                                                        }

                                                        Balance balance = new Balance(finalBalance, userIncomes,
                                                                finalStartDate);

                                                        List<Map<String, Object>> plans = new ArrayList<>();

                                                        for (Debt debt : userDebts) {
                                                            while (debt.getTotalValue() > 0) {
                                                                debt.jumpToCurrentIntervalOf(balance.getCurrentDate());

                                                                Payment payment = new Payment(debt,
                                                                        Math.min(debt.getTotalValue(),
                                                                                balance.getCurrentBalance()),
                                                                        debt.getParsedDateOfNextPayment());
                                                                debt.makePayment(payment);
                                                                balance.setCurrentBalance(balance.getCurrentBalance()
                                                                        - payment.getPaymentAmount());

                                                                Map<String, Object> paymentMap = new HashMap<>();
                                                                paymentMap.put("title", debt.getNameOf());
                                                                paymentMap.put("type", "debtPayment");
                                                                paymentMap.put("amount", payment.getPaymentAmount());
                                                                paymentMap.put("date", payment.getPayBefore());

                                                                plans.add(paymentMap);

                                                                // If debt is still ongoing, and we're out of balance,
                                                                // wait for next income
                                                                if (debt.getTotalValue() > 0
                                                                        && balance.getCurrentBalance() == 0) {
                                                                    Income nextIncome = balance.jumpToNextPayment();

                                                                    Map<String, Object> incomeMap = new HashMap<>();
                                                                    incomeMap.put("title", nextIncome.getNameOf());
                                                                    incomeMap.put("type", "incomePayment");
                                                                    incomeMap.put("amount", Double
                                                                            .parseDouble(nextIncome.getAmountOf()));
                                                                    incomeMap.put("date", balance.getCurrentDate());

                                                                    plans.add(incomeMap);
                                                                }
                                                            }
                                                        }

                                                        // Store the plans to Firestore
                                                        Map<String, Object> planData = new HashMap<>();
                                                        planData.put("createdAt", new Date());
                                                        planData.put("plans", plans);

                                                        db.collection("plans").document(currentUser.getUid())
                                                                .set(planData).addOnCompleteListener(
                                                                        new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(
                                                                                    @NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(
                                                                                            PaymentPlanFormActivity.this,
                                                                                            "Your payment plan has been created",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    Toast.makeText(
                                                                                            PaymentPlanFormActivity.this,
                                                                                            "Error storing plan: " +
                                                                                                    task.getException(),
                                                                                            Toast.LENGTH_SHORT)
                                                                                            .show();
                                                                                }

                                                                                onBackPressed();
                                                                            }
                                                                        });
                                                    } else {
                                                        Toast.makeText(PaymentPlanFormActivity.this,
                                                                "Error getting invoices: " +
                                                                        task.getException(),
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(PaymentPlanFormActivity.this, "Error getting debts: " +
                                            task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

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

    private void showDatePickerDialog(TextView dateInput) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    dateInput.setText(dateFormatter.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
