package com.cellaflora.muni.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;

public class DocumentFragment extends Fragment
{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.document_fragment, container, false);
        MainActivity.actionbarTitle.setText("Documents");
		return view;
	}
}
