package com.cellaflora.muni.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellaflora.muni.R;

/**
 * Created by sdickson on 7/4/13.
 */
public class PlaceDetailFragment extends Fragment
{
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.place_detail_fragment, container, false);
        return view;
    }
}
