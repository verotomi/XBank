package tamas.verovszki.xbank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MyPagerAdapterBranchesAndAtmList extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public MyPagerAdapterBranchesAndAtmList(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {

            case 0:
                Tab1Branches tab1 = new Tab1Branches();
                return tab1;
            case 1:
                Tab2Atms tab2 = new Tab2Atms();
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