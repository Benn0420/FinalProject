package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.finalproject.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBar.myToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.myDrawer, binding.appBar.myToolbar,
                R.string.open, R.string.close);

        binding.myDrawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, new HomeFragment());
        transaction.commit();

        binding.appBar.myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                int id = item.getItemId();

                if (id == R.id.appbar_item_home) {
                    transaction.replace(R.id.containers, new HomeFragment());
                    transaction.commit();
                } else if (id == R.id.appbar_item_save) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containers);

                    if (currentFragment instanceof ImageViewFragment) {
                        saveImage();
                        //Toast.makeText(MainActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No image to save", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.appbar_item_info) {
                    finishAffinity();
                }

                return true;
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                int id = item.getItemId();

                if (id == R.id.navbar_item_home) {
                    transaction.replace(R.id.containers, new HomeFragment());
                    transaction.addToBackStack("Home");
                    transaction.commit();
                } else if (id == R.id.navbar_item_today) {
                    transaction.replace(R.id.containers, new ImageViewFragment(), "ImageView");
                    transaction.addToBackStack("ImageView");
                    transaction.commit();
                } else if (id == R.id.navbar_item_choice) {
                    showDatePicker();
                } else if (id == R.id.navbar_item_folder) {
                    transaction.replace(R.id.containers, new CollectionsFragment());
                    transaction.addToBackStack("Collection");
                    transaction.commit();
                } else if (id == R.id.navbar_item_iss) {
                    transaction.replace(R.id.containers, new IssFragment());
                    transaction.addToBackStack("ISStracking");
                    transaction.commit();
                }

                binding.myDrawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    private void showDatePicker() {
        // Create a Calendar instance to get the current date
        Calendar currentDate = Calendar.getInstance();

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the date selection
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        Toast.makeText(MainActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

                        // Pass the selected date to the next fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("selectedDate", selectedDate);

                        // Create an instance of the next fragment and set the arguments
                        ImageViewFragment imageViewFragment = new ImageViewFragment();
                        imageViewFragment.setArguments(bundle);

                        // Replace the current fragment with the next one
                        getSupportFragmentManager().beginTransaction()
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

    private void saveImage() {

        try {
            // Finding ImageViewFragment by its tag
            ImageViewFragment imageViewFragment = (ImageViewFragment) getSupportFragmentManager().findFragmentByTag("ImageView");

            // Retrieving ImageInfo
            ImageViewFragment.ImageInfo imageInfo = imageViewFragment.getImageInfo();

            // Calling function to save the Image
            imageViewFragment.saveImage(imageInfo);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}