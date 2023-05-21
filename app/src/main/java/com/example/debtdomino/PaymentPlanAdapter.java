package com.example.debtdomino;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PaymentPlanAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final int mResource;
    private final List<String> documentIds; // Store document IDs
    private OnRemoveButtonClickListener mListener;

    public PaymentPlanAdapter(Context context, List<String> objects, OnRemoveButtonClickListener listener) {
        super(context, R.layout.payment_plan_item, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = R.layout.payment_plan_item;
        mListener = listener;
        documentIds = new ArrayList<>();
    }

    public interface OnRemoveButtonClickListener {
        void onRemoveButtonClick(int position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.planDetails);
            holder.removeButton = convertView.findViewById(R.id.removeButton);
            holder.removeButton.setFocusable(false); // add this line
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getItem(position));
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRemoveButtonClick(position);
                }
            }
        });

        return convertView;
    }


    // Method to set the document IDs
    public void setDocumentIds(List<String> documentIds) {
        this.documentIds.clear();
        this.documentIds.addAll(documentIds);
    }

    // Method to get a document ID for a given position
    public String getDocumentId(int position) {
        return documentIds.get(position);
    }

    private static class ViewHolder {
        TextView text;
        Button removeButton;
    }
}
