package com.cellaflora.muni.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.Poll;
import com.cellaflora.muni.R;

/**
 * Created by sdickson on 8/5/13.
 */
public class PollingPage extends Fragment
{
    View view;
    Poll poll;

    public PollingPage(Poll poll)
    {
        this.poll = poll;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.polling_page, container, false);
        MainActivity.actionbarTitle.setText("Polls");
        return view;
    }

    public void onResume()
    {
        super.onResume();
        TextView txtPoll = (TextView) view.findViewById(R.id.poll_text);
        txtPoll.setText(poll.question);
    }

}
