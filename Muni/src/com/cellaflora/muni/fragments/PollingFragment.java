package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import support.CirclePageIndicator;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import support.NetworkManager;
import support.PersistenceManager;
import support.PollingOptionDialog;

import com.cellaflora.muni.objects.Poll;
import com.cellaflora.muni.adapters.PollingPageAdapter;
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
    PollingFragment pollingFragment;
    public static ArrayList<Poll> polls;
    public static ArrayList<String[]> completedPolls;
    private ProgressDialog progressDialog;
    NetworkManager networkManager;
    TextView noPolls;
    ImageView pollOptions;
    ViewPager pollPager;
    PagerAdapter pagerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.polling_fragment, container, false);
        networkManager = new NetworkManager(view.getContext(), getActivity(), getFragmentManager());
        MainActivity.actionbarTitle.setText("Polls");
        pollPager = (ViewPager) view.findViewById(R.id.poll_pager);
        noPolls = (TextView) view.findViewById(R.id.polls_none);
        noPolls.setTypeface(MainActivity.myriadProSemiBold);
        pollOptions = (ImageView) view.findViewById(R.id.poll_options);
        pollOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), PollingOptionDialog.class);
                if(intent != null)
                {
                    //startActivity(intent);
                }
            }
        });
        pollingFragment = this;
        return view;
    }

    public void loadPolls()
    {
        polls = new ArrayList<Poll>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Polls");
        query.addDescendingOrder("createdAt");
        query.setLimit(MuniConstants.MAX_RECENT_POLLS);
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

                try
                {
                    completedPolls = (ArrayList<String[]>) PersistenceManager.readObject(view.getContext(), MuniConstants.SAVED_POLLS_PATH);
                    if(completedPolls != null)
                    {
                        for(String[] data : completedPolls)
                        {
                            for(Poll p : polls)
                            {
                                if(data[0].equals(p.objectId))
                                {
                                    p.completed = true;
                                    p.selected_option = Integer.parseInt(data[1]);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    completedPolls = new ArrayList<String[]>();
                }

                pollPager = (ViewPager) view.findViewById(R.id.poll_pager);
                ViewPager pager=(ViewPager) view.findViewById(R.id.poll_pager);
                pagerAdapter = buildAdapter();
                pager.setAdapter(pagerAdapter);
                CirclePageIndicator titleIndicator = (CirclePageIndicator) view.findViewById(R.id.poll_indicator);
                titleIndicator.setViewPager(pager);
                progressDialog.dismiss();
            }
        });
    }

    public static void setLivePolling(boolean livePollOn)
    {
        if(livePollOn)
        {

        }
        else
        {

        }
    }

    public void onPause()
    {
        super.onPause();

    }

    public void onResume()
    {
        super.onResume();
        MainActivity.mMenuAdapter.setSelected(7);
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        if(networkManager.isNetworkConnected())
        {
            loadPolls();
        }
        else
        {
            networkManager.showNoCacheErrorDialog();
        }
    }

    private PagerAdapter buildAdapter()
    {
        return (new PollingPageAdapter(getActivity(), getChildFragmentManager(), this));
    }

    public void savePollState()
    {
        try
        {
            if(completedPolls != null)
            {
                PersistenceManager.writeObject(view.getContext(), MuniConstants.SAVED_POLLS_PATH, completedPolls);
            }
        }
        catch(Exception e)
        {

        }
    }
}
