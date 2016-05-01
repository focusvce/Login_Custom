package com.hybrid.customlogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    ArrayList<Card_Object> urls;
    Context ctx;
    private String TAG = "adarsh_adapter";

    public RecyclerAdapter(ArrayList<Card_Object> urls, Context ctx) {
        this.urls = urls;
        this.ctx = ctx;

    }

    RecyclerAdapter_Comment adapter_c;
    ArrayList<Comment_Object> c_objects = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;

    private PopupWindow popWindow;

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView) view.findViewById(R.id.url);
                String link = t.getText().toString();
                Log.e("ADA :", link);
                Log.e("ADA", ":HOLDER");

            }
        });
        Log.e(TAG, "onCreateViewHolder");
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.urll.setText(urls.get(position).getLink());
        holder.title.setText(urls.get(position).getTitle());
        holder.description.setText(urls.get(position).getDescription());
        holder.up.setText(Integer.toString(urls.get(position).getUp()));
        holder.shared_by.setText("Shared by:"+urls.get(position).getUser_name());

        holder.comment_count.setText(Integer.toString(urls.get(position).getComment_count()));


        holder.urll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String url = holder.urll.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setPackage("com.android.chrome");
                i.setData(Uri.parse(url));
                ctx.startActivity(i);*/
                Toast.makeText(ctx, "U clicked " + holder.urll.getText().toString(), Toast.LENGTH_LONG).show();
                Intent i;
                i = new Intent(ctx, LoginActivity.class);
                i.putExtra("url", holder.urll.getText().toString());
                ctx.startActivity(i);

            }
        });
        holder.up_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "up_clicked");
                int id = urls.get(position).getId();
                int up_n = urls.get(position).getUp();
                BackgroundTask backgroundTask = new BackgroundTask(id, ctx, holder.up,up_n);
                backgroundTask.execute("up_counter","0");
                //holder.up.setText(Integer.toString(up_n + 1));
                // holder.up_view.setColorFilter(R.color.colorAccent);

            }
        });
        holder.down_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"down_clicked");
                int id = urls.get(position).getId();
                int up_down = urls.get(position).getDown();
                BackgroundTask backgroundTask = new BackgroundTask(id, ctx, holder.up, up_down);
                backgroundTask.execute("up_counter","1");

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate the custom popup layout
                final View inflatedView = layoutInflater.inflate(R.layout.focus_popup_layout, null, false);
                // show animation
                inflatedView.setAnimation(AnimationUtils.loadAnimation(ctx, R.anim.popup_show));
                // find the ListView in the popup layout
                final RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.commentsRecyclerView);
                final SwipeRefreshLayout swi = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipe_comment_focus);
                // get device size
                Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE))
                        .getDefaultDisplay();

                final Point size = new Point();
                display.getSize(size);
                int mDeviceHeight = size.y;

                final EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);
                final ImageView sendButton = (ImageView) inflatedView.findViewById(R.id.sendButton);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BackgroundTask backgroundTask = new BackgroundTask(ctx);
                        if (!writeComment.getText().toString().equals("")) {
                            backgroundTask.execute("add_comment", writeComment.getText().toString(), Integer.toString(urls.get(position).getId()));
                            //setting edit text
                            writeComment.setText("");
                            //refresh comment section
                            BackgroundTask backgroundTaskforRefresh;
                            backgroundTaskforRefresh = new BackgroundTask(ctx, recyclerView, swi);
                            backgroundTaskforRefresh.execute("get_comments", Integer.toString(urls.get(position).getId()));
                            //hides the keyboard
                            if (view != null) {
                                ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                        hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        } else
                            Toast.makeText(ctx, "No Text Entered", Toast.LENGTH_SHORT).show();
                    }
                });


                // fill the data to the list items
                //setSimpleList(listView);
                setMyRecyclerView(recyclerView);

                BackgroundTask backgroundTask;
                backgroundTask = new BackgroundTask(ctx, recyclerView, swi);
                backgroundTask.execute("get_comments", Integer.toString(urls.get(position).getId()));


                // set height depends on the device size
                popWindow = new PopupWindow(inflatedView, size.x - 50, size.y - 250, true);
                // set a background drawable with rounders corners
                popWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(ctx.getResources(),R.drawable.focus_popup_bg, null));
                // make it focusable to show the keyboard to enter in `EditText`
                popWindow.setFocusable(true);
                // make it outside touchable to dismiss the popup window
                popWindow.setOutsideTouchable(true);

                // show the popup at bottom of the screen and set some margin at bottom ie,
                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);



                Log.e("ADA", "comment clicked");
                //Fragment c = new Comment().newInstance("FromActivity","");
                //((FragmentActivity)ctx).getSupportFragmentManager().findFragmentById(R.id.comment_fragm);
                /*Fragment c = new Comment();
                Bundle bundle = new Bundle();
                bundle.putInt("key", urls.get(position).getId());
                c.setArguments(bundle);
                FragmentTransaction ft = ((FragmentActivity)ctx).getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main, c, "comment_frag");
                ft.show(c);
                ft.commit();
                TabLayout tabLayout = (TabLayout) ((FragmentActivity)ctx).findViewById(R.id.tabs);
                tabLayout.setVisibility(View.INVISIBLE);
                TextView textView = (TextView) ((FragmentActivity)ctx).findViewById(R.id.text_comment);
                textView.setVisibility(View.VISIBLE);*/
            }
        });


    }

    private void setMyRecyclerView(RecyclerView recyclerView) {
        adapter_c = new RecyclerAdapter_Comment(c_objects, ctx);
        layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setAdapter(adapter_c);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
    }


    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView urll, title, description, up, down,comment_count,shared_by;
        ImageView up_view, comment,down_view;


        public RecyclerViewHolder(View v) {
            super(v);
            urll = (TextView) v.findViewById(R.id.url);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.description);
            up = (TextView) v.findViewById(R.id.up_count);
            up_view = (ImageView) v.findViewById(R.id.up);
            comment = (ImageView) v.findViewById(R.id.comment);
            down_view = (ImageView) v.findViewById(R.id.down);
            comment_count = (TextView) v.findViewById(R.id.comment_count);
            shared_by = (TextView) v.findViewById(R.id.shared_by);
        }
    }
       /*

        Just for testing list - can be removed


        void setSimpleList(ListView listView){

            ArrayList<String> contactsList = new ArrayList<String>();

            for (int index = 0; index < 10; index++) {
                contactsList.add("I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
            }

            listView.setAdapter(new ArrayAdapter<String>(ctx,
                    R.layout.focus_comment_list_item, android.R.id.text1, contactsList));
        }
           */





}

