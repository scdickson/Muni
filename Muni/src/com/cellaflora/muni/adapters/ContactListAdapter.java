package com.cellaflora.muni.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellaflora.muni.R;

import java.util.ArrayList;

/**
 * Created by sdickson on 8/10/13.
 */
public class ContactListAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    Handler contactSelectionHandler;
    public String[][] contacts;
    ArrayList<String[]> contents = new ArrayList<String[]>();

    public static final int ACTION_ADD = 0;
    public static final int ACTION_REMOVE = 1;

    public ContactListAdapter(Context context, String[][] contacts, Handler contactSelectionHandler)
    {
        this.context = context;
        this.contacts = contacts;
        this.contactSelectionHandler = contactSelectionHandler;

        for(String[] name : contacts)
        {
            contents.add(name);
        }
    }

    public void resetContents()
    {
        contents = null;
        contents = new ArrayList<String[]>();

        for(String[] name : contacts)
        {
            contents.add(name);
        }
    }

    public void removeItem(String name)
    {
        int index = 0;

        for(String[] data : contents)
        {
            if(data[0].equals(name))
            {
                break;
            }
            index++;
        }

        contents.remove(index);
    }

    public int getCount()
    {
        return contents.size();
    }

    public String[] getItem(int position)
    {
        return contents.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.contact_list_item, parent, false);
        RelativeLayout layout = (RelativeLayout) itemView.findViewById(R.id.contact_list_item_layout);
        TextView txtName = (TextView) itemView.findViewById(R.id.contact_list_item_name);
        ImageView actionCancel = (ImageView) itemView.findViewById(R.id.contact_list_item_cancel);

        if(contents.get(position)[0] != null)
        {
            txtName.setText(contents.get(position)[0]);
        }

        //actionCancel.setOnClickListener(new ContactClickListener(contacts[position][0], ACTION_REMOVE));

        layout.setOnClickListener(new ContactClickListener(contents.get(position)[0], ACTION_ADD));

        return itemView;
    }

    public class ContactClickListener implements View.OnClickListener
    {
        String name;
        int option;

        public ContactClickListener(String name, int option)
        {
            this.name = name;
            this.option = option;
        }

        public void onClick(View view)
        {
            Message selected = contactSelectionHandler.obtainMessage();
            selected.obj = name;
            selected.arg1 = option;
            contactSelectionHandler.sendMessage(selected);
        }
    }
}
