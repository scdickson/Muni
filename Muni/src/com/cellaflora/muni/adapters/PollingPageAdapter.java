package com.cellaflora.muni.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.cellaflora.muni.fragments.PollingFragment;
import com.cellaflora.muni.fragments.PollingPage;

/**
 * Created by sdickson on 8/5/13.
 */
public class PollingPageAdapter extends FragmentPagerAdapter
{
    Context context;
    PollingFragment pf;

    public PollingPageAdapter(Context context, FragmentManager mgr, PollingFragment pf)
    {
        super(mgr);
        this.context = context;
        this.pf = pf;
    }

    @Override
    public Fragment getItem(int position)
    {
        return(new PollingPage(PollingFragment.polls.get(position), pf));
    }

    @Override
    public int getCount()
    {
        return PollingFragment.polls.size();
    }

}
