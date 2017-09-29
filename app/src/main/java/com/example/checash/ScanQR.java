package com.example.checash;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQR extends AppCompatActivity implements ZXingScannerView.ResultHandler{


    private ProgressDialog progress;
    private ZXingScannerView mScannerView;
    private HashMap<String, String> bill = new HashMap();
    private JSONObject jsonObject = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        mScannerView= (ZXingScannerView) findViewById(R.id.zxscan);
//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    public void gotomain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void QrScanner(View view) {

        mScannerView= (ZXingScannerView) findViewById(R.id.zxscan);
//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();


//        mScannerView.stopCamera();
//        setContentView(R.layout.activity_main);

        final TextView outputView = (TextView) findViewById(R.id.showOutput);
        String output = rawResult.getText();

        //parse Bill
        String[] keyVals = output.split("&");
        for (String keyVal : keyVals) {
            String[] parts = keyVal.split("=", 2);
            bill.put(parts[0], parts[1]);
        }

        // 3. build jsonObject
        try {
//            jsonObject.put("id", 1);
            jsonObject.put("fn", bill.get("fn"));
            jsonObject.put("i", bill.get("i"));
            jsonObject.put("fp", bill.get("fp"));
            jsonObject.put("n", bill.get("n"));
        }catch (JSONException e) {
            e.printStackTrace();
        }


        outputView.setText("fn=" + bill.get("fn") + " i=" + bill.get("i") + " fp=" + bill.get("fp") + " n=" + bill.get("n"));

        new PostClass(this).execute();
        // If you would like to resume scanning, call this method below:
         mScannerView.resumeCameraPreview(this);
    }

//    post/get request

    public void sendPostRequest(View View) {
        new PostClass(this).execute();
    }

    private class PostClass  extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c){

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection connection = null;

            try {

                final TextView outputView = (TextView) findViewById(R.id.showOutput);
                URL url = new URL("https://checash.herokuapp.com/user/1/add-bill/");

                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                final String json=jsonObject.toString();
                Log.i("JSON", json);

                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
//                os.writeBytes(URLEncoder.encode(json, "UTF-8"));
                os.writeBytes(json);
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post JSON : " + json);
                System.out.println("Response Code : " + responseCode);

                // 9. receive response as inputStream
                InputStream inputStream = null;
                String result = "";

                final StringBuilder output = new StringBuilder();
//                  output.append("Request URL " + url);
//                output.append(System.getProperty("line.separator") + "Request  " + json);
//                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
//                output.append(System.getProperty("line.separator")  + "Type " + "POST");

                int HttpResult =connection.getResponseCode();
                if(HttpResult==HttpURLConnection.HTTP_OK){
                    output.append("the Bill was addded");
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(),"utf-8"));
                    String line = null;
                    StringBuilder responseOutput = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line + "\n");
                    }
                    br.close();

                    System.out.println(""+responseOutput.toString());
//                    output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator")
//                            + System.getProperty("line.separator") + responseOutput.toString());

                }
                else{
//                    ByteArrayOutputStream out=new ByteArrayOutputStream();
//                    InputStream in =connection.getInputStream();
//                    throw new IOException(connection.getResponseMessage());
//                    int bytesRead;
//                    byte[] buffer=new byte[1024];
//                    while ((bytesRead=in.read(buffer))>0){
//                        out.write(buffer,0,bytesRead);
//                        String S=new String(out.toByteArray());
//                    output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator")
//                            + System.getProperty("line.separator") + connection.getResponseMessage());
//                    output.append("the Bill was addded");
                    output.append("error "+responseCode);
                    connection.disconnect();
                }

                ScanQR.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        outputView.setText(output);
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
