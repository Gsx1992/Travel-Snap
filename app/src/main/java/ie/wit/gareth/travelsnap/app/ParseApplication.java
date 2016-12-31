package ie.wit.gareth.travelsnap.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.models.UserPhoto;

/**
 * Created by Gareth on 21/01/2015.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(getApplicationContext()); //enables local datastore
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseACL defaultACL = new ParseACL();
        ParseObject.registerSubclass(UserPhoto.class);
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }

}