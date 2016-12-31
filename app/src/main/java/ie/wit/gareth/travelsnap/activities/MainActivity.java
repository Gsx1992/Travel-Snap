package ie.wit.gareth.travelsnap.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.internal.dr;
import com.parse.ParseUser;

import ie.wit.gareth.travelsnap.R;

/*
https://github.com/ParsePlatform/ParseUI-Android
https://parse.com/docs/android_guide
 */

public class MainActivity extends ActionBarActivity {


    private TextView welcomeText;
    private TextView photosTaken;
    private Button mapsButton;
    private Button cameraButton;
    private UiLifecycleHelper uiHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        photosTaken = (TextView) findViewById(R.id.photos_taken);
        mapsButton = (Button) findViewById(R.id.mapsButton);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        ParseUser currentUser = ParseUser.getCurrentUser();
        welcomeText.setText(welcomeText.getText().toString()+ " "+currentUser.get("name").toString().split(" ")[0]);
        photosTaken.setText(photosTaken.getText().toString() + " "+currentUser.get("noPhotosTaken"));

    }

    public void launchMap(View view){

        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_logout){
            ParseUser.logOut();
            Intent i = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(i);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void launchCamera(View view){
        Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
        startActivity(i);
        finish();
    }

    public void launchFacebook(View view){
    ParseUser user = ParseUser.getCurrentUser();
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setDescription("Look at my photos!")
                .setLink("https://travel-snap.herokuapp.com/map/"+user.getObjectId().toString())
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());

        // gets current user ID and shares their map to facebook

    }

    public void launchWebsite(View view){
        ParseUser user = ParseUser.getCurrentUser();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://travel-snap.herokuapp.com/map/"+user.getObjectId().toString()));
        startActivity(i);
        //launches the web browser on the device and views current users map
    }

    public void launchGallery(View view){
        Intent i = new Intent(getApplicationContext(), SavedPhotosActivity.class);
        startActivity(i);
        finish();

    }




















}
