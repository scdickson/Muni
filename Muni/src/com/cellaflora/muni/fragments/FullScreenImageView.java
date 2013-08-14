package com.cellaflora.muni.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by sdickson on 8/6/13.
 */
public class FullScreenImageView extends Activity
{
    String caption, image_path;
    ImageView fullscreenimage;
    TextView fullscreencaption;
    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreenimageview);
        Intent data = this.getIntent();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        if(data != null)
        {
            image_path = data.getStringExtra("image");
            caption = data.getStringExtra("caption");
            fullscreenimage = (ImageView) findViewById(R.id.fullscreenimage);
            fullscreencaption = (TextView) findViewById(R.id.fullscreenimage_caption);
            fullscreencaption.setTypeface(MainActivity.myriadProRegular);
        }
    }

    public void onResume()
    {
        super.onResume();

        if(image_path != null && fullscreenimage != null)
        {
            try
            {
                new loadImage().execute();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        if(caption != null && fullscreencaption != null)
        {
            fullscreencaption.setText(caption);
        }
    }

    private class loadImage extends AsyncTask<Object, Integer, Void>
    {
        Bitmap image;

        protected void onPreExecute()
        {
            //progressDialog.show();
        }

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                File f = new File(image_path);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f),null,o);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                    int width_tmp=o.outWidth, height_tmp=o.outHeight;
                    int scale=1;
                    while(true){
                        if(width_tmp/2<width || height_tmp/2<height)
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
            {}
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(image != null)
            {
                fullscreenimage.setImageBitmap(image);
                //progressDialog.dismiss();
            }
        }
    }
}
