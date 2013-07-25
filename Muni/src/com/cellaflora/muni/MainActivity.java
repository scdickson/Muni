package com.cellaflora.muni;

import com.cellaflora.muni.adapters.MenuListAdapter;
import com.cellaflora.muni.fragments.*;
import com.parse.Parse;
import com.parse.ParseAnalytics;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity{
	
	private String[] menuDrawerItems;
	private ListView menuDrawer;
	private DrawerLayout drawerLayout;
	public static String storedWeather = null;
	ActionBarDrawerToggle mMenuToggle;
    Fragment currentFragment = null;
    public static TextView actionbarTitle;
	//Parse constants
	public static final String PARSE_APPLICATION_ID = "ACXaa1A1Vo759kga9aYlMYGiUJABaKpphndbeFhn";
	public static final String PARSE_CLIENT_KEY = "7VthvZjSwbXzMV3h4hXOmnazhYYTn7CICKAGd7cJ";
	
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
        Typeface avenirBlack = Typeface.createFromAsset(getAssets(), "fonts/Avenir LT 95 Black.ttf");
        actionbarTitle.setTypeface(avenirBlack);
		
		//Initialize Parse
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		ParseAnalytics.trackAppOpened(getIntent());
		
		//Load initial fragment
		HomeFragment homeFragment = new HomeFragment();
		FragmentTransaction tx = getFragmentManager().beginTransaction();
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

	protected void onPostCreate(Bundle savedInstanceState) 
	{
        super.onPostCreate(savedInstanceState);
        mMenuToggle.syncState();
    }
	
	/*No options menu. Can re-implement later.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.front_page, menu);
		return true;
	}*/
	
	public boolean onOptionsItemSelected(MenuItem item) {

        if (mMenuToggle.onOptionsItemSelected(item)) 
        {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
	private void selectItem(int position)
	{
		//Fragment fragment = null;
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		switch(position)
		{
			case 0:
				currentFragment = new HomeFragment();
				break;
			case 1:
                currentFragment = new PeopleFragment();
				break;
			case 2:
                currentFragment = new AlertFragment();
				break;
			case 3:
                currentFragment = new PlaceFragment();
				break;
			case 4:
                currentFragment = new NewsFragment();
				break;
			case 5:
                currentFragment = new EventFragment();
				break;
			case 6:
                currentFragment = new TwitterFragment();
				break;
			case 7:
                currentFragment = new FacebookFragment();
				break;
			case 8:
                currentFragment = new ContactFragment();
				break;
			case 9:
                currentFragment = new DocumentFragment();
				break;
		}
		
        fragmentTransaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
		fragmentTransaction.replace(R.id.container, currentFragment);
		fragmentTransaction.addToBackStack(null);
	    fragmentTransaction.commit();
	    drawerLayout.closeDrawer(menuDrawer);
	}
	
	public void onBackPressed()
    {
        if(getFragmentManager().findFragmentById(R.id.container).getClass().equals(PeopleFragment.class))
        {
            PeopleFragment tmp = (PeopleFragment) currentFragment;
            if(tmp.adapter.level == 1)
            {
                tmp.adapter.clearContent();
                tmp.adapter.setLevel(0, " ", null);
                tmp.adapter.notifyDataSetChanged();
                tmp.peopleList.invalidateViews();
            }
            else if(tmp.adapter.level == 2)
            {
                tmp.adapter.clearContent();
                tmp.adapter.setLevel(1, tmp.adapter.groupA, null);
                tmp.adapter.notifyDataSetChanged();
                tmp.peopleList.invalidateViews();
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
