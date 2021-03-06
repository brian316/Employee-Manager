package edu.csusb.cse408.managerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListView searchArray;
    EditText search;
    String action;

    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Do an action from Intent
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            action = bundle.getString("action");
            if(action.equals("delete")) {
                Snackbar.make(findViewById(R.id.main), "User Deleted!", Snackbar.LENGTH_LONG).show();
            }
            if(action.equals("add")){
                Snackbar.make(findViewById(R.id.main), "User Added!", Snackbar.LENGTH_LONG).show();
            }
            if(action.equals("update")){
                Snackbar.make(findViewById(R.id.main), "User Updated!", Snackbar.LENGTH_LONG).show();
            }
        }

        // search for a user when text is changed
        search = findViewById(R.id.searchuser);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                finder();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // pull to refresh logic
        swipe = findViewById(R.id.refresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                getData();
            }
        });

        // menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // button to take user to new UserActivity Activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeUser();
            }
        });

        // This is the array adapter layout to display
        listView = findViewById(R.id.listview);
        // This is the array adapter layout to search (VISIBILITY=HIDDEN)
        searchArray = findViewById(R.id.listviewCopy);
        getData();

    }

    /**
     * Fetch data from servers
     */
    public void getData(){
        new FetchItemsTask().execute();
    }

    /**
     * Constructs a new array list for listView adapter from searchArray.
     * searchArray is a copy of listView with a different resource value
     * from 'content_main' layout that is hidden from GUI
     */
    public void finder(){
        int items = searchArray.getAdapter().getCount();
        ArrayList<String> strings = new ArrayList<String>();
        for(int i = 0; i < items; i++){
            if(searchArray.getAdapter().getItem(i).toString().contains(search.getText().toString())){
                strings.add(searchArray.getAdapter().getItem(i).toString());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(adapter);
    }

    /**
     * Creates intent activity to go to UserActivity. To be used within an onClickListener
     */
    public void makeUser(){
        Intent userActivity = new Intent(this, UserActivity.class);
        startActivity(userActivity);
    }

    /**
     * Send HTTP GET request in background processes/threads
     */
    class FetchItemsTask extends AsyncTask<Void,Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return new DataFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
            // add the returned list into the array adapter
            listView.setAdapter(adapter);

            // copy of Array adapter
            searchArray.setAdapter(adapter);

            //end refresh animation
            swipe.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
