package com.cellaflora.muni.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sdickson on 7/28/13.
 */
public class NewsObject implements Serializable
{
    public String objectId;
    public String headline;
    public String photo_url;
    public String document_url;
    public String sub_headline;
    public String news_url;
    public String photo_caption;
    public String counterId;
    public Date date;
}
