package com.example.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> items;

    public CustomAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        String currentItem = getItem(pos);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.itemText);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        ImageView itemIcon = convertView.findViewById(R.id.itemIcon);

        itemText.setText(currentItem);
        itemIcon.setImageResource(getIconForTask(currentItem));

        btnDelete.setOnClickListener(v -> {
            items.remove(pos);
            notifyDataSetChanged();
        });

        return convertView;
    }

    // Optional: Choose icons based on task text
    private int getIconForTask(String task) {
        task = task.toLowerCase();
        if (task.contains("medicine")) return R.drawable.medicines_img;
        if (task.contains("food")) return R.drawable.food_icon;
        if (task.contains("consultation")) return R.drawable.doctor_icon;
        if (task.contains("exercise")) return R.drawable.bell_img;
        if (task.contains("call")) return R.drawable.call_family_icon;
        if (task.contains("water")) return R.drawable.drink_water_icon;
        if (task.contains("nap")) return R.drawable.nap_time_icon;
        return R.drawable.pointer;
    }
}
