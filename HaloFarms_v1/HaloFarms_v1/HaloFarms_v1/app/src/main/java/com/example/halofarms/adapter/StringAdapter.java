package com.example.halofarms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.halofarms.R;

import java.util.ArrayList;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.MyViewHolder> {
    private final ArrayList<String> mapsList;

    private View.OnClickListener mOnItemClickListener;


    // Provide a suitable constructor (depends on the kind of dataset)
    public StringAdapter(ArrayList<String> mapsList) {
        this.mapsList = mapsList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // field name
        public TextView textViewFieldName;

        public MyViewHolder(TextView v) {
            super(v);
            textViewFieldName = v.findViewById(R.id.single_item_text_view);
            //TODO: Step 3 of 4: setTag() as current view holder along with
            // setOnClickListener() as your local View.OnClickListener variable.
            // You can set the same mOnItemClickListener on multiple views if required
            // and later differentiate those clicks using view's id.
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }


    }

    // Create new views (invoked by the layout manager)
    @Override
    public StringAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.textViewFieldName.setText(mapsList.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mapsList.size();
    }

    //TODO: Step 2 of 4: Assign itemClickListener to your local View.OnClickListener variable
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }
}