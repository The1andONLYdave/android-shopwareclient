package com.dlka.shopwareclient;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Login Activity Class
 */
public class LoginActivity extends Activity {
    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Email Edit View Object
    EditText emailET;
    // Passwpod Edit View Object
    EditText pwdET;
    EditText urlET;
    EditText pthET;
    TextView resultView;
    private Spinner spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Find Error Msg Text View control by ID
        errorMsg = (TextView) findViewById(R.id.login_error);
        // Find Email Edit View control by ID
        emailET = (EditText) findViewById(R.id.loginName);
        // Find Password Edit View control by ID
        pwdET = (EditText) findViewById(R.id.loginPassword);
        urlET = (EditText) findViewById(R.id.loginURL);
        resultView = (TextView) findViewById(R.id.login_result);
        pthET = (EditText) findViewById(R.id.loginURLpath);
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        addListenerOnSpinnerItemSelection();

        //TODO: make save prefs
        pthET.setText("caches");
        emailET.setText("android");
        pwdET.setText("f3POOlbHbrkcESgf1RnKJKNFrBUbmXqlFNHwgMF6");
        urlET.setText("192.168.1.146");

    }

    /**
     * Method gets triggered when Login button is clicked
     */
    public void loginUser(View view) {
        // Get Email Edit View Value
        String email = emailET.getText().toString();
        // Get Password Edit View Value
        String password = pwdET.getText().toString();
        // Get Password Edit View Value
        String url = urlET.getText().toString();
        String path = pthET.getText().toString();

        if (email.isEmpty()) { YoYo.with(Techniques.Wobble).duration(700).playOn(findViewById(R.id.loginName));}
        else if (password.isEmpty()) { YoYo.with(Techniques.Pulse).duration(700).playOn(findViewById(R.id.loginPassword));}
        else if (url.isEmpty()) { YoYo.with(Techniques.RubberBand).duration(700).playOn(findViewById(R.id.loginURL));}
        else if (path.isEmpty()) { YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.loginURLpath));}
        else{
            // Put Http parameter username with value of Email Edit View control
            // Invoke RESTful Web Service with Http parameters

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                prgDialog.show();
                String[] urlstring = {email, password, url, path};
                new invokeWS().execute(urlstring);
            } else {
                Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_LONG).show();
            }

        }



    }

    public String parser(String result) {

        Log.e("UtilityDebug", "called parser()");
        String serverCode = result.substring(0, 3);
        String json = result.substring(3);
        Log.d("UtilityDebug:json fill", json);
        String success = "";
        String message = "";
        String totalcount = "";
        String cacheNames = "";

        //error message before processing
        errorMsg.setText(serverCode);


        if (serverCode.equals("200")) {
            Toast.makeText(getApplicationContext(), "Server returned Error " + serverCode, Toast.LENGTH_LONG).show();
        }


        try {
            JSONObject jObj = new JSONObject(json);
            try {
                success = jObj.getString("success");

                //if success furher parsing
                try {
                    message = jObj.getString("message");
                } catch (JSONException e) {
                    Log.e("JSON Obj message:", "json string error" + e.toString());
                }
                try {
                    totalcount = "Counting: " + jObj.getString("total") + "\n";
                } catch (JSONException e) {
                    Log.e("JSON Obj total:", "json string error" + e.toString());
                }

                JSONArray jArr = jObj.getJSONArray("data");
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    Log.d("for-loop", i + obj.toString());
                    Log.d("for-loop-Caches", obj.getString("id"));
                    cacheNames = cacheNames + obj.getString("id") + "\n";
                }

            } catch (JSONException e) {
                Log.e("JSON Obj parsing:", "json string error" + e.toString());
            }
        } catch (JSONException e) {
            Log.e("JSON Obj create:", e.toString());

        }

//		JSONObject subObj = jObj.getJSONObject("message");
//		String city = subObj.getString("city");


        String answer = message + totalcount + cacheNames;

        return answer;
    }

    public void navigatetoSettingsActivity(View view) {
        Intent homeIntent = new Intent(getApplicationContext(),SettingsActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        //String.valueOf(spinner1.getSelectedItem();
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param
     */
    private class invokeWS extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String json_string;

            try {
                String email, password, url, path;

                email = urls[0];
                password = urls[1];
                url = urls[2];
                path = urls[3];

                ///TODO: switch if https
                HttpHost targetHost = new HttpHost(url, 80, "http");
                Log.i("targetHost.getHostName", targetHost.getHostName());

                DefaultHttpClient httpclient = new DefaultHttpClient();

                try {
                    // Store the user login

                    httpclient.getCredentialsProvider().setCredentials(
                            new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                            new UsernamePasswordCredentials(email, password));

                    Log.i("targetHost.getHostName", targetHost.getHostName());

                    // Create AuthCache instance
                    AuthCache authCache = new BasicAuthCache();
                    // Generate BASIC scheme object and add it to the local
                    // auth cache
                    BasicScheme basicAuth = new BasicScheme();
                    authCache.put(targetHost, basicAuth);

                    // Add AuthCache to the execution context
                    BasicHttpContext localcontext = new BasicHttpContext();
                    localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);

                    // Create request
                    // You can also use the full URI http://www.google.com/
                    HttpGet httpget = new HttpGet("/api/" + path);
                    // Execute request
                    HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
                    json_string = EntityUtils.toString(response.getEntity());
                    Log.i("LoginActivityDebug", json_string);
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.d("LoginActivityDebug", Integer.toString(statusCode));
                    json_string = Integer.toString(statusCode) + json_string;

                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";

            }
            return json_string;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("calling parser:", result);
            resultView.setText(parser(result));
            //Toast.makeText(getApplicationContext(), result.substring(0,3), Toast.LENGTH_LONG).show();
            prgDialog.hide();
        }
    }

}

