package com.example.debtdomino;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.debtdomino.Income;
import com.example.debtdomino.R;

import java.util.List;

import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.content.Context;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private Context context;
    private List<Income> data;

    public IncomeAdapter(Context context, List<Income> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = data.get(position);
        holder.incomeName.setText(income.getNameOf());
        holder.incomeAmount.setText(income.getAmountOf());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure to remove this $" + income.getAmountOf() + " income?");

        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                income.removeSelf().thenAccept(success -> {
                    Toast.makeText(context, success ? "Income removed successfully" : "Failed to remove income", Toast.LENGTH_SHORT).show();
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

        holder.incomeRemoveButton.setOnClickListener(new View.OnClickListener() {
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

    public void updateData(List<Income> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView incomeName;
        TextView incomeAmount;
        Button incomeRemoveButton;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeName = itemView.findViewById(R.id.income_name);
            incomeAmount = itemView.findViewById(R.id.income_amount);
            incomeRemoveButton = itemView.findViewById(R.id.income_remove_button);
        }
    }
}



