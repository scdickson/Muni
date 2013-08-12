package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.Place;
import com.cellaflora.muni.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by sdickson on 7/4/13.
 */
public class PlaceDetailFragment extends Fragment
{
    View view;
    Place place;
    GoogleMap map;

    public PlaceDetailFragment(Place place)
    {
        this.place = place;
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
                //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
        TextView txtPhone = (TextView) view.findViewById(R.id.place_detail_phone);
        TextView txtAddress = (TextView) view.findViewById(R.id.place_detail_address);
        TextView txtUrl = (TextView) view.findViewById(R.id.place_detail_url);
        Button btnMap = (Button) view.findViewById(R.id.place_detail_map);
        Button btnDirections = (Button) view.findViewById(R.id.place_detail_directions);
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
            txtPhone.setText(place.tel_number);
            txtPhone.setOnClickListener(
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
            txtPhone.setVisibility(View.GONE);
        }

        if(place.street_address != null && place.city != null && place.zip_code != null && place.state != null)
        {
            txtAddress.setText(place.street_address + "\n" + place.city + " " + place.state + " " + place.zip_code);

            btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String geopoint[] = place.geo_point.split(", ");
                LatLng coords = new LatLng(Double.parseDouble(geopoint[0]), Double.parseDouble(geopoint[1]));
                String uri = "geo:" + coords.latitude + "," + coords.longitude + "?z=16";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                view.getContext().startActivity(intent);
            }
            });

            btnDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        else
        {
            addressLayout.setVisibility(View.GONE);
        }

        if(place.web_url != null)
        {
            txtUrl.setText(Html.fromHtml("<u>" + place.web_url + "</u>"));
            final String url = place.web_url;
            txtUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
            });
        }
        else
        {
            txtUrl.setVisibility(View.GONE);
        }

    }
}
