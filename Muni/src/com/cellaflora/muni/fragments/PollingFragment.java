package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.Poll;
import com.cellaflora.muni.PollingPageAdapter;
import com.cellaflora.muni.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class PollingFragment extends Fragment
{
    View view;
    public static ArrayList<Poll> polls;
    ArrayList<String> completedPolls;
    private ProgressDialog progressDialog;

    public static final String SAVED_POLLS_PATH = "muni_saved_polls";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.polling_fragment, container, false);
        MainActivity.actionbarTitle.setText("Polls");
        return view;
    }

    public void loadPolls()
    {
        polls = new ArrayList<Poll>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Polls");
        query.addDescendingOrder("createdAt");
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if (e == null)
                {
                    for(ParseObject parse : result)
                    {
                        Poll tmp = new Poll();
                        tmp.objectId = parse.getObjectId();
                        tmp.option_A = parse.getString("Opt1_Title");
                        tmp.option_B = parse.getString("Opt2_Title");
                        tmp.option_C = parse.getString("Opt3_Title");
                        tmp.question = parse.getString("Question");
                        tmp.option_A_results = parse.getLong("Opt1_Results");
                        tmp.option_B_results = parse.getLong("Opt2_Results");
                        tmp.option_C_results = parse.getLong("Opt3_Results");
                        tmp.parse = parse;
                        polls.add(tmp);
                    }
                }

                ViewPager pager=(ViewPager) view.findViewById(R.id.poll_pager);
                pager.setAdapter(buildAdapter());
                progressDialog.dismiss();
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
            completedPolls = (ArrayList<String>) PersistenceManager.readObject(view.getContext(), SAVED_POLLS_PATH);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            completedPolls = new ArrayList<String>();
        }

        loadPolls();
    }

    private PagerAdapter buildAdapter()
    {
        return (new PollingPageAdapter(getActivity(), getChildFragmentManager()));
    }
}
