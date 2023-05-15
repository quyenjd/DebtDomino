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

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {
    private List<Debt> data;

    public DebtAdapter(List<Debt> data) {
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
        holder.debtAmount.setText(debt.getAmountOf());
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

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            debtName = itemView.findViewById(R.id.debt_name);
            debtAmount = itemView.findViewById(R.id.debt_amount);
        }
    }
}


