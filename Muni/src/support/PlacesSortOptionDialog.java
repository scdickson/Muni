package support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.cellaflora.muni.fragments.PlaceFragment;

/**
 * Created by sdickson on 11/6/13.
 */
public class PlacesSortOptionDialog extends DialogFragment
{
    String imageOptions[] = {"Sort by name", "Sort by distance"};
    public static final int NAME_REQUEST_CODE = 0;
    public static final int DISTANCE_REQUEST_CODE = 1;
    PlaceFragment placeFragment;

    public PlacesSortOptionDialog(PlaceFragment placeFragment)
    {
        this.placeFragment = placeFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort locations");
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //Sort by name
                        placeFragment.sortDialogCallback(true, NAME_REQUEST_CODE);
                        break;
                    case 1: //Sort by distance
                        placeFragment.sortDialogCallback(true, DISTANCE_REQUEST_CODE);
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                placeFragment.sortDialogCallback(false, -1);
                getDialog().dismiss();
            }
        });
        return builder.create();
    }

}

