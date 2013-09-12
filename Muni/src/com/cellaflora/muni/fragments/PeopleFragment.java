package com.cellaflora.muni.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import support.NetworkManager;
import support.PersistenceManager;
import support.PullToRefreshListView;
import com.cellaflora.muni.adapters.PeopleListAdapter;
import com.cellaflora.muni.objects.Person;
import com.cellaflora.muni.objects.PersonGroup;
import com.cellaflora.muni.R;
import com.parse.*;

public class PeopleFragment extends Fragment
{
	View view;
    ArrayList<PersonGroup> groups = null;
	ArrayList<Person> people = null;
    public PeopleListAdapter adapter = null;
    public PullToRefreshListView peopleList = null;
    private ProgressDialog progressDialog;
    Parcelable state;
    ArrayList<Object> searchResults;
    public int level = 0;
    public EditText searchBar;
    public TextView searchCancel;
    InputMethodManager imm;
    public String groupA = " ";
    public String groupB = null;
    NetworkManager networkManager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.people_fragment, container, false);
        networkManager = new NetworkManager(view.getContext(), getActivity(), getFragmentManager());
        MainActivity.actionbarTitle.setText("People");
        searchBar = (EditText) view.findViewById(R.id.people_search);
        searchBar.setTypeface(MainActivity.myriadProRegular);
        searchCancel = (TextView) view.findViewById(R.id.people_search_cancel);
        searchCancel.setTypeface(MainActivity.myriadProRegular);
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                adapter.clearContent();
                adapter.setLevel(level, groupA, groupB);
                adapter.notifyDataSetChanged();
                peopleList.invalidateViews();
                imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchBar.setText("");
                searchCancel.setVisibility(View.GONE);
                peopleList.requestFocus();
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i2, int i3)
            {
                searchResults = new ArrayList<Object>();

                if(cs != null && !cs.toString().isEmpty())
                {
                    searchCancel.setVisibility(View.VISIBLE);
                    if(level == 0)
                    {
                        for(Person person : people)
                        {
                            if(person.name.toUpperCase().contains(cs.toString().toUpperCase()))
                            {
                                searchResults.add(person);
                            }
                        }
                    }
                    else if(level == 1)
                    {
                        for(Person person : people)
                        {
                            if(person.group_a.equals(groupA))
                            {
                                if(person.name.toUpperCase().contains(cs.toString().toUpperCase()))
                                {
                                    searchResults.add(person);
                                }
                            }
                        }
                    }
                    else if(level == 2)
                    {
                        for(Person person : people)
                        {
                            if(person.group_a.equals(groupA) && person.group_b.equals(groupB))
                            {
                                if(person.name.toUpperCase().contains(cs.toString().toUpperCase()))
                                {
                                    searchResults.add(person);
                                }
                            }
                        }
                    }

                    adapter.clearContent();
                    adapter.setContent(searchResults);
                    adapter.notifyDataSetChanged();
                    peopleList.invalidateViews();

                }
                else
                {
                    adapter.clearContent();
                    adapter.setLevel(level, groupA, groupB);
                    adapter.notifyDataSetChanged();
                    peopleList.invalidateViews();
                    searchCancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

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

    public void onPause()
    {
        super.onPause();

        if(peopleList != null)
        {
            state = peopleList.onSaveInstanceState();
        }
    }

    public void loadPeople()
    {
        people = new ArrayList<Person>();
        groups = new ArrayList<PersonGroup>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("People");

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
                        ParseFile file = parse.getParseFile("H_Photo");

                        if(file != null && file.getUrl() != null)
                        {
                            tmp.url = file.getUrl();
                        }

                        if(tmp.group_a == null || tmp.group_a.isEmpty())
                            tmp.group_a = " ";
                        if(tmp.group_b == null || tmp.group_b.isEmpty())
                            tmp.group_b = " ";

                        people.add(tmp);
                        populateGroup(tmp);
                    }

                    try
                    {
                        PersistenceManager.writeObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PATH, groups);
                        PersistenceManager.writeObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PERSON_PATH, people);
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }

                    adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                    peopleList = (PullToRefreshListView) getActivity().findViewById(R.id.people_list);
                    peopleList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            loadPeople();
                        }
                    });
                    peopleList.onRefreshComplete();
                    peopleList.setAdapter(adapter);
                    peopleList.setOnItemClickListener(new PeopleItemClickListener());

                }
                else
                {
                    e.printStackTrace();
                    //Toast.makeText(getActivity().getApplicationContext(), "Error loading content--Please check your network connection.", Toast.LENGTH_LONG).show();
                    try
                    {
                        groups = (ArrayList<PersonGroup>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PATH);
                        if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }
                        adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                        peopleList = (PullToRefreshListView) getActivity().findViewById(R.id.people_list);
                        peopleList.setAdapter(adapter);
                        peopleList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadPeople();
                            }
                        });
                        peopleList.onRefreshComplete();
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

        if(state != null)
        {
                adapter = new PeopleListAdapter(view.getContext(), groups, level, groupA, groupB);
                if(level == 0)
                {
                    searchBar.setHint("Search all people");
                }
                else if(level == 1)
                {
                    searchBar.setHint("Search in " + groupA);
                }
                else if(level == 2)
                {
                    searchBar.setHint("Search in " + groupB);
                }
                peopleList = (PullToRefreshListView) getActivity().findViewById(R.id.people_list);
                peopleList.setAdapter(adapter);
                peopleList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadPeople();
                    }
                });
                peopleList.setOnItemClickListener(new PeopleItemClickListener());
                peopleList.onRestoreInstanceState(state);
        }
        else
        {
            if(networkManager.isNetworkConnected())
            {
                try
                {
                    File f = getActivity().getFileStreamPath(MuniConstants.SAVED_PEOPLE_PATH);
                    if((f.lastModified() + (MuniConstants.PEOPLE_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                    {
                        groups = (ArrayList<PersonGroup>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PATH);
                        people = (ArrayList<Person>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PERSON_PATH);
                        adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                        peopleList = (PullToRefreshListView) getActivity().findViewById(R.id.people_list);
                        peopleList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadPeople();
                            }
                        });
                        peopleList.setAdapter(adapter);
                        peopleList.setOnItemClickListener(new PeopleItemClickListener());
                    }
                    else
                    {
                        progressDialog.show();
                        loadPeople();
                    }
                }
                catch(Exception e)
                {
                    progressDialog.show();
                    loadPeople();
                }
            }
            else
            {
                try
                {
                        groups = (ArrayList<PersonGroup>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PATH);
                        people = (ArrayList<Person>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_PEOPLE_PERSON_PATH);
                        adapter = new PeopleListAdapter(view.getContext(), groups, 0, " ", null);
                        peopleList = (PullToRefreshListView) getActivity().findViewById(R.id.people_list);
                        peopleList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadPeople();
                            }
                        });
                        peopleList.setAdapter(adapter);
                        peopleList.setOnItemClickListener(new PeopleItemClickListener());
                }
                catch(Exception e)
                {
                    networkManager.showNoCacheErrorDialog();
                }
            }
        }

	}

	private void selectItem(int position)
	{
        Object tmp = adapter.content.get(position);

        if(tmp.getClass().equals(Person.class))
        {
            imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            PeopleDetailFragment fragment = new PeopleDetailFragment((Person) tmp);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if(tmp.getClass().equals(String.class))
        {
            adapter.clearContent();

            if(adapter.level == 0)
            {
                level = 1;
                groupA = (String) tmp;
                groupB = null;
                searchBar.setHint("Search in " + groupA);
                adapter.setLevel(level, groupA, groupB);
            }
            else if(adapter.level == 1)
            {
                level = 2;
                groupA = adapter.groupA;
                groupB = (String) tmp;
                searchBar.setHint("Search in " + groupB);
                adapter.setLevel(level, groupA, groupB);
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
