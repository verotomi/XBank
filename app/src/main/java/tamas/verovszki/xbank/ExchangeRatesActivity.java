package tamas.verovszki.xbank;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ExchangeRatesActivity extends AppCompatActivity implements Tab1Currencies.OnFragmentInteractionListener,Tab2Currencies.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.currency)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.foreign_currency)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_4);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final MyPagerAdapterExchangeRates adapter = new MyPagerAdapterExchangeRates(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onCFragmentInteraction(Uri uri) {

    }
}
