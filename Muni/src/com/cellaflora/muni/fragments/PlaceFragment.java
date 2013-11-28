package com.cellaflora.muni.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import support.NetworkManager;
import support.PersistenceManager;
import com.cellaflora.muni.objects.Place;

import support.PlacesSortOptionDialog;
import support.PullToRefreshListView;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.PlaceListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceFragment extends Fragment
{
    View view;
    PlaceFragment placeFragment;
    ArrayList<Place> places;
    ArrayList<Place> searchResults;
    public static PullToRefreshListView placeList;
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
    NetworkManager networkManager;
    public static TextView noPlaces;
    public int SORT_TYPE = PlacesSortOptionDialog.NAME_REQUEST_CODE;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.place_fragment, container, false);
        placeList = (PullToRefreshListView) view.findViewById(R.id.place_list);
        noPlaces = (TextView) view.findViewById(R.id.places_none);
        noPlaces.setTypeface(MainActivity.myriadProSemiBold);
        placeFragment = this;
        networkManager = new NetworkManager(view.getContext(), getActivity(), getFragmentManager());
        MainActivity.actionbarTitle.setText("Places");
        MainActivity.actionbarPlacesSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PlacesSortOptionDialog sortDialog = new PlacesSortOptionDialog(placeFragment);
                sortDialog.show(getFragmentManager(), null);
            }
        });
        searchBar = (EditText) view.findViewById(R.id.place_search);
        searchBar.setTypeface(MainActivity.myriadProRegular);
        searchCancel = (TextView) view.findViewById(R.id.place_search_cancel);
        searchCancel.setTypeface(MainActivity.myriadProRegular);
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
                searchResults = new ArrayList<Place>();

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
        MainActivity.actionbarPlacesSort.setVisibility(View.GONE);

        if(placeList != null)
        {
            state = placeList.onSaveInstanceState();
        }
    }

    public void loadPlaces()
    {
        places = new ArrayList<Place>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
        query.addAscendingOrder("A_Name");
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

                        if(currentLocation != null)
                        {
                            tmp.distance = getDistance(tmp);
                        }
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

                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }

                switch(SORT_TYPE)
                {
                    case PlacesSortOptionDialog.NAME_REQUEST_CODE:
                        Log.d("fatal", "SORT NAME");
                        Collections.sort(places, new PlaceNameComparable());
                        break;
                    case PlacesSortOptionDialog.DISTANCE_REQUEST_CODE:
                        Log.d("fatal", "SORT DISTANCE");
                        Collections.sort(places, new PlaceDistanceComparable());
                        break;
                }

                adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                placeList = (PullToRefreshListView) view.findViewById(R.id.place_list);
                placeList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadPlaces();
                    }
                });
                placeList.onRefreshComplete();
                placeList.setAdapter(adapter);
                placeList.setOnItemClickListener(new PlaceItemClickListener());
                //placeList.startAnimation(in);
            }
        });
    }

    public double getDistance(Place tmp)
    {
        String locationData[] = tmp.geo_point.split(", ");
        float result[] = new float[5];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(locationData[0]), Double.parseDouble(locationData[1]), result);
        return (result[0] / MuniConstants.METERS_PER_MILE);
    }

    public void onResume()
    {
        super.onResume();
        MainActivity.mMenuAdapter.setSelected(3);
        MainActivity.actionbarPlacesSort.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        if(state != null)
        {
            adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
            placeList = (PullToRefreshListView) view.findViewById(R.id.place_list);
            placeList.setAdapter(adapter);
            placeList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadPlaces();
                }
            });
            placeList.setOnItemClickListener(new PlaceItemClickListener());
            placeList.onRestoreInstanceState(state);
        }
        else
        {
            if(networkManager.isNetworkConnected())
            {
                try
                {
                    File f = getActivity().getFileStreamPath(MuniConstants.SAVED_PLACES_PATH);
                    if((f.lastModified() + (MuniConstants.PLACES_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                    {
                        places = (ArrayList<Place>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PLACES_PATH);
                        adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                        placeList = (PullToRefreshListView) view.findViewById(R.id.place_list);
                        placeList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadPlaces();
                            }
                        });
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
            else
            {
                try
                {
                        places = (ArrayList<Place>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PLACES_PATH);
                        adapter = new PlaceListAdapter(view.getContext(), places, currentLocation);
                        placeList = (PullToRefreshListView) view.findViewById(R.id.place_list);
                        placeList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadPlaces();
                            }
                        });
                        placeList.setAdapter(adapter);
                        placeList.setOnItemClickListener(new PlaceItemClickListener());
                }
                catch(Exception e)
                {
                    networkManager.showNoCacheErrorDialog();
                }
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
                currentLocation = location;
                adapter.setCurrentLocation(location);
                adapter.notifyDataSetChanged();
                placeList.invalidateViews();
            }
        }

        public void onProviderEnabled(String provider){}
        public void onProviderDisabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }

    public void sortDialogCallback(boolean callback, int code)
    {
        if(callback)
        {
            SORT_TYPE = code;

            if(code == PlacesSortOptionDialog.NAME_REQUEST_CODE)
            {
                Collections.sort(places, new PlaceNameComparable());
            }
            else if(code == PlacesSortOptionDialog.DISTANCE_REQUEST_CODE)
            {
                Collections.sort(places, new PlaceDistanceComparable());
            }

            adapter.setContent(places);
            adapter.notifyDataSetChanged();
            placeList.invalidateViews();
        }
    }

    public void selectItem(int position)
    {
        if(((Place) adapter.getItem(position)) != null)
        {
            PlaceDetailFragment fragment = new PlaceDetailFragment(((Place) adapter.getItem(position)), currentLocation);
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

    private class PlaceNameComparable implements Comparator<Place>
    {
        public int compare(Place p1, Place p2)
        {
            return p1.name.compareTo(p2.name);
        }
    }

    private class PlaceDistanceComparable implements Comparator<Place>
    {
        public int compare(Place p1, Place p2)
        {
            if(p1.distance != 0 && p2.distance != 0)
            {
                if(p1.distance < p2.distance)
                {
                    return -1;
                }
                else if(p1.distance > p2.distance)
                {
                    return 1;
                }
            }

            return 0;
        }
    }
}