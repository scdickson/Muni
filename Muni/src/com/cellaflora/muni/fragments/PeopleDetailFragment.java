package com.cellaflora.muni.fragments;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.Person;
import com.cellaflora.muni.R;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PeopleDetailFragment extends Fragment
{
	Person requested;
    View view;
    private ProgressDialog progressDialog;

    public PeopleDetailFragment(Person requested)
    {
        this.requested = requested;
    }

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_detail_fragment, container, false);
		return view;
	}
	
	public void onResume()
	{
		super.onResume();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        TextView name = (TextView) getActivity().findViewById(R.id.people_detail_name);
        name.setTypeface(MainActivity.myriadProSemiBold);
		name.setText(requested.name);

        if(requested.title != null)
        {
            TextView title = (TextView) getActivity().findViewById(R.id.people_detail_title);
            title.setTypeface(MainActivity.myriadProRegular);
            title.setText(requested.title);
        }

        if(requested.tel_number != null)
        {
            TextView tel_number = (TextView) getActivity().findViewById(R.id.people_detail_tel_number);
            tel_number.setTypeface(MainActivity.myriadProRegular);
            tel_number.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                            // set title
                            alertDialogBuilder.setTitle("Confirm");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Call " + requested.name + "?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id)
                                        {
                                            String uri = "tel:" + requested.tel_number.trim();
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse(uri));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id)
                                        {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing
                                            dialog.cancel();
                                        }
                                    });

                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                        }
                    }
            );
            tel_number.setText(requested.tel_number);
        }

        if(requested.email != null)
        {
            TextView email = (TextView) getActivity().findViewById(R.id.people_detail_email);
            email.setTypeface(MainActivity.myriadProRegular);
            email.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            String to[] = {requested.email};
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
                            emailIntent.setType("plain/text");
                            startActivity(Intent.createChooser(emailIntent, "Send your email using:"));
                        }
                    }
            );
            email.setText(requested.email);
        }

        if(requested.notes != null)
        {
            TextView notes = (TextView) getActivity().findViewById(R.id.people_detail_notes);
            notes.setTypeface(MainActivity.myriadProRegular);
            notes.setText(requested.notes);
        }

        if(requested.url != null)
        {
            ImageView photo = (ImageView) getActivity().findViewById(R.id.people_detail_image);

            File f = new File(view.getContext().getFilesDir() + "/" + requested.objectId);
            if(f != null && f.exists())
            {
                try
                {
                    photo.setImageBitmap(null);
                    new loadImage().execute(photo, f);
                }
                catch(Exception ex)
                {
                    new loadImageFromParse().execute(photo);
                }
            }
            else
            {
                new loadImageFromParse().execute(photo);
            }
        }

	}

    private class loadImage extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;
        File f;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                f = (File) arg0[1];
                image = BitmapFactory.decodeStream(new FileInputStream(f));
            }
            catch(OutOfMemoryError ome)
            {
                try
                {
                    photo = (ImageView) arg0[0];
                    f = (File) arg0[1];

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(f),null,o);
                    final int REQUIRED_SIZE=80;

                    int width_tmp=o.outWidth, height_tmp=o.outHeight;
                    int scale=1;
                    while(true){
                        if(width_tmp/2<120 || height_tmp/2<147)
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
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(image != null)
            {
                photo.setImageBitmap(image);
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent fullscreenimage = new Intent(getActivity(), FullScreenImageView.class);
                        fullscreenimage.putExtra("image", f.getAbsolutePath() + "_uncompressed");
                        getActivity().startActivity(fullscreenimage);
                    }
                });
            }
        }
    }



    private class loadImageFromParse extends AsyncTask<Object, Integer, Void>
    {
        ImageView photo;
        Bitmap image;
        File compressed, file;

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                photo = (ImageView) arg0[0];
                URL url = new URL(requested.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();

                file = new File(view.getContext().getFilesDir() + "/" + requested.objectId + "_uncompressed");
                compressed = new File(view.getContext().getFilesDir() + "/" + requested.objectId);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, MuniConstants.IMAGE_BUFFER_SIZE);
                byte data[] = new byte[MuniConstants.IMAGE_BUFFER_SIZE];

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
                    if(width_tmp/2<120 || height_tmp/2<147)
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

                    photo.setImageBitmap(image);
                    photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent fullscreenimage = new Intent(getActivity(), FullScreenImageView.class);
                            fullscreenimage.putExtra("image", file.getAbsolutePath());
                            getActivity().startActivity(fullscreenimage);
                        }
                    });

            }
        }
    }
}
