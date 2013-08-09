package com.cellaflora.muni;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sdickson on 7/25/13.
 */
public class Event implements Serializable
{
    public String objectId;
    public String title;
    public Date start_time;
    public Date end_time;
    public String photo_caption;
    public String photo_url;
    public String location;
    public String address;
    public Place associated_place = new Place();
    public String event_url;
    public String description;
    public boolean isAllDay;
}
