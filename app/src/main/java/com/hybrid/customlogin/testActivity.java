package com.hybrid.customlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
public class testActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    CircleImageView circleImageView;
    TextView nameTV, emailTV;
    String photourl, name, email;
    GoogleApiClient mGoogleApiClient;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        circleImageView = (CircleImageView) findViewById(R.id.profilePic);
        nameTV = (TextView) findViewById(R.id.nameTV);
        emailTV = (TextView) findViewById(R.id.emailTV);
        Bundle result = getIntent().getExtras();
        intent = new Intent(testActivity.this,LoginActivity.class);

        if(result.getString("photoURL")==null)
            Picasso.with(this).load(R.drawable.ic_profile_black).into(circleImageView);
        else {
            photourl = result.getString("photoURL");
            Picasso.with(this).load(photourl).into(circleImageView);
        }
        name = result.getString("Name");
        email = result.getString("Email");
        nameTV.setText(name);
        emailTV.setText(email);



        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    public void logout(View view){
        signOut();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
     //Nothing special
    }



    // [START signOut]
    public void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        startActivity(intent);
                        finish();

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]
}
