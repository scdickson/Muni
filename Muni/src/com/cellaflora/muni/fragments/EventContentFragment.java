package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.event_content_fragment, container, false);
        return view;
    }

    public void onResume()
    {
        super.onResume();

    }
}
