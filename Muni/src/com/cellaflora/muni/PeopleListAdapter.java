package com.cellaflora.muni;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PeopleListAdapter extends BaseAdapter
{
	Context context;
    ArrayList<Person> people;
    LayoutInflater inflater;
    
	public PeopleListAdapter(Context context, ArrayList<Person> people)
	{
        this.context = context;
        this.people = people;
    }
 
    public int getCount() 
    {
        return people.size();
    }
 
    public Object getItem(int position) 
    {
        return people.get(position);
    }
 
    public long getItemId(int position) 
    {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	TextView txtName, txtTitle;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.people_list_row, parent, false);
        
        //Load special font
        txtName = (TextView) itemView.findViewById(R.id.people_name);
        txtTitle = (TextView) itemView.findViewById(R.id.people_title);
        //Typeface avenirBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Avenir LT 95 Black.ttf");
        //txtName.setTypeface(avenirBlack);
        //txtTitle.setTypeface(avenirBlack);
	    
	    //Populate menu 
        txtName.setText(people.get(position).name);
        txtTitle.setText(people.get(position).title);
        return itemView;
    }
}
