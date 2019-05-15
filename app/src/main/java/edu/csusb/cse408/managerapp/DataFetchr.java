package edu.csusb.cse408.managerapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Example to interact with Employee Management Web Server
 * TODO: add the rest of CRUD functions, i.e., insert, update, search
 *
 */

public class DataFetchr {
    private static final String TAG = "DataFetchr";
    //10.0.2.2 for Android Emulator refers to local host
    //Read more on:
    //Android Emulator Networking https://developer.android.com/studio/run/emulator-networking
    private static final String SERVER = "http://10.0.2.2:5000";

    /**
     * request HTTP GET method by url
      * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Parse received JSON string and store them in an ArrayList
     * @return
     */
    public ArrayList<String> fetchItems() {
        ArrayList<String> items = new ArrayList<String>();
        try {
            //the default port 80 for HTTP, 443 for HTTPS
            String url = Uri.parse(SERVER)
                    .buildUpon()
                    .appendPath("employee")
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            Iterator<String> iter = jsonBody.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                items.add(jsonBody.get(key).toString());
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        Log.i(TAG,items.toString());
        return items;
    }

    /**
     * Example of delete
     * Delete employee record by employee id, using HTTP DELETE
     * @param id
     * @return
     * @throws IOException
     */
    public String delete(String id) throws IOException {
        String delurl = Uri.parse(SERVER)
                .buildUpon()
                .appendPath("employee")
                .appendPath(id)
                .build().toString();
        URL url = new URL(delurl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("DELETE");
        StringBuilder temp = new StringBuilder();

        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine())!= null){
                temp.append(line).append('\n');
            }

        } finally {
            connection.disconnect();
        }
        return temp.toString();
    }
}