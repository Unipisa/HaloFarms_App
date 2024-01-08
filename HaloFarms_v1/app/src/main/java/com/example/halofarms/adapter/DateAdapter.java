package com.example.halofarms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.halofarms.R;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {
    private final ArrayList<String> dates;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DateAdapter(ArrayList<String> dates) {
        this.dates = dates;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // field name
        public TextView textViewFieldName;

        public MyViewHolder(TextView v) {
            super(v);
            textViewFieldName = v.findViewById(R.id.single_item_text_view);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_text_view, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textViewFieldName.setText(dates.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dates.size();
    }

}