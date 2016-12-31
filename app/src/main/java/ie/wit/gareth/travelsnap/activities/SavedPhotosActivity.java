package ie.wit.gareth.travelsnap.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.helpers.LocalPhotoAdapter;
import ie.wit.gareth.travelsnap.models.UserPhoto;

public class SavedPhotosActivity extends ActionBarActivity {

    private LayoutInflater inflater;
    private LocalPhotoAdapter photos;
    private ImageButton imageDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_test);


        final ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
                query.fromPin("UserPhotos");
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;

            }
        };

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        photos = new LocalPhotoAdapter(this, factory);
        final ListView localListView = (ListView) findViewById(R.id.localListView);
        localListView.setAdapter(photos);

        localListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final ParseObject a = (ParseObject) parent.getItemAtPosition(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(SavedPhotosActivity.this);
                alert.setTitle("Upload");
                alert.setMessage("Would you like to upload this photo?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseFile photo = new ParseFile("img.jpg", a.getBytes("photo"));
                        final UserPhoto up = new UserPhoto();
                        ParseGeoPoint point = a.getParseGeoPoint("location");
                        ParseFile icon = new ParseFile("icon.jpg",a.getBytes("icon"));
                        try {
                            List<Address> address = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                            if(address.get(0).getAddressLine(2) == null){
                                up.setCity(address.get(0).getLocality());
                            }
                            else
                            {
                                up.setCity("City");
                            }

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        up.setUser(user);
                        up.setIconImage(icon);
                        up.setPhoto(photo);
                        up.setTitle(a.getString("title"));
                        up.setPoint(point);
                        a.unpinInBackground("UserPhotos");
                        up.saveInBackground();
                        Toast.makeText(getApplicationContext(), "Photo Uploaded!", Toast.LENGTH_LONG).show();
                        user.increment("noPhotosTaken");
                        user.saveInBackground();
                        factory.create();
                        localListView.setAdapter(photos);

                    }
                });


                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                return false;
            }
        });



        localListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ParseObject a = (ParseObject) parent.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(), SinglePhotoActivity.class);
                i.putExtra("localID", a.getString("localID"));
                startActivity(i);
                finish();


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
            query.fromPin("UserPhotos");
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList,
                                 ParseException e) {
                    if (e == null) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        ParseUser user = ParseUser.getCurrentUser();
                        for (ParseObject s: scoreList) {
                            ParseGeoPoint point = s.getParseGeoPoint("location");
                            UserPhoto up = new UserPhoto();
                            up.setCity("none");
                            up.setUser(s.getParseUser("user"));
                            ParseFile photo = new ParseFile("img.jpg",s.getBytes("photo"));
                            ParseFile icon = new ParseFile("icon.jpg",s.getBytes("icon"));
                            up.setPhoto(photo);
                            up.setIconImage(icon);
                            up.setTitle(s.get("title").toString());
                            up.setPoint(point);
                            try {
                                List<Address> address = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                                if(address.get(0).getAddressLine(2) == null){
                                    up.setCity(address.get(0).getLocality());
                                    //attempts to get current users city, if null sets it as city to stop a crash
                                }
                                else
                                {
                                    up.setCity("City");
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            up.saveInBackground();
                            s.unpinInBackground("UserPhotos");
                            user.increment("noPhotosTaken");
                            user.saveInBackground();
                        }

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(), "All images uploaded!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        else if(id == R.id.action_delete_all_local){

            AlertDialog.Builder alert = new AlertDialog.Builder(SavedPhotosActivity.this);
            alert.setTitle("Delete all?");
            alert.setMessage(getResources().getString(R.string.del_all_local));

            alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ParseObject.unpinAllInBackground("UserPhotos");
                    finish();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), "All Saved Photos Deleted!", Toast.LENGTH_SHORT).show();


                }
            });
            alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }


}
