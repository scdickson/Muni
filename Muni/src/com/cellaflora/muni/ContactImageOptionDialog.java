package com.cellaflora.muni;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.cellaflora.muni.fragments.ContactFragment;

import java.io.File;

/**
 * Created by sdickson on 8/11/13.
 */
public class ContactImageOptionDialog extends DialogFragment
{
    String imageOptions[] = {"Take Photo", "Choose Existing Photo"};
    public static final int CAMERA_REQUEST_CODE = 0;
    public static final int EXISTING_REQUEST_CODE = 1;
    ContactFragment contactFragment;

    public ContactImageOptionDialog(ContactFragment contactFragment)
    {
        this.contactFragment = contactFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attach an Image");
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //Take photo
                        contactFragment.photoDialogCallback(true, CAMERA_REQUEST_CODE);
                        break;
                    case 1: //Choose existing photo
                        contactFragment.photoDialogCallback(true, EXISTING_REQUEST_CODE);
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                contactFragment.photoDialogCallback(false, -1);
                getDialog().dismiss();
            }
        });
        return builder.create();
    }

}
