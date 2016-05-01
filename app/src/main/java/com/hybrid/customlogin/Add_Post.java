package com.hybrid.customlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Add_Post extends AppCompatActivity {

    EditText link,title,desc;
    StringRequest stringRequest = null;
    private ProgressDialog mProgressDialog;
    Button button;
    private static final String ADD_URL = Utility.getIPADDRESS()+"add.php";
    private static final String KEY_LINK = "link";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__post);
        link = (EditText) findViewById(R.id.link_input);
        title = (EditText) findViewById(R.id.title_input);
        desc = (EditText) findViewById(R.id.desc_input);
        button = (Button) findViewById(R.id.enter);

    }
    public void button(View view){

        //Mota Background task
        //BackgroundTask backgroundTask = new BackgroundTask(this);
        //backgroundTask.execute("add", link.getText().toString(), title.getText().toString(), desc.getText().toString());


        //volley Asif
        showProgressDialog();
        stringRequest = new StringRequest(Request.Method.POST, ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.isEmpty()) {
                            Toast.makeText(Add_Post.this, "Post added", Toast.LENGTH_SHORT).show();
                           hideProgressDialog();
                            finish();
                        } else {
                           hideProgressDialog();
                            Toast.makeText(Add_Post.this, "Error while posting!", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       hideProgressDialog();
                        if(!isOnline())
                            Toast.makeText(Add_Post.this, "No internet connection!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Add_Post.this, "Error Occurred!", Toast.LENGTH_LONG).show();
                        Log.e("Volley Error : ", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_LINK, link.getText().toString());
                params.put(KEY_TITLE, title.getText().toString());
                params.put(KEY_DESC, desc.getText().toString());
                params.put("uid",getSharedPreferences("User_Id",Context.MODE_PRIVATE).getString("id",""));
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);




        finish();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

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

}
