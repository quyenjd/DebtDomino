package com.example.debtdomino;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentPlan extends AppCompatActivity {
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

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        debtSpinner = findViewById(R.id.debt_spinner);
        calculateButton = findViewById(R.id.calculate_payment_plan_button);
        installmentListTextView = findViewById(R.id.payment_plan_info);
        startDateInput = findViewById(R.id.start_date_input);
        paymentAmountInput = findViewById(R.id.payment_amount_input);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

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
                                debtNames.add(debt.getNameOf());  // assuming Debt class has a getNameOf method
                            }

                            // Create an ArrayAdapter using the string array and a default spinner layout
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentPlan.this,
                                    android.R.layout.simple_spinner_item, debtNames);

                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply the adapter to the spinner
                            debtSpinner.setAdapter(adapter);
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
                String selectedDebt = debtSpinner.getSelectedItem().toString();
                String startDateStr = startDateInput.getText().toString();
                String paymentAmountStr = paymentAmountInput.getText().toString();

                if (startDateStr.isEmpty() || paymentAmountStr.isEmpty()) {
                    Toast.makeText(PaymentPlan.this, "Please enter all the required fields", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PaymentPlan.this, "Error parsing the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the selected debt from Firestore
                Date finalStartDate = startDate;
                double finalPaymentAmount = paymentAmount;
                db.collection("debts")
                        .whereEqualTo("uid", currentUser.getUid())
                        .whereEqualTo("nameOf", selectedDebt)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Debt debt = document.toObject(Debt.class);

                                        double totalAmount = Double.parseDouble(debt.getAmountOf());
                                        double rate = Double.parseDouble(debt.getRate());
                                        String frequency = debt.getFrequency();

                                        List<String> installmentList = generateInstallmentList(totalAmount, rate, frequency, finalStartDate, finalPaymentAmount);

                                        String installmentListStr = String.join("\n", installmentList);
                                        installmentListTextView.setText(installmentListStr);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
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
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private List<String> generateInstallmentList(double totalAmount, double rate, String frequency, Date startDate, double paymentAmount) {
        List<String> installmentList = new ArrayList<>();

        double remainingAmount = totalAmount;
        double totalPaid = 0.0;  // Track the total amount paid
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (remainingAmount > 0) {
            double interest = computeInterest(remainingAmount, rate, frequency); // Calculate interest based on frequency
            double currentInstallment = Math.min(paymentAmount - interest, remainingAmount);
            remainingAmount -= currentInstallment;
            totalPaid += currentInstallment + interest;

            String installment = "Installment: $" + decimalFormat.format(currentInstallment) + " (Interest: $" + decimalFormat.format(interest) + ") - " + dateFormatter.format(calendar.getTime());
            installmentList.add(installment);

            advanceCalendar(frequency, calendar); // Advance calendar based on frequency
        }

        String totalPaidString = "Total Paid: $" + decimalFormat.format(totalPaid);
        installmentList.add(totalPaidString);

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
        switch(frequency) {
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
}
