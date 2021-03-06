package com.project.cyim.masterchef;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * Created by yu fen on 2017/8/10.
 * Ingredient
 */

public class ReciFrag extends Fragment{
    ListView ingredient_list;

    public ReciFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_reci, container, false);
        ingredient_list = (ListView)v.findViewById(R.id.ingredient_list);

        int id = ((OnFragmentInteractionListener)getActivity()).recipe_id();
        new RecipeData(this).execute(id + "");
        return v;
    }

    // Get Recipe detail
    class RecipeData extends AsyncTask<String, String, String> {
        private ReciFrag context;

        public RecipeData(ReciFrag context) {
            this.context = context;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String id = (String) arg0[0];

                String ip = "http://140.135.113.99";
                String link = ip + "/GetRecipeIngredient.php";
                String data = URLEncoder.encode("id", "UTF-8") + "=" +
                        URLEncoder.encode(id, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
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

        //String item2;
        @Override
        protected void onPostExecute(String result) {
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
            try {
                JSONArray reci = new JSONArray(result);
                for (int i = 0; i < reci.length(); i++) {
                    JSONObject c = reci.getJSONObject(i);

                    HashMap<String, String> item = new HashMap<>();
                    item.put("NAME", c.getString("ingredient"));
                    item.put("WEIGHT", c.getString("weight"));

                    list.add(item);
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
            Log.i("ingredient", list.toString());
            IngredientAdapter adapter = new IngredientAdapter(getContext(), list);
            ingredient_list.setAdapter(adapter);
        }
    }

    // Ingredient Adapter
    class IngredientAdapter extends ArrayAdapter
    {
        Context context;
        ArrayList<HashMap<String,String>> list;

        public IngredientAdapter(Context context, ArrayList<HashMap<String,String>> list)
        {
            super(context,R.layout.ingredient_row,list);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.list = list;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.ingredient_row, parent, false);
            HashMap<String, String> item = list.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView weight = (TextView) convertView.findViewById(R.id.weight);
            name.setText(item.get("NAME"));
            weight.setText(item.get("WEIGHT"));
            //TextView weight = (TextView) convertView.findViewById(R.id.weight);
            //weight.setText(item.get("WEIGHT"));

            return convertView;
        }
    }
}

