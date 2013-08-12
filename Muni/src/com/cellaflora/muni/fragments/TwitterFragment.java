package com.cellaflora.muni.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.MuniJSONParser;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;
import com.cellaflora.muni.Tweet;
import com.cellaflora.muni.adapters.TwitterListAdapter;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

public class TwitterFragment extends Fragment
{
    View view;
    Twitter twitter;
    Handler handler;
    ListView twitterList;
    TwitterListAdapter adapter;
    private ProgressDialog progressDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        view = inflater.inflate(R.layout.twitter_fragment, container, false);
        handler = new twitterLogInHandler();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");
        MainActivity.actionbarTitle.setTextSize(25);
        MainActivity.actionbarTitle.setText(Html.fromHtml("<font color=\"#EC4B43\">@</font>" + MuniConstants.TWITTER_NAME));
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null)
        {
            twitter = ParseTwitterUtils.getTwitter();
            handler.sendEmptyMessage(0);
        }
        else
        {
            ParseTwitterUtils.logIn(view.getContext(), new LogInCallback()
            {
                @Override
                public void done(ParseUser user, ParseException err)
                {
                    if (user == null)
                    {
                        HomeFragment fragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    } else if (user.isNew())
                    {
                        if (!ParseTwitterUtils.isLinked(user))
                        {
                            ParseTwitterUtils.link(user, view.getContext(), new SaveCallback()
                            {
                                @Override
                                public void done(ParseException ex)
                                {}
                            });
                        }
                        twitter = ParseTwitterUtils.getTwitter();
                        handler.sendEmptyMessage(0);
                    } else
                    {
                        twitter = ParseTwitterUtils.getTwitter();
                        handler.sendEmptyMessage(0);
                    }
                }
            });
        }
		return view;
	}

    public void onResume()
    {
        super.onResume();

    }

    private class twitterLogInHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            new loadTwitter().execute();
        }
    }

    private class loadTwitter extends AsyncTask<Object, Integer, Void>
    {
        ArrayList<Tweet> tweets;

        protected void onPreExecute()
        {
            progressDialog.show();
        }

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpGet verifyGet = new HttpGet(MuniConstants.TWITTER_URL + MuniConstants.TWITTER_NAME);
                twitter.signRequest(verifyGet);
                HttpResponse response = client.execute(verifyGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                MuniJSONParser parser = new MuniJSONParser(body);
                tweets = parser.parseTweet();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if(tweets != null)
            {
                adapter = new TwitterListAdapter(view.getContext(), tweets);
                twitterList = (ListView) getActivity().findViewById(R.id.twitter_list);
                twitterList.setAdapter(adapter);
            }

            progressDialog.dismiss();
        }
    }
}
