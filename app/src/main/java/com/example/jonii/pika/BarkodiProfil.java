package com.example.jonii.pika;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class BarkodiProfil extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    Bundle bundle;
    Bundle toFragmentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkodi);
        bundle = getIntent().getExtras();
        String barkodi_tdhanat = bundle.getString("kodi");
        String formati = bundle.getString("format");
        String emri = bundle.getString("emri");
        int position = bundle.getInt("position");

        toFragmentBundle = new Bundle();
        toFragmentBundle.putString("kodi", barkodi_tdhanat);
        toFragmentBundle.putString("format", formati);
        toFragmentBundle.putString("emri", emri);
        toFragmentBundle.putInt("position", position);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.sliding_tabs);
        viewPager = findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BarcodeFragment barcodeFragment = BarcodeFragment.newInstance();
        barcodeFragment.setArguments(toFragmentBundle);
        InfoFragment infoFragment = InfoFragment.newInstance();
        infoFragment.setArguments(toFragmentBundle);
        adapter.addFragment(barcodeFragment, "Kartela");
        adapter.addFragment(infoFragment, "Info");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(BarkodiProfil.this, Lista.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(setIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent setIntent = new Intent(BarkodiProfil.this, Lista.class);
                startActivity(setIntent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
