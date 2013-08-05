package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cellaflora.muni.Event;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.EventListAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sdickson on 7/24/13.
 */
public class EventContentFragment extends Fragment
{
    View view;
    Handler handler;

    public EventContentFragment(Handler handler)
    {
        this.handler = handler;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.event_content_fragment, container, false);
        handler.sendEmptyMessage(0);
        return view;
    }
}
