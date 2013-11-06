package com.cellaflora.muni.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cellaflora.muni.*;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    ImageView mainImage;
    ImageView weatherImage;
    ImageView infoButton;

    final Animation imageFadeIn = new AlphaAnimation(0.0f, 1.0f);
    final Animation menuFadeIn = new AlphaAnimation(0.0f, 1.0f);
    final Animation menuFadeOut = new AlphaAnimation(1.0f, 0.0f);
    final Animation imageFadeOut = new AlphaAnimation(1.0f, 0.0f);

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.home_fragment, container, false);
        MainActivity.actionbarTitle.setText("");
        mainImage = (ImageView) view.findViewById(R.id.home_imageview);
        weatherBox = (TextView) view.findViewById(R.id.weather_box);
        weatherBox.setTypeface(MainActivity.myriadProSemiBold);
        weatherImage = (ImageView) view.findViewById(R.id.weather_image);
        infoButton = (ImageView) view.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                displayInfo();
            }
        });
		return view;
	}

    public void displayInfo()
    {

        menuFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                int rsc = getResources().getIdentifier("com.cellaflora.muni:drawable/wlaf_home", null, null);
                mainImage.setImageResource(rsc);
                mainImage.setVisibility(View.VISIBLE);
                weatherImage.setVisibility(View.VISIBLE);
                weatherBox.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.VISIBLE);

                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Nothing
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageFadeOut.setDuration(MuniConstants.ABOUT_FADE_DELAY);
        imageFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mainImage.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                    mainImage.startAnimation(menuFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        imageFadeIn.setDuration(MuniConstants.ABOUT_FADE_DELAY);
        imageFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                int rsc = getResources().getIdentifier("com.cellaflora.muni:drawable/cella_logo", null, null);
                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(MuniConstants.CELLAFLORA_IMAGE_LINK));
                        startActivity(intent);
                    }
                });
                mainImage.setImageResource(rsc);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                try
                {
                    Thread.sleep(MuniConstants.ABOUT_HOLD_DELAY);
                }
                catch(Exception e){}
                mainImage.startAnimation(imageFadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        menuFadeOut.setDuration(MuniConstants.ABOUT_FADE_DELAY);
        menuFadeIn.setDuration(MuniConstants.ABOUT_FADE_DELAY);

        menuFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                weatherImage.setVisibility(View.GONE);
                weatherBox.setVisibility(View.GONE);
                infoButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mainImage.startAnimation(imageFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainImage.startAnimation(menuFadeOut);


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
        MainActivity.mMenuAdapter.setSelected(0);

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

            Log.d("fatal", weatherBox.getText().toString());
		}
	}
}
