package com.cellaflora.muni.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.objects.Place;
import com.cellaflora.muni.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sdickson on 7/3/13.
 */
public class PlaceListAdapter extends BaseAdapter
{
    LayoutInflater inflater;
    Context context;
    ArrayList<Place> places;
    ArrayList<Place> content;
    Location currentLocation;

    public PlaceListAdapter(Context context, ArrayList<Place> places, Location currentLocation)
    {
        content = new ArrayList<Place>();
        this.context = context;
        this.places = places;
        this.currentLocation = currentLocation;
        reloadPlaces();
    }

    public void reloadPlaces()
    {
        clearContent();

        for(Place place : places)
        {
            content.add(place);
        }
    }

    public void clearContent()
    {
        content = null;
        content = new ArrayList<Place>();
    }

    public void setContent(ArrayList<Place> places)
    {
        this.content = places;
    }

    public void setCurrentLocation(Location currentLocation)
    {
        this.currentLocation = currentLocation;
    }

    public int getCount()
    {
        return content.size();
    }

    public Object getItem(int position)
    {
        return content.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView txtName, txtDistance, txtGroup;
        ImageView imgPlace;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.place_list_row, parent, false);

        txtName = (TextView) itemView.findViewById(R.id.place_name);
        txtName.setTypeface(MainActivity.myriadProSemiBold);

        txtDistance = (TextView) itemView.findViewById(R.id.place_distance);
        txtDistance.setTypeface(MainActivity.myriadProRegular);

        txtGroup = (TextView) itemView.findViewById(R.id.place_group);
        txtGroup.setTypeface(MainActivity.myriadProRegular);

        imgPlace = (ImageView) itemView.findViewById(R.id.imgPlace);
        imgPlace.setVisibility(View.GONE);
        TextView header = (TextView) itemView.findViewById(R.id.place_list_section_header);
        header.setTypeface(MainActivity.myriadProRegular);

            Place tmp = (Place) content.get(position);

            txtName.setText(tmp.name);

            if(currentLocation != null && tmp.geo_point != null)
            {
                String locationData[] = tmp.geo_point.split(", ");
                float result[] = new float[5];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(locationData[0]), Double.parseDouble(locationData[1]), result);
                Double distance = (result[0] / MuniConstants.METERS_PER_MILE);
                tmp.distance = distance;
                DecimalFormat df = new DecimalFormat("0.#");

                //txtDistance.setText(df.format(distance) + " miles");
                txtDistance.setText(String.format("%.1f", distance) + " miles");
                txtGroup.setVisibility(View.GONE);
            }
            else
            {
                txtDistance.setVisibility(View.GONE);
                txtGroup.setVisibility(View.VISIBLE);

                if(tmp.category != null)
                {
                    txtGroup.setText(tmp.category);
                }
            }

        return itemView;
    }
}
