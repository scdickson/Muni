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
import com.cellaflora.muni.PersonGroup;
import com.cellaflora.muni.R;
import com.parse.*;

public class PeopleFragment extends Fragment
{
	View view;
    ArrayList<PersonGroup> groups = null;
	ArrayList<Person> people = null;
    public PeopleListAdapter adapter = null;
    public ListView peopleList = null;

    private static final String SAVED_PEOPLE_PATH = "com.cellaflora.muni.saved_people"; //Name of saved people file
    private static final int PEOPLE_REPLACE_INTERVAL = 1; //In minutes!

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_fragment, container, false);
		return view;
	}

    public void populateGroup(Person person)
    {
        boolean found_a = false;
        for(PersonGroup group : groups)
        {
            if(person.group_a.equalsIgnoreCase(group.groupName))
            {
                found_a = true;
                boolean found_b = false;
                for(PersonGroup subGroup : group.subGroup)
                {
                    if(person.group_b.equalsIgnoreCase(subGroup.groupName))
                    {
                        subGroup.people.add(person);
                        found_b = true;
                        break;
                    }
                }

                if(!found_b)
                {
                    PersonGroup tmp = new PersonGroup(person.group_b);
                    tmp.people.add(person);
                    group.subGroup.add(tmp);
                }
            }
        }

        if(!found_a)
        {
            groups.add(new PersonGroup(person.group_a));
            populateGroup(person);
        }
    }

	public void onResume()
	{
		super.onResume();

        Calendar now = Calendar.getInstance();
        File f = getActivity().getFileStreamPath(SAVED_PEOPLE_PATH);

        if((f.lastModified() + (PEOPLE_REPLACE_INTERVAL)) < now.getTimeInMillis())
        {

                people = new ArrayList<Person>();
                groups = new ArrayList<PersonGroup>();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
                //query.whereNotEqualTo("A_Grouping", "City Council");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> result, ParseException e)
                    {
                        /*FileOutputStream out = null;

                        try
                        {
                            out = getActivity().openFileOutput(SAVED_PEOPLE_PATH, Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
                        }
                        catch(Exception ex){}*/

                        if (e == null)
                        {
                            for(ParseObject parse : result)
                            {
                                Person tmp = new Person();
                                tmp.objectId = (parse.getString("objectId"));
                                tmp.name = (parse.getString("D_Firstname") + " " + parse.getString("C_Lastname"));
                                tmp.email = (parse.getString("F_Email"));
                                tmp.group_a = (parse.getString("A_Grouping"));
                                tmp.group_b = (parse.getString("B_Subgrouping"));
                                tmp.title = (parse.getString("E_Position"));
                                tmp.notes = (parse.getString("I_Notes"));
                                tmp.tel_number = (parse.getString("G_Phone"));

                                if(tmp.group_a == null)
                                    tmp.group_a = " ";
                                if(tmp.group_b == null)
                                    tmp.group_b = " ";

                                populateGroup(tmp);
                                //people.add(tmp);
                                //Log.d("people", "Writing: " + tmp.toString());
                                /*try
                                {
                                    out.write(tmp.toString().getBytes());
                                    out.flush();
                                }
                                catch(Exception ex)
                                {
                                    Log.d("People", "97: " + ex.getMessage());
                                }*/
                            }

                            /*try
                            {
                                out.close();
                            }
                            catch(Exception ex)
                            {
                                Log.d("People", "107: " + ex.getMessage());
                            }*/

                            final Animation in = new AlphaAnimation(0.0f, 1.0f);
                            in.setDuration(1000);
                            adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                            peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                            peopleList.setAdapter(adapter);
                            peopleList.setOnItemClickListener(new PeopleItemClickListener());
                            peopleList.startAnimation(in);

                        }
                        else
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
                    //tmp.notes = personData[6];
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

            PeopleListAdapter adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
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
        Object tmp = adapter.content.get(position);

        if(tmp.getClass().equals(Person.class))
        {
            PeopleDetailFragment fragment = new PeopleDetailFragment();
            fragment.setPerson(((Person) tmp));
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if(tmp.getClass().equals(String.class))
        {
            adapter.clearContent();

            if(adapter.level == 0)
            {

                adapter.setLevel(1, ((String) tmp), null);
            }
            else if(adapter.level == 1)
            {
                adapter.setLevel(2, adapter.groupA, ((String) tmp));
            }

            adapter.notifyDataSetChanged();
            peopleList.invalidateViews();
        }

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
