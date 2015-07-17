package com.prgguru.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class which has Utility methods
 * 
 */
public class Utility {
	private static Pattern pattern;
	private static Matcher matcher;
	//Email Pattern
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * Validate Email with regular expression
	 * 
	 * @param email
	 * @return true for Valid Email and false for Invalid Email
	 */
	public static boolean validate(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
 
	}
	/**
	 * Checks for Null String object
	 * 
	 * @param txt
	 * @return true for not null and false for null String object
	 */
	public static boolean isNotNull(String txt){
		return txt!=null && txt.trim().length()>0 ? true: false;
	}

	public static String parser(String result){

        String serverCode=result.substring(0,3);
        String json=result.substring(3);
        String success="";

       try {
           JSONObject jObj = new JSONObject(result);
           success = jObj.getString("success");

       } catch (JSONException e) {
           //Log.e("Json error",e);
       }

//		JSONObject subObj = jObj.getJSONObject("message");
//		String city = subObj.getString("city");

//		JSONArray jArr = jObj.getJSONArray("list");
//		for (int i=0; i < jArr.length(); i++) {
//			JSONObject obj = jArr.getJSONObject(i);



		return success;
	}
}
