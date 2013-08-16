package com.cellaflora.muni.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.NewsObject;
import com.cellaflora.muni.R;
import com.cellaflora.muni.fragments.FullScreenImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sdickson on 7/28/13.
 */
public class NewsListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<NewsObject> news;
    LayoutInflater inflater;
    Activity activity;

    public static final int IMAGE_BUFFER_SIZE = 20480;

    public NewsListAdapter(Context context, ArrayList<NewsObject> news, Activity activity)
    {
        this.context = context;
        this.inflater = inflater;
        this.news = news;
        this.activity = activity;
    }

    public int getCount()
    {
        return news.size();
    }

    public Object getItem(int position)
    {
        return news.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView txtHeadline, txtDate, txtSubheadline;
        ImageView imgNews;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.news_list_row, parent, false);

        txtHeadline = (TextView) itemView.findViewById(R.id.news_headline);
        txtHeadline.setTypeface(MainActivity.myriadProSemiBold);

        txtDate = (TextView) itemView.findViewById(R.id.news_date);
        txtDate.setTypeface(MainActivity.myriadProRegular);

        txtSubheadline = (TextView) itemView.findViewById(R.id.news_sub_headline);
        txtSubheadline.setTypeface(MainActivity.myriadProRegular);

        imgNews = (ImageView) itemView.findViewById(R.id.news_image);

        NewsObject n = news.get(position);

        if(n.photo_url != null)
        {
            File f = new File(context.getFilesDir() + "/" + n.objectId);
            if(f != null && f.exists())
            {
                try
                {
                    imgNews.setImageBitmap(null);
                    new loadImage().execute(imgNews, f, n.photo_caption);
                }
                catch(Exception ex)
                {
                    new loadImageFromParse().execute(imgNews, n);
                }
            }
            else
            {
                new loadImageFromParse().execute(imgNews, n);
            }
        }
        else
        {
            imgNews.setVisibility(View.GONE);
        }

        if(n.headline != null)
        {
            txtHeadline.setText(n.headline);
        }
        else
        {
            txtHeadline.setVisibility(View.GONE);
        }

        if(n.date != null)
        {
            String format = "EEE, MMMM d";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            txtDate.setText(dateFormat.format(n.date));
        }
        else
        {
            txtDate.setVisibility(View.GONE);
        }

        if(n.sub_headline != null)
        {
            txtSubheadline.setText(n.sub_headline);
        }
        else
        {
            txtSubheadline.setVisibility(View.GONE);
        }

        return itemView;
    }

    private class loadImage extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;
        File f;
        String caption;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                f = (File) arg0[1];
                caption = (String) arg0[2];

                image = BitmapFactory.decodeStream(new FileInputStream(f));
            }
            catch(OutOfMemoryError ome)
            {
                try
                {
                    photo = (ImageView) arg0[0];
                    f = (File) arg0[1];
                    caption = (String) arg0[2];

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
                catch(Exception ex){}
            }
            catch(Exception e)
            {}
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(image != null)
            {
                //final Animation in = new AlphaAnimation(0.0f, 1.0f);
                //in.setDuration(100);
                photo.setImageBitmap(image);
                //photo.startAnimation(in);

                photo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent fullscreenimage = new Intent(activity, FullScreenImageView.class);
                        fullscreenimage.putExtra("image", f.getAbsolutePath() + "_uncompressed");
                        fullscreenimage.putExtra("caption", caption);
                        activity.startActivity(fullscreenimage);
                    }
                });
            }
        }
    }

    private class loadImageFromParse extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;
        NewsObject n;
        File file, compressed;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                n = (NewsObject) arg0[1];
                URL url = new URL(n.photo_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();

                file = new File(context.getFilesDir() + "/" + n.objectId + "_uncompressed");
                compressed = new File(context.getFilesDir() + "/" + n.objectId);
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

                FileOutputStream out = new FileOutputStream(compressed);
                image.compress(Bitmap.CompressFormat.JPEG, 90, out);

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

                photo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent fullscreenimage = new Intent(activity, FullScreenImageView.class);
                        fullscreenimage.putExtra("image", file.getAbsolutePath() + "_uncompressed");
                        fullscreenimage.putExtra("caption", n.photo_caption);
                        activity.startActivity(fullscreenimage);
                    }
                });
            }
        }
    }

}
