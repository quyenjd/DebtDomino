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
import android.view.MenuItem;
import android.widget.Toast;

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

public class IncomeInventoryActivity extends AppCompatActivity {
    private static final String TAG = "IncomeInventoryActivity";
    Button buttonSave;
    LinearLayout incomeLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) { // null check
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_inventory);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonSave = findViewById(R.id.buttonSave);
        incomeLayout = findViewById(R.id.incomeLayout);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from fields and save to Firestore
                saveDataToFirestore();
                Intent intent = new Intent(IncomeInventoryActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDatePickerDialog(final View v) {
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
                        if (v instanceof EditText) {
                            EditText editText = (EditText) v;
                            c.set(year, month, dayOfMonth);
                            editText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime()));
                        }
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

        // Iterate through the children of incomeLayout.
        for (int i = 0; i < incomeLayout.getChildCount(); i += 5) {
            // Retrieve data from EditTexts and Spinner.
            // Check if the views are of the correct type before casting.
            if (!(incomeLayout.getChildAt(i) instanceof EditText &&
                    incomeLayout.getChildAt(i + 1) instanceof EditText &&
                    incomeLayout.getChildAt(i + 2) instanceof Spinner &&
                    incomeLayout.getChildAt(i + 3) instanceof EditText)) {
                continue;
            }

            // Retrieve data from EditTexts and Spinner.
            EditText nameEditText = (EditText) incomeLayout.getChildAt(i);
            EditText amountEditText = (EditText) incomeLayout.getChildAt(i + 1);
            Spinner frequencySpinner = (Spinner) incomeLayout.getChildAt(i + 2);
            EditText datePickerButton = (EditText) incomeLayout.getChildAt(i + 3);

            String name = nameEditText.getText().toString();
            String amount = amountEditText.getText().toString();
            String frequency = frequencySpinner.getSelectedItem().toString();
            String dateOfNextPayment = datePickerButton.getText().toString();

            // Check if any of the fields are empty before storing.
            if (!name.isEmpty() && !amount.isEmpty() && !frequency.isEmpty() && !dateOfNextPayment.isEmpty()) {
                Map<String, Object> income = new HashMap<>();
                income.put("nameOf", name);
                income.put("amountOf", amount);
                income.put("frequency", frequency);
                income.put("dateOfNextPayment", dateOfNextPayment);
                income.put("uid", userId);

                db.collection("incomes")
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
            } else {
                Toast.makeText(this, "Invalid income information, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
