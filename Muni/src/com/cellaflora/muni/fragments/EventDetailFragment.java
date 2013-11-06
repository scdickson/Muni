package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.objects.Event;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import support.PersistenceManager;
import com.cellaflora.muni.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sdickson on 7/28/13.
 */
public class EventDetailFragment extends Fragment
{
    View view;
    File file;
    Event event;
    TextView txtTitle, txtDescription, txtDate, txtLocation, txtUrl, txtAddress, txtRecommendNumber, txtRecommendAction;
    ImageView imgEvent, mapAction, callAction, webAction;
    View mapDivider, callDivider;
    Button btnAdd;
    Intent calIntent;
    ParseObject counter = null;

    public EventDetailFragment(Event event)
    {
        this.event = event;

        for(String objectId : EventFragment.recommendedEvents)
        {
            if(event.objectId.equals(objectId))
            {
                event.didRecommend = true;
                break;
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.event_detail_fragment, container, false);
        MainActivity.actionbarTitle.setText(" ");
        return view;
    }

    public void startCalendarIntent()
    {
        if(calIntent != null)
        {
            startActivity(calIntent);
        }
    }

    public void onPause()
    {
        super.onPause();

        try
        {
            PersistenceManager.writeObject(view.getContext(), MuniConstants.SAVED_EVENTS_RECOMMENDED_PATH, EventFragment.recommendedEvents);
        }
        catch(Exception e){}
    }

    public void onResume()
    {
        super.onResume();
        txtRecommendNumber = (TextView) view.findViewById(R.id.event_detail_recommend_number);

        if(event.counterId != null && !event.counterId.isEmpty())
        {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsCounters");
            query.whereEqualTo("objectId", event.counterId);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> result, ParseException e)
                {
                    if(e == null)
                    {
                        for(ParseObject parse : result)
                        {
                            counter = parse;
                            event.recommends = parse.getInt("Count");
                        }

                        if(txtRecommendNumber != null)
                        {
                            if(event.recommends >= 1000)
                            {
                                txtRecommendNumber.setTextSize(20);
                                double roundOff = Math.round(((double) event.recommends / 1000.0) * 100.0) / 100.0;
                                txtRecommendNumber.setText(roundOff + "k");
                            }
                            else
                            {
                                txtRecommendNumber.setText(event.recommends + "");
                            }
                        }
                    }
                }
            });
        }

        txtTitle = (TextView) view.findViewById(R.id.event_title_detail);
        txtTitle.setTypeface(MainActivity.myriadProSemiBold);

        txtDescription = (TextView) view.findViewById(R.id.event_description_detail);
        txtDescription.setTypeface(MainActivity.myriadProRegular);

        txtDate = (TextView) view.findViewById(R.id.event_time_detail);
        txtDate.setTypeface(MainActivity.myriadProRegular);

        txtLocation = (TextView) view.findViewById(R.id.event_location_detail);
        txtLocation.setTypeface(MainActivity.myriadProRegular);

        callAction = (ImageView) view.findViewById(R.id.event_call_action);
        webAction = (ImageView) view.findViewById(R.id.event_web_action);
        mapAction = (ImageView) view.findViewById(R.id.event_map_action);

        txtAddress = (TextView) view.findViewById(R.id.event_address_detail);
        txtAddress.setTypeface(MainActivity.myriadProRegular);

        txtRecommendNumber.setTypeface(MainActivity.myriadProSemiBold);

        txtRecommendAction = (TextView) view.findViewById(R.id.event_detail_recommend_action);
        txtRecommendAction.setTypeface(MainActivity.myriadProRegular);

        if(event.didRecommend)
        {
            txtRecommendAction.setTextColor(Color.parseColor("#EC4B43"));
        }

        txtRecommendAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!event.didRecommend)
                {
                    event.didRecommend = true;
                    EventFragment.recommendedEvents.add(event.objectId);
                    txtRecommendAction.setTextColor(Color.parseColor("#EC4B43"));
                    counter.increment("Count");
                    counter.saveEventually();
                    
                    event.recommends++;
                    if(event.recommends >= 1000)
                    {
                        txtRecommendNumber.setTextSize(20);
                        double roundOff = Math.round(((double) event.recommends / 1000.0) * 100.0) / 100.0;
                        txtRecommendNumber.setText(roundOff + "k");
                    }
                    else
                    {
                        txtRecommendNumber.setText(event.recommends + "");
                    }
                }
            }
        });

        imgEvent = (ImageView) view.findViewById(R.id.event_image_detail);

        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        btnAdd = (Button) view.findViewById(R.id.event_calendar_add);
        btnAdd.setTypeface(MainActivity.myriadProSemiBold);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendarIntent();
            }
        });

        if(event.recommends >= 0)
        {
            if(event.recommends >= 1000)
            {
                txtRecommendNumber.setTextSize(20);
                double roundOff = Math.round(((double) event.recommends / 1000.0) * 100.0) / 100.0;
                txtRecommendNumber.setText(roundOff + "k");
            }
            else
            {
                txtRecommendNumber.setText(event.recommends + "");
            }
        }

        if(event.photo_url != null)
        {
            try
            {
                file = new File(getActivity().getFilesDir() + "/" + event.objectId);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file));
                imgEvent.setImageBitmap(image);
                imgEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent fullscreenimage = new Intent(getActivity(), FullScreenImageView.class);
                        fullscreenimage.putExtra("image", file.getAbsolutePath() + "_uncompressed");
                        getActivity().startActivity(fullscreenimage);
                    }
                });
            }
            catch(OutOfMemoryError ome)
            {
                try
                {
                    file = new File(getActivity().getFilesDir() + "/" + event.objectId);

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(file),null,o);
                    final int REQUIRED_SIZE=80;

                    int width_tmp=o.outWidth, height_tmp=o.outHeight;
                    int scale=1;
                    while(true){
                        if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                            break;
                        width_tmp/=2;
                        height_tmp/=2;
                        scale*=2;
                    }

                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize=scale;
                    Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
                    imgEvent.setImageBitmap(image);
                    imgEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent fullscreenimage = new Intent(getActivity(), FullScreenImageView.class);
                            fullscreenimage.putExtra("image", file.getAbsolutePath() + "_uncompressed");
                            getActivity().startActivity(fullscreenimage);
                        }
                    });
                }
                catch(Exception ex){}
            }
            catch(Exception e)
            {
                e.printStackTrace();
                imgEvent.setVisibility(View.GONE);
            }


        }
        else
        {
            imgEvent.setVisibility(View.GONE);
        }

        if(event.title != null)
        {
            txtTitle.setText(event.title);
            calIntent.putExtra(CalendarContract.Events.TITLE, event.title);
        }
        else
        {
            txtTitle.setVisibility(View.GONE);
        }

        if(event.description != null)
        {
            txtDescription.setText(event.description);
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, event.description);
        }
        else
        {
            txtDescription.setVisibility(View.GONE);
        }

        if(event.start_time != null)
        {
            String startFormat = null, endFormat = null;
            calIntent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault());
            Calendar calendar_start = new GregorianCalendar();
            calendar_start.setTime(event.start_time);
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar_start.getTimeInMillis());

            if(event.end_time != null)
            {
                Calendar calendar_end = new GregorianCalendar();
                calendar_end.setTime(event.end_time);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar_end.getTimeInMillis());
            }


            if(event.isAllDay)
            {
                calIntent.putExtra(CalendarContract.Events.ALL_DAY, true);
                if(event.start_time != null && event.end_time != null)
                {
                    if(event.start_time.equals(event.end_time))
                    {
                        startFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time));
                    }
                    else
                    {
                        startFormat = endFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time) + " - " + end.format(event.end_time));
                    }
                }
                else
                {
                    if(event.start_time != null)
                    {
                        startFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time));
                    }
                    else if(event.end_time != null)
                    {
                        endFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(end.format(event.end_time));
                    }
                }
            }
            else
            {
                calIntent.putExtra(CalendarContract.Events.ALL_DAY, false);
                if(event.start_time != null && event.end_time != null)
                {

                    if(event.start_time.getDate() < event.end_time.getDate())
                    {
                        startFormat = "EEE, MMMM d, h:mm a";
                        endFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time) + " -\n" + end.format(event.end_time));
                    }
                    else if(event.start_time.getDate() == event.end_time.getDate())
                    {
                        startFormat = "EEE, MMMM d, h:mm a";
                        endFormat = "h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time) + " - " + end.format(event.end_time));
                    }
                }
                else
                {
                    if(event.start_time != null)
                    {
                        startFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(event.start_time));
                    }
                    else if(event.end_time != null)
                    {
                        endFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(end.format(event.end_time));
                    }
                }
            }
        }
        else
        {
            txtDate.setVisibility(View.GONE);
        }

        if(event.associated_place != null)
        {
            if(event.associated_place.street_address != null)
            {
                txtAddress.setText(event.associated_place.street_address + "\n" + event.associated_place.city + " " + event.associated_place.state + " " + event.associated_place.zip_code);
            }

            if(event.location == null)
            {
                txtLocation.setText(event.associated_place.name);
                txtLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        PlaceDetailFragment fragment = new PlaceDetailFragment(event.associated_place, null);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            if(event.associated_place.tel_number != null)
            {
                callAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Confirm");
                        alertDialogBuilder
                                .setMessage("Call " + event.associated_place.name + "?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        String uri = "tel:" + event.associated_place.tel_number.trim();
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse(uri));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
            else
            {

                //callAction.setImageResource(view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/", null, null));
            }
        }
        else
        {
            txtAddress.setVisibility(View.GONE);
        }

        if(event.location != null)
        {
            txtLocation.setText(event.location);
            calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.location);
        }
        else
        {
            if(event.associated_place == null)
            {
                txtLocation.setVisibility(View.GONE);
            }
        }

        if(event.event_url != null)
        {
            webAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(event.event_url));
                    view.getContext().startActivity(intent);
                }
            });
        }
        else
        {

        }

        if(event.address != null || event.associated_place != null)
        {
            if(event.address != null)
            {
                txtAddress.setText(event.address);
                mapAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + event.address));
                        startActivity(intent);
                    }
                });
            }
            else if(event.associated_place.street_address != null)
            {
                txtAddress.setText(event.associated_place.street_address + "\n" + event.associated_place.city + " " + event.associated_place.state + " " + event.associated_place.zip_code);
                mapAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlaceDetailFragment fragment = new PlaceDetailFragment(event.associated_place, null);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        }
        else
        {
            txtAddress.setVisibility(View.GONE);
        }
    }
}
