package com.cellaflora.muni.fragments;

import com.cellaflora.muni.Person;
import com.cellaflora.muni.R;

import android.app.AlertDialog;
import android.app.Fragment;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PeopleDetailFragment extends Fragment
{
	Person requested;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.people_detail_fragment, container, false);
		return view;
	}
	
	public void setPerson(Person requested)
	{
		this.requested = requested;
	}
	
	public void onResume()
	{
		super.onResume();

        TextView name = (TextView) getActivity().findViewById(R.id.people_detail_name);
		name.setText(requested.name);

        if(requested.title != null)
        {
            TextView title = (TextView) getActivity().findViewById(R.id.people_detail_title);
            title.setText(requested.title);
        }

        if(requested.tel_number != null)
        {
            TextView tel_number = (TextView) getActivity().findViewById(R.id.people_detail_tel_number);
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
            notes.setText(requested.notes);
        }

        if(requested.url != null)
        {
            ImageView photo = (ImageView) getActivity().findViewById(R.id.people_detail_image);
            new loadWeather().execute(photo);
        }

	}

    private class loadWeather extends AsyncTask<ImageView, Integer, Void>
    {
        ImageView photo;
        Bitmap image;

        protected Void doInBackground(ImageView... arg0)
        {
            try
            {
                photo = arg0[0];
                URL url = new URL(requested.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                image = BitmapFactory.decodeStream(input);
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
            }
        }
    }
}
