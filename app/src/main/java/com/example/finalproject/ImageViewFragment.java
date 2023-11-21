package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ImageViewFragment extends Fragment {

    private ImageView imageView;
    private TextView iotdDateTextView;
    private TextView iotdTitleTextView;
    private TextView iotdDescriptionTextView;
    private ImageInfo imageInfo;

    public ImageViewFragment() {
        // Required empty public constructor
    }

    public static ImageViewFragment newInstance() {
        return new ImageViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);

        imageView = view.findViewById(R.id.iotdPhoto);
        iotdDateTextView = view.findViewById(R.id.iotdDate);
        iotdTitleTextView = view.findViewById(R.id.iotdTitle);
        iotdDescriptionTextView = view.findViewById(R.id.iotdText);

        // Fetch and display the image
        new FetchAndDisplayTodaysImage().execute();

        iotdDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        iotdDescriptionTextView.setMaxLines(10);

        return view;
    }

    // Custom class to hold both Bitmap and information
    class ImageInfo {
        Bitmap bitmap;
        String imageDate;
        String imageTitle;
        String imageDescription;
    }

    public String getCurrentDate() {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date
        Date currentDate = new Date();

        // Return formatted date
        return sdf.format(currentDate);
    }

    // Getter for ImageInfo
    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    // Setter for ImageInfo
    private void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public void saveImage (ImageInfo imageInfo) {
        try {
            // Create a unique filename based on the title or any other criteria
            String filename = imageInfo.imageTitle.replaceAll("\\s+", "") + ".jpg";

            // Saving image
            FileOutputStream outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            imageInfo.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();


            // Call to save details
            saveDetailsLocally(filename, imageInfo.imageTitle, imageInfo.imageDescription, imageInfo.imageDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDetailsLocally(String filename, String savedTitle, String savedDescription, String savedDate) {

        //Saving image details using Shared Preferences
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(filename, filename);
        editor.putString(filename + "_title", savedTitle);
        editor.putString(filename + "_description", savedDescription);
        editor.putString(filename + "_date", savedDate);
        editor.apply();

        // Retrieve data for verification
        String retrievedFile = preferences.getString(filename + "_title", "NONEXISTENT");
        // Toast for save verification
        Toast.makeText(getActivity(), "File has been created: " + retrievedFile, Toast.LENGTH_SHORT).show();
    }

    private class FetchAndDisplayTodaysImage extends AsyncTask<Void, Void, ImageInfo> {

        private String imageDate;
        private String imageTitle;
        private String imageDescription;
        @Override
        protected ImageInfo doInBackground(Void... voids) {
            try {
                Bundle arguments = getArguments();
                String selectedDate = null;

                if (arguments != null) {
                    // Extract the selected date from arguments
                    selectedDate = arguments.getString("selectedDate");
                }

                // If selectedDate is not null, use it; otherwise, use the current date
                String apiDate = (selectedDate != null) ? selectedDate : getCurrentDate();
                String apiKey = "vyjMP61lFHmEgc7bdKgKbx0K0oooccJXOesCfVWe";
                String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + apiDate;

                URL url = new URL(apiUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String imageUrl = jsonObject.getString("url");
                    String imageDate = jsonObject.getString("date");
                    // Replace dashes with spaces in the date
                    imageDate = imageDate.replace("-", " ");
                    String imageTitle = jsonObject.getString("title");
                    String imageDescription = jsonObject.getString("explanation");

                    // Download image
                    URL imageUrlObj = new URL(imageUrl);
                    HttpsURLConnection imageConnection = (HttpsURLConnection) imageUrlObj.openConnection();
                    imageConnection.connect();
                    InputStream imageInputStream = imageConnection.getInputStream();

                    // Create an ImageInfo object
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.bitmap = BitmapFactory.decodeStream(imageInputStream);
                    imageInfo.imageDate = "Image of " + imageDate;
                    imageInfo.imageTitle = imageTitle;
                    imageInfo.imageDescription = imageDescription;

                    return imageInfo;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImageInfo imageInfo) {
            if (imageInfo != null) {
                // Display the image
                imageView.setImageBitmap(imageInfo.bitmap);

                // Update TextViews with parsed information
                iotdDateTextView.setText(imageInfo.imageDate);
                iotdTitleTextView.setText(imageInfo.imageTitle);
                iotdDescriptionTextView.setText(imageInfo.imageDescription);

                // Set ImageInfo in the fragment
                setImageInfo(imageInfo);

                iotdDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fullDescription = imageInfo.imageDescription; // Get the full text
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(fullDescription)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK, close the dialog
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        }
    }
}
