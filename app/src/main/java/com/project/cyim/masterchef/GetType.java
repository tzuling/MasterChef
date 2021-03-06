package com.project.cyim.masterchef;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.project.cyim.masterchef.R.layout.myfridge_search;

/**
 * Created by user on 2017/8/8.
 */
public class GetType extends AppCompatActivity {
    TextView noresult;
    //ArrayList<String> result_list = new ArrayList<String>();
    ListView listview;

    public GetType() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myfridge_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_result));

        listview = (ListView)findViewById(R.id.list);
        noresult = (TextView)findViewById(R.id.noresult);

        String type = "";
        // get search items
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
               // type = "-1";
            } else {
                type = extras.getString("Type");
            }
        } else {
           type = (String)savedInstanceState.getSerializable("Type");
        }

        String str = "食譜類別:  " +  type ;
       // str = str.substring(0, str.length() - 1); // remove last character
        TextView result = (TextView)findViewById(R.id.items) ;
        result.setText(str);

        new RecipeGetType(this, "type").execute(type);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class RecipeGetType extends AsyncTask<String, String, String> {
        private GetType context;
        String task;

        public RecipeGetType(GetType context, String task) {
            this.context = context;
            this.task = task;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            // String task = (String) arg0[0];
            String type = (String) arg0[0] ;

            if (task.equals("type")) {
                String types = (String) arg0[0];
                try {
                    String ip = "http://140.135.113.99";
                    String link = ip + "/GetType.php?type=" +
                            URLEncoder.encode(type, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    //wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if ( result.equals("null") )
                noresult.setText(R.string.noresult);
            else {
                ArrayList<HashMap<String, String>> result_list = new ArrayList<HashMap<String, String>>();
                try {
                    JSONArray reci = new JSONArray(result);
                    for (int i = 0; i < reci.length(); i++) {
                        HashMap<String, String> item = new HashMap<>();
                        JSONObject c = reci.getJSONObject(i);
                        String title = c.getString("recipes_name");
                        String author = c.getString("user_id");
                        String thumbnail = c.getString("thumbnail");
                        String id = c.getString("recipes_id");

                        item.put("TITLE", title);
                        item.put("AUTHOR", author);
                        item.put("THUMBNAIL", thumbnail);
                        item.put("USERNAME", c.getString("fullname"));
                        item.put("ID", id);
                        result_list.add(item);
                    }

                } catch (final JSONException e) {
                }
                LazyAdapter adapter;
                adapter = new LazyAdapter(GetType.this,result_list);
                listview.setAdapter(adapter);
            } // else

        }
    }
}
