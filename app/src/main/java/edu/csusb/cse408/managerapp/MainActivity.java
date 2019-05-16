package edu.csusb.cse408.managerapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            action = bundle.getString("action");
            if(action.equals("delete")) {
                Snackbar.make(findViewById(R.id.main), "User Deleted!", Snackbar.LENGTH_LONG).show();
            }
            if(action.equals("add")){
                Snackbar.make(findViewById(R.id.main), "User Added!", Snackbar.LENGTH_LONG).show();
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeUser();
//                DeleteItemsTask delItem = new DeleteItemsTask("001", view);
//                delItem.execute();
//                new FetchItemsTask().execute();
            }
        });

        listView = findViewById(R.id.listview);
        new FetchItemsTask().execute();

    }

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
            listView.setAdapter(adapter);

            Iterator<String> iter = strings.iterator();
            while(iter.hasNext()){
                String obj = iter.next();
                if (obj.contains("001")){
                    Log.i("TestFetcher", "005: IN LIST!");
                } else{
                    Log.i("TestFetcher", "005: NOT IN LIST!");
                }
                Log.i("TestFetcher", obj);
            }
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
