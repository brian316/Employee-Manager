package edu.csusb.cse408.managerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText name;
    EditText id;
    EditText dept;
    EditText title;

    Button next;

    // Using this to pass the MainActivity view context to Task classes.
    // This is used to close the virtual keyboard
    Context context;

    // keeping track of spinner class option
    int option = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_emp_main);
        context = this;

        // spinner
        Spinner spinner = (Spinner) findViewById(R.id.employee_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.employee_actions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Set an item click listener from implementation
        // Can also call a 'new OnItemClickListener' instead of 'this' but this is prob better
        spinner.setOnItemSelectedListener(this);

        // Get ids of views
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        dept = findViewById(R.id.dept);
        title = findViewById(R.id.title);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action(view);
            }
        });
    }

    /**
     * Action to perform based on spinner option chosen
     * @param view sending current view to Task classes
     */
    private void action(View view) {
        if(this.option == 0){
            do_add(view);
        }
        if(this.option == 1){
            do_delete(view);
        }
        if(this.option == 2){
            do_update(view);
        }
    }

    /**
     * Delete function for DeleteItemsTask class
     * @param view sending current view to Task classes
     */
    public void do_delete(View view){
        new DeleteItemsTask(id.getText().toString(), view,context).execute();
    }

    /**
     * Add function for InsertItemsTask class
     * @param view sending current view to Task classes
     */
    public void do_add(View view){
        String n = name.getText().toString();
        String i = id.getText().toString();
        String d = dept.getText().toString();
        String t = title.getText().toString();
        // Check that all fields are used
        if(!n.isEmpty() && !i.isEmpty() && !d.isEmpty() && !t.isEmpty()) {
            new InsertItemsTask(n, i, d, t, view, context).execute();
        }else{
            // Virtual keyboard reference
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            // Hide keyboard
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            // Show error to user
            Snackbar.make(view, "Not all fields are filled!", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Update function for UpdateItemsTask class
     * @param view view sending current view to Task classes
     */
    public void do_update(View view){
        String id = this.id.getText().toString();
        String dept = this.dept.getText().toString();
        new UpdateItemsTask(id, dept, view).execute();
    }

    /**
     * Spinner implementation
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        // Used to move the position of the EditTextbox for a cleaner look
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        parent.getItemAtPosition(pos);
        // Change menu options based on selection
        if(pos == 0){
            findViewById(R.id.name).setVisibility(View.VISIBLE);
            findViewById(R.id.id).setVisibility(View.VISIBLE);

            params.addRule(RelativeLayout.BELOW, R.id.name);
            View dept = findViewById(R.id.dept);
            dept.setVisibility(View.VISIBLE);
            dept.setLayoutParams(params);

            findViewById(R.id.title).setVisibility(View.VISIBLE);
            this.option = 0;

        }
        if(pos == 1){
            findViewById(R.id.name).setVisibility(View.INVISIBLE);
            findViewById(R.id.id).setVisibility(View.VISIBLE);

            params.addRule(RelativeLayout.BELOW, R.id.name);
            View dept = findViewById(R.id.dept);
            dept.setVisibility(View.INVISIBLE);
            dept.setLayoutParams(params);

            findViewById(R.id.title).setVisibility(View.INVISIBLE);
            this.option = 1;
        }
        if(pos == 2){
            findViewById(R.id.name).setVisibility(View.INVISIBLE);
            findViewById(R.id.id).setVisibility(View.VISIBLE);

            params.addRule(RelativeLayout.BELOW, R.id.id);
            View dept = findViewById(R.id.dept);
            dept.setVisibility(View.VISIBLE);
            dept.setLayoutParams(params);

            findViewById(R.id.title).setVisibility(View.INVISIBLE);
            this.option = 2;
        }

    }

    // spinner implementation
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class UpdateItemsTask extends AsyncTask<Void,Void, Integer>{

        public View view;
        public String id;
        public String dept;

        public UpdateItemsTask(String id, String dept, View view){
            this.view = view;
            this.id = id;
            this.dept = dept;
        }
        @Override
        protected void onPostExecute(Integer statusCode) {
            //super.onPostExecute(statusCode);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

            if(statusCode == 200) {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Successfully deleted user, redirect to main page
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("action", "update");
                context.startActivity(intent);
            }else {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Show error to user
                Snackbar.make(view, "Cannot update user!", Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return new DataFetchr().update(this.id,this.dept);
        }
    }

    class DeleteItemsTask extends AsyncTask<Void,Void, Integer> {

        public String id;
        public Context context;
        public View view;

        public DeleteItemsTask(String id, View view, Context context){
            this.id = id;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int statusCode = 0;
            try{
                statusCode = new DataFetchr().delete(this.id);
            }
            finally {
                return statusCode;
            }

        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

            if(statusCode == 200) {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Successfully deleted user, redirect to main page
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("action", "delete");
                context.startActivity(intent);
            }else {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Show error to user
                Snackbar.make(view, "Cannot delete user!", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    class InsertItemsTask extends AsyncTask<Void,Void, Integer> {

        public String id;
        public String name;
        public String dept;
        public String title;
        public Context context;
        public View view;

        public InsertItemsTask(String name, String id, String dept, String title, View view, Context context){
            this.id = id;
            this.context = context;
            this.view = view;
            this.name = name;
            this.dept = dept;
            this.title = title;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int statusCode = 0;
            try{
                statusCode = new DataFetchr().insert(this.name,this.id,this.dept,this.title);
            }
            finally {
                return statusCode;
            }

        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

            if(statusCode == 200) {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Successfully deleted user, redirect to main page
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("action", "add");
                context.startActivity(intent);
            }else {
                // Hide keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                // Show error to user
                Snackbar.make(view, "Cannot add user!", Snackbar.LENGTH_SHORT).show();
            }

        }
    }


}


