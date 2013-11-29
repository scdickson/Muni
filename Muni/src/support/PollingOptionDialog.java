package support;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cellaflora.muni.R;
import com.cellaflora.muni.fragments.PollingFragment;

/**
 * Created by sdickson on 11/27/13.
 */
public class PollingOptionDialog extends Activity
{
    SharedPreferences pollOptionPrefs;
    SharedPreferences.Editor pollOptionEditor;
    Switch livePolling;
    Button savePreferences;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polling_option_dialog);
        pollOptionPrefs = getSharedPreferences("POLLS", Context.MODE_PRIVATE);
        pollOptionEditor = pollOptionPrefs.edit();
        livePolling = (Switch) findViewById(R.id.toggle_live_polls);
        savePreferences = (Button) findViewById(R.id.poll_options_save);
        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(livePolling.isChecked())
                {
                    PollingFragment.setLivePolling(true);
                    pollOptionEditor.putBoolean("LIVE_POLLS", true);
                }
                else
                {
                    PollingFragment.setLivePolling(false);
                    pollOptionEditor.putBoolean("LIVE_POLLS", false);
                }

                pollOptionEditor.commit();
                finish();
            }
        });


        if(pollOptionPrefs.getBoolean("LIVE_POLLS", false))
        {
            livePolling.setChecked(true);
        }
        else
        {
            livePolling.setChecked(false);
        }
    }
}
