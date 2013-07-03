package com.cellaflora.muni;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleListAdapter extends BaseAdapter
{
	Context context;
    ArrayList<PersonGroup> groups;
    public ArrayList<Object> content;
    LayoutInflater inflater;
    public int level = 0;
    public String groupA, groupB;

	public PeopleListAdapter(Context context, ArrayList<PersonGroup> groups, int level, String groupA, String groupB)
	{
        this.context = context;
        this.groups = groups;
        this.level = level;
        this.groupA = groupA;
        this.groupB = groupB;
        content = new ArrayList<Object>();

        loadData();
    }

    public void loadData()
    {
        if(level == 0)
        {
            for(PersonGroup group : groups)
            {
                if(group.groupName.equals(groupA))
                {
                    for(PersonGroup subGroup : group.subGroup)
                    {
                        for(Person person : subGroup.people)
                        {
                            content.add(person);
                        }
                    }
                }
            }

            for(PersonGroup group : groups)
            {
                if(!group.groupName.equals(groupA))
                {
                    content.add(group.groupName);
                }
            }
        }
        else if(level == 1)
        {
            for(PersonGroup group : groups)
            {
                if(group.groupName.equals(groupA))
                {
                    for(Person person : group.people)
                    {
                        content.add(person);
                    }

                    for(PersonGroup subGroup : group.subGroup)
                    {
                        content.add(subGroup.groupName);
                    }
                }
            }
        }
        else if(level == 2)
        {
            for(PersonGroup group : groups)
            {
                if(group.groupName.equals(groupA))
                {
                    for(PersonGroup subGroup : group.subGroup)
                    {
                        if(subGroup.groupName.equals(groupB))
                        {
                            for(Person person : subGroup.people)
                            {
                                content.add(person);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setLevel(int level, String groupA, String groupB)
    {
        this.level = level;
        this.groupA = groupA;
        this.groupB = groupB;
        loadData();
    }

    public void clearContent()
    {
        content = null;
        content = new ArrayList<Object>();
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
    	TextView txtName, txtTitle;
        ImageView imgArrow;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.people_list_row, parent, false);
        
        //Load special font
        txtName = (TextView) itemView.findViewById(R.id.people_name);
        txtTitle = (TextView) itemView.findViewById(R.id.people_title);
        imgArrow = (ImageView) itemView.findViewById(R.id.imgArrow);
        //Typeface avenirBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Avenir LT 95 Black.ttf");
        //txtName.setTypeface(avenirBlack);
        //txtTitle.setTypeface(avenirBlack);
	    
	    //Populate menu

        //Person tmp = groups.get(0).people.get(0);
        //Log.d("Adapter", tmp.toString());

        if(content.get(position).getClass().equals(Person.class))
        {
            Person tmp = (Person) content.get(position);
            txtName.setText(tmp.name);
            txtTitle.setText(tmp.title);
        }
        else
        {
            txtName.setText(content.get(position).toString());
            int id = context.getResources().getIdentifier("com.cellaflora.muni:drawable/transparent_arrow_right", null, null);
            imgArrow.setImageResource(id);
        }

        return itemView;
    }
}
