package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.objects.Place;
import com.cellaflora.muni.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sdickson on 7/4/13.
 */
public class PlaceDetailFragment extends Fragment
{
    View view;
    Place place;
    GoogleMap map;
    Location currentLocation = null;

    public PlaceDetailFragment(Place place, Location currentLocation)
    {
        this.place = place;
        this.currentLocation = currentLocation;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
            {
                parent.removeView(view);
            }
        }

        view = inflater.inflate(R.layout.place_detail_fragment, container, false);
        return view;
    }

    public void onDestroyView()
    {
        super.onDestroyView();

        try
        {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.place_map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
        catch(Exception e){}
    }

    public void onResume()
    {
        super.onResume();

        if(map == null)
        {
            try
            {
                map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.place_map)).getMap();
                map.setMapType(MuniConstants.DETAIL_MAP_TYPE);
                String geopoint[] = place.geo_point.split(", ");
                LatLng coords = new LatLng(Double.parseDouble(geopoint[0]), Double.parseDouble(geopoint[1]));
                Marker poi = map.addMarker(new MarkerOptions().position(coords));
                poi.setTitle(place.name);
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(coords, 16);
                map.animateCamera(cu);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        TextView txtName = (TextView) view.findViewById(R.id.place_detail_name);
        txtName.setTypeface(MainActivity.myriadProRegular);

        TextView txtAddress = (TextView) view.findViewById(R.id.place_detail_address);
        txtAddress.setTypeface(MainActivity.myriadProRegular);

        TextView txtNotes = (TextView) view.findViewById(R.id.place_detail_notes);
        txtNotes.setTypeface(MainActivity.myriadProRegular);

        TextView btnMap = (TextView) view.findViewById(R.id.place_detail_map);
        btnMap.setTypeface(MainActivity.myriadProRegular);

        ImageView actionCall = (ImageView) view.findViewById(R.id.place_detail_call_action);
        ImageView actionMap = (ImageView) view.findViewById(R.id.place_detail_map_action);
        ImageView actionWeb = (ImageView) view.findViewById(R.id.place_detail_web_action);
        RelativeLayout addressLayout = (RelativeLayout) view.findViewById(R.id.place_detail_address_layout);

        if(place.name != null)
        {
            txtName.setText(place.name);
        }
        else
        {
            txtName.setVisibility(View.GONE);
        }

        if(place.tel_number != null)
        {
            actionCall.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setTitle("Confirm");
                            alertDialogBuilder
                                    .setMessage("Call " + place.name + "?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id)
                                        {
                                            String uri = "tel:" + place.tel_number.trim();
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse(uri));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id)
                                        {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
            );
        }
        else
        {

        }

        if(place.street_address != null && place.city != null && place.zip_code != null && place.state != null)
        {
            txtAddress.setText(place.street_address + "\n" + place.city + " " + place.state + " " + place.zip_code);

            btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String geopoint[] = place.geo_point.split(", ");
                LatLng coords = new LatLng(Double.parseDouble(geopoint[0]), Double.parseDouble(geopoint[1]));
                String uri = "geo:" + coords.latitude + "," + coords.longitude + "?q=" + place.name;

                if(currentLocation != null)
                {
                        uri = "http://maps.google.com/maps?saddr="+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"&daddr="+coords.latitude+","+coords.longitude+"&q=" + place.name +"&mode=driving";
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                view.getContext().startActivity(intent);
            }
            });

            actionMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String geopoint[] = place.geo_point.split(", ");
                    LatLng coords = new LatLng(Double.parseDouble(geopoint[0]), Double.parseDouble(geopoint[1]));
                    String url = "geo:" + coords.latitude + "," + coords.longitude + "?q=" + place.name;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
            });


        }
        else
        {
            addressLayout.setVisibility(View.GONE);
        }

        if(place.notes != null)
        {
            txtNotes.setText(place.notes);
        }
        else
        {
            txtNotes.setVisibility(View.GONE);
        }

        if(place.web_url != null)
        {
            actionWeb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(place.web_url));
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
}
