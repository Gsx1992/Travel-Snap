package ie.wit.gareth.travelsnap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import ie.wit.gareth.travelsnap.R;

//currently replaced by parseUI


public class RegistrationActivity extends ActionBarActivity {

    private EditText nameRegister;
    private EditText emailRegister;
    private EditText passwordRegister;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nameRegister = (EditText) findViewById(R.id.nameRegister);
        emailRegister = (EditText) findViewById(R.id.emailRegister);
        passwordRegister = (EditText) findViewById(R.id.passwordRegister);
        signupButton = (Button) findViewById(R.id.signupButton);


    }

    //

    public void userRegister(View view){

        //add validation checks
        ParseUser user = new ParseUser();
        user.setUsername(emailRegister.getText().toString());
        user.setPassword(passwordRegister.getText().toString());
        user.put("name", nameRegister.getText().toString());
        user.put("noPhotosTaken", 0);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(),
                            "Logging in", Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "An account with that email already exists", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
