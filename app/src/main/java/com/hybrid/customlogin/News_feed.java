package com.hybrid.customlogin;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class News_feed extends Fragment implements Comment.OnFragmentInteractionListener{

    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    Comment c;
    String TAG = "ADA";
    public News_feed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        getUrlStrings(recyclerView, swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        return view ;
    }
    public void refresh() {
        BackgroundTask backgroundTask = new BackgroundTask(getContext(), recyclerView, swipeRefreshLayout);
        backgroundTask.execute("refresh");
    }
    public void getUrlStrings(RecyclerView v, SwipeRefreshLayout vi) {
        BackgroundTask backgroundTask;
        backgroundTask = new BackgroundTask(getContext(), v, vi);
        backgroundTask.execute("html_link");
        Log.e(TAG, "getUrlsStrings");
        Log.e("ADA", "before request");
    }




    @Override
    public void onResume() {
        BackgroundTask backgroundTask;
        backgroundTask = new BackgroundTask(getContext(), recyclerView, swipeRefreshLayout);
        backgroundTask.execute("html_link");
        Log.e(TAG, "getUrlsStrings");
        Log.e("ADA", "before request");
        super.onResume();
    }

}
