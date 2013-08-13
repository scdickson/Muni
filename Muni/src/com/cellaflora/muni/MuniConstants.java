package com.cellaflora.muni;

import android.graphics.Typeface;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by sdickson on 8/12/13.
 */
public class MuniConstants
{
    //Main Activity Constants
    public static final int MAX_CACHE_SIZE = 20; //In Megabytes!
    public static final int CACHE_DECREASE_AMOUNT = 7; //In Megabytes!
    public static final String PARSE_APPLICATION_ID = "ACXaa1A1Vo759kga9aYlMYGiUJABaKpphndbeFhn";
    public static final String PARSE_CLIENT_KEY = "7VthvZjSwbXzMV3h4hXOmnazhYYTn7CICKAGd7cJ";
    public static final String TWITTER_CONSUMER_KEY = "N6X3G2gj4RJNO6c5B1xlA";
    public static final String TWITTER_CONSUMER_SECRET = "zU2eCl3vnZsRcwhDpCgIj4AFORGq6gVXiiApTNo0Q";

    //Alert Fragment Constants
    public static final int MAX_RECENT_ALERTS = 40;

    //Contact Fragment Constants
    public static final String[][] CONTACTS = {{"Traffic","sam@cellaflora.com"},
            {"Construction","sam@cellaflora.com"},{"Parks Department","sam@cellaflora.com"},
            {"Waste Management","sam@cellaflora.com"},{"Police Department","sam@cellaflora.com"},
            {"Legislature","sam@cellaflora.com"},{"Congress","sam@cellaflora.com"},
            {"Mayor","sam@cellaflora.com"},{"Taxes","sam@cellaflora.com"},
            {"Sewage Department","sam@cellaflora.com"},{"Water","sam@cellaflora.com"}};

    //Document Fragment Constants
    public static final String SAVED_DOCUMENTS_PATH = "muni_saved_docs";
    public static final String SAVED_DOCUMENT_FILE_PATH = "muni_saved_doc_files";
    public static final int DOCUMENTS_REPLACE_INTERVAL = 60; //In minutes!

    //Event Fragment Constants
    public static final String SAVED_EVENTS_PATH = "muni_saved_events";
    public static final int IMAGE_BUFFER_SIZE = 1024; //In bytes!
    public static final int EVENTS_REPLACE_INTERVAL = 60; //In minutes!

    //Home Fragment Constants
    public static final String WEATHER_KEY = "mg8xd4e3c3vc2tjkh2hvtcau";
    public static final int WEATHER_ZIPCODE = 47906;
    public static final int WEATHER_NUM_DAYS = 1;
    public static final String SAVED_WEATHER_KEY = "muni_saved_weather";
    public static final int WEATHER_REPLACE_INTERVAL = 60; //In minutes!

    //News Fragment Constants
    public static final String SAVED_NEWS_PATH = "muni_saved_news";
    public static final int NEWS_REPLACE_INTERVAL = 60; //In minutes!
    public static final int PDF_BUFFER_SIZE = 1024; //In bytes!

    //People Fragment Constants
    public static final String SAVED_PEOPLE_PATH = "muni_saved_people";
    public static final String SAVED_PEOPLE_PERSON_PATH = "muni_saved_people_person";
    public static final int PEOPLE_REPLACE_INTERVAL = 60; //In minutes!

    //Place Fragment Constants
    public static final String SAVED_PLACES_PATH = "muni_saved_places";
    public static final int PLACES_REPLACE_INTERVAL = 60; //In minutes!
    public static final double METERS_PER_MILE = 1609.3472;
    public static final int DETAIL_MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;

    //Polling Fragment Constants
    public static final String SAVED_POLLS_PATH = "muni_saved_polls";
    public static final int MAX_RECENT_POLLS = 6;
    public static final String OPTION_A_COLOR = "#323C45";
    public static final String OPTION_B_COLOR = "#EC4B43";
    public static final String OPTION_C_COLOR = "#A0A6B2";

    //Twitter Fragment Constants
    public static final String TWITTER_NAME = "WestLafayetteIN";
    public static final String TWITTER_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json?include_entities=false&include_rts=false&screen_name=";


}
