package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cellaflora.muni.Alert;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.NetworkManager;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.AlertListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AlertFragment extends Fragment
{
    View view;
    AlertListAdapter adapter;
    ListView alertList;
    ArrayList<Alert> alerts;
    ProgressDialog progressDialog;
    NetworkManager networkManager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.alert_fragment, container, false);
        MainActivity.actionbarTitle.setText("Notifications");
        networkManager = new NetworkManager(view.getContext(), getActivity(), getFragmentManager());
		return view;
	}

    private Date fixDate(Date date)
    {
        TimeZone tz = TimeZone.getDefault();
        Date fixed = new Date(date.getTime() - tz.getRawOffset());

        if(tz.inDaylightTime(fixed))
        {
            Date dst = new Date(fixed.getTime() - tz.getDSTSavings());

            if(tz.inDaylightTime(dst))
            {
                fixed = dst;
            }
        }

        return fixed;
    }

    public void loadNotifications()
    {
            alerts = new ArrayList<Alert>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Alerts");
            query.addDescendingOrder("updatedAt");
            query.setLimit(MuniConstants.MAX_RECENT_ALERTS);
            progressDialog.show();
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> result, ParseException e)
                {
                    if (e == null)
                    {
                        for(ParseObject parse : result)
                        {
                            Alert tmp = new Alert();
                            tmp.objectId = parse.getObjectId();
                            tmp.alert = parse.getString("Alert_Message");
                            tmp.time = fixDate(parse.getUpdatedAt());
                            alerts.add(tmp);
                        }

                        progressDialog.dismiss();
                        adapter = new AlertListAdapter(view.getContext(), alerts);
                        alertList = (ListView) getActivity().findViewById(R.id.alert_list);
                        alertList.setAdapter(adapter);

                    }
                }
            });
    }

    public void onResume()
    {
        super.onResume();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if(networkManager.isNetworkConnected())
        {
            loadNotifications();
        }
        else
        {
            networkManager.showNoCacheErrorDialog();
        }

    }
}