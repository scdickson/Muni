package com.cellaflora.muni.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.objects.Poll;
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
    ScrollView pollOptionsView;
    LinearLayout pollResultsView;
    TextView txtOptionA, txtOptionB, txtOptionC, txtPollExternal;
    Button btnPollA, btnPollB, btnPollC;
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

    public int getPercent(int option)
    {
        long total = poll.option_A_results + poll.option_B_results + poll.option_C_results;
        int percent = 0;

        switch(option)
        {
            case 1:
                //percent = (int)(((float) poll.option_A_results / (float) total) * 100.0);
                percent = (int) poll.option_A_results;
                break;
            case 2:
                //percent = (int)(((float) poll.option_B_results / (float) total) * 100.0);
                percent = (int) poll.option_B_results;
                break;
            case 3:
                //percent = (int)(((float) poll.option_C_results / (float) total) * 100.0);
                percent = (int) poll.option_C_results;
                break;
        }

        return percent;
    }

    public void onResume()
    {
        super.onResume();
        TextView txtPoll = (TextView) view.findViewById(R.id.poll_question);
        txtPollExternal = (TextView) view.findViewById(R.id.poll_question_external);
        txtPollExternal.setTypeface(MainActivity.myriadProSemiBold);

        txtOptionA = (TextView) view.findViewById(R.id.poll_option_A_text);
        txtOptionA.setTypeface(MainActivity.myriadProRegular);

        txtOptionB = (TextView) view.findViewById(R.id.poll_option_B_text);
        txtOptionB.setTypeface(MainActivity.myriadProRegular);

        txtOptionC = (TextView) view.findViewById(R.id.poll_option_C_text);
        txtOptionC.setTypeface(MainActivity.myriadProRegular);

        btnPollA = (Button) view.findViewById(R.id.poll_option_a);
        btnPollA.setTypeface(MainActivity.myriadProSemiBold);

        btnPollB = (Button) view.findViewById(R.id.poll_option_b);
        btnPollB.setTypeface(MainActivity.myriadProSemiBold);

        btnPollC = (Button) view.findViewById(R.id.poll_option_c);
        btnPollC.setTypeface(MainActivity.myriadProSemiBold);

        pollOptionsView = (ScrollView) view.findViewById(R.id.poll_option_view);
        pollResultsView = (LinearLayout) view.findViewById(R.id.poll_results_layout);
        points = new ArrayList<Bar>();

        txtPoll.setText(poll.question);
        txtPoll.setTypeface(MainActivity.myriadProRegular);
        txtPollExternal.setText(poll.question);

        if(poll.completed)
        {
            if(poll.option_A != null)
            {
                txtOptionA.setText(poll.option_A + "\n" + getPercent(1));
                Bar optionA = new Bar();
                optionA.setColor(Color.parseColor(MuniConstants.OPTION_A_COLOR));
                optionA.setValue(poll.option_A_results);
                optionA.setName("OPTION_A");
                points.add(optionA);
            }
            else
            {
                txtOptionA.setVisibility(View.GONE);
            }

            if(poll.option_B != null)
            {
                txtOptionB.setText(poll.option_B + "\n" + getPercent(2));
                Bar optionB = new Bar();
                optionB.setColor(Color.parseColor(MuniConstants.OPTION_B_COLOR));
                optionB.setValue(poll.option_B_results);
                optionB.setName("OPTION_B");
                points.add(optionB);
            }
            else
            {
                txtOptionB.setVisibility(View.GONE);
            }

            if(poll.option_C != null)
            {
                txtOptionC.setText(poll.option_C + "\n" + getPercent(3));
                Bar optionC = new Bar();
                optionC.setColor(Color.parseColor(MuniConstants.OPTION_C_COLOR));
                optionC.setValue(poll.option_C_results);
                optionC.setName("OPTION_C");
                points.add(optionC);
            }
            else
            {
                txtOptionC.setVisibility(View.GONE);
            }

            displayPollResults(false);
        }
        else
        {
            if(poll.option_A != null)
            {
                btnPollA.setText(poll.option_A);
                btnPollA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!poll.completed)
                        {
                            poll.option_A_results++;
                            pollCompleted(1);
                        }
                    }
                });
                txtOptionA.setText(poll.option_A);

            }
            else
            {
                btnPollA.setVisibility(View.GONE);
                txtOptionA.setVisibility(View.GONE);
            }

            if(poll.option_B != null)
            {
                btnPollB.setText(poll.option_B);
                btnPollB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!poll.completed)
                        {
                            poll.option_B_results++;
                            pollCompleted(2);
                        }
                    }
                });
                txtOptionB.setText(poll.option_B);

            }
            else
            {
                btnPollB.setVisibility(View.GONE);
                txtOptionB.setVisibility(View.GONE);
            }

            if(poll.option_C != null)
            {
                btnPollC.setText(poll.option_C);
                btnPollC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!poll.completed)
                        {
                            poll.option_C_results++;
                            pollCompleted(3);
                        }
                    }
                });
                txtOptionC.setText(poll.option_C);

            }
            else
            {
                btnPollC.setVisibility(View.GONE);
                txtOptionC.setVisibility(View.GONE);
            }
        }
    }

    public void displayPollResults(boolean animate)
    {
        if(pollOptionsView != null && pollResultsView != null)
        {
            g = (BarGraph) view.findViewById(R.id.poll_graph);
            g.setShowBarText(false);
            g.setBars(points);
            txtPollExternal.setVisibility(View.VISIBLE);

            final Animation in = new AlphaAnimation(0.0f, 1.0f);
            in.setDuration(1000);
            pollOptionsView.setVisibility(View.GONE);
            pollResultsView.setVisibility(View.VISIBLE);
            if(animate)
            {
                pollResultsView.startAnimation(in);
            }
        }
    }

    public void pollCompleted(int option)
    {
        poll.increment(option);
        pf.savePollState();

        if(poll.option_A != null)
        {
            txtOptionA.setText(poll.option_A + "\n" + getPercent(1) + "%");
            Bar optionA = new Bar();
            optionA.setColor(Color.parseColor(MuniConstants.OPTION_A_COLOR));
            optionA.setValue(poll.option_A_results);
            optionA.setName("OPTION_A");
            points.add(optionA);
        }

        if(poll.option_B != null)
        {
            txtOptionB.setText(poll.option_B + "\n" + getPercent(2) + "%");
            Bar optionB = new Bar();
            optionB.setColor(Color.parseColor(MuniConstants.OPTION_B_COLOR));
            optionB.setValue(poll.option_B_results);
            optionB.setName("OPTION_B");
            points.add(optionB);
        }

        if(poll.option_C != null)
        {
            txtOptionC.setText(poll.option_C + "\n" + getPercent(3) + "%");
            Bar optionC = new Bar();
            optionC.setColor(Color.parseColor(MuniConstants.OPTION_C_COLOR));
            optionC.setValue(poll.option_C_results);
            optionC.setName("OPTION_C");
            points.add(optionC);
        }

        displayPollResults(true);
    }

}
