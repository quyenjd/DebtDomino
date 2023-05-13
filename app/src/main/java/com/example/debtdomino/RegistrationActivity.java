package com.example.debtdomino;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        if (getSupportActionBar() != null) { // null check
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // when back button is pressed, go back to the previous activity
        return true;
    }
}
