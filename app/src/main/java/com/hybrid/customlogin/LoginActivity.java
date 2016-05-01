package com.hybrid.customlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private static final String LOGIN_URL = Utility.getIPADDRESS()+"login.php";
    private static final String REGISTER_URL = Utility.getIPADDRESS()+"register.php";
    StringRequest stringRequest = null;
    public static final String KEY_USEREMAIL = "user_email";
    public static final String KEY_PASSWORD = "user_password";
    public static final String KEY_USERNAME = "user_name";
    public static final String KEY_USERPIC = "user_pic";
    EditText input_email, input_password;
    ImageView logoImageView;
    TextView invalidPop;
    GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 9001;

    private static final String TAG = "SignInActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences1 = getSharedPreferences("com.hybrid.customlogin", MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("log_stat", MODE_PRIVATE);
        if (sharedPreferences1.getBoolean("firstrun", true) || sharedPreferences.getString("status", "false").equals("false")) {
            sharedPreferences1.edit().putBoolean("firstrun", false).apply();
            setContentView(R.layout.activity_login_activity);


        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            SharedPreferences sharedPreferences2 = getSharedPreferences("log_stat", MODE_PRIVATE);
            if (sharedPreferences2.getString("status", "false").equals("false")) {
                finish();
            } else if (sharedPreferences2.getString("status", "false").equals("true")) {
                setContentView(R.layout.activity_login_activity);

            }
        }
        invalidPop = (TextView) findViewById(R.id.invalidPop);
        logoImageView = (ImageView) findViewById(R.id.logoImageeView);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        Picasso.with(this).load(R.drawable.focus).into(logoImageView);
        // Configure sign-in to request the user's ID, email address, and basic
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

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);


    }


    public void loginUser(View view) {
        final String email = input_email.getText().toString().trim();
        if (email.isEmpty()) {
            input_email.setError("Email can't be empty!");
            return;
        }
        final String password = input_password.getText().toString().trim();
        if (password.isEmpty()) {
            input_password.setError("Password can't be empty!");
            return;
        } else if (password.length() < 8) {
            input_password.setError("Password should be at least 8 characters!");
            return;
        }

        //initialse your params

        if (Utility.validate(email)) {
            //handle params
            showProgressDialog();
            stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.contains("Welcome")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("log_stat", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putString("status", "true").apply();
                                String welcome = "W";
                                String[] parts = response.split("W");
                                String part1 = parts[0]; // 004
                                String part2 = welcome.concat(parts[1]);
                                SharedPreferences sh1 = getSharedPreferences("User_Id",MODE_PRIVATE);
                                sh1.edit().putString("id",part1).commit();
                                Toast.makeText(LoginActivity.this, part2, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                hideProgressDialog();
                                startActivity(intent);
                                invalidPop.setText("");
                                finish();
                            } else {
                                invalidPop.setText("Invalid email or password!");
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            if (!isOnline())
                                Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_LONG).show();
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_USEREMAIL, email);
                    params.put(KEY_PASSWORD, password);
                    return params;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else {
            input_email.setError("Enter a valid Email");
            return;
        }

    }


    public void navigateToRegister(View view) {

        startActivity(new Intent(LoginActivity.this, Register.class));

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            //get details and start an activity for results
            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            SharedPreferences sharedPreferences = getSharedPreferences("log_stat", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("status", "true").apply();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("Registration Success")) {


                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_USEREMAIL, acct.getEmail());
                    params.put(KEY_USERNAME, acct.getDisplayName());
                    //params.put(KEY_USERPIC,"");
                    if(acct.getPhotoUrl() != null)
                        params.put(KEY_USERPIC,acct.getPhotoUrl().toString());
                    else
                        params.put(KEY_USERPIC,"");
                    //Log.e(TAG, acct.getPhotoUrl().toString());
                    //params.put(KEY_PASSWORD, password);
                    return params;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.contains("Welcome")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("log_stat", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putString("status", "true").apply();
                                String welcome = "W";
                                String[] parts = response.split("W");
                                String part1 = parts[0]; // 004
                                String part2 = welcome.concat(parts[1]);
                                SharedPreferences sh1 = getSharedPreferences("User_Id",MODE_PRIVATE);
                                sh1.edit().putString("id",part1).commit();
                                Toast.makeText(LoginActivity.this, part2, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                hideProgressDialog();
                                startActivity(intent);
                                invalidPop.setText("");
                                finish();
                            } else {
                                invalidPop.setText("Invalid email or password!");
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            if (!isOnline())
                                Toast.makeText(LoginActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_LONG).show();
                            Log.e("Volley Error : ", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_USEREMAIL,acct.getEmail() );
                    params.put(KEY_PASSWORD, "");

                    return params;
                }

            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(this);
            requestQueue1.add(stringRequest);


        } else {
            // Signed out, show unauthenticated UI.

        }




    }


    // [END handleSignInResult]

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
