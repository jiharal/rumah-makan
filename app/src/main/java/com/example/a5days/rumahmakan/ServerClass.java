package com.example.a5days.rumahmakan;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerClass {
	
	public static String getUrl(String subUrl){
		return "http://iddota.hol.es/rm/"+subUrl;
		//return "http://10.0.2.2/rm/"+subUrl;
		//return "http://192.168.10.5/rm/"+subUrl;
		//return "http://pestoyk.greatplato.com/"+subUrl;
		//return "http://192.168.10.5/rm/"+subUrl;
	}
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		//client.setMaxRetriesAndTimeout(0, 10000);
		try {
			AsyncHttpClient client = new AsyncHttpClient();
			//client.setTimeout(30*1000);
			client.post(getUrl(url), params, responseHandler);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
