package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CollectionsListAdapter extends ArrayAdapter<String> {

    /*
    This java class is used to adapt the collections list with the list item layout.
     */

    public CollectionsListAdapter (Context context, ArrayList<String> titles) {
        super(context, 0, titles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        // Retrieving the titles list position
        String title = getItem(position);
        // converting the title and setting it to the text views text
        TextView titleTextView = convertView.findViewById(R.id.itemTitleTextView);
        titleTextView.setText(title);

        return convertView;
    }

}
