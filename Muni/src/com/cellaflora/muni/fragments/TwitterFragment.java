package com.cellaflora.muni.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;

public class TwitterFragment extends Fragment
{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.twitter_fragment, container, false);
        MainActivity.actionbarTitle.setText("@WestLafayetteIN");
		return view;
	}
}
