package com.cellaflora.muni.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.PersonGroup;
import com.cellaflora.muni.Place;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.PeopleListAdapter;
import com.cellaflora.muni.adapters.PlaceListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlaceFragment extends Fragment
{
    View view;
    ArrayList<Place> places = new ArrayList<Place>();
    ListView placeList;
    PlaceListAdapter adapter;
    private ProgressDialog progressDialog;

    public static final String SAVED_PLACES_PATH = "muni_saved_places";
    public static final int PLACES_REPLACE_INTERVAL = 60; //In minutes!

    LocationManager service;
    LocationListener locationListener;
    String provider;
    Location currentLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.place_fragment, container, false);
        MainActivity.actionbarTitle.setText("Places");
		return view;
	}

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new CoarseLocationListener();
        service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocation != null)
        {
            currentLocation = lastKnownLocation;
        }
    }

    public void loadPlaces()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if(e == null)
                {
                    for(ParseObject parse : result)
                    {
                        Place tmp = new Place(parse.getString("A_Name"));
                        tmp.category = parse.getString("B_Category").trim();

                        if(tmp.category == null || tmp.category.equals("") || tmp.category.equals(" "))
                        {
                            tmp.category = "Uncategorized";
                        }

                        tmp.street_address = parse.getString("C_Street_Address");
                        tmp.city = parse.getString("D_City");
                        tmp.state = parse.getString("E_State");
                        tmp.zip_code = parse.getString("F_Zip_Code");
                        tmp.tel_number = parse.getString("G_Phone_Number");
                        tmp.web_url = parse.getString("H_Website");
                        tmp.geo_point = parse.getParseGeoPoint("I_GeoPoint").getLatitude() + ", " + parse.getParseGeoPoint("I_GeoPoint").getLongitude();
                        tmp.notes = parse.getString("J_Notes");
                        places.add(tmp);
                    }
                }
                else
                {
                    e.printStackTrace();
                }

                //final Animation in = new AlphaAnimation(0.0f, 1.0f);
                //in.setDuration(1000);
                try
                {
                    PersistenceManager.writeObject(getActivity().getApplicationContext(), SAVED_PLACES_PATH, places);
                }
                catch(Exception ex){}

                progressDialog.dismiss();
                adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                placeList = (ListView) getActivity().findViewById(R.id.place_list);
                placeList.setAdapter(adapter);
                //placeList.setOnItemClickListener(new PlaceItemClickListener());
                //placeList.startAnimation(in);
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        try
        {
            File f = getActivity().getFileStreamPath(SAVED_PLACES_PATH);
            if((f.lastModified() + (PLACES_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
            {
                places = (ArrayList<Place>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_PLACES_PATH);
                adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                placeList = (ListView) getActivity().findViewById(R.id.place_list);
                placeList.setAdapter(adapter);
            }
            else
            {
                loadPlaces();
            }
        }
        catch(Exception e)
        {
            loadPlaces();
        }


    }

    private class CoarseLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (location != null && adapter != null)
            {
                adapter.setCurrentLocation(location);
                adapter.notifyDataSetChanged();
            }
        }

        public void onProviderEnabled(String provider){}
        public void onProviderDisabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }
}