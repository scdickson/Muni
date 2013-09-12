package com.cellaflora.muni.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sdickson on 8/8/13.
 */
public class DocumentFolder implements Serializable
{
    public String objectId;
    public String title;
    public Date date;
    public DocumentFolder parentFolder;
    public ArrayList<Document> documents = new ArrayList<Document>();
    public ArrayList<DocumentFolder> folders = new ArrayList<DocumentFolder>();

    public DocumentFolder(){}
    public DocumentFolder(String objectId, String title, Date date)
    {
        this.objectId = objectId;
        this.title = title;
        this.date = date;
    }

    public boolean equals(Object obj)
    {
        DocumentFolder other = (DocumentFolder) obj;
        if(other.objectId.equals(this.objectId))
        {
            return true;
        }

        return false;
    }
}
