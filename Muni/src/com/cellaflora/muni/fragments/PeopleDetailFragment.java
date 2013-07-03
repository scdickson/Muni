package com.cellaflora.muni.fragments;

import com.cellaflora.muni.Person;
import com.cellaflora.muni.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PeopleDetailFragment extends Fragment
{
	Person requested;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.people_detail_fragment, container, false);
		return view;
	}
	
	public void setPerson(Person requested)
	{
		this.requested = requested;
	}
	
	public void onResume()
	{
		super.onResume();
		TextView name = (TextView) getActivity().findViewById(R.id.people_detail_name);
        TextView title = (TextView) getActivity().findViewById(R.id.people_detail_title);
		name.setText(requested.name);
        title.setText(requested.title);

        try
        {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("People");
            //http://www.androidbegin.com/tutorial/android-parse-com-image-download-tutorial/
            // Locate the objectId from the class
            query.getInBackground(requested.objectId, new GetCallback<ParseObject>() {

                public void done(ParseObject object, ParseException e) {
                    // TODO Auto-generated method stub

                    // Locate the column named "ImageName" and set the string
                    ParseFile fileObject = (ParseFile) object.get("H_Photo");
                    fileObject.getDataInBackground(new GetDataCallback() {

                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Log.d("test", "We've got data in data.");
                                // Decode the Byte[] into Bitmap
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                        data.length);

                                // Get the ImageView from main.xml
                                ImageView image = (ImageView) getActivity().findViewById(R.id.people_detail_image);

                                // Set the Bitmap into the ImageView
                                image.setImageBitmap(bmp);


                            } else {
                                Log.d("test",
                                        "There was a problem downloading the data.");
                            }
                        }
                    });
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


	}
}
