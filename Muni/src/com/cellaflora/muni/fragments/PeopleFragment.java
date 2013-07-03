package com.cellaflora.muni.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cellaflora.muni.PeopleListAdapter;
import com.cellaflora.muni.Person;
import com.cellaflora.muni.R;
import com.parse.*;

public class PeopleFragment extends Fragment
{
	View view;
	ArrayList<Person> people = null;

    private static final String SAVED_PEOPLE_PATH = "com.cellaflora.muni.saved_people"; //Name of saved people file
    private static final int PEOPLE_REPLACE_INTERVAL = 2; //In minutes!

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_fragment, container, false);
		return view;
	}
	
	public void onResume()
	{
		super.onResume();

        Calendar now = Calendar.getInstance();
        File f = getActivity().getFileStreamPath(SAVED_PEOPLE_PATH);

        if((f.lastModified() + (PEOPLE_REPLACE_INTERVAL * 60 * 1000)) < now.getTimeInMillis())
        {
            if(people == null)
            {
                people = new ArrayList<Person>();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
                query.whereNotEqualTo("A_Grouping", "City Council");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> result, ParseException e)
                    {
                        FileOutputStream out = null;

                        try
                        {
                            out = getActivity().openFileOutput(SAVED_PEOPLE_PATH, Context.MODE_APPEND);
                        }
                        catch(Exception ex){}

                        if (e == null)
                        {
                            for(ParseObject parse : result)
                            {
                                Person tmp = new Person();
                                tmp.name = (parse.getString("D_Firstname") + " " + parse.getString("C_Lastname"));
                                tmp.email = (parse.getString("F_Email"));
                                tmp.group_a = (parse.getString("A_Grouping"));
                                tmp.group_b = (parse.getString("B_Grouping"));
                                tmp.title = (parse.getString("E_Position"));
                                tmp.notes = (parse.getString("I_Notes"));
                                tmp.tel_number = (parse.getString("G_Phone"));
                                people.add(tmp);
                                Log.d("people", "Writing: " + tmp.toString());
                                try
                                {
                                    out.write(tmp.toString().getBytes());
                                    out.flush();
                                }
                                catch(Exception ex){}
                            }

                            try
                            {
                                out.close();
                            }
                            catch(Exception ex){}

                            final Animation in = new AlphaAnimation(0.0f, 1.0f);
                            in.setDuration(1000);
                            PeopleListAdapter adapter = new PeopleListAdapter(view.getContext(), people);
                            ListView peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                            peopleList.setAdapter(adapter);
                            peopleList.setOnItemClickListener(new PeopleItemClickListener());
                            peopleList.startAnimation(in);

                        } else
                        {
                            Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                //TextView peopleHeader = (TextView) view.findViewById(R.id.people_header);
                //Typeface avenirBlack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Avenir LT 95 Black.ttf");
                //peopleHeader.setTypeface(avenirBlack);
            }
            else
            {
                PeopleListAdapter adapter = new PeopleListAdapter(view.getContext(), people);
                ListView peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                peopleList.setAdapter(adapter);
                peopleList.setOnItemClickListener(new PeopleItemClickListener());
            }
        }
        else
        {
            try
            {
                if(people == null)
                {
                    people = new ArrayList<Person>();
                }

                FileInputStream in = getActivity().openFileInput(SAVED_PEOPLE_PATH);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    String personData[] = line.split(",");
                    Person tmp = new Person();
                    tmp.name = personData[0];
                    tmp.title = personData[1];
                    tmp.group_a = personData[2];
                    tmp.group_b = personData[3];
                    tmp.email = personData[4];
                    tmp.tel_number = personData[5];
                    tmp.notes = personData[6];
                    people.add(tmp);
                }

                in.close();
                reader.close();
            }
            catch(Exception e){}

            for(Person p : people)
            {
                Log.d("people", p.toString());
            }

            PeopleListAdapter adapter = new PeopleListAdapter(view.getContext(), people);
            ListView peopleList = (ListView) getActivity().findViewById(R.id.people_list);
            peopleList.setAdapter(adapter);
            peopleList.setOnItemClickListener(new PeopleItemClickListener());
        }



	}
	
	public void onDestroy()
	{
		super.onDestroy();
		people = null;
	}
	
	private void selectItem(int position)
	{
		PeopleDetailFragment fragment = new PeopleDetailFragment();
		fragment.setPerson(people.get(position));
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.addToBackStack(null);
	    fragmentTransaction.commit();
	}
	
	private class PeopleItemClickListener implements ListView.OnItemClickListener 
	{
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) 
	    {
	        selectItem(position);
	    }
	}
	
}
