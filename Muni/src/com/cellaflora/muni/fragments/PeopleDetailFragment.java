package com.cellaflora.muni.fragments;

import com.cellaflora.muni.Person;
import com.cellaflora.muni.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
		name.setText(requested.name + "\n" + requested.title);
	}
}
