package com.cellaflora.muni.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.Event;
import com.cellaflora.muni.R;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sdickson on 7/28/13.
 */
public class EventDetailFragment extends Fragment
{
    View view;
    Event event;
    TextView txtTitle, txtDescription, txtDate, txtLocation, txtUrl, txtAddress;
    ImageView imgEvent;
    Button btnAdd;
    Intent calIntent;

    public EventDetailFragment(Event event)
    {
        this.event = event;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.event_detail_fragment, container, false);
        return view;
    }

    public void startCalendarIntent()
    {
        if(calIntent != null)
        {
            startActivity(calIntent);
        }
    }

    public void onResume()
    {
        super.onResume();
        txtTitle = (TextView) view.findViewById(R.id.event_title_detail);
        txtDescription = (TextView) view.findViewById(R.id.event_description_detail);
        txtDate = (TextView) view.findViewById(R.id.event_time_detail);
        txtLocation = (TextView) view.findViewById(R.id.event_location_detail);
        txtUrl = (TextView) view.findViewById(R.id.event_url_detail);
        txtAddress = (TextView) view.findViewById(R.id.event_address_detail);
        imgEvent = (ImageView) view.findViewById(R.id.event_image_detail);

        calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        btnAdd = (Button) view.findViewById(R.id.event_calendar_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendarIntent();
            }
        });

        if(event.photo_url != null)
        {
            try
            {
                File f = new File(getActivity().getFilesDir() + "/" + event.objectId);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                imgEvent.setImageBitmap(image);
            }
            catch(OutOfMemoryError ome)
            {
                try
                {
                    File f = new File(getActivity().getFilesDir() + "/" + event.objectId);

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(f),null,o);
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
                    Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
                    imgEvent.setImageBitmap(image);
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
                        txtDate.setText(start.format(event.start_time) + " - " + end.format(event.end_time));
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

        if(event.location != null)
        {
            txtLocation.setText(event.location);
            calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.location);
        }
        else
        {
            txtLocation.setVisibility(View.GONE);
        }

        if(event.event_url != null)
        {
            txtUrl.setText(Html.fromHtml("<u>" + event.event_url + "</u>"));
            final String url = event.event_url;
            txtUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
            });
        }
        else
        {
            txtUrl.setVisibility(View.GONE);
        }

        if(event.address != null)
        {
            txtAddress.setText(event.address);
        }
        else
        {
            txtAddress.setVisibility(View.GONE);
        }
    }
}
