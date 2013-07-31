package com.cellaflora.muni.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.Event;
import com.cellaflora.muni.R;
import com.cellaflora.muni.fragments.EventDetailFragment;
import com.cellaflora.muni.fragments.EventFragment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sdickson on 7/25/13.
 */
public class EventListAdapter extends BaseAdapter
{
    LayoutInflater inflater;
    ArrayList<Event> events;
    ArrayList<Event> allEvents;
    Context context;
    Calendar c;
    Date now;

    public static final int IMAGE_BUFFER_SIZE = 20480;

    public EventListAdapter(Context context, ArrayList<Event> events, int event_selector)
    {
        this.context = context;
        this.events = new ArrayList<Event>();
        this.allEvents = events;
        this.c = Calendar.getInstance();
        this.now = c.getTime();

        if(event_selector == EventFragment.EVENT_TYPE_UPCOMING)
        {
            for(Event e : events)
            {
                if(e.start_time.after(now))
                {
                    this.events.add(e);
                }
            }
        }
        else if(event_selector == EventFragment.EVENT_TYPE_PAST)
        {
            for(Event e : events)
            {
                if(e.start_time.before(now))
                {
                    this.events.add(e);
                }
            }
        }
    }

    public void switchView(int event_selector)
    {
        this.events = null;
        this.events = new ArrayList<Event>();

        if(event_selector == EventFragment.EVENT_TYPE_UPCOMING)
        {
            for(Event e : allEvents)
            {
                if(e.start_time.after(now))
                {
                    this.events.add(e);
                }
            }
        }
        else if(event_selector == EventFragment.EVENT_TYPE_PAST)
        {
            for(Event e : allEvents)
            {
                if(e.start_time.before(now))
                {
                    this.events.add(e);
                }
            }
        }
    }

    public int getCount()
    {
        return events.size();
    }

    public Object getItem(int position)
    {
        return events.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView txtTitle, txtDescription, txtDate, txtLocation, txtUrl;
        ImageView imgEvent;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.event_list_row, parent, false);
        Event e = events.get(position);

        txtTitle = (TextView) itemView.findViewById(R.id.event_title);
        txtDescription = (TextView) itemView.findViewById(R.id.event_description);
        txtDate = (TextView) itemView.findViewById(R.id.event_time);
        txtLocation = (TextView) itemView.findViewById(R.id.event_location);
        txtUrl = (TextView) itemView.findViewById(R.id.event_url);
        imgEvent = (ImageView) itemView.findViewById(R.id.event_image);
        Typeface avenirBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Avenir LT 95 Black.ttf");

        if(e.photo_url != null)
        {
            File f = new File(context.getFilesDir() + "/" + e.objectId);
            if(f != null && f.exists())
            {
                try
                {
                    imgEvent.setImageBitmap(null);
                    new loadImage().execute(imgEvent, f);
                }
                catch(Exception ex)
                {
                    new loadImageFromParse().execute(imgEvent, e);
                }
            }
            else
            {
                new loadImageFromParse().execute(imgEvent, e);
            }
        }
        else
        {
            imgEvent.setVisibility(View.GONE);
        }

        if(e.title != null)
        {
            txtTitle.setText(e.title);
            //txtTitle.setTypeface(avenirBlack);
        }
        else
        {
            txtTitle.setVisibility(View.GONE);
        }

        if(e.description != null)
        {
            txtDescription.setText(e.description);
            //txtDescription.setTypeface(avenirBlack);
        }
        else
        {
            txtDescription.setVisibility(View.GONE);
        }

        if(e.start_time != null)
        {
            String startFormat = null, endFormat = null;
            //txtDate.setTypeface(avenirBlack);

            if(e.isAllDay)
            {
                if(e.start_time != null && e.end_time != null)
                {
                    if(e.start_time.equals(e.end_time))
                    {
                        startFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time));
                    }
                    else
                    {
                        startFormat = endFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time) + " - " + end.format(e.end_time));
                    }
                }
                else
                {
                    if(e.start_time != null)
                    {
                        startFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time));
                    }
                    else if(e.end_time != null)
                    {
                        endFormat = "EEE, MMMM d, yyyy";
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(end.format(e.end_time));
                    }
                }
            }
            else
            {
                if(e.start_time != null && e.end_time != null)
                {

                    if(e.start_time.getDate() < e.end_time.getDate())
                    {
                        startFormat = "EEE, MMMM d, h:mm a";
                        endFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time) + " - " + end.format(e.end_time));
                    }
                    else if(e.start_time.getDate() == e.end_time.getDate())
                    {
                        startFormat = "EEE, MMMM d, h:mm a";
                        endFormat = "h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time) + " - " + end.format(e.end_time));
                    }
                }
                else
                {
                    if(e.start_time != null)
                    {
                        startFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.US);
                        txtDate.setText(start.format(e.start_time));
                    }
                    else if(e.end_time != null)
                    {
                        endFormat = "EEE, MMMM d, h:mm a z";
                        SimpleDateFormat end = new SimpleDateFormat(endFormat, Locale.US);
                        txtDate.setText(end.format(e.end_time));
                    }
                }
            }
        }
        else
        {
            txtDate.setVisibility(View.GONE);
        }

        if(e.location != null)
        {
            txtLocation.setText(e.location);
            //txtLocation.setTypeface(avenirBlack);
        }
        else
        {
            txtLocation.setVisibility(View.GONE);
        }

        if(e.event_url != null)
        {
            txtUrl.setText(e.event_url);
            //txtUrl.setTypeface(avenirBlack);
        }
        else
        {
            txtUrl.setVisibility(View.GONE);
        }


        return itemView;
    }

    private class loadImage extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                File f = (File) arg0[1];

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
                image = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(image != null)
            {
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(150);
                photo.setImageBitmap(image);
                photo.startAnimation(in);
            }
        }
    }

    private class loadImageFromParse extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                Event event = (Event) arg0[1];
                URL url = new URL(event.photo_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();

                File file = new File(context.getFilesDir() + "/" + event.objectId);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, IMAGE_BUFFER_SIZE);
                byte data[] = new byte[IMAGE_BUFFER_SIZE];

                int bytesRead = 0;
                while((bytesRead = is.read(data, 0, data.length)) >= 0)
                {
                    bos.write(data, 0, bytesRead);
                }

                bos.close();
                fos.close();
                is.close();

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
                image = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(image != null)
            {
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(1000);
                photo.setImageBitmap(image);
                photo.startAnimation(in);
            }
        }
    }
}
