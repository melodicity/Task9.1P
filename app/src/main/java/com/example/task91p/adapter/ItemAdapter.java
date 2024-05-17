package com.example.task91p.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task91p.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    // Initialise fields
    private final List<String> items;
    private final Context context;
    private static ItemClickListener clickListener;

    // Constructor takes the list of items to show and context
    public ItemAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    // Inflates the row layout from xml layout file
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    // Binds the data to the view and elements in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemTitle.setText(String.format("%s ...", items.get(position)));
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvItemTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    // Method for getting item at click position
    public String getItem(int position) {
        return items.get(position);
    }

    // Allow click events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    // Implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }
}