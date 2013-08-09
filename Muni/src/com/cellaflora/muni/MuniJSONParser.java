package com.cellaflora.muni;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sdickson on 8/8/13.
 */
public class MuniJSONParser
{
    org.json.simple.parser.JSONParser parser;
    String data;

    public MuniJSONParser(String data)
    {
        parser = new org.json.simple.parser.JSONParser();
        this.data = data;
    }

    private void extractURL(Tweet t)
    {
        String url = null;
        String text = t.content;

        if(text != null)
        {
            if(text.contains("http://"))
            {
                int startIndex = text.indexOf("http://");
                t.urlIndices[0] = startIndex;
                int endIndex = text.length();
                for(int i = startIndex; i < text.length(); i++)
                {
                    if(text.charAt(i) == ' ')
                    {
                        endIndex = ++i;
                    }
                    i++;
                }
                t.urlIndices[1] = endIndex;
                url = text.substring(startIndex, endIndex);
            }
        }

        t.url = url;
    }


    public ArrayList<Tweet> parseTweet()
    {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        try
        {
            Object obj= JSONValue.parse(data);
            JSONArray array = (JSONArray) obj;

            for(int i = 0; i < array.size(); i++)
            {
                Tweet tmp = new Tweet();
                JSONObject tweet = (JSONObject) array.get(i);
                tmp.content = tweet.get("text").toString();
                extractURL(tmp);
                tmp.time = tweet.get("created_at").toString();
                tweets.add(tmp);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return tweets;
    }
}
