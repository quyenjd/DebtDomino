package com.example.debtdomino;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Income> data;

    public IncomeAdapter(List<Income> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_debt, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = data.get(position);
        holder.incomeName.setText(income.getName());
        holder.incomeAmount.setText(income.getAmount());
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

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeName = itemView.findViewById(R.id.debt_name);
            incomeAmount = itemView.findViewById(R.id.debt_amount);
        }
    }
}
