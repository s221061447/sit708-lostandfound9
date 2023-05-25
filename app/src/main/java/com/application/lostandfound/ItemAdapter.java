package com.application.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    static Context context;
    static List<Item> items;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public ItemAdapter(Context _context, List<Item> itemsList) {
        context = _context;
        items = itemsList;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        // Set the data
        holder.itemTitleTV.setText(String.format("%s %s", items.get(position).getLostOrFound(), items.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemTitleTV;

        public ItemViewHolder(@NonNull View itemView) {
            // Initialize the views
            super(itemView);

            itemTitleTV = itemView.findViewById(R.id.itemTitleTV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked
            int position = getAdapterPosition();

            // Get the item at the position
            Item item = items.get(position);

            // Open the DisplayItemActivity
            openDisplayItemActivity(item);
        }
    }

    public static void openDisplayItemActivity(Item item) {
        try {
            Intent intent = new Intent(context, DisplayItemActivity.class);
            intent.putExtra("item", objectMapper.writeValueAsString(item));
            context.startActivity(intent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
