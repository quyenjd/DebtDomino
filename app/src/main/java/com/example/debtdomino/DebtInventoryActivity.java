package com.example.debtdomino;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;




public class DebtInventoryActivity extends AppCompatActivity {
    private static final String TAG = "";
    Button buttonRegister;
    LinearLayout debtLayout;
    LinearLayout incomeLayout;
    Button addDebtButton;
    Button addIncomeButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_inventory);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonRegister = (Button) findViewById(R.id.buttonFinalise);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from fields and save to Firestore
                saveDataToFirestore();
                Intent intent = new Intent(DebtInventoryActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
            }
        });

        debtLayout = findViewById(R.id.debtLayout);
        incomeLayout = findViewById(R.id.incomeLayout);
        addDebtButton = findViewById(R.id.buttonAddDebt);
        addIncomeButton = findViewById(R.id.buttonAddIncome);

        addDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDebtFields();
            }
        });

        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIncomeFields();
            }
        });
    }

    private void addDebtFields() {
        // Create new EditTexts and Spinner
        EditText newDebtName = new EditText(this);
        EditText newDebtAmount = new EditText(this);
        EditText newDebtRate = new EditText(this);
        Spinner newDebtFrequency = new Spinner(this);
        Button newDebtDatePicker = new Button(this);
        View lineView = new View(this);

        // Set properties for the View (line)
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 5);
        lineParams.setMargins(0, 10, 0, 10);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#000000"));

        // Set some properties of the EditTexts
        newDebtName.setHint("Name of Debt");
        newDebtAmount.setHint("Amount of Debt");
        newDebtRate.setHint("Rate");
        newDebtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        newDebtRate.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Set properties for the Button
        newDebtDatePicker.setText("Choose Date");
        newDebtDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        // Set some properties of the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.debt_frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newDebtFrequency.setAdapter(adapter);

        // Add the EditTexts and Spinner to the layout
        debtLayout.addView(newDebtName);
        debtLayout.addView(newDebtAmount);
        debtLayout.addView(newDebtRate);
        debtLayout.addView(newDebtFrequency);
        debtLayout.addView(newDebtDatePicker);
        debtLayout.addView(lineView);
    }

    private void addIncomeFields() {
        // Create new EditTexts and Spinner
        EditText newIncomeName = new EditText(this);
        EditText newIncomeAmount = new EditText(this);
        Spinner newIncomeFrequency = new Spinner(this);
        Button newIncomeDatePicker = new Button(this);
        View lineView = new View(this);

        // Set properties for the View (line)
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 5);
        lineParams.setMargins(0, 10, 0, 10);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#000000"));

        // Set some properties of the EditTexts
        newIncomeName.setHint("Name of Income");
        newIncomeAmount.setHint("Amount of Income");
        newIncomeAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Set properties for the Button
        newIncomeDatePicker.setText("Choose Date");
        newIncomeDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        // Set some properties of the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.income_frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newIncomeFrequency.setAdapter(adapter);

        // Add the EditTexts and Spinner to the layout
        incomeLayout.addView(newIncomeName);
        incomeLayout.addView(newIncomeAmount);
        incomeLayout.addView(newIncomeFrequency);
        incomeLayout.addView(newIncomeDatePicker);
        incomeLayout.addView(lineView);
    }

    public void showDatePickerDialog(View v) {
        // Get the current date.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Store the date selected for use when saving to Firestore.
                        c.set(year, month, dayOfMonth);
                        ((Button)v).setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    private void saveDataToFirestore() {
        // Get the current user's ID from Firebase Authentication.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Handle the case where the user is not signed in.
            return;
        }
        String userId = user.getUid();

        // Initialize Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Iterate through the children of debtLayout and incomeLayout.
        for (int i = 0; i < debtLayout.getChildCount(); i += 5) {
            // Retrieve data from EditTexts and Spinner.
            String name = ((EditText) debtLayout.getChildAt(i)).getText().toString();
            String amount = ((EditText) debtLayout.getChildAt(i + 1)).getText().toString();
            String rate = ((EditText) debtLayout.getChildAt(i + 2)).getText().toString();
            String frequency = ((Spinner) debtLayout.getChildAt(i + 3)).getSelectedItem().toString();
            String dateOfNextPayment = ((Button) debtLayout.getChildAt(i + 4)).getText().toString();

            // Create a new document in the "userDebts" collection.
            Map<String, Object> debt = new HashMap<>();
            debt.put("nameOf", name);
            debt.put("amountOf", amount);
            debt.put("rate", rate);
            debt.put("frequency", frequency);
            debt.put("type", "debt");
            debt.put("dateOfNextPayment", dateOfNextPayment);
            debt.put("uid", userId);

            db.collection("userDebts")
                    .add(debt)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

        for (int i = 0; i < incomeLayout.getChildCount(); i += 4) {
            // Retrieve data from EditTexts and Spinner.
            String name = ((EditText) incomeLayout.getChildAt(i)).getText().toString();
            String amount = ((EditText) incomeLayout.getChildAt(i + 1)).getText().toString();
            String frequency = ((Spinner) incomeLayout.getChildAt(i + 2)).getSelectedItem().toString();
            String dateOfNextPayment = ((Button) incomeLayout.getChildAt(i + 3)).getText().toString();

            // Create a new document in the "userDebts" collection.
            Map<String, Object> income = new HashMap<>();
            income.put("nameOf", name);
            income.put("amountOf", amount);
            income.put("frequency", frequency);
            income.put("type", "income");
            income.put("dateOfNextPayment", dateOfNextPayment);
            income.put("uid", userId);

            db.collection("userDebts")
                    .add(income)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

}
