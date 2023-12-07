package com.example.finalproject;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    /*
    This fragment is the home fragment. The first fragment displayed upon
    loading. The fragment presents four buttons leading to different fragments
    within the app.
     */

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Finds buttons by their IDs
        Button todayButton = view.findViewById(R.id.today_button);
        Button pickADayButton = view.findViewById(R.id.pickaday_button);
        Button collectionsButton = view.findViewById(R.id.collections_button);
        Button trackISSButton = view.findViewById(R.id.trackiss_button);

        // Click listeners for each button
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for "Today's Image of Space" button
                Toast.makeText(getActivity(), getString(R.string.HFtodayT), Toast.LENGTH_SHORT).show();
                // logic for this button click
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, new ImageViewFragment(), "ImageView");
                transaction.addToBackStack("ImageView");
                transaction.commit();
            }
        });

        pickADayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for "Pick a Date" button

                // Create a Calendar instance to get the current date
                Calendar currentDate = Calendar.getInstance();

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Handle the date selection
                                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                                Toast.makeText(getActivity(), getString(R.string.HFpickT) + selectedDate, Toast.LENGTH_SHORT).show();

                                // Pass the selected date to the next fragment
                                Bundle bundle = new Bundle();
                                bundle.putString("selectedDate", selectedDate);

                                // Create an instance of the next fragment and set the arguments
                                ImageViewFragment imageViewFragment = new ImageViewFragment();
                                imageViewFragment.setArguments(bundle);

                                // Replace the current fragment with the next one
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.containers, imageViewFragment, "ImageView")
                                        .addToBackStack("ImageView")
                                        .commit();
                            }
                        },
                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)
                );

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        collectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for "Saved Photos" button
                Toast.makeText(getActivity(), getString(R.string.HFsavedT), Toast.LENGTH_SHORT).show();
                // logic for this button click
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, new CollectionsFragment(), "CollectionsList");
                transaction.addToBackStack("CollectionsList");
                transaction.commit();
            }
        });

        trackISSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for "Track the I.S.S." button
                Toast.makeText(getActivity(), getString(R.string.HFissT), Toast.LENGTH_SHORT).show();
                // logic for this button click
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, new IssFragment(), "IssTracking");
                transaction.addToBackStack("IssTracking");
                transaction.commit();
            }
        });

        return view;
    }
}