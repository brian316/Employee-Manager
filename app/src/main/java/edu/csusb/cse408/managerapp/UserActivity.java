package edu.csusb.cse408.managerapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class UserActivity extends AppCompatActivity {

    EditText name;
    EditText id;
    EditText dept;
    EditText title;

    Button add;
    Button delete;
    Button update;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_emp_main);

        context = this;

        // Get ids of views
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        dept = findViewById(R.id.dept);
        title = findViewById(R.id.title);
        add = findViewById(R.id.adduser);
        delete = findViewById(R.id.deleteuser);
        update = findViewById(R.id.updateuser);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                do_delete(view);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                do_add(view);
            }
        });
    }

    public void do_delete(View view){
        new DeleteItemsTask(id.getText().toString(), view,context).execute();
    }
    public void do_add(View v){
        String n = name.getText().toString();
        String i = id.getText().toString();
        String d = dept.getText().toString();
        String t = title.getText().toString();
        new InsertItemsTask(n,i,d,t,v,context).execute();
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


