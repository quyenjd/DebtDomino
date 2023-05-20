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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
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
        setContentView(R.layout.user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null || db == null) {
            onBackPressed();
            return;
        }

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

        progressBar.setVisibility(View.VISIBLE);

        editFormTitle.setText("Edit Profile");
        editTextEmail.setEnabled(false);
        editTextEmail.setAlpha(0.5f);
        editTextEmail.setKeyListener(null);
        editTextEmail.setText(user.getEmail());
        editFormButton.setText("Save");

        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            editTextName.setText(task.getResult().getString("name"));
                            editTextPhoneNumber.setText(task.getResult().getString("phone_number"));
                            editSpinnerPayoffMethod.setSelection(
                                    String.valueOf(task.getResult().getString("payoff_method")).equals("Snowball") ? 1
                                            : 0);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Cannot load user profile",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });

        editFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString();
                final String email = editTextEmail.getText().toString();
                final String phoneNumber = editTextPhoneNumber.getText().toString();
                final String debtPayoffMethod = editSpinnerPayoffMethod.getSelectedItem().toString();
                String currentPassword = editTextCurrentPassword.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                // If all passwords are provided, change the user's password.
                if (!currentPassword.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (password.equals(confirmPassword)) {
                                            user.updatePassword(password)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ProfileActivity.this,
                                                                        "Password has been updated",
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ProfileActivity.this,
                                                                        "Failed to update password: "
                                                                                + task.getException().getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(ProfileActivity.this,
                                                    "Passwords do not match",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(ProfileActivity.this,
                                                "Authentication failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // Store information to Firestore.

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("name", name);
                userMap.put("phone_number", phoneNumber);
                userMap.put("payoff_method", debtPayoffMethod);

                db.collection("users").document(user.getUid())
                        .set(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(ProfileActivity.this,
                                            ProgressTrackingActivity.class);
                                    startActivity(intent);

                                    Toast.makeText(ProfileActivity.this,
                                            "Profile has been updated",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this,
                                            "Error occurred while storing user data.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // when back button is pressed, go back to the previous activity
        return true;
    }
}
