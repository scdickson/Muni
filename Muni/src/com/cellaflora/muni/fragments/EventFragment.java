package com.cellaflora.muni.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.cellaflora.muni.Event;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.EventListAdapter;
import com.cellaflora.muni.fileComparator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventFragment extends Fragment
{
    public static final int EVENT_TYPE_UPCOMING = 0;
    public static final int EVENT_TYPE_PAST = 1;
    public static final String SAVED_EVENTS_PATH = "muni_saved_events";
    public static final int EVENTS_REPLACE_INTERVAL = 60; //In minutes!


    View view;
    ListView eventList;
    EventListAdapter adapter;
    ArrayList<Event> events;
    Button upcomingEventsSelector, pastEventsSelector;
    private ProgressDialog progressDialog;
    EventContentFragment ecf;
    LayoutInflater inflater;
    ViewGroup container;
    private int current_event_type = EVENT_TYPE_UPCOMING;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.inflater = inflater;
        this.container = container;
        if(view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
            {
                parent.removeView(view);
            }
        }

        try
        {
            view = inflater.inflate(R.layout.event_fragment, container, false);
            ecf = new EventContentFragment(new nestedFragmentHandler());
            getFragmentManager().beginTransaction().replace(R.id.event_content, ecf).commit();

            MainActivity.actionbarTitle.setText("Events");
            upcomingEventsSelector = (Button) view.findViewById(R.id.events_upcoming_selector);
            pastEventsSelector = (Button) view.findViewById(R.id.events_past_selector);

            upcomingEventsSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(current_event_type != EVENT_TYPE_UPCOMING)
                    {
                        if(events == null)
                        {
                            loadEvents();
                            adapter = new EventListAdapter(view.getContext(), events, EVENT_TYPE_UPCOMING);
                            eventList = (ListView) getActivity().findViewById(R.id.event_list);
                            eventList.setAdapter(adapter);
                        }
                        else
                        {
                            adapter = new EventListAdapter(view.getContext(), events, EVENT_TYPE_UPCOMING);
                            eventList = (ListView) getActivity().findViewById(R.id.event_list);
                            eventList.setAdapter(adapter);
                        }

                        upcomingEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                        upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_left);
                        pastEventsSelector.setTextColor(Color.parseColor("#50667B"));
                        pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_right);
                        current_event_type = EVENT_TYPE_UPCOMING;
                    }
                }
            });
            pastEventsSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(current_event_type != EVENT_TYPE_PAST)
                    {
                        if(events == null)
                        {
                            loadEvents();
                            adapter = new EventListAdapter(view.getContext(), events, EVENT_TYPE_PAST);
                            eventList = (ListView) getActivity().findViewById(R.id.event_list);
                            eventList.setAdapter(adapter);
                        }
                        else
                        {
                            adapter = new EventListAdapter(view.getContext(), events, EVENT_TYPE_PAST);
                            eventList = (ListView) getActivity().findViewById(R.id.event_list);
                            eventList.setAdapter(adapter);
                        }

                        pastEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                        pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_right);
                        upcomingEventsSelector.setTextColor(Color.parseColor("#50667B"));
                        upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_left);
                        current_event_type = EVENT_TYPE_PAST;
                    }
                }
            });
        }
        catch(InflateException ie){}

        return view;
    }

    /*public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("current_event_type", current_event_type);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
        {
            current_event_type = savedInstanceState.getInt("current_event_type");
        }
    }*/

    public void loadEvents()
    {
        events = new ArrayList<Event>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.addDescendingOrder("C_Start_Time");
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if(e == null)
                {
                    for(ParseObject parse : result)
                    {
                        Event tmp = new Event();
                        tmp.objectId = parse.getObjectId();
                        tmp.title = parse.getString("A_Title");
                        tmp.description = parse.getString("B_Description");
                        tmp.start_time = parse.getDate("C_Start_Time");
                        tmp.end_time = parse.getDate("D_End_Time");
                        tmp.photo_caption = parse.getString("F_Photo_Caption");
                        tmp.location = parse.getString("H1_Location_Title");
                        tmp.address = parse.getString("H_Street_Address") + "\n" + parse.getString("I_City") + " " + parse.getString("J_State") + " " + parse.getString("K_Zip_Code");
                        tmp.event_url = parse.getString("L_Hyperlink");

                        ParseFile file = (ParseFile) parse.get("G_Photo");

                        if(file != null && file.getUrl() != null)
                        {
                            tmp.photo_url = file.getUrl();
                        }
                        events.add(tmp);
                    }

                    try
                    {
                        PersistenceManager.writeObject(getActivity().getApplicationContext(), SAVED_EVENTS_PATH, events);
                    }
                    catch(Exception ex){}
                }
                else
                {
                    e.printStackTrace();
                }

                //final Animation in = new AlphaAnimation(0.0f, 1.0f);
                //in.setDuration(1000);
                progressDialog.dismiss();

                adapter = new EventListAdapter(view.getContext(), events, current_event_type);
                eventList = (ListView) getActivity().findViewById(R.id.event_list);
                eventList.setAdapter(adapter);
                //placeList.setOnItemClickListener(new PlaceItemClickListener());
                //placeList.startAnimation(in);
            }
        });
    }

    public void onResume()
    {
        super.onResume();

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        try
        {
            File f = getActivity().getFileStreamPath(SAVED_EVENTS_PATH);
            if((f.lastModified() + (EVENTS_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
            {
                events = (ArrayList<Event>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_EVENTS_PATH);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            loadEvents();
        }
    }


    private class nestedFragmentHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            adapter = new EventListAdapter(view.getContext(), events, EVENT_TYPE_UPCOMING);
            eventList = (ListView) getActivity().findViewById(R.id.event_list);
            eventList.setAdapter(adapter);
        }
    }
}
