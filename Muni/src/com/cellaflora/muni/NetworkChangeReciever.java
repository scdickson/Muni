package com.cellaflora.muni;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by sdickson on 8/16/13.
 */
public class NetworkChangeReciever extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork)
        {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                return;
            }
        }
        MainActivity.networkManager.showNetworkErrorDialog();
        return;
    }

}
