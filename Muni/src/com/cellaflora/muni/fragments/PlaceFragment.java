package com.cellaflora.muni.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
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
    ArrayList<Object> searchResults;
    ListView placeList;
    PlaceListAdapter adapter;
    private ProgressDialog progressDialog;
    Parcelable state;
    public EditText searchBar;
    public TextView searchCancel;
    InputMethodManager imm;
    LocationManager service;
    LocationListener locationListener;
    String provider;
    Location currentLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.place_fragment, container, false);
        MainActivity.actionbarTitle.setText("Places");
        searchBar = (EditText) view.findViewById(R.id.place_search);
        searchCancel = (TextView) view.findViewById(R.id.place_search_cancel);
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                adapter.clearContent();
                adapter.reloadPlaces();
                adapter.notifyDataSetChanged();
                placeList.invalidateViews();
                imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchBar.setText("");
                searchCancel.setVisibility(View.GONE);
                placeList.requestFocus();
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i2, int i3)
            {
                searchResults = new ArrayList<Object>();

                if(cs != null && !cs.toString().isEmpty())
                {
                    searchCancel.setVisibility(View.VISIBLE);

                    for(Place place : places)
                    {
                        if(place.name.toUpperCase().contains(cs.toString().toUpperCase()))
                        {
                            searchResults.add(place);
                        }
                    }

                    adapter.clearContent();
                    adapter.setContent(searchResults);
                    adapter.notifyDataSetChanged();
                    placeList.invalidateViews();

                }
                else
                {
                    adapter.clearContent();
                    adapter.reloadPlaces();
                    adapter.notifyDataSetChanged();
                    placeList.invalidateViews();
                    searchCancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });
		return view;
	}

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new CoarseLocationListener();
        service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocation != null)
        {
            currentLocation = lastKnownLocation;
        }
    }

    public void onPause()
    {
        super.onPause();
        state = placeList.onSaveInstanceState();
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
                        tmp.objectId = parse.getObjectId();
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
                    PersistenceManager.writeObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PLACES_PATH, places);
                }
                catch(Exception ex){}

                progressDialog.dismiss();
                adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                placeList = (ListView) getActivity().findViewById(R.id.place_list);
                placeList.setAdapter(adapter);
                placeList.setOnItemClickListener(new PlaceItemClickListener());
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

        if(state != null)
        {
            adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
            placeList = (ListView) getActivity().findViewById(R.id.place_list);
            placeList.setAdapter(adapter);
            placeList.setOnItemClickListener(new PlaceItemClickListener());
            placeList.onRestoreInstanceState(state);
        }
        else
        {
            try
            {
                File f = getActivity().getFileStreamPath(MuniConstants.SAVED_PLACES_PATH);
                if((f.lastModified() + (MuniConstants.PLACES_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                {
                    places = (ArrayList<Place>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PLACES_PATH);
                    adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                    placeList = (ListView) getActivity().findViewById(R.id.place_list);
                    placeList.setAdapter(adapter);
                    placeList.setOnItemClickListener(new PlaceItemClickListener());
                }
                else
                {
                    loadPlaces();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                loadPlaces();
            }
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
                placeList.invalidateViews();
            }
        }

        public void onProviderEnabled(String provider){}
        public void onProviderDisabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }

    public void selectItem(int position)
    {
        if(((Place) adapter.getItem(position)) != null)
        {
            PlaceDetailFragment fragment = new PlaceDetailFragment(((Place) adapter.getItem(position)));
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private class PlaceItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }
}