package com.cellaflora.muni.adapters;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.Place;
import com.cellaflora.muni.R;
import com.cellaflora.muni.fragments.PlaceFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by sdickson on 7/3/13.
 */
public class PlaceListAdapter extends BaseAdapter
{
    Comparator<Place> PlaceComparator = new Comparator<Place>()
    {

        public int compare(Place p1, Place p2) {

            String p1Name = p1.category;
            String p2Name = p2.category;

            return p1Name.compareToIgnoreCase(p2Name);
        }
    };

    LayoutInflater inflater;
    Context context;
    ArrayList<Object> content = new ArrayList<Object>();
    ArrayList<Place> places;
    Location currentLocation;

    public PlaceListAdapter(Context context, ArrayList<Place> places, Location currentLocation)
    {
        this.context = context;
        this.places = places;
        this.currentLocation = currentLocation;

        Collections.sort(places, PlaceComparator);

        Place previous = null;
        boolean addSeparator = false;

        for(int i = 0; i < places.size(); i++)
        {
            if(previous != null)
            {
                if(!places.get(i).category.equalsIgnoreCase(previous.category))
                {
                    addSeparator = true;
                }
            }
            else
            {
                addSeparator = true;
            }

            if(addSeparator)
            {
                content.add(places.get(i).category);
                addSeparator = false;
            }

            content.add(places.get(i));
            previous = places.get(i);


        }
    }

    public void reloadPlaces()
    {


        Collections.sort(places, PlaceComparator);

        Place previous = null;
        boolean addSeparator = false;

        for(int i = 0; i < places.size(); i++)
        {
            if(previous != null)
            {
                if(!places.get(i).category.equalsIgnoreCase(previous.category))
                {
                    addSeparator = true;
                }
            }
            else
            {
                addSeparator = true;
            }

            if(addSeparator)
            {
                content.add(places.get(i).category);
                addSeparator = false;
            }

            content.add(places.get(i));
            previous = places.get(i);
        }
    }

    public void clearContent()
    {
        content = null;
        content = new ArrayList<Object>();
    }

    public void setContent(ArrayList<Object> content)
    {
        this.content = content;
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
        txtDistance = (TextView) itemView.findViewById(R.id.place_distance);
        txtGroup = (TextView) itemView.findViewById(R.id.place_group);
        imgPlace = (ImageView) itemView.findViewById(R.id.imgPlace);
        TextView header = (TextView) itemView.findViewById(R.id.place_list_section_header);

        if(content.get(position).getClass().equals(Place.class))
        {
            Place tmp = (Place) content.get(position);

            txtName.setText(tmp.name);

            if(currentLocation != null && tmp.geo_point != null)
            {
                String locationData[] = tmp.geo_point.split(", ");
                float result[] = new float[5];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(locationData[0]), Double.parseDouble(locationData[1]), result);
                Double distance = (result[0] / MuniConstants.METERS_PER_MILE);
                DecimalFormat df = new DecimalFormat("0.#");

                txtDistance.setText(df.format(distance) + " miles");
            }
            else
            {
                txtDistance.setVisibility(View.GONE);
            }

            if(tmp.category != null)
            {
                txtGroup.setText(tmp.category);
                int id = context.getResources().getIdentifier("com.cellaflora.muni:drawable/" +tmp.category.toLowerCase(), null, null);
                imgPlace.setImageResource(id);
            }
        }
        else if(content.get(position).getClass().equals(String.class))
        {
            header.setText(content.get(position).toString());
            header.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.GONE);
            txtDistance.setVisibility(View.GONE);
            txtGroup.setVisibility(View.GONE);
            imgPlace.setVisibility(View.GONE);
        }


        return itemView;
    }
}
