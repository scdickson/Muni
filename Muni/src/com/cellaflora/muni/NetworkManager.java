package com.cellaflora.muni;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cellaflora.muni.fragments.HomeFragment;

/**
 * Created by sdickson on 8/16/13.
 */
public class NetworkManager
{
    Context context;
    Activity activity;
    FragmentManager fragmentManager;

    private long lastThrown = 0;

    public NetworkManager(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public NetworkManager(Context context, Activity activity, FragmentManager fragmentManager)
    {
        this.context = context;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    public boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo[] activeNetwork = cm.getAllNetworkInfo();
            for(int i = 0; i < activeNetwork.length; i++)
            {
                if(activeNetwork[i].getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void showNetworkErrorDialog()
    {
        if(activity != null)
        {
            if(lastThrown + (MuniConstants.CONNECTION_LOST_REMINDER * 60 * 1000) <= System.currentTimeMillis())
            {
                lastThrown = System.currentTimeMillis();
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("The Internet connection appears to be offline. Some content may not be available until a connection is made.");
                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }
    }

    public void showNoCacheErrorDialog()
    {
        if(activity != null)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Network Error");
            alertDialog.setMessage("This page is not available. Please check your Internet connection.");
            alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    try
                    {
                        HomeFragment fragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    catch(Exception e){}
                }
            });
            alertDialog.show();
        }
    }
}
