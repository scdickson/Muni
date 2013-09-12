package com.cellaflora.muni.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;
import com.cellaflora.muni.objects.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sdickson on 8/8/13.
 */
public class TwitterListAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Tweet> tweets;

    public TwitterListAdapter(Context context, ArrayList<Tweet> tweets)
    {
        this.context = context;
        this.tweets = tweets;
    }

    public int getCount()
    {
        return tweets.size();
    }

    public Object getItem(int position)
    {
        return tweets.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public String[] splitText(Tweet t)
    {
        String split[] = new String[2];
        split[0] = t.content.substring(0, t.urlIndices[0] - 1);
        split[1] = t.content.substring(t.urlIndices[1]);
        return split;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.twitter_list_row, parent, false);
        TextView txtTweet = (TextView) itemView.findViewById(R.id.twitter_tweet);
        txtTweet.setTypeface(MainActivity.myriadProRegular);
        TextView txtTweetTime = (TextView) itemView.findViewById(R.id.twitter_time);
        txtTweetTime.setTypeface(MainActivity.myriadProSemiBold);

        Tweet t = tweets.get(position);

        if(t.content != null)
        {
            if(t.url != null)
            {
                String split[] = splitText(t);
                txtTweet.setText(Html.fromHtml(split[0] + " <font color=\"#EC4B43\">" + t.url + "</font> " + split[1]));
                final String url = t.url;
                txtTweet.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        view.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                txtTweet.setText(t.content);
            }
        }

        if(t.time != null)
        {
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss +0000 yyyy");
            SimpleDateFormat month_day = new SimpleDateFormat("MMM dd");
            try
            {
                txtTweetTime.setText(month_day.format(format.parse(t.time)));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        return itemView;
    }

}
