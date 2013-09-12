package com.cellaflora.muni.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cellaflora.muni.*;

import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import support.PersistenceManager;

public class HomeFragment extends Fragment
{
    View view;
    TextView weatherBox;
    ImageView weatherImage;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.home_fragment, container, false);
        MainActivity.actionbarTitle.setText("");
        weatherBox = (TextView) view.findViewById(R.id.weather_box);
        weatherBox.setTypeface(MainActivity.myriadProSemiBold);
        weatherImage = (ImageView) view.findViewById(R.id.weather_image);
		return view;
	}

    public int setWeatherIcon(int code)
    {
        int id = -1;

        for(int group = 0; group < MuniConstants.WEATHER_CODES.length; group++)
        {
            for(int value = 0; value < MuniConstants.WEATHER_CODES[group].length; value++)
            {
                if(code == MuniConstants.WEATHER_CODES[group][value])
                {
                    id = getActivity().getResources().getIdentifier("com.cellaflora.muni:drawable/group_" + (group + 1), null, null);
                    break;
                }
            }
        }

        return id;
    }

	public void onResume()
	{
		super.onResume();

        try
        {
            //Load weather asynchronously and set custom font

            File f = new File(getActivity().getFilesDir(), MuniConstants.SAVED_WEATHER_KEY);
            if((f.lastModified() + (MuniConstants.WEATHER_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
            {
                String weather[] = (String[])(PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_WEATHER_KEY));
                weatherBox.setText(weather[0]);
                weatherImage.setImageResource(setWeatherIcon(Integer.parseInt(weather[2])));
            }
            else
            {
                new loadWeather().execute(weatherBox, weatherImage);
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Error loading weather.", Toast.LENGTH_LONG).show();
        }
	}

    private int substringIndex(String line, int start, char to)
    {
        int end = start;

        for(int i = start; i < line.length(); i++)
        {
            if(line.charAt(i) == to)
            {
                end = i;
                break;
            }
        }

        return end;
    }

	private class loadWeather extends AsyncTask<Object, Integer, Void>
	{
        private String weather[] = new String[3];
		TextView weatherBox;
        ImageView weatherImage;
		
		protected Void doInBackground(Object... arg0)
		{
			try
			{
                weatherBox = (TextView) arg0[0];
                weatherImage = (ImageView) arg0[1];

                URL url = new URL(MuniConstants.WEATHER_URL + MuniConstants.WOEID);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while((line = in.readLine()) != null)
                {
                    if(line.contains("yweather:condition"))
                    {
                        weather[0] = line.substring(line.indexOf("temp=\"") + 6, substringIndex(line, line.indexOf("temp=\"") + 6, '"')) + (char) 0x00B0;
                        weather[1] = line.substring(line.indexOf("text=\"") + 6, substringIndex(line, line.indexOf("text=\"") + 6, '"'));
                        weather[2] = line.substring(line.indexOf("code=\"") + 6, substringIndex(line, line.indexOf("code=\"") + 6, '"'));
                        break;
                    }
                }
			}
			catch(Exception e){}
			return null;
		}
		
		protected void onPostExecute(Void v) 
		{
			if(weather != null)
			{
				try
                {
                    final Animation in = new AlphaAnimation(0.0f, 1.0f);
                    in.setDuration(1200);
                    weatherBox.setText(weather[0]);
                    weatherImage.setImageResource(setWeatherIcon(Integer.parseInt(weather[2])));
                    weatherImage.startAnimation(in);
                    weatherBox.startAnimation(in);
                    PersistenceManager.writeObject(getActivity().getApplicationContext(), MuniConstants.SAVED_WEATHER_KEY, weather);
                }
                catch(Exception e){}
			}
			else
			{
				//Weather couldn't be loaded for some reason
				//Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
               try
               {
                   weatherBox.setText((String)(PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_WEATHER_KEY)));
               }
               catch(Exception e)
               {
                   //Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
               }
            }
		}
	}
}
