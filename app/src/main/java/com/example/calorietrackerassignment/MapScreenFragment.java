package com.example.calorietrackerassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MapScreenFragment extends Fragment{
    private View vMapScreen;
    private MapView parkMap;
    private GoogleMap map;
    private String nearbyPlaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vMapScreen = inflater.inflate(R.layout.fragment_map_screen, container, false);

        //initialziing the maps
        parkMap = vMapScreen.findViewById(R.id.mapParks);
        parkMap.onCreate(savedInstanceState);
        parkMap.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

       // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        parkMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map =  googleMap;
                SharedPreferences forUserAddress = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);
                String locationName  = forUserAddress.getString("address",null);

                Geocoder gc = new Geocoder(getActivity().getApplicationContext());
                List<Address> addresses = null;

                try {
                    addresses = gc.getFromLocationName(locationName, 1);

                    SearchNearbyPlaces searchNearbyPlaces = new SearchNearbyPlaces();

                    searchNearbyPlaces.execute(Double.toString(addresses.get(0).getLatitude()),
                            Double.toString(addresses.get(0).getLongitude()),"5000","park");
                            ;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LatLng home = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                googleMap.addMarker(new MarkerOptions().position(home).title("Home").snippet("Your Home Location: "+locationName));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(home).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });
        return vMapScreen;
    }

    private class SearchNearbyPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return SearchGoogleAPI.searchNearByPlaces(params[0],params[1],params[2],params[3]);
        }

        @Override
        protected void onPostExecute(String searchResult) {
            nearbyPlaces = searchResult;
            Log.d("NEARBYPLACE",searchResult);
            JSONArray jsonArray = null;
            LatLng places;
            try {
                JSONObject placesObject = new JSONObject(searchResult);
                jsonArray = placesObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject allCategoriesJSON = jsonArray.getJSONObject(i);
                //String geometry = allPlaces + allCategoriesJSON.getString("geometry") + ",";
                String latitude = allCategoriesJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
                String longitutde = allCategoriesJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
                String placeName = allCategoriesJSON.getString("name");
                String rating = allCategoriesJSON.getString("rating");
                places=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitutde));
                //LatLng sydney = new LatLng(-34, 151);
                map.addMarker(new MarkerOptions().position(places).title(placeName).snippet("Nearby park with rating: "+rating).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        parkMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        parkMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parkMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        parkMap.onLowMemory();
    }
}



