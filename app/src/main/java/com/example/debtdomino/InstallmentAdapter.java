package com.example.debtdomino;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InstallmentAdapter extends ArrayAdapter<Installment> {

    private final LayoutInflater mInflater;
    private final int mResource;

    public InstallmentAdapter(Context context, List<Installment> objects) {
        super(context, R.layout.installment_item, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = R.layout.installment_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.installment_date); // Initialize dateText TextView
            holder.amountText = convertView.findViewById(R.id.installment_amount);
            holder.interestText = convertView.findViewById(R.id.installment_interest);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Installment installment = getItem(position);
        holder.dateText.setText(formatDate(installment.getDate()));
        holder.amountText.setText(String.valueOf(installment.getInstallmentAmount()));
        holder.interestText.setText(String.valueOf(installment.getInterest()));

        return convertView;
    }

    private static class ViewHolder {
        TextView dateText;
        TextView amountText;
        TextView interestText;
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
}
