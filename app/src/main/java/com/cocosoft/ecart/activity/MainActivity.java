package com.cocosoft.ecart.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.Tab1;
import com.cocosoft.ecart.Tab2;
import com.cocosoft.ecart.common.ViewPagerAdapter;
import com.cocosoft.ecart.database.DatabaseHandler;
import com.cocosoft.ecart.scanlistmodule.ProductItem;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DatabaseHandler mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }
    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mDB = new DatabaseHandler(this);
       /* mDB.addProduct(new ProductItem("501", "Dove Soap", 20, 1,0,false));
        mDB.addProduct(new ProductItem("502", "Dove Shampoo", 30, 1,0,false));
        mDB.addProduct(new ProductItem("503", "Fair & Lovely", 25, 1,0,false));
        mDB.addProduct(new ProductItem("504", "Fog Perfume", 50, 1,0,false));
        mDB.addProduct(new ProductItem("505", "Hair Oil", 40, 1,0,false));*/
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        Tab1 afrag = new Tab1();
        Tab2 safrag = new Tab2();
        adapter.addFragment(afrag, "IOT NFC Tags");
        adapter.addFragment(safrag, "Action Settings");
        viewPager.setAdapter(adapter);
    }
}

