package com.example.debtdomino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DebtDetailsActivity extends AppCompatActivity {
    private static final String TAG = "DebtDetailsActivity";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private DebtAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_debt);  // Change to your activity's layout

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.debt_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DebtAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            // Fetch user debts.
            db.collection("userDebts")
                    .whereEqualTo("uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Debt> debts = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    String nameOf = document.getString("nameOf");
                                    String amountOfStr = document.getString("amountOf");
                                    String rate = document.getString("rate");
                                    String frequency = document.getString("frequency");
                                    String type = document.getString("type");
                                    String dateOfNextPayment = document.getString("dateOfNextPayment");
                                    String uid = document.getString("uid");

                                    Debt debt = new Debt(nameOf, amountOfStr, rate, frequency, type, dateOfNextPayment, uid);
                                    debts.add(debt);
                                }
                                adapter.updateData(debts);  // Update RecyclerView data
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}
