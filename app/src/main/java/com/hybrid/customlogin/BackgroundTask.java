package com.hybrid.customlogin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class BackgroundTask extends AsyncTask<String, Void, String> {

    int flag;
    int count;
    Context ctx;
    RecyclerView view;
    SwipeRefreshLayout v;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerAdapter adapter;
    RecyclerAdapter_Comment adapter_c;
    RecyclerView.LayoutManager layoutManager;
    String TAG = "async_task";
    ArrayList<Card_Object> objects = new ArrayList<>();
    ArrayList<Card_Object> objectr = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> usersc = new ArrayList<>();
    int id;
    String method;
    TextView img;
    int up_count;
    ProgressDialog progressDialog;
    ArrayList<Card_Object> search_objects = new ArrayList<>();
    ArrayList<Comment_Object> c_objects = new ArrayList<>();
    String[] uids = new String[0];
    String[] uidsc = new String[0];
    int j;
    JSONArray jsonArray1;
    JSONObject paramsss;
    TextView user_name, user_email;
    String name, email, user_pic = null;
    ImageView circular;

    public BackgroundTask(Context ct, RecyclerView v, SwipeRefreshLayout vi) {
        this.ctx = ct;
        this.view = v;
        this.v = vi;
        flag = 0;
        count = 0;
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        //progressDialog.show();
    }

    public BackgroundTask(Context ctx) {
        this.ctx = ctx;
    }

    public BackgroundTask(Context ct, TextView u, TextView e, ImageView i) {
        this.ctx = ct;
        this.user_name = u;
        this.user_email = e;
        this.circular = i;
    }

    public BackgroundTask(int id, Context ctx, TextView img, int up_count) {
        this.id = id;
        this.ctx = ctx;
        this.img = img;
        this.up_count = up_count;
    }


    @Override
    protected String doInBackground(String... params) {
        method = params[0];


        if (method.equals("html_link")) {
            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Utility.getIPADDRESS() + "json_test.php", future, future);
            String response = "";

            requestQueue.add(request);
            Log.e("ADA", "here");
            try {
                response = future.get().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray;


            try {

                final JSONArray jsonArray3 = new JSONArray(response);
                uids = new String[jsonArray3.length()];
                for (int i = 0; i < jsonArray3.length(); i++) {
                    uids[jsonArray3.length() - i - 1] = Integer.toString(jsonArray3.getJSONObject(jsonArray3.length() - i - 1).getInt("uid"));
                    Log.e("UIDS", uids[jsonArray3.length() - i - 1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Utility.getIPADDRESS() + "user_name_extract.php";


            try {
                jsonArray1 = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (j = 0; j < jsonArray1.length(); j++) {
                try {
                    paramsss = new JSONObject();
                    paramsss.put("uid", uids[j]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("2nd UIDS", uids[j]);
                    /*JSONArray paramss = new JSONArray();
                    paramss.put(0,uids[j]);*/

                RequestFuture<JSONObject> futureu = RequestFuture.newFuture();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Utility.getIPADDRESS() + "user_name_extract.php", paramsss, futureu, futureu);
                RequestQueue requestQueue2 = Volley.newRequestQueue(ctx);
                requestQueue2.add(jsObjRequest);
                try {
                    JSONObject responsee = futureu.get();//blocking code
                    Log.e("VALUE OF J:", uids[j]);
                    Log.e("RESPONSEE", responsee.getString("user_name"));
                    Log.e("VALUE OF J:", Integer.toString(j));
                    users.add(j, responsee.getString("user_name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            try {
                jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String html = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("link");
                    final String title = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("title");
                    String description = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("description");
                    int up = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("up");
                    int down = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("down");
                    int sr_key = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("sr_key");
                    int comment = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("comment_count");
                    //String u_name = jsonArray.getJSONObject(jsonArray.length()-i-1).getString("u_name");
                    Card_Object ob = new Card_Object(html, title, description, up, down, sr_key, comment, users.get(jsonArray.length() - i - 1));
                    objects.add(i, ob);
                    Log.e(TAG, html);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //progressDialog.dismiss();
            return response;

        } else if (method.equals("up_counter")) {

            String up_php = Utility.getIPADDRESS() + "up_count.php";
            String down_php = Utility.getIPADDRESS() + "down_count.php";
            String response = "";
            try {
                URL up_p;
                if (params[1].equals("0"))
                    up_p = new URL(up_php);
                else
                    up_p = new URL(down_php);
                HttpURLConnection httpURLConnection = (HttpURLConnection) up_p.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(id), "UTF-8") + "&" +
                        URLEncoder.encode("lid", "UTF-8") + "=" + URLEncoder.encode(ctx.getSharedPreferences("User_Id", Context.MODE_PRIVATE).getString("id", "")
                        , "UTF-8");
                Log.e(TAG, data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
                Log.e("UP RESPONSE", response);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else if (method.equals("refresh")) {
            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Utility.getIPADDRESS() + "json_test.php", future, future);
            String response = "";

            requestQueue.add(request);
            Log.e("ADA", "here");
            try {
                response = future.get().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray;
            try {

                final JSONArray jsonArray3 = new JSONArray(response);
                uids = new String[jsonArray3.length()];
                for (int i = 0; i < jsonArray3.length(); i++) {
                    uids[jsonArray3.length() - i - 1] = Integer.toString(jsonArray3.getJSONObject(jsonArray3.length() - i - 1).getInt("uid"));
                    Log.e("UIDS", uids[jsonArray3.length() - i - 1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Utility.getIPADDRESS() + "user_name_extract.php";


            try {
                jsonArray1 = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (j = 0; j < jsonArray1.length(); j++) {
                try {
                    paramsss = new JSONObject();
                    paramsss.put("uid", uids[j]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("2nd UIDS", uids[j]);
                    /*JSONArray paramss = new JSONArray();
                    paramss.put(0,uids[j]);*/

                RequestFuture<JSONObject> futureu = RequestFuture.newFuture();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Utility.getIPADDRESS() + "user_name_extract.php", paramsss, futureu, futureu);
                RequestQueue requestQueue2 = Volley.newRequestQueue(ctx);
                requestQueue2.add(jsObjRequest);
                try {
                    JSONObject responsee = futureu.get();//blocking code
                    Log.e("VALUE OF J:", uids[j]);
                    Log.e("RESPONSEE", responsee.getString("user_name"));
                    Log.e("VALUE OF J:", Integer.toString(j));
                    users.add(j, responsee.getString("user_name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String html = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("link");
                    String title = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("title");
                    String description = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("description");
                    int up = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("up");
                    int down = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("down");
                    int sr_key = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("sr_key");
                    int comment = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("comment_count");
                    Card_Object ob = new Card_Object(html, title, description, up, down, sr_key, comment, users.get(jsonArray.length() - i - 1));
                    /*if (flag == 0)
                        objectr.add(i, ob);
                    else if (flag == 1 && i < count)
                        objectr.set(i, ob);
                    else
                        objectr.add(i, ob);*/

                    objects.add(i, ob);
                }
                count = jsonArray.length();
                flag = 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        } else if (method.equals("search")) {
            String search_php = Utility.getIPADDRESS() + "search.php";
            String response = "";
            String search = params[1];
            try {
                URL up_p = new URL(search_php);
                HttpURLConnection httpURLConnection = (HttpURLConnection) up_p.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("search", "UTF-8") + "=" + URLEncoder.encode(search, "UTF-8");
                Log.e(TAG, data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("ADA", response);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String html = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("link");
                    String title = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("title");
                    String description = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("description");
                    int up = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("up");
                    int down = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("down");
                    int sr_key = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("sr_key");
                    int comment = jsonArray.getJSONObject(jsonArray.length() - i - 1).getInt("comment_count");
                    Card_Object ob = new Card_Object(html, title, description, up, down, sr_key, comment);
                    search_objects.add(i, ob);
                    Log.e(TAG, html);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
            return response;
        } else if (method.equals("add")) {
            String add_php = Utility.getIPADDRESS() + "add.php";
            String response = "";
            Log.e("ADD", "INSIDE ADD");
            try {
                URL up_p = new URL(add_php);
                HttpURLConnection httpURLConnection = (HttpURLConnection) up_p.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("link", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("desc", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(ctx.getSharedPreferences("User_Id", Context.MODE_PRIVATE).getString("id", "")
                        , "UTF-8");
                Log.e("ADD_DATA", data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
                Log.e("ADD", response);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "added";

        } else if (method.equals("get_comments")) {
            String getcomment_php = Utility.getIPADDRESS() + "comment_json.php";
            String response = "";
            String key = params[1];
            try {
                URL up_p = new URL(getcomment_php);
                HttpURLConnection httpURLConnection = (HttpURLConnection) up_p.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
                Log.e(TAG, data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
                Log.e("ADA_COM", response);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray;


            try {

                final JSONArray jsonArray3 = new JSONArray(response);
                uids = new String[jsonArray3.length()];
                for (int i = 0; i < jsonArray3.length(); i++) {
                    uids[jsonArray3.length() - i - 1] = Integer.toString(jsonArray3.getJSONObject(jsonArray3.length() - i - 1).getInt("uid"));
                    Log.e("UIDS", uids[jsonArray3.length() - i - 1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Utility.getIPADDRESS() + "user_name_extract.php";


            try {
                jsonArray1 = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (j = 0; j < jsonArray1.length(); j++) {
                try {
                    paramsss = new JSONObject();
                    paramsss.put("uid", uids[j]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("2nd UIDS", uids[j]);
                    /*JSONArray paramss = new JSONArray();
                    paramss.put(0,uids[j]);*/

                RequestFuture<JSONObject> futureu = RequestFuture.newFuture();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Utility.getIPADDRESS() + "user_name_extract.php", paramsss, futureu, futureu);
                RequestQueue requestQueue2 = Volley.newRequestQueue(ctx);
                requestQueue2.add(jsObjRequest);
                //users = new ArrayList<>();
                try {
                    JSONObject responsee = futureu.get();//blocking code
                    Log.e("VALUE OF J:", uids[j]);
                    Log.e("RESPONSEE", responsee.getString("user_name"));
                    Log.e("VALUE OF J:", Integer.toString(j));
                    users.add(j, responsee.getString("user_name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String uid = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("uid");
                    String comment = jsonArray.getJSONObject(jsonArray.length() - i - 1).getString("comment");

                    Comment_Object ob = new Comment_Object(comment, users.get(jsonArray.length()-i-1));

                    c_objects.add(i, ob);
                    Log.e("OBJECT", c_objects.get(i).getComment());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
            return response;
        } else if (method.equals("add_comment")) {
            String getcomment_php = Utility.getIPADDRESS() + "add_comment.php";
            String response = "";
            String key = params[2];
            String comment = params[1];
            try {
                URL up_p = new URL(getcomment_php);
                HttpURLConnection httpURLConnection = (HttpURLConnection) up_p.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8") + "&" +
                        URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(ctx.getSharedPreferences("User_Id", Context.MODE_PRIVATE).getString("id", "")
                        , "UTF-8") + "&" +
                        URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8");
                Log.e(TAG, data);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                IS.close();
                Log.e("ADA_COM", response);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        } else if (method.equals("user_activity_data")) {
            JSONObject jsonObject;
            jsonObject = new JSONObject();
            try {
                jsonObject.put("id", ctx.getSharedPreferences("User_Id", Context.MODE_PRIVATE).getString("id", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestFuture<JSONObject> futureu = RequestFuture.newFuture();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Utility.getIPADDRESS() + "get_user_detail.php", jsonObject, futureu, futureu);
            RequestQueue requestQueue2 = Volley.newRequestQueue(ctx);
            requestQueue2.add(jsObjRequest);
            try {
                JSONObject responsee = futureu.get();//blocking code
                Log.e("RES", responsee.getString("user_name"));
                name = responsee.getString("user_name");
                email = responsee.getString("user_email");
                user_pic = responsee.getString("user_pic");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        return null;
    }


    @Override
    protected void onPostExecute(String s) {

        //Log.e("UPPER",s);
        if (method.equals("html_link")) {

            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            adapter = new RecyclerAdapter(objects, ctx);
            layoutManager = new LinearLayoutManager(ctx);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        } else if (method.equals("refresh")) {
            progressDialog.dismiss();
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            adapter = new RecyclerAdapter(objects, ctx);
            layoutManager = new LinearLayoutManager(ctx);
            Log.e("ADA", "adapter created");
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setHasFixedSize(true);
        } else if (method.equals("up_counter"))
            img.setText(s);
        else if (method.equals("search")) {
            if (s.equals("[]e"))
                Toast.makeText(ctx, "No Results!!!!", Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(ctx, "Search Over", Toast.LENGTH_LONG).show();
                swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
                recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
                adapter = new RecyclerAdapter(search_objects, ctx);
                layoutManager = new LinearLayoutManager(ctx);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }
        } else if (s.equals("added"))
            Toast.makeText(ctx, "Post Added", Toast.LENGTH_LONG).show();
        else if (method.equals("get_comments")) {
            Log.e("GET_COMM", "onPost");
            SwipeRefreshLayout swipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_comment_focus);
            RecyclerView recyclerView1 = (RecyclerView) view.findViewById(R.id.commentsRecyclerView); //change
            //Log.e("ADA_COMM",c_objects.get(0).getComment());
            adapter_c = new RecyclerAdapter_Comment(c_objects, ctx);
            layoutManager = new LinearLayoutManager(ctx);
            recyclerView1.setAdapter(adapter_c);
            recyclerView1.setLayoutManager(new LinearLayoutManager(ctx));
        } else if (method.equals("user_activity_data")) {
            TextView n = (TextView) user_name.findViewById(R.id.user_activity_name);
            n.setText(name);
            TextView e = (TextView) user_email.findViewById(R.id.user_activity_email);
            e.setText(email);
            Bitmap bmImg = null;
            Log.e("USER_PIC","Hi");
            if (!user_pic.equals("")) {
                try {
                    URL url = new URL(user_pic);
                    Log.e("USER_PIC","HI");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bmImg = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ImageView view = (ImageView) circular.findViewById(R.id.circular_image);
                view.setImageBitmap(bmImg);
            }else{
                Log.e("BG","NULL");
                ImageView view = (ImageView) circular.findViewById(R.id.circular_image);
                view.setImageResource(R.drawable.defaultprofile);
            }

        }


    }


}
