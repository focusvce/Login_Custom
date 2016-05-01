package com.hybrid.customlogin;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Comment.OnFragmentInteractionListener{

    private MenuItem mSearchAction,mAddAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    ArrayList<Card_Object> objects = new ArrayList<>();
    String TAG = "ADA_MAIN";
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FOCUS");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        textView = (TextView) findViewById(R.id.text_comment);
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter.addFrag(new News_feed(), "Workshops");
        adapter.addFrag(new News_feed(), "Trending");
        adapter.addFrag(new News_feed(), "Sessions");
        viewPager.setAdapter(adapter);
    }



    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        mAddAction = menu.findItem(R.id.action_add);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                handleMenuSearch();
                return true;
            case R.id.action_add:
                handleMenuAdd();
                return true;
            case R.id.action_settings:
                Log.e("MAIN","starting user activity");
                startActivity(new Intent(this, UserProfile.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void handleMenuAdd(){
        startActivity(new Intent(this,Add_Post.class));
    }
    protected void handleMenuSearch(){
        final ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_black_24dp, null));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search);//add the custom view

            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        action.setDisplayShowCustomEnabled(false);
                        action.setDisplayShowTitleEnabled(true);
                        mSearchAction.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_black_24dp, null));
                        doSearch(edtSeach.getText().toString());
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_black_24dp, null));

            isSearchOpened = true;
        }
    }
    private void doSearch(String search) {

        BackgroundTask backgroundTask = new BackgroundTask(this,(RecyclerView)findViewById(R.id.recycler),(SwipeRefreshLayout) findViewById(R.id.swipe));
        backgroundTask.execute("search", search);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        ActionBar action = getSupportActionBar();
        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_black_24dp, null));

            isSearchOpened = false;
            return;
        }
        if((getSupportFragmentManager().findFragmentByTag("comment_frag"))!=null){
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag("comment_frag"));
            fragmentTransaction.commit();
            tabLayout.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
        else {
            super.onBackPressed();
            finish();
        }
    }
    public void texty(View v){
        Fragment fragment ;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment=fragmentManager.findFragmentByTag("comment_frag");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        tabLayout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }
    public void card_clicked(View view){

    }



}
