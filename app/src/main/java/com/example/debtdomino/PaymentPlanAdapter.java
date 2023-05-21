package com.example.debtdomino;

import android.graphics.Color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;

public class PaymentPlanAdapter extends ArrayAdapter<Map<String, Object>> {

    private final LayoutInflater mInflater;
    private final int mResource;

    public PaymentPlanAdapter(Context context, List<Map<String, Object>> objects) {
        super(context, R.layout.item_payment_plan, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = R.layout.item_payment_plan;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.planNameTextView = convertView.findViewById(R.id.plan_name);
            holder.amountTextView = convertView.findViewById(R.id.amount);
            holder.dueDateTextView = convertView.findViewById(R.id.due_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> paymentPlan = getItem(position);

        holder.planNameTextView.setText((String) paymentPlan.get("title"));
        holder.dueDateTextView.setText(formatDate((Timestamp) paymentPlan.get("date")));

        if (((String) paymentPlan.get("type")).equals("incomePayment")) {
            holder.amountTextView.setTextColor(Color.rgb(0, 255, 0));
            holder.amountTextView.setText("+" + ((Object) paymentPlan.get("amount")).toString());
        } else if (((String) paymentPlan.get("type")).equals("debtPayment")) {
            holder.amountTextView.setTextColor(Color.rgb(255, 0, 0));
            holder.amountTextView.setText("-" + ((Object) paymentPlan.get("amount")).toString());
        } else {
            holder.amountTextView.setText(((Object) paymentPlan.get("amount")).toString());
        }

        return convertView;
    }

    private String formatDate(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = timestamp.toDate();
        return formatter.format(date);
    }

    private static class ViewHolder {
        TextView planNameTextView;
        TextView amountTextView;
        TextView dueDateTextView;
    }
}