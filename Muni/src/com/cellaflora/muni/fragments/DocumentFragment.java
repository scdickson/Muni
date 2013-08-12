package com.cellaflora.muni.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cellaflora.muni.Document;
import com.cellaflora.muni.DocumentFolder;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.NewsObject;
import com.cellaflora.muni.PersistenceManager;
import com.cellaflora.muni.R;
import com.cellaflora.muni.adapters.DocumentListAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DocumentFragment extends Fragment
{
    View view;
    ProgressDialog progressDialog, pdfProgress;
    ListView documentList;
    Parcelable state;
    EditText searchBar;
    TextView searchCancel;
    InputMethodManager imm;
    public DocumentFolder currentDir = null;
    ArrayList<Object> searchResults;
    public ArrayList<DocumentFolder> folders;
    public ArrayList<Document> documents;
    public DocumentListAdapter adapter;

    public static final String SAVED_DOCUMENTS_PATH = "muni_saved_docs";
    public static final String SAVED_DOCUMENT_FILE_PATH = "muni_saved_doc_files";
    public static final int DOCUMENTS_REPLACE_INTERVAL = 60;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.document_fragment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchBar = (EditText) view.findViewById(R.id.document_search);
        searchCancel = (TextView) view.findViewById(R.id.document_search_cancel);
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                adapter.clearContent();
                changeFolder(currentDir);
                adapter.notifyDataSetChanged();
                documentList.invalidateViews();
                imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchBar.setText("");
                searchCancel.setVisibility(View.GONE);
                documentList.requestFocus();
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i2, int i3)
            {
                searchResults = new ArrayList<Object>();

                if(cs != null && !cs.toString().isEmpty())
                {
                    searchCancel.setVisibility(View.VISIBLE);

                    if(currentDir == null)
                    {
                        for(Document document : documents)
                        {
                            if(document.title.toUpperCase().contains(cs.toString().toUpperCase()))
                            {
                                searchResults.add(document);
                            }
                        }
                    }
                    else
                    {
                        for(Document document : currentDir.documents)
                        {
                            if(document.title.toUpperCase().contains(cs.toString().toUpperCase()))
                            {
                                searchResults.add(document);
                            }
                        }

                        for(DocumentFolder folder : currentDir.folders)
                        {
                            for(Document document : folder.documents)
                            {
                                if(document.title.toUpperCase().contains(cs.toString().toUpperCase()))
                                {
                                    searchResults.add(document);
                                }
                            }
                        }
                    }

                    adapter.clearContent();
                    adapter.setContent(searchResults);
                    adapter.notifyDataSetChanged();
                    documentList.invalidateViews();

                }
                else
                {
                    searchCancel.setVisibility(View.GONE);
                    adapter.clearContent();
                    changeFolder(currentDir);
                    adapter.notifyDataSetChanged();
                    documentList.invalidateViews();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });
        MainActivity.actionbarTitle.setText("Documents");
		return view;
	}

    public void onPause()
    {
        super.onPause();
        state = documentList.onSaveInstanceState();
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

    public void setParentFolder(DocumentFolder child, DocumentFolder parent)
    {
        for(DocumentFolder folder : folders)
        {
            if(folder.equals(parent))
            {
                folder.folders.add(child);
                break;
            }
        }
    }

    public void setFileFolder(Document document, String folderObjectId)
    {
        for(DocumentFolder folder : folders)
        {
            if(folder.objectId.equals(folderObjectId))
            {
                folder.documents.add(document);
                break;
            }
        }
    }

    public void loadDocuments()
    {
        folders = new ArrayList<DocumentFolder>();
        documents = new ArrayList<Document>();
        ParseQuery<ParseObject> folder_query = ParseQuery.getQuery("Doc_Folders");
        folder_query.include("Parent_Folder");
        progressDialog.show();
        folder_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if (e == null)
                {
                    for(ParseObject parse : result)
                    {
                        DocumentFolder tmp = new DocumentFolder();
                        tmp.objectId = parse.getObjectId();
                        tmp.date = fixDate(parse.getUpdatedAt());
                        tmp.title = parse.getString("Title");
                        ParseObject parentFolder = parse.getParseObject("Parent_Folder");

                        if(parentFolder != null)
                        {
                            tmp.parentFolder = new DocumentFolder(parentFolder.getObjectId(), parentFolder.getString("Title"), fixDate(parentFolder.getUpdatedAt()));
                        }

                        folders.add(tmp);
                    }

                    for(DocumentFolder folder : folders)
                    {
                        if(folder.parentFolder != null)
                        {
                            setParentFolder(folder, folder.parentFolder);
                        }
                    }

                    ParseQuery<ParseObject> file_query = ParseQuery.getQuery("Doc_Files");
                    file_query.include("Folder");
                    file_query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> result, ParseException e)
                        {
                            if (e == null)
                            {
                                for(ParseObject parse : result)
                                {
                                    Document tmp = new Document();
                                    tmp.title = parse.getString("Title");
                                    tmp.objectId = parse.getObjectId();
                                    tmp.date = fixDate(parse.getUpdatedAt());
                                    ParseFile data = (ParseFile) parse.get("Document_Data");

                                    if(data != null && data.getUrl() != null)
                                    {
                                        tmp.document_url = data.getUrl();
                                    }

                                    ParseObject folder = parse.getParseObject("Folder");

                                    if(folder != null)
                                    {
                                        setFileFolder(tmp, folder.getObjectId());
                                    }

                                    documents.add(tmp);
                                }
                            }

                            try
                            {
                                PersistenceManager.writeObject(getActivity().getApplicationContext(), SAVED_DOCUMENTS_PATH, folders);
                                PersistenceManager.writeObject(getActivity().getApplicationContext(), SAVED_DOCUMENT_FILE_PATH, documents);
                            }
                            catch(Exception ex){}
                            adapter = new DocumentListAdapter(view.getContext(), folders, null);
                            documentList = (ListView) getActivity().findViewById(R.id.document_list);
                            documentList.setAdapter(adapter);
                            documentList.setOnItemClickListener(new DocumentItemClickListener());
                            documentList.requestFocus();
                            progressDialog.dismiss();
                        }
                    });
                }
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
            if(searchResults != null)
            {
                adapter.clearContent();
                adapter.setContent(searchResults);
                adapter.notifyDataSetChanged();
                documentList.invalidateViews();
                searchBar.requestFocus();
                searchCancel.setVisibility(View.VISIBLE);
            }
            else
            {
                adapter = new DocumentListAdapter(view.getContext(), folders, currentDir);
                documentList = (ListView) getActivity().findViewById(R.id.document_list);
                documentList.setAdapter(adapter);
                documentList.setOnItemClickListener(new DocumentItemClickListener());
                documentList.onRestoreInstanceState(state);
                documentList.requestFocus();
            }
        }
        else
        {
            try
            {
                File f = getActivity().getFileStreamPath(SAVED_DOCUMENTS_PATH);
                if((f.lastModified() + (DOCUMENTS_REPLACE_INTERVAL * 60 * 1000)) >= System.currentTimeMillis())
                {
                    folders = (ArrayList<DocumentFolder>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_DOCUMENTS_PATH);
                    documents = (ArrayList<Document>) PersistenceManager.readObject(getActivity().getApplicationContext(), SAVED_DOCUMENT_FILE_PATH);
                    adapter = new DocumentListAdapter(view.getContext(), folders, null);
                    documentList = (ListView) getActivity().findViewById(R.id.document_list);
                    documentList.setAdapter(adapter);
                    documentList.setOnItemClickListener(new DocumentItemClickListener());
                    documentList.requestFocus();
                }
                else
                {
                    loadDocuments();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                loadDocuments();
            }
        }
    }

    public void changeFolder(DocumentFolder folder)
    {
        currentDir = folder;
        if(folder == null)
        {
            searchBar.setHint("Search all documents");
        }
        else
        {
            searchBar.setHint("Search in " + currentDir.title);
        }
        adapter.clearContent();
        adapter.loadDirectory(folder);
        adapter.notifyDataSetChanged();
        documentList.invalidateViews();
    }

    public void selectItem(int position)
    {
        Object obj = adapter.content.get(position);

        if(obj.getClass().equals(Document.class))
        {
            new loadPdf().execute((Document) obj);
        }
        else if(obj.getClass().equals(DocumentFolder.class))
        {
            changeFolder((DocumentFolder) obj);
        }
    }

    private class DocumentItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            searchCancel.setVisibility(View.GONE);
            documentList.requestFocus();
            selectItem(position);
        }
    }

    private class loadPdf extends AsyncTask<Object, Integer, Void>
    {
        Document document;
        File file;

        protected void onPreExecute()
        {
            super.onPreExecute();
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
                document = (Document) arg0[0];
                URL url = new URL(document.document_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                InputStream is = connection.getInputStream();

                file = new File(Environment.getExternalStorageDirectory() + "/" + document.objectId);
                FileOutputStream fos = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;
                int bytesRead = 0;

                while((bytesRead = is.read(data, 0, data.length)) >= 0)
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
            if(file != null)
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
