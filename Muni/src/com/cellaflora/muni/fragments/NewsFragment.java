package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.MuniConstants;
import com.cellaflora.muni.NewsObject;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.NewsListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsFragment extends Fragment
{
    View view;
    ArrayList<NewsObject> news;
    private ProgressDialog progressDialog, pdfProgress;
    ListView newsList;
    NewsListAdapter adapter;
    Parcelable state;
    loadPdf lp;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.news_fragment, container, false);
        MainActivity.actionbarTitle.setText("News");
		return view;
	}

    private Date fixDate(Date date)
    {
        TimeZone tz = TimeZone.getDefault();
        Date fixed = new Date(date.getTime() - tz.getRawOffset());

        if(tz.inDaylightTime(fixed))
        {
            Date dst = new Date(fixed.getTime() - tz.getDSTSavings());

            if(tz.inDaylightTime(dst))
            {
                fixed = dst;
            }
        }

        return fixed;
    }

    public void onPause()
    {
        super.onPause();
        state = newsList.onSaveInstanceState();
    }

    public void loadNews()
    {
        news = new ArrayList<NewsObject>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("News");
        query.addDescendingOrder("E_Date");
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if (e == null)
                {
                    for(ParseObject parse : result)
                    {
                        NewsObject tmp = new NewsObject();
                        tmp.objectId = parse.getObjectId();
                        tmp.headline = parse.getString("A_Headline");
                        tmp.sub_headline = parse.getString("B_Subheadline");

                        if(parse.getDate("E_Date") != null)
                        {
                            tmp.date = fixDate(parse.getDate("E_Date"));
                        }

                        tmp.photo_caption = parse.getString("D_Photo_Caption");
                        tmp.news_url = parse.getString("G_Hyperlink");

                        ParseFile photo = (ParseFile) parse.get("C_Photo");
                        ParseFile document = (ParseFile) parse.get("F_Document");

                        if(photo != null && photo.getUrl() != null)
                        {
                            tmp.photo_url = photo.getUrl();
                        }

                        if(document != null && document.getUrl() != null)
                        {
                            tmp.document_url = document.getUrl();
                        }

                        news.add(tmp);
                    }

                    try
                    {
                        PersistenceManager.writeObject(getActivity().getApplicationContext(), MuniConstants.SAVED_NEWS_PATH, news);
                    }
                    catch(Exception ex){}
                }

                progressDialog.dismiss();

                adapter = new NewsListAdapter(view.getContext(), news, getActivity());
                newsList = (ListView) getActivity().findViewById(R.id.news_list);
                newsList.setAdapter(adapter);
                newsList.setOnItemClickListener(new NewsItemClickListener());
            }
        });
    }

    public void onResume()
    {
        super.onResume();

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading...");

        pdfProgress = new ProgressDialog(view.getContext());
        pdfProgress.setTitle("");
        pdfProgress.setMessage("Loading PDF...");
        pdfProgress.setIndeterminate(false);
        pdfProgress.setMax(100);
        pdfProgress.setProgressNumberFormat(null);
        pdfProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if(state != null)
        {
            adapter = new NewsListAdapter(view.getContext(), news, getActivity());
            newsList = (ListView) getActivity().findViewById(R.id.news_list);
            newsList.setAdapter(adapter);
            newsList.setOnItemClickListener(new NewsItemClickListener());
            newsList.onRestoreInstanceState(state);
        }
        else
        {
            try
            {
                File f = getActivity().getFileStreamPath(MuniConstants.SAVED_NEWS_PATH);
                if((f.lastModified() + (MuniConstants.NEWS_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                {
                    news = (ArrayList<NewsObject>) PersistenceManager.readObject(getActivity().getApplicationContext(), MuniConstants.SAVED_NEWS_PATH);
                    adapter = new NewsListAdapter(view.getContext(), news, getActivity());
                    newsList = (ListView) getActivity().findViewById(R.id.news_list);
                    newsList.setAdapter(adapter);
                    newsList.setOnItemClickListener(new NewsItemClickListener());
                }
                else
                {
                    loadNews();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                loadNews();
            }
        }
    }

    public void selectItem(int position)
    {
        lp = new loadPdf();
        lp.execute(news.get(position));
    }

    private class NewsItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private class loadPdf extends AsyncTask<Object, Integer, Void>
    {
        NewsObject news_item;
        File file;

        protected void onPreExecute()
        {
            super.onPreExecute();
            pdfProgress.setCanceledOnTouchOutside(false);
            pdfProgress.setCancelable(true);
            pdfProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    lp.cancel(true);
                    publishProgress(0);
                }
            });
            pdfProgress.show();
        }

        protected void onProgressUpdate(Integer... progress)
        {
            super.onProgressUpdate(progress);
            pdfProgress.setProgress(progress[0]);
        }

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                news_item = (NewsObject) arg0[0];
                URL url = new URL(news_item.document_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                InputStream is = connection.getInputStream();

                file = new File(Environment.getExternalStorageDirectory() + "/" + news_item.objectId);
                FileOutputStream fos = new FileOutputStream(file);
                byte data[] = new byte[MuniConstants.PDF_BUFFER_SIZE];
                long total = 0;
                int bytesRead = 0;

                while(((bytesRead = is.read(data, 0, data.length)) >= 0) && !isCancelled())
                {
                    total += bytesRead;
                    publishProgress((int) (total * 100 / fileLength));
                    fos.write(data, 0, bytesRead);
                    fos.flush();
                }

                fos.close();
                is.close();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            pdfProgress.dismiss();
            if(file != null && !isCancelled())
            {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("No PDF Viewer Found");
                    alertDialogBuilder
                            .setMessage("You do not have an application that allows you to view PDF files. To view this file, please download Adobe Reader from the Android Market.")
                            .setCancelable(false)
                            .setPositiveButton("Download",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id)
                                {
                                    try
                                    {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                                    }
                                    catch(ActivityNotFoundException ex)
                                    {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.adobe.reader")));
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
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
