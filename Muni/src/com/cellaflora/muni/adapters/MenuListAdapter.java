package com.cellaflora.muni.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;

import java.util.ArrayList;

public class MenuListAdapter extends BaseAdapter
{
    public static int COLOR_SELECTED, COLOR_UNSELECTED;

	Context context;
    String menuDrawerItems[];
    LayoutInflater inflater;
    int selectedIndex = 0;
    
	public MenuListAdapter(Context context, String[] menuDrawerItems)
	{
        this.context = context;
        this.menuDrawerItems = menuDrawerItems;
        COLOR_SELECTED = Color.parseColor("#E94D3E");
        COLOR_UNSELECTED = Color.parseColor("#ffffff");
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

    public void setSelected(int index)
    {
        selectedIndex = index;
        notifyDataSetChanged();
    }
 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	TextView txtNav;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        txtNav = (TextView) itemView.findViewById(R.id.nav_item);
        ImageView imgNav = (ImageView) itemView.findViewById(R.id.nav_image);

            //Load special font
            txtNav.setTypeface(MainActivity.myriadProSemiBold);
            int id;
            String stringRsc = null;

            switch(position)
            {
                case 0://Home
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/home_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/home";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 1://People
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/users_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/users";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 2://Notifications
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/alerts_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/alerts";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 3://Places
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/map_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/map";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 4://News
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/news_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/news";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 5://Events
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/calendar_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/calendar";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 6://Twitter
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/twitter_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/twitter";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 7://Polling
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/polling_enabled";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/polling";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 8://Contact
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/mail_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/mail";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
                    break;
                case 9://Documents
                    if(position == selectedIndex)
                    {
                        stringRsc = "com.cellaflora.muni:drawable/document_selected";
                        txtNav.setTextColor(COLOR_SELECTED);
                    }
                    else
                    {
                        stringRsc = "com.cellaflora.muni:drawable/document";
                        txtNav.setTextColor(COLOR_UNSELECTED);
                    }
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
