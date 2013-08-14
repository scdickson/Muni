package com.cellaflora.muni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellaflora.muni.Document;
import com.cellaflora.muni.MainActivity;
import com.cellaflora.muni.R;
import com.cellaflora.muni.DocumentFolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sdickson on 8/8/13.
 */
public class DocumentListAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<DocumentFolder> folders;
    public ArrayList<Object> content;
    public DocumentFolder currentDir;

    public DocumentListAdapter(Context context, ArrayList<DocumentFolder> folders, DocumentFolder currentDir)
    {
        content = new ArrayList<Object>();
        this.context = context;
        this.folders = folders;
        loadDirectory(currentDir);
    }

    public void clearContent()
    {
        content = null;
        content = new ArrayList<Object>();
    }

    public void setContent(ArrayList<Object> content)
    {
        this.content = content;
    }

    public void loadDirectory(DocumentFolder currentDir)
    {
        this.currentDir = currentDir;

        if(currentDir == null)
        {
            for(DocumentFolder folder : folders)
            {
                if(folder.parentFolder == null)
                {
                    content.add(folder);
                }
            }
        }
        else
        {
            for(Document document : currentDir.documents)
            {
                content.add(document);
            }

            for(DocumentFolder folder : currentDir.folders)
            {
                content.add(folder);
            }
        }
    }

    public int getCount()
    {
        return content.size();
    }

    public Object getItem(int position)
    {
        return content.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.document_list_row, parent, false);
        TextView txtTitle = (TextView) itemView.findViewById(R.id.document_title);
        txtTitle.setTypeface(MainActivity.myriadProSemiBold);
        TextView txtDate = (TextView) itemView.findViewById(R.id.document_date);
        txtDate.setTypeface(MainActivity.myriadProRegular);
        ImageView imgDocument = (ImageView) itemView.findViewById(R.id.document_type_image);

        Object obj = content.get(position);

        if(obj.getClass().equals(Document.class))
        {
            Document document = (Document) obj;

            int id = context.getResources().getIdentifier("com.cellaflora.muni:drawable/document_file", null, null);
            imgDocument.setImageResource(id);

            if(document.title != null)
            {
                txtTitle.setText(document.title);
            }

            if(document.date != null)
            {
                String dateFormat = "EEEE, MMMM d";
                SimpleDateFormat start = new SimpleDateFormat(dateFormat, Locale.US);
                txtDate.setText(start.format(document.date));
            }
        }
        else if(obj.getClass().equals(DocumentFolder.class))
        {
            DocumentFolder folder = (DocumentFolder) obj;

            int id = context.getResources().getIdentifier("com.cellaflora.muni:drawable/document_folder", null, null);
            imgDocument.setImageResource(id);

            if(folder.title != null)
            {
                txtTitle.setText(folder.title);
            }

            if(folder.date != null)
            {
                if(folder.documents.size() == 0 && folder.folders.size() == 0)
                {
                    txtDate.setText("(Folder is empty)");
                }
                else
                {
                    String dateFormat = "EEEE, MMMM d";
                    SimpleDateFormat start = new SimpleDateFormat(dateFormat, Locale.US);
                    txtDate.setText(start.format(folder.date));
                }
            }
        }

        return itemView;
    }
}
