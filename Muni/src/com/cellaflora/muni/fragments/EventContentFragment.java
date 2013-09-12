package com.cellaflora.muni.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellaflora.muni.R;

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
