package com.cellaflora.muni;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cellaflora.muni.fragments.PollingFragment;
import com.cellaflora.muni.fragments.PollingPage;

/**
 * Created by sdickson on 8/5/13.
 */
public class PollingPageAdapter extends FragmentPagerAdapter
{
    Context context;

    public PollingPageAdapter(Context context, FragmentManager mgr)
    {
        super(mgr);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        return(new PollingPage(PollingFragment.polls.get(position)));
    }

    @Override
    public int getCount()
    {
        return PollingFragment.polls.size();
    }

}
