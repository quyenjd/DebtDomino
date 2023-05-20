package com.example.debtdomino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView editFormTitle, editFormPasswordNote;
    private EditText editTextName, editTextEmail, editTextCurrentPassword, editTextPassword, editTextConfirmPassword,
            editTextPhoneNumber;
    private Spinner editSpinnerPayoffMethod;
    private Button editFormButton;
    private ProgressBar progressBar;

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
        setContentView(R.layout.user_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editFormTitle = findViewById(R.id.editFormTitle);
        editFormPasswordNote = findViewById(R.id.editFormPasswordNote);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editSpinnerPayoffMethod = findViewById(R.id.editSpinnerPayoffMethod);
        editFormButton = findViewById(R.id.editFormButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        editFormTitle.setText("Registration");
        editFormPasswordNote.setVisibility(View.GONE);
        editTextCurrentPassword.setVisibility(View.GONE);
        editFormButton.setText("Register");


        editFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString();
                final String email = editTextEmail.getText().toString();
                final String phoneNumber = editTextPhoneNumber.getText().toString();
                final String debtPayoffMethod = editSpinnerPayoffMethod.getSelectedItem().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (password.equals(confirmPassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("name", name);
                                        userMap.put("email", email);
                                        userMap.put("phone_number", phoneNumber);
                                        userMap.put("payoff_method", debtPayoffMethod);

                                        db.collection("users").document(user.getUid())
                                                .set(userMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign up success, update UI with the signed-in user's
                                                            // information
                                                            Intent intent = new Intent(RegistrationActivity.this,
                                                                    ProgressTrackingActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(RegistrationActivity.this,
                                                                    "Error occurred while storing user data.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Toast.makeText(RegistrationActivity.this,
                                                "Authentication failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // when back button is pressed, go back to the previous activity
        return true;
    }
}
