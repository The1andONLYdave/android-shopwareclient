package com.prgguru.example;

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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;

//#import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
/**
 * 
 * Login Activity Class 
 *
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// Find Error Msg Text View control by ID
		errorMsg = (TextView)findViewById(R.id.login_error);
		// Find Email Edit View control by ID
		emailET = (EditText)findViewById(R.id.loginName);
		// Find Password Edit View control by ID
		pwdET = (EditText)findViewById(R.id.loginPassword);
		urlET = (EditText)findViewById(R.id.loginURL);
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        emailET.setText("android");
        pwdET.setText("f3POOlbHbrkcESgf1RnKJKNFrBUbmXqlFNHwgMF6");
        urlET.setText("192.168.1.146");
	}
	
	/**
	 * Method gets triggered when Login button is clicked
	 * 
	 * @param view
	 */
	public void loginUser(View view){
		// Get Email Edit View Value
		String email = emailET.getText().toString();
		// Get Password Edit View Value
		String password = pwdET.getText().toString();
		// Get Password Edit View Value
		String url = urlET.getText().toString();

		// When Email Edit View and Password Edit View have values other than Null
		if(Utility.isNotNull(email) && Utility.isNotNull(password) && Utility.isNotNull(url)){
				// Put Http parameter username with value of Email Edit View control
				// Invoke RESTful Web Service with Http parameters

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //String rueckgabe=invokeWS().execute(email, password, url);
                prgDialog.show();
                String urlstring =email+"..."+password+"..."+url;
                new invokeWS().execute(urlstring);
                Toast.makeText(getApplicationContext(), "rueckgabe", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_LONG).show();
            }

		} 
		// When any of the Edit View control left blank
		else{
			Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
		}
		
	}
	
	/**
	 * Method that performs RESTful webservice invocations
	 *
	 * @param params
	 */
	private class invokeWS extends AsyncTask<String, Void, String> {
        // Show Progress Dialog

        String email = "android";
        String password = "f3POOlbHbrkcESgf1RnKJKNFrBUbmXqlFNHwgMF6";
        String url = "192.168.1.146";

        @Override
        protected String doInBackground(String... urls) {


            try {
                ///TODO: switch if https
                HttpHost targetHost = new HttpHost(url, 80, "http");

                DefaultHttpClient httpclient = new DefaultHttpClient();
                try {
                    // Store the user login
                    httpclient.getCredentialsProvider().setCredentials(
                            new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                            new UsernamePasswordCredentials(email, password));

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
                    HttpGet httpget = new HttpGet("/API/");
                    // Execute request
                    HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);

                    HttpEntity entity = response.getEntity();
                    System.out.println(EntityUtils.toString(entity));

                    String json_string = EntityUtils.toString(response.getEntity());
                    Log.i("JSON", json_string);
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.i("RESP", response.getEntity().getContent().toString());

                    Log.i("STATUS", "" + statusCode);
                    //        prgDialog.hide();
                    //return downloadUrl(urls[0]);
                    return response.toString();


                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return "failed";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            urlET.setText(result);
        }
    }



	/**
	 * Method which navigates from Login Activity to Home Activity
	 */
	public void navigatetoHomeActivity(){
		Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}
	
	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */
	public void navigatetoRegisterActivity(View view){
		Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(loginIntent);
	}
	
}
