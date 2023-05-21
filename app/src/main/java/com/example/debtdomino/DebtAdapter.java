package com.example.debtdomino;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.debtdomino.Debt;
import com.example.debtdomino.R;

import java.util.List;

import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.content.Context;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {
    private Context context;
    private List<Debt> data;

    public DebtAdapter(Context context, List<Debt> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
        Debt debt = data.get(position);
        holder.debtName.setText(debt.getNameOf());
        holder.debtAmount.setText(debt.getAmountOf() + " " + debt.getFrequency());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure to remove this $" + debt.getAmountOf() + " debt?");

        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                debt.removeSelf().thenAccept(success -> {
                    Toast.makeText(context, success ? "Debt removed successfully" : "Failed to remove debt",
                            Toast.LENGTH_SHORT).show();
                    data.remove(position);
                    notifyDataSetChanged();
                });
            }
        });

        // Set negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        holder.debtRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Debt> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class DebtViewHolder extends RecyclerView.ViewHolder {
        TextView debtName;
        TextView debtAmount;
        Button debtRemoveButton;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            debtName = itemView.findViewById(R.id.debt_name);
            debtAmount = itemView.findViewById(R.id.debt_amount);
            debtRemoveButton = itemView.findViewById(R.id.debt_remove_button);
        }
    }
}
