    package ie.wit.gareth.travelsnap.activities;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.Bundle;
    import android.support.v7.app.ActionBarActivity;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.widget.Toast;

    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.parse.FindCallback;
    import com.parse.Parse;
    import com.parse.ParseException;
    import com.parse.ParseObject;
    import com.parse.ParseQuery;
    import com.parse.ParseUser;

    import java.lang.reflect.Array;
    import java.util.ArrayList;
    import java.util.List;

    import ie.wit.gareth.travelsnap.R;
    import ie.wit.gareth.travelsnap.helpers.CircularBitmap;


    /*
    https://developers.google.com/maps/documentation/android/
    http://stackoverflow.com/questions/5882180/how-to-set-bitmap-in-circular-imageview
     */
    public class MapsActivity extends ActionBarActivity {

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        private ProgressDialog progress;


        ArrayList<ParseObject> userPhotos;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_maps, menu);
            return true;
        }

        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();
            ParseUser user = ParseUser.getCurrentUser();
            if (id == R.id.action_camera) {
                finish();
                Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(i);

            }

            else if(id == R.id.action_delete_all_remote){

                user.increment("noPhotosTaken", -(userPhotos.size() -1)); //decrease photos taken by size of array
                user.saveInBackground();
                ParseObject.deleteAllInBackground(userPhotos);
                finish();
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "All Photos Deleted!", Toast.LENGTH_SHORT).show();
            }

            else if(id == R.id.action_saved_photos){

                finish();
                Intent i = new Intent(getApplicationContext(), SavedPhotosActivity.class);
                startActivity(i);

            }

            return super.onOptionsItemSelected(item);
        }


        private void setUpMapIfNeeded() {
            if (mMap == null) {
                // Try to obtain the map
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                //setUpMap();

                // check if map is available
                if (mMap != null) {
                    setUpMap();
                }
            }
        }

        private void setUpMap() {

            progress = ProgressDialog.show(this, null,
                    getResources().getString(R.string.loading), true, false);
            ParseUser user = ParseUser.getCurrentUser();
            mMap.setMyLocationEnabled(true); //adds button to go to current location
            mMap.getUiSettings().setMapToolbarEnabled(false); //disables getdirectons and google+

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
                    query.whereEqualTo("user", user);
                    userPhotos = new ArrayList<ParseObject>();
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> photos, ParseException e) {
                            CircularBitmap cm = new CircularBitmap();
                            if (e == null) {
                                for (ParseObject s : photos) {
                                    try {
                                        byte[] bytes = s.getParseFile("icon").getData();
                                        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), 80, 80, true); //creates a bitmap and resizes the file to 80x80
                                        Bitmap circleBM = cm.createCircle(bitmap);
                                        bitmap.recycle();
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(s.getParseGeoPoint("location").getLatitude(), s.getParseGeoPoint("location").getLongitude()))
                                                .title(s.getObjectId())
                                                .icon(BitmapDescriptorFactory.fromBitmap(circleBM))); //adds the bitmap as the icon
                                        circleBM.recycle();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    userPhotos.add(s);
                                }
                                progress.dismiss();
                            }

                        }
                    });
                }
            };

            new Thread(runnable).start();


            //add clicklistener to the icons on the map
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


                                              @Override
                                              public boolean onMarkerClick(Marker marker) {
                     mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition())); //adding a onclicklistener disables all basic functionality of the map, must add basic animation to move to the marker when clicked
                     Intent i = new Intent(getApplicationContext(), PhotoSliderActivity.class);
                     i.putExtra("img", marker.getPosition().latitude + "," + marker.getPosition().longitude); //grabs the geolocation from the marker
                     i.putExtra("activity", "MapsActivity");
                     startActivity(i);
                     finish();
                     return true;
                         }
                                          }
            );


        }

        @Override
        public void onBackPressed() {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }


