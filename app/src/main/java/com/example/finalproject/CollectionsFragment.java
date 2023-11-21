package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class CollectionsFragment extends Fragment {

    private ListView collectionsListView;
    private ArrayList<String> titlesList;
    private CollectionsListAdapter adapter;

    public CollectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        collectionsListView = view.findViewById(R.id.collectionsList);

        // Initializing title list
        titlesList = loadTitlesFromSharedPreferences();

        // Initializing adapter
        adapter = new CollectionsListAdapter(getContext(), titlesList);

        // Setting the adapter to the ListView
        collectionsListView.setAdapter(adapter);

        return view;
    }

    private ArrayList<String> loadTitlesFromSharedPreferences() {

        ArrayList<String> titlesList = new ArrayList<>();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        //Retrieving all entries
        Map<String, ?> allEntries = preferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();

            //Checking if title key
            if (key.endsWith("_title")) {
                //Extract and add to list
                String title = preferences.getString(key, "");
                titlesList.add(title);
            }
        }

        return titlesList;
    }
}