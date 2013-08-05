package com.cellaflora.muni;

import com.parse.ParseObject;

/**
 * Created by sdickson on 8/4/13.
 */
public class Poll
{
    public String objectId, option_A, option_B, option_C, question;
    public long option_A_results, option_B_results, option_C_results;
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
                parse.increment(selected_key);
                parse.saveInBackground();
            }
        }
    }
}
