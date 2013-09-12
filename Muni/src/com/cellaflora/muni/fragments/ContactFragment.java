package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import support.ContactImageOptionDialog;
import support.ContactListItem;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.ContactListAdapter;

import java.io.File;


public class ContactFragment extends Fragment
{
    View view;
    ContactFragment contactFragment;
    ContactListItem contactListItem;
    GridView contactGrid;
    Button sendAction;
    EditText txtDescription, txtSubject;
    RelativeLayout toLayout;
    ImageView photoAction, locationAction;
    ContactListAdapter adapter;
    String currentLocation;
    boolean locationEnabled = false, photoEnabled = false;
    File photoFile;

    public static final int SEND_REQUEST_CODE = 2;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        contactFragment = this;
		view = inflater.inflate(R.layout.contact_fragment, container, false);
        MainActivity.actionbarTitle.setText("Contact");
        MainActivity.actionbarContactReset.setVisibility(View.VISIBLE);
        MainActivity.actionbarContactReset.setTypeface(MainActivity.myriadProRegular);
        TextView toField = (TextView) view.findViewById(R.id.contact_to_field);
        toField.setTypeface(MainActivity.myriadProRegular);
        TextView subjectLabel = (TextView) view.findViewById(R.id.contact_subject_label);
        subjectLabel.setTypeface(MainActivity.myriadProRegular);
        TextView disclaimer = (TextView) view.findViewById(R.id.contact_emergency_disclaimer);
        disclaimer.setTypeface(MainActivity.myriadProRegular);
        contactGrid = (GridView) view.findViewById(R.id.contact_list_grid);
        contactListItem = (ContactListItem) view.findViewById(R.id.contact_to_view);
        photoAction = (ImageView) view.findViewById(R.id.contact_photo_action);
        txtDescription = (EditText) view.findViewById(R.id.contact_description_field);
        txtDescription.setTypeface(MainActivity.myriadProRegular);
        txtSubject = (EditText) view.findViewById(R.id.contact_subject_field);
        txtSubject.setTypeface(MainActivity.myriadProRegular);
        locationAction = (ImageView) view.findViewById(R.id.contact_location_action);
        sendAction = (Button) view.findViewById(R.id.contact_action_send);
        sendAction.setTypeface(MainActivity.myriadProSemiBold);
        MainActivity.actionbarContactReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Reset");
                alertDialogBuilder
                        .setMessage("Are you sure you want to reset? All entered data will be lost.")
                        .setCancelable(false)
                        .setPositiveButton("Reset",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                resetForm();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        contactListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                removeFromField(contactListItem.getName());
            }
        });
        adapter = new ContactListAdapter(view.getContext(), MuniConstants.CONTACTS, new contactSelectionHandler());
        contactGrid.setAdapter(adapter);
        toLayout = (RelativeLayout) view.findViewById(R.id.contact_to_layout);
        toLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.animator.slide_in);
                contactGrid.setVisibility(View.VISIBLE);
                contactGrid.startAnimation(animation);
            }
        });
        photoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!photoEnabled)
                {
                    photoEnabled = true;
                    int id_enabled = view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/contact_photo_action_enabled", null, null);
                    photoAction.setImageResource(id_enabled);
                    ContactImageOptionDialog imageDialog = new ContactImageOptionDialog(contactFragment);
                    imageDialog.show(getFragmentManager(), null);

                }
                else
                {
                    photoEnabled = false;
                    int id_disabled = view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/contact_photo_action", null, null);
                    photoAction.setImageResource(id_disabled);
                }
            }
        });
        locationAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!locationEnabled)
                {
                    locationEnabled = true;
                    int id_enabled = view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/contact_location_action_enabled", null, null);
                    locationAction.setImageResource(id_enabled);
                    LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = new ContactLocationListener();
                    service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
                else
                {
                    locationEnabled = false;
                    int id_disabled = view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/contact_location_action", null, null);
                    locationAction.setImageResource(id_disabled);
                }

            }
        });
        sendAction.setOnClickListener(new sendActionListener());
		return view;
	}

    public void onPause()
    {
        super.onPause();
        MainActivity.actionbarContactReset.setVisibility(View.GONE);
    }

    public void photoDialogCallback(boolean callback, int code)
    {
        if(callback)
        {
            if(code == ContactImageOptionDialog.CAMERA_REQUEST_CODE)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = new File(Environment.getExternalStorageDirectory() + "/" + "photo");
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, ContactImageOptionDialog.CAMERA_REQUEST_CODE);
            }
            else if(code == ContactImageOptionDialog.EXISTING_REQUEST_CODE)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(galleryIntent ,"Select a Picture"), ContactImageOptionDialog.EXISTING_REQUEST_CODE);
            }
        }
        else
        {
            photoEnabled = false;
            int id_disabled = view.getContext().getResources().getIdentifier("com.cellaflora.muni:drawable/contact_photo_action", null, null);
            photoAction.setImageResource(id_disabled);
        }
    }

    public void onResume()
    {
        super.onResume();
    }

    public void addToField(String name)
    {
        contactListItem.setVisibility(View.VISIBLE);
        contactListItem.setCancelEnabled(true);
        contactListItem.setName(name);
        adapter.removeItem(name);
        adapter.notifyDataSetChanged();
        contactGrid.invalidateViews();

        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.animator.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                contactGrid.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

        });
        contactGrid.startAnimation(animation);
    }

    public void removeFromField(String name)
    {
        contactListItem.setVisibility(View.GONE);

        if(contactGrid.getVisibility() == View.GONE)
        {
            adapter.resetContents();
            adapter.notifyDataSetChanged();
            contactGrid.invalidateViews();
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.animator.slide_in);
            contactGrid.setVisibility(View.VISIBLE);
            contactGrid.startAnimation(animation);
        }
        else
        {
            adapter.resetContents();
            adapter.notifyDataSetChanged();
            contactGrid.invalidateViews();
        }
    }

    private class contactSelectionHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            if(msg != null)
            {
                if(msg.arg1 == ContactListAdapter.ACTION_ADD)
                {
                    addToField(msg.obj.toString());
                }
                else if(msg.arg1 == ContactListAdapter.ACTION_REMOVE)
                {
                    removeFromField(msg.obj.toString());
                }
            }
        }
    }

    private class sendActionListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            if(!contactListItem.getName().isEmpty() && !txtDescription.getText().toString().isEmpty())
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Send Message");
                alertDialogBuilder
                        .setMessage("Please review your message and click \"Send\" in the top right corner when you have finished.")
                        .setCancelable(false)
                        .setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id)
                            {

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                String to[] = new String[1];
                                String content = txtDescription.getText().toString();

                                for(String[] contact : MuniConstants.CONTACTS)
                                {
                                    if(contact[0].equals(contactListItem.getName()))
                                    {
                                        to[0] = contact[1];
                                        break;
                                    }
                                }

                                intent.putExtra(Intent.EXTRA_EMAIL, to);

                                if(txtSubject.getText() != null)
                                {
                                    intent.putExtra(Intent.EXTRA_SUBJECT, txtSubject.getText().toString());
                                }

                                if(locationEnabled && currentLocation != null)
                                {
                                    content = content + "Tagged Location:\nhttps://maps.google.com/?q=" + currentLocation;
                                }

                                if(photoEnabled && photoFile != null)
                                {
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                                }

                                intent.putExtra(Intent.EXTRA_TEXT, content);
                                intent.setType("plain/text");

                                try
                                {
                                    startActivity(intent);
                                    resetForm();
                                }
                                catch(ActivityNotFoundException anfe)
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    alertDialog.setTitle("Send Message");
                                    alertDialog.setMessage("It appears you do not have a email client installed. An application capable of sending email is required to use this feature.");
                                    alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                }


                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Send Message");
                alertDialog.setMessage("Please make sure you have selected a recipient and entered a message.");
                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }

        }
    }

    public void resetForm()
    {
        locationEnabled = false;
        photoEnabled = false;
        ContactFragment fragment = new ContactFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private class ContactLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (location != null)
            {
                currentLocation = location.getLatitude() + "," + location.getLongitude();
            }
        }

        public void onProviderEnabled(String provider){}
        public void onProviderDisabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ContactImageOptionDialog.CAMERA_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_CANCELED)
            {
                photoDialogCallback(false, -1);
            }
        }
        else if(requestCode == ContactImageOptionDialog.EXISTING_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK && data != null)
            {
                try
                {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    photoFile = new File(picturePath);
                }
                catch(Exception e)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Error attaching photo. Please try a different image.", Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                photoDialogCallback(false, -1);
            }
        }
    }

}
