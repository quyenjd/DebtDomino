package com.example.debtdomino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DebtInventoryActivity extends AppCompatActivity {
    Button buttonRegister;
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

    }



}