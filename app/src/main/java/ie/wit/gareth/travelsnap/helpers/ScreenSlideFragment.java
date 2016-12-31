package ie.wit.gareth.travelsnap.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.activities.MapsActivity;


public class ScreenSlideFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private ParseImageView imageView;
    private TextView imageTitle;
    private TextView imageDate;
    private TextView imageID;
    private TextView pageNo;
    private ProgressDialog progress;



    public static ScreenSlideFragment create(int pageNumber) {

        ScreenSlideFragment fragment = new ScreenSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageNumber = getArguments().getInt(ARG_PAGE);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide, container, false);


        final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        imageView = (ParseImageView) rootView.findViewById(R.id.mapImageView);
        imageTitle = (TextView) rootView.findViewById(R.id.imageTitle);
        imageDate = (TextView) rootView.findViewById(R.id.imageDate);
        imageID = (TextView) rootView.findViewById(R.id.imageID);
        pageNo = (TextView) rootView.findViewById(R.id.pageNo);

        ParseUser user = ParseUser.getCurrentUser();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mapLocation = sharedPreferences.getString("location", "");

        ParseGeoPoint point = new ParseGeoPoint(Double.parseDouble(mapLocation.split(",")[0]), Double.parseDouble(mapLocation.split(",")[1]));
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhotos");
        query.whereEqualTo("user", user);
        query.whereNear("location", point);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    int index = 0;
                    int size = photos.size();
                    for(ParseObject s: photos){
                        if(mPageNumber == index) {
                            getActivity().setTitle(s.get("city").toString());
                            imageID.setText(s.getObjectId());
                            imageTitle.setText(s.get("title").toString());
                            imageDate.setText(imageDate.getText().toString().concat(dateFormatter.format(s.getCreatedAt())));
                            imageView.setParseFile(s.getParseFile("photo"));
                            imageView.loadInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                }
                            });
                        }
                        index++;
                        pageNo.setText((mPageNumber+1)+"/"+size);
                    }
                    }

             else {
            }


        }
        });


        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Are you sure?");
                alert.setMessage("Delete this photo?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseObject.createWithoutData("UserPhotos", imageID.getText().toString()).deleteInBackground();
                        user.increment("noPhotosTaken", -1); //decrease photos taken by 1
                        user.saveInBackground();
                        getActivity().finish();
                        Intent i = new Intent(getActivity(), MapsActivity.class);
                        startActivity(i);
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

        return rootView;
    }

}
