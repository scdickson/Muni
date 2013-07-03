package com.cellaflora.muni.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import com.cellaflora.muni.*;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment
{
	//Constants for loading weather
	private static final String WEATHER_KEY = "mg8xd4e3c3vc2tjkh2hvtcau";
	private static final int WEATHER_ZIPCODE = 47906;
	private static final int WEATHER_NUM_DAYS = 1;
    private static final String SAVED_WEATHER_PATH = "com.cellaflora.muni.saved_weather"; //Name of saved weather file
    private static final int WEATHER_REPLACE_INTERVAL = 60; //In Minutes!
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		return view;
	}
	
	public void onResume()
	{
		super.onResume();

        try
        {
            //Load weather asynchronously and set custom font
            TextView weatherBox = (TextView) getView().findViewById(R.id.weather_box);


            Calendar now = Calendar.getInstance();
            File f = getActivity().getFileStreamPath(SAVED_WEATHER_PATH);

            if((f.lastModified() + (WEATHER_REPLACE_INTERVAL * 60 * 1000)) < now.getTimeInMillis())
            {
                new loadWeather().execute(weatherBox);
            }
            else
            {
                FileInputStream in = getActivity().openFileInput(SAVED_WEATHER_PATH);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                weatherBox.setText(reader.readLine());
                in.close();
                reader.close();
            }

            Typeface avenirBlack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Avenir LT 95 Black.ttf");
            weatherBox.setTypeface(avenirBlack);
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Error loading weather.", Toast.LENGTH_LONG).show();
        }
	}
	
	private class loadWeather extends AsyncTask<TextView, Integer, Void>
	{
		private String temp = null;
		TextView weatherBox;
		
		protected Void doInBackground(TextView... arg0) 
		{
			try
			{
				//Fetch raw data as a String
				weatherBox = arg0[0];
				URL url = new URL("http://api.worldweatheronline.com/free/v1/weather.ashx?q=" + WEATHER_ZIPCODE + "&format=json&num_of_days=" + WEATHER_NUM_DAYS + "&key=" + WEATHER_KEY);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String data[] = in.readLine().split(", ");
				
				for(String element : data)
				{
					if(element.contains("temp_F")) //Could also allow Celsius in settings or something
					{
						//Parse temperature
						temp = element.substring(element.indexOf(" ") + 2, element.length() - 1) + (char) 0x00B0;
                        FileOutputStream out = getActivity().openFileOutput(SAVED_WEATHER_PATH, Context.MODE_PRIVATE);
                        out.write(temp.getBytes());
                        out.flush();
                        out.close();
						break;
					}
				}
			}
			catch(Exception e)
			{
				Log.w("myApp", "ERROR: " + e.getMessage());
			}
			return null;
		}
		
		protected void onPostExecute(Void v) 
		{
			if(temp != null)
			{
				//Kind of a cool fade in animation for the weather
				final Animation in = new AlphaAnimation(0.0f, 1.0f);
			    in.setDuration(1200);
			    weatherBox.setText(temp);
			    weatherBox.startAnimation(in);
			}
			else
			{
				//Weather couldn't be loaded for some reason
				//Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
               try
               {
                    FileInputStream in = getActivity().openFileInput(SAVED_WEATHER_PATH);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    weatherBox.setText(reader.readLine());
                    in.close();
                    reader.close();
               }
               catch(Exception e)
               {
                   Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
               }
            }
		}
	}
}
