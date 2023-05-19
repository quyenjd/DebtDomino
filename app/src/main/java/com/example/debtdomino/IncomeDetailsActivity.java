package com.example.debtdomino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IncomeDetailsActivity extends AppCompatActivity {
    private static final String TAG = "IncomeDetailsActivity";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private IncomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_debt);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.debt_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IncomeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            // Fetch user incomes.
            db.collection("userDebts")
                    .whereEqualTo("uid", currentUser.getUid())
                    .whereEqualTo("type", "income")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Income> incomes = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    String nameOf = document.getString("nameOf");
                                    String amountOfStr = document.getString("amountOf");
                                    String frequency = document.getString("frequency");
                                    String type = document.getString("type");
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Income income = new Income(nameOf, amountOfStr, frequency, type, dateOfNextPayment, uid);
                                    incomes.add(income);
                                }
                                adapter.updateData(incomes);  // Update RecyclerView data
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, ProgressTrackingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

} }
