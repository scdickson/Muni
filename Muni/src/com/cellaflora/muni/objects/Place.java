package com.cellaflora.muni.objects;

import android.location.LocationManager;

import java.io.Serializable;

/**
 * Created by sdickson on 7/3/13.
 */
public class Place implements Serializable
{
    public String objectId;
    public String name;
    public String category;
    public String street_address;
    public String city;
    public String state;
    public String zip_code;
    public String tel_number;
    public String web_url;
    public String geo_point;
    public String notes;

    public Place(String name)
    {
        this.name = name;
    }

    public Place(){}

    public String toString()
    {
        return (name + ", " + category + ", " + street_address + ", " + city + ", " + state + ", " + zip_code + ", " + tel_number + ", " + web_url + ", " + geo_point + ", " + notes);
    }

}
