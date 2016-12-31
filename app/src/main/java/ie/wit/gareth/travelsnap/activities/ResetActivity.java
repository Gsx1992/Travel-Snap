package ie.wit.gareth.travelsnap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import ie.wit.gareth.travelsnap.R;

//currently replaced by parseUI

public class ResetActivity extends ActionBarActivity {

    private TextView emailText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset, menu);
        return true;
    }

    public void resetPassword(View view){

        emailText = (TextView) findViewById(R.id.emailText);
        ParseUser.requestPasswordResetInBackground(emailText.getText().toString(),
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Email sent to " + emailText.getText().toString(), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "No account with that email could be found, please try another email", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
