package com.cellaflora.muni.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_fragment, container, false);
		return view;
	}
	
	public void onResume()
	{
		super.onResume();
		
		if(people == null)
		{
			people = new ArrayList<Person>();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
			query.whereNotEqualTo("A_Grouping", "City Council");
			query.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> result, ParseException e) 
			    {
			        if (e == null) 
			        {
			           for(ParseObject parse : result)
			           {
			        	   Person tmp = new Person(parse.getString("D_Firstname") + " " + parse.getString("C_Lastname"));
			        	   tmp.setEmail(parse.getString("F_Email"));
			        	   tmp.setGroupA(parse.getString("A_Grouping"));
			        	   tmp.setGroupB(parse.getString("B_Grouping"));
			        	   tmp.setTitle(parse.getString("E_Position"));
			        	   tmp.setNotes(parse.getString("I_Notes"));
			        	   tmp.setTelNumber(parse.getString("G_Phone"));
			        	   people.add(tmp);
			           }
			           
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
