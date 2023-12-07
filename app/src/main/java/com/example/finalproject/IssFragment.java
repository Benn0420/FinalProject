package com.example.finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class IssFragment extends Fragment {

    /*
    This fragment is used to track the International Space Station
    and display relevant information all while marking the location
    on a map view. Users have the choice to start and pause active
    tracking.
     */

    private MapView mapView;
    private View view;
    private TextView locationText;
    private TextView latText;
    private TextView longText;
    private TextView altitudeText;
    private TextView velocityText;
    private TextView visibilityText;
    private ProgressBar progressBar;
    private boolean isTracking = false;

    public IssFragment() {
        // Required empty public constructor
    }

    public static IssFragment newInstance() {
        return new IssFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        view = inflater.inflate(R.layout.fragment_iss, container, false);

        mapView = view.findViewById(R.id.issMap);
        Button trackingISS = view.findViewById(R.id.trackissButton);
        locationText = view.findViewById(R.id.ISSlocation);
        latText = view.findViewById(R.id.ISSlat);
        longText = view.findViewById(R.id.ISSlong);
        altitudeText = view.findViewById(R.id.ISSaltitude);
        velocityText = view.findViewById(R.id.ISSvelocity);
        visibilityText = view.findViewById(R.id.ISSvisibility);
        progressBar = view.findViewById(R.id.progressBar);

        // Initializing the map
        mapView.onCreate(savedInstanceState);

        // Fetch and track ISS location
        new FetchAndTrackISSLocation().execute();

        // Click listeners for each button
        trackingISS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for "Tracking ISS" button
                if (!isTracking) {
                    trackingISS.setBackgroundColor(0xFF00FF00);
                    trackingISS.setText(getString(R.string.ISFtracking));
                    startTracking();
                } else {
                    trackingISS.setBackgroundColor(0xFFFF0000);
                    trackingISS.setText(getString(R.string.ISFresume));
                    stopTracking();
                }
            }
        });

        return view;
    }

    private void startTracking() {
        isTracking = true;
        Toast.makeText(getActivity(), getString(R.string.ISFstart), Toast.LENGTH_SHORT).show();
        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        // Start periodic updates
        handler.postDelayed(updateRunnable, 0);
    }

    private void stopTracking() {
        isTracking = false;
        Toast.makeText(getActivity(), getString(R.string.ISFstop), Toast.LENGTH_SHORT).show();
        // Hide the progress bar
        progressBar.setVisibility(View.INVISIBLE);

        // Stop periodic updates
        handler.removeCallbacks(updateRunnable);
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    // Checks if tracking button has been clicked if so it loops the fetching of data
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTracking) {
                // Fetch and track ISS location
                new FetchAndTrackISSLocation().execute();
                // Schedule the next update after 10 seconds
                handler.postDelayed(this, 10000);
            }
        }
    };

    public class IssInfo {

        /*
        This class is used to store the information parsed from the url links
         */

        public String location;
        public double latitude;
        public double longitude;
        public double altitude;
        public double velocity;
        public String visibility;
    }

    private class FetchAndTrackISSLocation extends AsyncTask<Void, Void, IssInfo> {

        @Override
        protected IssInfo doInBackground(Void... voids) {
            try {
                // Making a network request to get ISS location data
                String ISSapi = "https://api.wheretheiss.at/v1/satellites/25544";
                URL url = new URL(ISSapi);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

                    // Parsing JSON
                    JSONObject jsonObject = new JSONObject(response.toString());
                    IssInfo issInfo = new IssInfo();
                    issInfo.latitude = jsonObject.getDouble("latitude");
                    issInfo.longitude = jsonObject.getDouble("longitude");
                    issInfo.altitude = jsonObject.getDouble("altitude");
                    issInfo.velocity = jsonObject.getDouble("velocity");
                    issInfo.visibility = jsonObject.getString("visibility");

                    // Perform reverse geocoding using Geoapify API
                    String geoapifyApiKey = "0fc956d00aa44f09ab1b9c1a8cb1f70a";
                    String geoapifyApiUrl = "https://api.geoapify.com/v1/geocode/reverse" +
                            "?lat=" + issInfo.latitude +
                            "&lon=" + issInfo.longitude +
                            "&apiKey=" + geoapifyApiKey;

                    URL geoapifyUrl = new URL(geoapifyApiUrl);
                    HttpsURLConnection geoapifyConnection = (HttpsURLConnection) geoapifyUrl.openConnection();
                    geoapifyConnection.setRequestMethod("GET");
                    geoapifyConnection.connect();

                    if (geoapifyConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        InputStream geoapifyIs = geoapifyConnection.getInputStream();
                        BufferedReader geoapifyReader = new BufferedReader(new InputStreamReader(geoapifyIs));
                        StringBuilder geoapifyResponse = new StringBuilder();
                        String geoapifyLine;
                        while ((geoapifyLine = geoapifyReader.readLine()) != null) {
                            geoapifyResponse.append(geoapifyLine);
                        }
                        geoapifyReader.close();

                        // Parse Geoapify response
                        JSONObject geoapifyJsonObject = new JSONObject(geoapifyResponse.toString());
                        JSONArray featuresArray = geoapifyJsonObject.getJSONArray("features");
                        if (featuresArray.length() > 0) {
                            JSONObject propertiesObject = featuresArray.getJSONObject(0).optJSONObject("properties");
                            String locationName = propertiesObject.optString("formatted", "");
                            issInfo.location = locationName;
                        }
                    }

                    return issInfo;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(IssInfo issInfo) {
            if (issInfo != null) {
                // Update the map with the ISS location
                updateMap(issInfo);

                // Set TextViews with ISS information
                setTextViews(issInfo);
            } else {
                Toast.makeText(getActivity(), getString(R.string.ISFfail), Toast.LENGTH_SHORT).show();
            }
        }

        // Method used to set text views according to parsed ISS info
        private void setTextViews(IssInfo issInfo) {
            // Setting textViews with the IDs ISSlat, ISSlong, ISSaltitude, ISSvelocity, and ISSvisibility
            locationText.setText(issInfo.location);
            locationText.setMaxLines(1);
            locationText.setEllipsize(TextUtils.TruncateAt.END);
            latText.setText("Latitude: " + String.valueOf(issInfo.latitude));
            longText.setText("Longitude: " + String.valueOf(issInfo.longitude));
            altitudeText.setText("Altitude: " + String.valueOf(issInfo.altitude) + " kms");
            velocityText.setText("Velocity: " + String.valueOf(issInfo.velocity) + " kms/h");
            visibilityText.setText("Visibility: " + issInfo.visibility);
        }

        // Method used to update the marker on the mapview with a new LatLng position
        private void updateMap(IssInfo issInfo) {
            LatLng issLocation = new LatLng(issInfo.latitude, issInfo.longitude);
            long timestamp = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(new Date(timestamp));

            mapView.getMapAsync(googleMap -> {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(issLocation, 1));
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(issLocation)
                        .title(getString(R.string.ISFlocation) + issInfo.location)
                        .snippet(formattedDate)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // Add the marker to the map
                Marker marker = googleMap.addMarker(markerOptions);

            });
        }
    }
}
