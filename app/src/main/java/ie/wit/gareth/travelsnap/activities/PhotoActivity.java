package ie.wit.gareth.travelsnap.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.helpers.CircularBitmap;
import ie.wit.gareth.travelsnap.helpers.Location;
import ie.wit.gareth.travelsnap.models.UserPhoto;

/*

http://developer.android.com/training/basics/data-storage/files.html
http://developer.android.com/reference/android/location/Geocoder.html
http://developer.android.com/reference/android/location/LocationManager.html
http://developer.android.com/reference/android/location/LocationListener.html

 */

public class PhotoActivity extends ActionBarActivity {

    private EditText photoTitle;
    private Button cameraButton;
    private ImageView imagePreview;
    private Button uploadButton;
    private final int TAKE_PHOTO = 100;
    private CircularBitmap cm;
    private Uri _photoFileUri;
    private Bitmap imageBitmap = null;
    private Bitmap rotatedBitmap = null;
    private Bitmap rotatedIconBitmap = null;
    LocationManager lm;
    LocationListener gpsLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoTitle = (EditText) findViewById(R.id.photoTitle);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);

        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(lm != null) {
            gpsLocation = new Location();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, gpsLocation);
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                _photoFileUri = generateFileUri();
                if (_photoFileUri != null) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, _photoFileUri);
                    startActivityForResult(i, TAKE_PHOTO);
                }
            } else {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        ImageView imageView = (ImageView) findViewById(R.id.imagePreview);

        if(resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
            finish();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

        switch(requestCode){
            case TAKE_PHOTO:
                imageBitmap = BitmapFactory.decodeFile(_photoFileUri.getPath());
                break;
        }
        if(imageBitmap != null){

            Bitmap resizeBitmap = Bitmap.createScaledBitmap(imageBitmap, 1024, 1024, true);
            Bitmap iconBitmap = Bitmap.createScaledBitmap(imageBitmap, 80, 80, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotatedBitmap = Bitmap.createBitmap(resizeBitmap , 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight(), matrix, true);
            rotatedIconBitmap = Bitmap.createBitmap(iconBitmap , 0, 0, iconBitmap.getWidth(), iconBitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotatedBitmap);
            resizeBitmap.recycle();
            iconBitmap.recycle();


        }
    }


    @Override
    protected void onResume() {
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsLocation);
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }


    public void uploadPhoto(View view){
        ParseUser user = ParseUser.getCurrentUser();
        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsLocation);
            if (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                double lat = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                double lng = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> address = geocoder.getFromLocation(lat, lng, 1);
                    Bitmap resizeBitmap = Bitmap.createScaledBitmap(imageBitmap, 1024, 1024, true);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(resizeBitmap, 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    byte[] imageBytes = os.toByteArray();
                    ParseFile file = new ParseFile("img.jpg", imageBytes);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    rotatedIconBitmap.compress(Bitmap.CompressFormat.JPEG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();
                    ParseFile iconFile = new ParseFile("icon.jpg", iconBytes);
                    file.saveInBackground();
                    ParseGeoPoint point = new ParseGeoPoint(lat, lng);
                    String city;
                    if(address.get(0).getAddressLine(2) == null){
                        city = address.get(0).getLocality();
                    }
                    else
                    {
                        city = "City";
                    }
                    UserPhoto userPhotos = new UserPhoto(photoTitle.getText().toString(), point, city, file, user, iconFile);
                    userPhotos.uploadPhoto();
                    user.increment("noPhotosTaken");
                    user.saveInBackground();
                    imageBitmap.recycle();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            lm.removeUpdates(gpsLocation);
            gpsLocation = null;

            if (!photoTitle.getText().toString().isEmpty()) {

                AlertDialog.Builder alert = new AlertDialog.Builder(PhotoActivity.this);
                alert.setTitle(getResources().getString(R.string.another));
                alert.setMessage(getResources().getString(R.string.another_photo));

                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

                alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();


            }
            else{
                Toast.makeText(this, "Please enter a title!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Unable to obtain GPS, please try again", Toast.LENGTH_LONG).show();
        }

    }




    private File getPhotoDirectory(){
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){
            File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, "TravelSnap");
            if(!outputDir.exists()){
                if(!outputDir.mkdirs()){
                    Toast.makeText(this, "Failed to create dir", Toast.LENGTH_SHORT).show();
                    outputDir = null;
                }
            }

        }

        return outputDir;
    }

    private Uri generateFileUri(){
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if(outputDir!= null){
            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            String fileName = "IMG_"+timeStamp+".jpg";
            File photoFile = new File(outputDir, fileName);
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    public void savePhoto(View view) throws IOException {

        ParseUser user = ParseUser.getCurrentUser();
        cm = new CircularBitmap();
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsLocation);

            if (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                double lat = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                double lng = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                ParseGeoPoint p = new ParseGeoPoint(lat, lng);
                byte[] imageBytes = os.toByteArray();
                ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                rotatedIconBitmap.compress(Bitmap.CompressFormat.JPEG, 100, iconStream);
                byte[] iconBytes = iconStream.toByteArray();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                UserPhoto up = new UserPhoto();
                up.setLocalDate(currentDate);
                up.setCity("none");
                up.setUser(user);
                up.setPhotoBytes(imageBytes);
                up.setTitle(photoTitle.getText().toString());
                up.setPointLocal(p);
                up.setIconBytes(iconBytes);
                up.setLocalID(UUID.randomUUID().toString());
                up.pinInBackground("UserPhotos", new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });
                imageBitmap.recycle();
                lm.removeUpdates(gpsLocation);
                gpsLocation = null;

                if (!photoTitle.getText().toString().isEmpty()) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(PhotoActivity.this);
                    alert.setTitle(getResources().getString(R.string.another));
                    alert.setMessage(getResources().getString(R.string.another_photo));

                    alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
                            startActivity(i);
                            rotatedBitmap.recycle();
                            finish();
                        }
                    });

                    alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            rotatedBitmap.recycle();
                            finish();
                        }
                    });

                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();


                } else {

                    Toast.makeText(getApplicationContext(), "Empty title", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else{
            Toast.makeText(this, "Unable to obtain GPS, please try again", Toast.LENGTH_LONG).show();
        }

}}
