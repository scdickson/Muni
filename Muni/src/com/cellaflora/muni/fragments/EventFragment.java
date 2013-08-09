package com.cellaflora.muni.fragments;

import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cellaflora.muni.Event;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.Place;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.EventListAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventFragment extends Fragment
{
    public static final int EVENT_TYPE_UPCOMING = 0;
    public static final int EVENT_TYPE_PAST = 1;
    public static final String SAVED_EVENTS_PATH = "muni_saved_events";
    public static final int EVENTS_REPLACE_INTERVAL = 1; //In minutes!


    View view;
    ListView eventList;
    EventListAdapter adapter;
    ArrayList<Event> events;
    Button upcomingEventsSelector, pastEventsSelector;
    private ProgressDialog progressDialog;
    EventContentFragment ecf;
    LayoutInflater inflater;
    ViewGroup container;
    Parcelable state;
    private int current_event_type = EVENT_TYPE_UPCOMING;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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
                public void onClick(View view) {
                    if(current_event_type != EVENT_TYPE_UPCOMING)
                    {
                        current_event_type = EVENT_TYPE_UPCOMING;
                        if(events == null)
                        {
                            loadEvents();
                        }

                        adapter.switchView(current_event_type);
                        adapter.notifyDataSetChanged();
                        eventList.invalidateViews();
                        upcomingEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                        upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_left);
                        pastEventsSelector.setTextColor(Color.parseColor("#50667B"));
                        pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_right);
                    }
                }
            });
            pastEventsSelector.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(current_event_type != EVENT_TYPE_PAST)
                    {
                        current_event_type = EVENT_TYPE_PAST;
                        if(events == null)
                        {
                            loadEvents();
                        }

                        adapter.switchView(current_event_type);
                        adapter.notifyDataSetChanged();
                        eventList.invalidateViews();
                        pastEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                        pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_right);
                        upcomingEventsSelector.setTextColor(Color.parseColor("#50667B"));
                        upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_left);
                    }
                }
            });
        }
        catch(InflateException ie){}

        return view;
    }

    public void onPause()
    {
        super.onPause();
        state = eventList.onSaveInstanceState();
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


    public void loadEvents()
    {
        events = new ArrayList<Event>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.addDescendingOrder("C_Start_Time");
        query.include("O_Location");
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
                        tmp.description = parse.getString("B_Description");//.replace("\n", "");

                        if(parse.getDate("C_Start_Time") != null)
                        {
                            tmp.start_time = fixDate(parse.getDate("C_Start_Time"));
                        }

                        if(parse.getDate("D_End_Time") != null)
                        {
                            tmp.end_time = fixDate(parse.getDate("D_End_Time"));
                        }

                        tmp.photo_caption = parse.getString("F_Photo_Caption");
                        tmp.location = parse.getString("H1_Location_Title");

                        tmp.event_url = parse.getString("L_Hyperlink");
                        tmp.isAllDay = parse.getBoolean("E_Event_Is_All_Day");

                        ParseFile photo = (ParseFile) parse.get("G_Photo");

                        if(photo != null && photo.getUrl() != null)
                        {
                            tmp.photo_url = photo.getUrl();
                        }

                        ParseObject location = parse.getParseObject("O_Location");
                        if(location != null && location.getObjectId() != null)
                        {
                            try
                            {
                                tmp.associated_place.objectId = location.getObjectId();
                                tmp.associated_place.name = location.getString("A_Name");
                                tmp.associated_place.street_address = location.getString("C_Street_Address");
                                tmp.associated_place.city = location.getString("D_City");
                                tmp.associated_place.state = location.getString("E_State");
                                tmp.associated_place.zip_code = location.getString("F_Zip_Code");
                                tmp.associated_place.tel_number = location.getString("G_Phone_Number");
                                tmp.associated_place.web_url = location.getString("H_Website");
                                tmp.associated_place.geo_point = location.getParseGeoPoint("I_GeoPoint").getLatitude() + ", " + parse.getParseGeoPoint("I_GeoPoint").getLongitude();
                                tmp.associated_place.notes = location.getString("J_Notes");
                            }
                            catch(Exception ex)
                            {
                                ex.printStackTrace();
                            }
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

                progressDialog.dismiss();

                adapter = new EventListAdapter(view.getContext(), events, current_event_type, getActivity());
                eventList = (ListView) getActivity().findViewById(R.id.event_list);
                eventList.setAdapter(adapter);
                eventList.setOnItemClickListener(new EventItemClickListener());
            }
        });
    }

    public void onResume()
    {
        super.onResume();

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        if(events == null)
        {
            try
            {
                File f = getActivity().getFileStreamPath(SAVED_EVENTS_PATH);
                if((f.lastModified() + (EVENTS_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                {
                    events = (ArrayList<Event>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_EVENTS_PATH);
                }
                else
                {
                    loadEvents();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                loadEvents();
            }
        }
        else
        {
            switch(current_event_type)
            {
                case EVENT_TYPE_UPCOMING:
                    upcomingEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                    upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_left);
                    pastEventsSelector.setTextColor(Color.parseColor("#50667B"));
                    pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_right);
                    break;
                case EVENT_TYPE_PAST:
                    pastEventsSelector.setTextColor(Color.parseColor("#F5FDFF"));
                    pastEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_selected_right);
                    upcomingEventsSelector.setTextColor(Color.parseColor("#50667B"));
                    upcomingEventsSelector.setBackgroundResource(R.drawable.rounded_rectangle_button_unselected_left);
                    break;
            }
        }

    }

    public void selectItem(int position)
    {
        Event selected = null;
        int current_position = 0;
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        if(current_event_type == EVENT_TYPE_UPCOMING)
        {
            for(Event e : events)
            {
                if(e.start_time.after(now))
                {
                    if(current_position == position)
                    {
                        selected = e;
                    }
                    current_position++;
                }
            }
        }
        else if(current_event_type == EVENT_TYPE_PAST)
        {
            for(Event e : events)
            {
                if(e.start_time.before(now))
                {
                    if(current_position == position)
                    {
                        selected = e;
                    }
                    current_position++;
                }
            }
        }

        EventDetailFragment fragment = new EventDetailFragment(selected);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class EventItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private class nestedFragmentHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            adapter = new EventListAdapter(view.getContext(), events, current_event_type, getActivity());
            eventList = (ListView) getActivity().findViewById(R.id.event_list);
            eventList.setAdapter(adapter);
            eventList.setOnItemClickListener(new EventItemClickListener());

            if(state != null)
            {
                eventList.onRestoreInstanceState(state);
            }
        }
    }
}
