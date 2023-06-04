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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PaymentPlanActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Spinner debtSpinner;
    private Button calculateButton;
    private TextView installmentListTextView;
    private TextView startDateInput;
    private TextView paymentAmountInput;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_plan_layout);

        // Set up the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Spinner debtRepaymentMethodSpinner; // add this line
        calculateButton = findViewById(R.id.calculate_payment_plan_button);
        installmentListTextView = findViewById(R.id.payment_plan_info);
        startDateInput = findViewById(R.id.start_date_input);
        paymentAmountInput = findViewById(R.id.payment_amount_input);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        debtRepaymentMethodSpinner = findViewById(R.id.debt_repayment_method_spinner);
        ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(this,
                R.array.debt_repayment_methods, android.R.layout.simple_spinner_item);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        debtRepaymentMethodSpinner.setAdapter(methodAdapter);

        // Fetch user debts.
        db.collection("debts")
                .whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> debtNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Debt debt = document.toObject(Debt.class);
                                debtNames.add(debt.getNameOf()); // assuming Debt class has a getNameOf method
                            }

                            // Create an ArrayAdapter using the string array and a default spinner layout
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentPlanActivity.this,
                                    android.R.layout.simple_spinner_item, debtNames);

                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply the adapter to the spinner
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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
                String paymentAmountStr = paymentAmountInput.getText().toString();

                if (startDateStr.isEmpty() || paymentAmountStr.isEmpty()) {
                    Toast.makeText(PaymentPlanActivity.this, "Please enter all the required fields", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Parse the input values
                Date startDate = null;
                double paymentAmount = 0.0;

                try {
                    startDate = dateFormatter.parse(startDateStr);
                    paymentAmount = Double.parseDouble(paymentAmountStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentPlanActivity.this, "Error parsing the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch all user debts from Firestore
                Date finalStartDate = startDate;
                double finalPaymentAmount = paymentAmount;

                // Create an array to hold the modified value
                final double[] modifiedPaymentAmount = { finalPaymentAmount };

                db.collection("debts")
                        .whereEqualTo("uid", currentUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<Debt> userDebts = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Debt debt = document.toObject(Debt.class);
                                        userDebts.add(debt);
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

                                    for (Debt debt : userDebts) {
                                        double totalAmount = Double.parseDouble(debt.getAmountOf());
                                        double rate = Double.parseDouble(debt.getRate());
                                        String frequency = debt.getFrequency();

                                        List<Map<String, Object>> installmentList = generateInstallmentList(totalAmount,
                                                rate, frequency, finalStartDate, modifiedPaymentAmount[0]);
                                        createPaymentPlanInFirestore(debt.getNameOf(), totalAmount, rate, frequency,
                                                finalStartDate, modifiedPaymentAmount[0], installmentList);

                                        // Update the payment amount for the next debt
                                        modifiedPaymentAmount[0] -= totalAmount;
                                        if (modifiedPaymentAmount[0] <= 0) {
                                            break;
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
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

    private void fetchPaymentPlan(String planId) {
        db.collection("paymentPlans").document(planId).collection("installments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> installments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> installment = document.getData();
                                // Convert the installment map to a string representation for displaying.
                                // Modify this based on how you want to display the installments.
                                installments.add(installment.toString());
                            }
                            displayInstallments(installments);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void displayInstallments(List<String> installments) {
        // Get a reference to the TextView
        TextView paymentPlanInfo = findViewById(R.id.payment_plan_info);

        // Convert the list of installments into a single String
        String installmentText = TextUtils.join("\n", installments);

        // Display the installments
        paymentPlanInfo.setText(installmentText);
    }

    private List<Map<String, Object>> generateInstallmentList(double totalAmount, double rate, String frequency,
            Date startDate, double paymentAmount) {
        List<Map<String, Object>> installmentList = new ArrayList<>();

        double remainingAmount = totalAmount;
        double totalPaid = 0.0; // Track the total amount paid
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int installments = 0;

        while (remainingAmount > 0) {
            double interest = computeInterest(remainingAmount, rate, frequency); // Calculate interest based on
                                                                                 // frequency
            double currentInstallment = Math.min(paymentAmount - interest, remainingAmount);
            remainingAmount -= currentInstallment;
            totalPaid += currentInstallment + interest;

            Map<String, Object> installment = new HashMap<>();
            installment.put("installment", decimalFormat.format(currentInstallment));
            installment.put("interest", decimalFormat.format(interest));
            installment.put("date", dateFormatter.format(calendar.getTime()));

            installmentList.add(installment);

            advanceCalendar(frequency, calendar); // Advance calendar based on frequency
            installments++;
        }

        Map<String, Object> totalPaidMap = new HashMap<>();
        totalPaidMap.put("totalPaid", decimalFormat.format(totalPaid));
        installmentList.add(totalPaidMap);

        Map<String, Object> durationMap = new HashMap<>();
        String durationString = "Duration of Payment Plan: " + installments + " " + frequency
                + (installments > 1 ? "s" : "");
        durationMap.put("duration", durationString);
        installmentList.add(durationMap);

        return installmentList;
    }

    private void advanceCalendar(String frequency, Calendar calendar) {
        if (frequency.equals("Fortnightly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 14);
        } else if (frequency.equals("Weekly")) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } else if (frequency.equals("Quarterly")) {
            calendar.add(Calendar.MONTH, 3);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }
    }

    private double computeInterest(double remainingAmount, double rate, String frequency) {
        switch (frequency) {
            case "Weekly":
                return remainingAmount * (rate / 100) / 52;
            case "Fortnightly":
                return remainingAmount * (rate / 100) / 26;
            case "Quarterly":
                return remainingAmount * (rate / 100) / 4;
            default: // assume monthly by default
                return remainingAmount * (rate / 100) / 12;
        }
    }

    private void createPaymentPlanInFirestore(String selectedDebt, double totalAmount, double rate, String frequency,
            Date startDate, double paymentAmount, List<Map<String, Object>> installmentList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the payment plans collection
        CollectionReference paymentPlansCollection = db.collection("paymentPlans");

        // Create a new payment plan document
        Map<String, Object> planData = new HashMap<>();
        planData.put("uid", currentUser.getUid());
        planData.put("debtId", selectedDebt); // using the debt name as id, you might want to use the actual id
        planData.put("firstDate", startDate);
        planData.put("lastDate", calculateLastDate(startDate, frequency, installmentList.size()));
        planData.put("paymentAmount", paymentAmount);
        planData.put("totalAmount", totalAmount);
        planData.put("rate", rate);
        planData.put("frequency", frequency);

        paymentPlansCollection.add(planData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Get a reference to the installments sub-collection
                CollectionReference installmentsCollection = documentReference.collection("installments");

                for (Map<String, Object> installment : installmentList) {
                    installmentsCollection.add(installment);
                }
            }
        });
    }

    private Date calculateLastDate(Date startDate, String frequency, int installmentCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        if (frequency.equals("Weekly")) {
            calendar.add(Calendar.DAY_OF_MONTH, installmentCount * 7);
        } else if (frequency.equals("Fortnightly")) {
            calendar.add(Calendar.DAY_OF_MONTH, installmentCount * 14);
        } else if (frequency.equals("Quarterly")) {
            calendar.add(Calendar.MONTH, installmentCount * 3);
        } else { // assume monthly by default
            calendar.add(Calendar.MONTH, installmentCount);
        }

        return calendar.getTime();
    }
}
