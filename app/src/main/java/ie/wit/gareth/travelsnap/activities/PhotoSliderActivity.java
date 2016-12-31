package ie.wit.gareth.travelsnap.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.helpers.ScreenSlideFragment;

/*
http://developer.android.com/reference/android/support/v4/view/ViewPager.html
 */
public class PhotoSliderActivity extends ActionBarActivity {

    private int NUM_PAGES ;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private double lat;
    private double lng;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_slider);
        Intent intent = getIntent();
        String test = intent.getStringExtra("img");
        lat = Double.parseDouble(test.split(",")[0]);
        lng = Double.parseDouble(test.split(",")[1]);
        mPager = (ViewPager)findViewById(R.id.viewPager);



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location", lat+","+lng);
        editor.commit();

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

        });

    }



    @Override
    public void onBackPressed() {
       Intent intent = getIntent();
       String previousActivity = intent.getStringExtra("activity");

        if(previousActivity.equalsIgnoreCase("MapsActivity")){
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);


            ParseUser user = ParseUser.getCurrentUser();
            ParseGeoPoint point = new ParseGeoPoint(lat, lng);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
            query.whereEqualTo("user", user);
            query.whereNear("location", point);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> photos, ParseException e) {
                    if (e == null) {
                        NUM_PAGES = photos.size();
                        notifyDataSetChanged();
                    } else {
                    }
                }
            });

         ;

        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlideFragment.create(position);
        }


        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
