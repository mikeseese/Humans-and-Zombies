package com.java.android.packages.hvz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.java.android.packages.hvz.R;
import com.java.android.util.Functions;
import com.java.android.util.MyThread;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity
{
	public static DefaultHttpClient httpClient;
	private EditText username;
	private EditText password;
	public static String currentPassword;
	private List<String> errors = new ArrayList<String>();
	public static Activity loginActivity;
	
	public Login()
	{
		super();
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		
		Login.loginActivity = getParent();
		
		Functions.textTip(username, "Username");
		Functions.textTip(password, "Password", true);
	}
	
	public void login(View v) throws UnsupportedEncodingException
	{
		MyThread t = new MyThread(this.username.getText().toString(), this.password.getText().toString())
		{
			public void run()
			{
				String username = s1;
				String password = s2;
				currentPassword = password;
				
				HttpPost httpPost = new HttpPost(HvZ.siteURL + "/index.php");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("user", username));
		        nameValuePairs.add(new BasicNameValuePair("pass", password));
		        nameValuePairs.add(new BasicNameValuePair("submit", "Login"));
				try
				{
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
				
				try
				{
					httpClient.execute(httpPost);
				} 
				catch (ClientProtocolException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				if(isLoggedIn())
				{
					setResult(Activity.RESULT_OK);
					finish();
				}
				else
				{
					errors.add("Wrong login information");
					showDialog(0);
				}
			}
		};
		t.start();
	}
    
    public static boolean isLoggedIn()
    {
    	if(httpClient != null)
    	{
    		HttpGet httpGet = new HttpGet(HvZ.siteURL + "/account.php");
			HttpClientParams.setRedirecting(httpGet.getParams(), false);
    		try
			{
				HttpResponse response = httpClient.execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 302)
				{
					//a status code of 302 means that it needs to redirect to index.php, therefore not logged in
					return false;
				}
				else if(response.getStatusLine().getStatusCode() == 200)
				{
					//a status code of 200 means it is at account.php, therefore logged in
					return true;
				}
			}
    		catch (ClientProtocolException e)
    		{
    			e.printStackTrace();
    		}
    		catch (IOException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return false;
    }
    
    public static void logout()
    {
    	HttpGet httpGet = new HttpGet(HvZ.siteURL + "/logout.php");
    	try
		{
			httpClient.execute(httpGet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    
    public void cancel(View v)
    {
    	setResult(Activity.RESULT_CANCELED);
    	finish();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
        	setResult(Activity.RESULT_CANCELED);
        	finish();
        }
        return false;
    }
	
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		switch(id)
		{
			case 0:
				String e = new String();

				dialog.setContentView(R.layout.error);
				dialog.setTitle("Errors");

				TextView text = (TextView) dialog.findViewById(R.id.text);
				for(int i = 0; i < errors.size(); i++)
				{
					e = e + errors.get(i) + "\n";
				}
				errors = new ArrayList<String>();
				text.setText(e);
				
				dialog.setCanceledOnTouchOutside(true);
				break;
		}
	}
	
	protected Dialog onCreateDialog(int id)
	{
	    Dialog dialog = null;
	    
	    switch(id)
	    {
	    case 0:
			dialog = new Dialog(this);
	        break;
	    case 1:
	        // do the work to define the game over Dialog
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}
    
    public static void initiateHttpClient()
    {
    	httpClient = new DefaultHttpClient();
    	httpClient.setCookieStore(new BasicCookieStore());
    }
}