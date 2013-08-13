package com.cellaflora.muni.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
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
        ImageView imgNav;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        
        //Load special font
        txtNav = (TextView) itemView.findViewById(R.id.nav_item);
	    txtNav.setTypeface(MainActivity.myriadProSemiBold);
        imgNav = (ImageView) itemView.findViewById(R.id.nav_image);
        int id;
        String stringRsc = null;

        switch(position)
        {
            case 0://Home
                stringRsc = "com.cellaflora.muni:drawable/home";
                break;
            case 1://People
                stringRsc = "com.cellaflora.muni:drawable/users";
                break;
            case 2://Notifications
                stringRsc = "com.cellaflora.muni:drawable/alerts";
                break;
            case 3://Places
                stringRsc = "com.cellaflora.muni:drawable/map";
                break;
            case 4://News
                stringRsc = "com.cellaflora.muni:drawable/news";
                break;
            case 5://Events
                stringRsc = "com.cellaflora.muni:drawable/calendar";
                break;
            case 6://Twitter
                stringRsc = "com.cellaflora.muni:drawable/twitter";
                break;
            case 7://Polling
                stringRsc = "com.cellaflora.muni:drawable/polling";
                break;
            case 8://Contact
                stringRsc = "com.cellaflora.muni:drawable/mail";
                break;
            case 9://Documents
                stringRsc = "com.cellaflora.muni:drawable/document";
                break;
        }
	    
	    //Populate menu
        if(stringRsc != null)
        {
            id = context.getResources().getIdentifier(stringRsc, null, null);
            imgNav.setImageResource(id);
        }

        txtNav.setText(menuDrawerItems[position]);
        return itemView;
    }
}
