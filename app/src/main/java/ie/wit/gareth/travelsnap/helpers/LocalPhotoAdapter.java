package ie.wit.gareth.travelsnap.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.ge;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import ie.wit.gareth.travelsnap.R;
import ie.wit.gareth.travelsnap.models.UserPhoto;

public class LocalPhotoAdapter extends ParseQueryAdapter<ParseObject> {



    public LocalPhotoAdapter(Context context, QueryFactory<ParseObject> factory) {
        super(context, factory);

    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_list_photos, null);
        }

        byte [] bytes = object.getBytes("photo");
        ParseFile photoFile = new ParseFile(bytes);
        ParseImageView imageView = (ParseImageView) v.findViewById(R.id.testView);
        TextView listImageTitle = (TextView) v.findViewById(R.id.listImageTitle);
        TextView listDate = (TextView) v.findViewById(R.id.listDate);
        imageView.setParseFile(photoFile);
        listImageTitle.setText(object.getString("title"));
        listDate.setText(v.getResources().getString(R.string.date)+" "+object.getString("localDate"));
        imageView.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                // nothing to do
            }
        });


        return v;
    }
}

