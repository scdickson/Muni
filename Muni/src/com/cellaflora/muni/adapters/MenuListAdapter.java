package com.cellaflora.muni.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cellaflora.muni.R;

public class MenuListAdapter extends BaseAdapter
{
	Context context;
    String[] menuDrawerItems;
    LayoutInflater inflater;
    
	public MenuListAdapter(Context context, String[] menuDrawerItems)
	{
        this.context = context;
        this.menuDrawerItems = menuDrawerItems;
    }
 
    public int getCount() 
    {
        return menuDrawerItems.length;
    }
 
    public Object getItem(int position) 
    {
        return menuDrawerItems[position];
    }
 
    public long getItemId(int position) 
    {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	TextView txtNav;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        
        //Load special font
        txtNav = (TextView) itemView.findViewById(R.id.nav_item);
        Typeface avenirBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Avenir LT 95 Black.ttf");
	    txtNav.setTypeface(avenirBlack);
	    
	    //Populate menu 
        txtNav.setText(menuDrawerItems[position]);
        return itemView;
    }
}
