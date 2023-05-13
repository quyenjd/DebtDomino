package com.example.debtdomino;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.text.InputType;


import androidx.appcompat.app.AppCompatActivity;

public class DebtInventoryActivity extends AppCompatActivity {
    Button buttonRegister;
    LinearLayout debtLayout;
    LinearLayout incomeLayout;
    Button addDebtButton;
    Button addIncomeButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_inventory);

        buttonRegister = (Button) findViewById(R.id.buttonFinalise);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        View lineView = new View(this);

        // Set properties for the View (line)
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 5); // Change height here for thicker line
        lineParams.setMargins(0, 10, 0, 10);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#000000")); // This is a light gray color


        // Set some properties of the EditTexts
        newDebtName.setHint("Name of Debt");
        newDebtAmount.setHint("Amount of Debt");
        newDebtRate.setHint("Rate");
        newDebtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        newDebtRate.setInputType(InputType.TYPE_CLASS_NUMBER);

        newDebtName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newDebtAmount.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newDebtRate.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

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
        debtLayout.addView(lineView); // Add the line
    }


    private void addIncomeFields() {
        // Create new EditTexts and Spinner
        EditText newIncome = new EditText(this);
        EditText newIncomeAmount = new EditText(this);
        Spinner newIncomeFrequency = new Spinner(this);
        View lineView = new View(this);

        // Set properties for the View (line)
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 5); // Change height here for thicker line
        lineParams.setMargins(0, 10, 0, 10);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.parseColor("#000000")); // This is a light gray color

        // Set some properties of the EditTexts
        newIncome.setHint("Name of Income");
        newIncome.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        newIncomeAmount.setHint("Amount of Income");
        newIncomeAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        newIncomeAmount.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Set some properties of the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,
                        R.array.income_frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newIncomeFrequency.setAdapter(adapter);

        // Add the EditTexts and Spinner to the layout
        incomeLayout.addView(newIncome);
        incomeLayout.addView(newIncomeAmount);
        incomeLayout.addView(newIncomeFrequency);
        incomeLayout.addView(lineView); // Add the line
    }

}

