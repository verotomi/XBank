package tamas.verovszki.xbank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Chirag on 30-Jul-17.
 */

public class MyPagerAdapterExchangeRates extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public MyPagerAdapterExchangeRates(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {

            case 0:
                Tab1Currencies tab1 = new Tab1Currencies();
                return tab1;
            case 1:
                Tab2ForeignCurrencies tab2 = new Tab2ForeignCurrencies();
                return  tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}