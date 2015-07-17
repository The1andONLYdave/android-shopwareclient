package com.prgguru.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
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
				String rueckgabe=invokeWS(email, password, url);
            Toast.makeText(getApplicationContext(), rueckgabe, Toast.LENGTH_LONG).show();
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
	public String invokeWS(String email, String password, String url){
		// Show Progress Dialog
		 prgDialog.show();
        String BASE_URL="http://"+url+"/api";

        HttpHost targetHost = new HttpHost(url,80,"http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(email, password));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost,new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        SSLContext sslContext = SSLContexts.createSystemDefault();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

        HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
        try {
            HttpResponse response = client.execute(new HttpGet(BASE_URL), context);
            String json_string = EntityUtils.toString(response.getEntity());
            Log.i("JSON", json_string);
            int statusCode = response.getStatusLine().getStatusCode();
            Log.i("RESP",response.getEntity().getContent().toString());

            Log.i("STATUS", "" + statusCode);
            prgDialog.hide();

            return response.toString();

        }catch(Exception e){
            e.printStackTrace();
        }
        prgDialog.hide();

        return "";


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
