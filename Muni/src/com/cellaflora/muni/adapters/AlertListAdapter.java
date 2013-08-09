package com.cellaflora.muni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cellaflora.muni.Alert;
import com.cellaflora.muni.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sdickson on 8/8/13.
 */
public class AlertListAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Alert> alerts;

    public AlertListAdapter(Context context, ArrayList<Alert> alerts)
    {
        this.context = context;
        this.alerts = alerts;
    }

    public int getCount()
    {
        return alerts.size();
    }

    public Object getItem(int position)
    {
        return alerts.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.alert_list_row, parent, false);
        TextView txtDate = (TextView) itemView.findViewById(R.id.alert_time);
        TextView txtAlert = (TextView) itemView.findViewById(R.id.alert_content);

        Alert tmp = alerts.get(position);

        if(tmp.alert != null)
        {
            txtAlert.setText(tmp.alert);
        }

        if(tmp.time != null)
        {
            SimpleDateFormat month_day = new SimpleDateFormat("MMM dd");
            txtDate.setText(month_day.format(tmp.time));
        }

        return itemView;
    }
}
