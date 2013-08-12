package com.cellaflora.muni;

import com.cellaflora.muni.adapters.MenuListAdapter;
import com.cellaflora.muni.fragments.*;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends FragmentActivity {
	
	private String[] menuDrawerItems;
	private ListView menuDrawer;
	private DrawerLayout drawerLayout;
	public static String storedWeather = null;
	ActionBarDrawerToggle mMenuToggle;
    Fragment currentFragment = null;
    public static TextView actionbarTitle;
    public static TextView actionbarContactReset;
    public static LinearLayout actionbarEventLayout;
    public static TextView actionBarEventUpcoming, actionBarEventPast;

    public static final int MAX_CACHE_SIZE = 20; //In Megabytes!
    public static final int CACHE_DECREASE_AMOUNT = 7; //In Megabytes!

	//Parse constants
	public static final String PARSE_APPLICATION_ID = "ACXaa1A1Vo759kga9aYlMYGiUJABaKpphndbeFhn";
	public static final String PARSE_CLIENT_KEY = "7VthvZjSwbXzMV3h4hXOmnazhYYTn7CICKAGd7cJ";
    public static final String TWITTER_CONSUMER_KEY = "N6X3G2gj4RJNO6c5B1xlA";
    public static final String TWITTER_CONSUMER_SECRET = "zU2eCl3vnZsRcwhDpCgIj4AFORGq6gVXiiApTNo0Q";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.title_bar);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionbarTitle = (TextView) findViewById(R.id.actionbar_title);
        actionbarContactReset = (TextView) findViewById(R.id.contact_action_reset);
        actionbarEventLayout = (LinearLayout) findViewById(R.id.event_selector_layout);
        actionBarEventUpcoming = (TextView) findViewById(R.id.event_selector_upcoming);
        actionBarEventPast = (TextView) findViewById(R.id.event_selector_past);
        Typeface avenirBlack = Typeface.createFromAsset(getAssets(), "fonts/Avenir LT 95 Black.ttf");
        actionbarTitle.setTypeface(avenirBlack);
		
		//Initialize Parse
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseTwitterUtils.initialize(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
		
		//Load initial fragment
		HomeFragment homeFragment = new HomeFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.add(R.id.container, homeFragment);
        tx.commit();
        
		//Set up menu drawer
		menuDrawerItems = getResources().getStringArray(R.array.menuItems);
		menuDrawer = (ListView) findViewById(R.id.left_drawer);
		menuDrawerItems = getResources().getStringArray(R.array.menuItems);
		drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
		
		mMenuToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.drawer_arrow, R.string.app_name, R.string.app_name);
		drawerLayout.setDrawerListener(mMenuToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	    //getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_HOME);
		
		MenuListAdapter mMenuAdapter = new MenuListAdapter(this, menuDrawerItems);
        menuDrawer.setAdapter(mMenuAdapter);
        DrawerItemClickListener drawerListener = new DrawerItemClickListener();
        menuDrawer.setOnItemClickListener(drawerListener);

        ImageButton ib = (ImageButton) findViewById(R.id.toggle_button);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(drawerLayout.isDrawerOpen(menuDrawer))
                {
                    drawerLayout.closeDrawer(menuDrawer);
                }
                else
                {
                    drawerLayout.openDrawer(menuDrawer);
                }
            }
        });
	}

    private long getDirSize()
    {
        long size = 0;
        File[] files = getFilesDir().listFiles();

        for(File f : files)
        {
            size += f.length();
        }

        return size;
    }

    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mMenuToggle.syncState();
    }

	protected void onDestroy()
	{
        super.onDestroy();

        try
        {
            if(getDirSize() >= (MAX_CACHE_SIZE * 1000000))
            {
                ArrayList<File> files = new ArrayList(Arrays.asList(getFilesDir().listFiles()));
                Collections.sort(files, new fileComparator());
                int i = 0;

                //Log.d("err", "Size before: " + getDirSize());
                while(getDirSize() >= ((MAX_CACHE_SIZE * 1000000) - (CACHE_DECREASE_AMOUNT * 1000000)) && i < files.size())
                {
                    if(!(files.get(i).getName().contains("muni_saved")))
                    {
                        //Log.d("err", "Deleting " + files.get(i).getName() + " to free " + files.get(i).length());
                        files.get(i).delete();
                    }
                    i++;
                }
                //Log.d("err", "Size after: " + getDirSize());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
	
	/*No options menu. Can re-implement later.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.front_page, menu);
		return true;
	}*/
	
	public boolean onOptionsItemSelected(MenuItem item)
    {

        if (mMenuToggle.onOptionsItemSelected(item)) 
        {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
	private void selectItem(int position)
	{
		//Fragment fragment = null;
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		
		switch(position)
		{
			case 0:
				currentFragment = new HomeFragment();
				break;
			case 1:
                currentFragment = new NewsFragment();
				break;
			case 2:
                currentFragment = new TwitterFragment();
				break;
			case 3:
                currentFragment = new EventFragment();
				break;
			case 4:
                currentFragment = new ContactFragment();
				break;
			case 5:
                currentFragment = new AlertFragment();
				break;
			case 6:
                currentFragment = new PollingFragment();
				break;
			case 7:
                currentFragment = new PlaceFragment();
				break;
			case 8:
                currentFragment = new DocumentFragment();
				break;
			case 9:
                currentFragment = new PeopleFragment();
				break;
		}

        if(currentFragment != null)
        {
            fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
		    fragmentTransaction.replace(R.id.container, currentFragment);
		    fragmentTransaction.addToBackStack(null);
	        fragmentTransaction.commit();
            drawerLayout.closeDrawer(menuDrawer);
        }
	}
	
	public void onBackPressed()
    {
        if(getSupportFragmentManager().findFragmentById(R.id.container).getClass().equals(PeopleFragment.class))
        {
            PeopleFragment tmp = (PeopleFragment) currentFragment;
            if(tmp.adapter.level == 1)
            {
                tmp.groupA = " ";
                tmp.groupB = null;
                tmp.level = 0;
                tmp.adapter.clearContent();
                tmp.adapter.setLevel(tmp.level, tmp.groupA, tmp.groupB);
                tmp.searchBar.setHint("Search all people ");
                tmp.adapter.notifyDataSetChanged();
                tmp.peopleList.invalidateViews();
            }
            else if(tmp.adapter.level == 2)
            {
                tmp.level = 1;
                tmp.groupA = tmp.adapter.groupA;
                tmp.groupB = null;
                tmp.searchBar.setHint("Search in " +  tmp.groupA);
                tmp.adapter.clearContent();
                tmp.adapter.setLevel(tmp.level, tmp.groupA, tmp.groupB);
                tmp.adapter.notifyDataSetChanged();
                tmp.peopleList.invalidateViews();
            }
            else
            {
                super.onBackPressed();
            }
        }
        else if(getSupportFragmentManager().findFragmentById(R.id.container).getClass().equals(DocumentFragment.class))
        {
            DocumentFragment tmp = (DocumentFragment) currentFragment;
            DocumentFolder currentDir = tmp.adapter.currentDir;

            if(currentDir != null)
            {
                DocumentFolder parentDir = null;

                for(DocumentFolder folder : tmp.folders)
                {
                    for(DocumentFolder child : folder.folders)
                    {
                        if(child.equals(currentDir))
                        {
                            parentDir = folder;
                            break;
                        }
                    }
                }

                if(parentDir != null)
                {
                    tmp.currentDir = parentDir;
                    tmp.changeFolder(tmp.currentDir);
                }
                else
                {
                    tmp.currentDir = null;
                    tmp.changeFolder(tmp.currentDir);
                }
            }
            else
            {
                super.onBackPressed();
            }


        }
        else
        {
            super.onBackPressed();
        }
    }

	private class DrawerItemClickListener implements ListView.OnItemClickListener 
	{
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) 
	    {
	        selectItem(position);
	    }
	}

}
