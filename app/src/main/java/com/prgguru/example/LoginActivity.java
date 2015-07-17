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

import static com.prgguru.example.Utility.parser;

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
    TextView resultView;
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
        resultView = (TextView)findViewById(R.id.login_result);
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        //TODO: make save prefs
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
                prgDialog.show();
                String[] urlstring ={email, password, url};
                new invokeWS().execute(urlstring);
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

        @Override
        protected String doInBackground(String... urls) {
            String json_string;

            try {
                String email, password, url;

                email=urls[0];
                password=urls[1];
                url=urls[2];
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
                    HttpGet httpget = new HttpGet("/api/");
                    // Execute request
                    HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
                    json_string = EntityUtils.toString(response.getEntity());
                    Log.i("LoginActivityDebug", json_string);
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.d("LoginActivityDebug", Integer.toString(statusCode));
                    json_string =Integer.toString(statusCode)+ json_string;

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
            resultView.setText(parser(result));
            Toast.makeText(getApplicationContext(), result.substring(0,3), Toast.LENGTH_LONG).show();
            prgDialog.hide();
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
