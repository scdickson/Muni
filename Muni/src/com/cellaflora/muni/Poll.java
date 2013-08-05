package com.cellaflora.muni;

import com.cellaflora.muni.fragments.PollingFragment;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by sdickson on 8/4/13.
 */
public class Poll
{
    public String objectId, option_A, option_B, option_C, question;
    public boolean completed = false;
    public int selected_option = -1;
    public long option_A_results = 0, option_B_results = 0, option_C_results = 0;
    public ParseObject parse;

    public void increment(int option)
    {
        if(parse != null)
        {
            String selected_key = null;
            switch(option)
            {
                case 1:
                    selected_key = "Opt1_Results";
                    break;
                case 2:
                    selected_key = "Opt2_Results";
                    break;
                case 3:
                    selected_key = "Opt3_Results";
                    break;
            }

            if(selected_key != null)
            {
                selected_option = option;
                completed = true;
                setPollFinished();
                parse.increment(selected_key);
                parse.saveInBackground();
            }
        }
    }

    public boolean equals(Object o)
    {
        boolean result = false;

        try
        {
            Poll other = (Poll) o;
            if(other.objectId.equals(objectId))
            {
                result = true;
            }
        }
        catch(Exception e){}

        return result;
    }

    public void setPollFinished()
    {
        try
        {
            if(PollingFragment.completedPolls != null)
            {
                String values[] = {objectId, selected_option + ""};
                PollingFragment.completedPolls.add(values);
            }
        }
        catch(Exception e)
        {

        }
    }
}
