package ie.wit.gareth.travelsnap.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import ie.wit.gareth.travelsnap.R;

public class SinglePhotoActivity extends ActionBarActivity {

    private ParseImageView imageView;
    private TextView imageTitle;
    private TextView imageID;
    private TextView imageDate;
    private ProgressDialog progress;
    private ImageButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_screen_slide);

        progress = ProgressDialog.show(this, null,
                "Loading", true, false);

        imageView = (ParseImageView) findViewById(R.id.mapImageView);
        imageTitle = (TextView) findViewById(R.id.imageTitle);
        imageID = (TextView) findViewById(R.id.imageID);
        imageDate = (TextView) findViewById(R.id.imageDate);

        Intent intent = getIntent();
        final String localID = intent.getStringExtra("localID");
        Log.d("LOOK AT ME", localID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
        query.fromLocalDatastore();
        query.fromPin("UserPhotos");
        query.whereEqualTo("localID", localID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    for (ParseObject s : photos) {
                        ParseFile p = new ParseFile(s.getBytes("photo"));

                        setTitle(s.get("title").toString());
                        imageView.setParseFile(p);
                        imageDate.setText(imageDate.getText().toString() + s.getString("localDate"));
                        imageView.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {

                            }
                        });
                        imageTitle.setText(s.getString("title"));
                        imageID.setText(localID);

                    }
                    progress.dismiss();
                } else {
                }

            }


        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SinglePhotoActivity.this);
                alert.setTitle("Are you sure?");
                alert.setMessage("Delete this photo?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
                        query.fromLocalDatastore();
                        query.fromPin("UserPhotos");
                        query.whereEqualTo("localID", imageID.getText().toString());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> photos, ParseException e) {
                                if (e == null) {
                                    for (ParseObject s : photos) {
                                        s.unpinInBackground("UserPhotos");
                                    }
                                } else {
                                }

                                Toast.makeText(getApplicationContext(), "Photo deleted", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), SavedPhotosActivity.class);
                                startActivity(i);
                                finish();

                            }


                        });
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });
    }



    @Override
    public void onBackPressed() {
            Intent i = new Intent(getApplicationContext(), SavedPhotosActivity.class);
            startActivity(i);
            finish();
        }

}
