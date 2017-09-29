package com.example.checash;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class getSegmentation extends AppCompatActivity {

    private ProgressDialog progress;
    DetailedBill[] listDetBills;
    String out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_get_bills);
            new getSegmentation.GetClass(this).execute();

        }

    public void sendGetRequest(View View) {
        new getSegmentation.GetClass(this).execute();
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public GetClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                final TextView outputView = (TextView) findViewById(R.id.showOutput);
                URL url = new URL("https://checash.herokuapp.com/user/1/get-statistics-short/");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                final int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder();

                final StringBuilder ReadableOutput=new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    StringBuilder responseOutput = new StringBuilder();
                    System.out.println("output===============" + br);
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line);
                    }
                    br.close();

                    output.append(responseOutput);

                } else {
                    ReadableOutput.append("error "+responseCode);
                    connection.disconnect();
                }
                getSegmentation.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        out = new String(output);


                        HashMap<String, String> nameAddresses = new HashMap<>();
//                        nameAddresses.put(out," ");
                        ListView resultsListView = (ListView) findViewById(R.id.results_listview);

                        String[] keyVals = out.substring(2,out.length()-1).split(",\"|\":");
                        for(int i=0;i<keyVals.length;i+=2){
                            int a=new Integer(keyVals[i+1])/100;
                            nameAddresses.put(keyVals[i], Integer.toString(a)+" rubles");
                        }

                        List<HashMap<String, String>> listItems = new ArrayList<>();
                        SimpleAdapter adapter = new SimpleAdapter(resultsListView.getContext(), listItems, R.layout.list_item,
                                new String[]{"First Line", "Second Line"},
                                new int[]{R.id.text1, R.id.text2});


                        Iterator it = nameAddresses.entrySet().iterator();
                        while (it.hasNext())
                        {
                            HashMap<String, String> resultsMap = new HashMap<>();
                            Map.Entry pair = (Map.Entry)it.next();
                            resultsMap.put("First Line", pair.getKey().toString());
                            resultsMap.put("Second Line", pair.getValue().toString());
                            listItems.add(resultsMap);
                        }

                        resultsListView.setAdapter(adapter);

                        progress.dismiss();

                    }
                });


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();

        }
    }
}
