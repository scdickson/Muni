package com.cellaflora.muni.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.Poll;
import com.cellaflora.muni.R;
import com.cellaflora.muni.graph.Bar;
import com.cellaflora.muni.graph.BarGraph;

import java.util.ArrayList;

/**
 * Created by sdickson on 8/5/13.
 */
public class PollingPage extends Fragment
{
    View view;
    Poll poll;
    long total;
    PollingFragment pf;
    TextView txtOptionA, txtOptionB, txtOptionC;
    ArrayList<Bar> points;
    BarGraph g;

    public PollingPage(Poll poll, PollingFragment pf)
    {
        this.poll = poll;
        this.pf = pf;
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
        TextView txtPoll = (TextView) view.findViewById(R.id.poll_question);
        txtOptionA = (TextView) view.findViewById(R.id.poll_option_A_text);
        txtOptionB = (TextView) view.findViewById(R.id.poll_option_B_text);
        txtOptionC = (TextView) view.findViewById(R.id.poll_option_C_text);

        if(poll.completed)
        {
            if(txtOptionA != null)
            {
                if(poll.selected_option != 1)
                {
                    txtOptionA.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            if(txtOptionB != null)
            {
                if(poll.selected_option != 2)
                {
                    txtOptionB.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            if(txtOptionC != null)
            {
                if(poll.selected_option != 3)
                {
                    txtOptionC.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }

        txtPoll.setText(poll.question);

        points = new ArrayList<Bar>();

        if(poll.option_A != null)
        {
            txtOptionA.setText(poll.option_A);
            txtOptionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!poll.completed)
                    {
                        pollCompleted(1);
                    }
                }
            });
            Bar tmp = new Bar();
            tmp.setColor(Color.parseColor("#99CC00"));
            tmp.setValue(poll.option_A_results);
            tmp.setName("OPTION_A");
            tmp.option = 1;
            points.add(tmp);
        }
        else
        {
            txtOptionA.setVisibility(View.GONE);
        }

        if(poll.option_B != null)
        {
            txtOptionB.setText(poll.option_B);
            txtOptionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!poll.completed)
                    {
                        pollCompleted(2);
                    }
                }
            });
            Bar tmp = new Bar();
            tmp.setColor(Color.parseColor("#FFBB33"));
            tmp.setValue(poll.option_B_results);
            tmp.setName("OPTION_B");
            tmp.option = 2;
            points.add(tmp);
        }
        else
        {
            txtOptionB.setVisibility(View.GONE);
        }

        if(poll.option_C != null)
        {
            txtOptionC.setText(poll.option_C);
            txtOptionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!poll.completed)
                    {
                        pollCompleted(3);
                    }
                }
            });
            Bar tmp = new Bar();
            tmp.setColor(Color.parseColor("#007ab7"));
            tmp.setName("OPTION_C");
            tmp.option = 3;
            tmp.setValue(poll.option_C_results);
            points.add(tmp);
        }
        else
        {
            txtOptionC.setVisibility(View.GONE);
        }

        g = (BarGraph) view.findViewById(R.id.poll_graph);
        g.setShowBarText(false);
        g.setBars(points);

    }

    public void pollCompleted(int option)
    {
        for(Bar b : points)
        {
            if(b.option == option)
            {
                b.setValue(b.getValue() + 1);
                g.invalidate();
                g.setBars(points);
                break;
            }
        }

        poll.increment(option);
        pf.savePollState();

        if(txtOptionA != null && poll.selected_option != 1)
        {
            txtOptionA.setBackgroundColor(Color.TRANSPARENT);
            //txtOptionA.startAnimation(in);
        }

        if(txtOptionB != null && poll.selected_option != 2)
        {
            txtOptionB.setBackgroundColor(Color.TRANSPARENT);
            //txtOptionB.startAnimation(in);
        }

        if(txtOptionC != null && poll.selected_option != 3)
        {
            txtOptionC.setBackgroundColor(Color.TRANSPARENT);
            //txtOptionC.startAnimation(in);
        }

    }

}
