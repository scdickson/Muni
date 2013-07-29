package com.cellaflora.muni.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.adapters.PeopleListAdapter;
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
    private ProgressDialog progressDialog;

    private static final String SAVED_PEOPLE_PATH = "muni_saved_people"; //Name of saved people file
    private static final int PEOPLE_REPLACE_INTERVAL = 60; //In minutes!

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_fragment, container, false);
        MainActivity.actionbarTitle.setText("People");
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

    public void loadPeople()
    {
        people = new ArrayList<Person>();
        groups = new ArrayList<PersonGroup>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if (e == null)
                {
                    for(ParseObject parse : result)
                    {
                        Person tmp = new Person();
                        tmp.objectId = (parse.getObjectId());
                        tmp.name = (parse.getString("D_Firstname") + " " + parse.getString("C_Lastname"));
                        tmp.email = (parse.getString("F_Email"));
                        tmp.group_a = (parse.getString("A_Grouping"));
                        tmp.group_b = (parse.getString("B_Subgrouping"));
                        tmp.title = (parse.getString("E_Position"));
                        tmp.notes = (parse.getString("I_Notes"));
                        tmp.tel_number = (parse.getString("G_Phone"));
                        ParseFile file = (ParseFile) parse.get("H_Photo");

                        if(file != null && file.getUrl() != null)
                        {
                            tmp.url = file.getUrl();
                        }

                        if(tmp.group_a == null)
                            tmp.group_a = " ";
                        if(tmp.group_b == null)
                            tmp.group_b = " ";

                        populateGroup(tmp);
                    }

                    try
                    {
                        PersistenceManager.writeObject(getActivity().getApplicationContext(), SAVED_PEOPLE_PATH, groups);
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    progressDialog.dismiss();
                    adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                    peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                    peopleList.setAdapter(adapter);
                    peopleList.setOnItemClickListener(new PeopleItemClickListener());

                }
                else
                {
                    e.printStackTrace();
                    //Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
                    try
                    {
                        groups = (ArrayList<PersonGroup>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_PEOPLE_PATH);
                        progressDialog.dismiss();
                        adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                        peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                        peopleList.setAdapter(adapter);
                        peopleList.setOnItemClickListener(new PeopleItemClickListener());
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

	public void onResume()
	{
		super.onResume();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        try
        {
            File f = getActivity().getFileStreamPath(SAVED_PEOPLE_PATH);
            if((f.lastModified() + (PEOPLE_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
            {
                groups = (ArrayList<PersonGroup>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_PEOPLE_PATH);
                adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                peopleList = (ListView) getActivity().findViewById(R.id.people_list);
                peopleList.setAdapter(adapter);
                peopleList.setOnItemClickListener(new PeopleItemClickListener());
            }
            else
            {
                loadPeople();
            }
        }
        catch(Exception e)
        {
            loadPeople();
        }

	}

	private void selectItem(int position)
	{
        Object tmp = adapter.content.get(position);

        if(tmp.getClass().equals(Person.class))
        {
            PeopleDetailFragment fragment = new PeopleDetailFragment((Person) tmp);
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
