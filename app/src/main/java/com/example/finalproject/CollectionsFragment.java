package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class CollectionsFragment extends Fragment {

    /*
    This fragment displays the collection of the users saved photos.
    Users are able to click the items in the list and view their photo
    in the collection fragment editor. The fragments main purpose is to
    populate the list with saved title names.
     */

    private ListView collectionsListView;
    private ArrayList<String> titlesList;
    private CollectionsListAdapter adapter;
    private static CollectionsFragment instance = null;

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

        // Setting the fragments instance
        instance = this;

        // OnItemClickListener to handle item clicks
        collectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handling item click in CollectionsList
                onListItemClick(position);
            }
        });

        return view;
    }

    public static CollectionsFragment getInstance() {
        return instance;
    }

    private void onListItemClick(int position) {
        // Creating instance of CollectionsEditorFragment
        CollectionsEditorFragment editorFragment = new CollectionsEditorFragment();

        // You might want to pass data to the dialog fragment, e.g., image title
        String imageTitle = titlesList.get(position);
        Bundle args = new Bundle();
        args.putString("imageTitle", imageTitle);
        editorFragment.setArguments(args);

        // Showing the dialog
        editorFragment.show(getFragmentManager(), "CollectionsEditorFragment");
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

    // Method to update the list
    public void updateList() {
        ArrayList<String> updatedList = loadTitlesFromSharedPreferences();
        titlesList.clear();
        titlesList.addAll(updatedList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}