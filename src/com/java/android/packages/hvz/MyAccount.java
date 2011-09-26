package com.java.android.packages.hvz;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.message.BasicNameValuePair;

import com.java.android.util.Functions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyAccount extends Activity
{
	EditText current_password;
	EditText new_password;
	EditText confirm_password;
	private List<String> errors = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.my_account);

        current_password = (EditText) findViewById(R.id.current_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        
        TextView tag_id = (TextView) findViewById(R.id.my_tag_id);
        String myTag = "Your ID is " + getTagID() + ".";
        tag_id.setText(myTag);
        
        Functions.textTip(current_password, "Current Password", true);
        Functions.textTip(new_password, "New Password", true);
        Functions.textTip(confirm_password, "Confirm Password", true);
    }
	
	public static String getTagID()
	{
		HttpGet httpGet = new HttpGet(HvZ.siteURL + "/getID.php");
		HttpClientParams.setRedirecting(httpGet.getParams(), false);
		try
		{
			HttpResponse response = Login.httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 302)
			{
				//a status code of 302 means that it needs to redirect to index.php, therefore not logged in
				return "";
			}
			else if(response.getStatusLine().getStatusCode() == 200)
			{
				//a status code of 200 means it is at getID.php, parse tag
				InputStream is = response.getEntity().getContent();
		        String content = Functions.generateString(is);
		        String tagID = content.substring(2, content.length() - 2);
				return tagID;
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
		return "";
	}
	
	public static boolean isZombie() throws UnsupportedEncodingException
	{
		HttpPost httpPost = new HttpPost(HvZ.siteURL + "/kill.php");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("victim_id", getTagID()));
        nameValuePairs.add(new BasicNameValuePair("feed1", ""));
        nameValuePairs.add(new BasicNameValuePair("feed2", ""));
        nameValuePairs.add(new BasicNameValuePair("hour", "0"));
        nameValuePairs.add(new BasicNameValuePair("minute", "0"));
        nameValuePairs.add(new BasicNameValuePair("day", "1"));
        nameValuePairs.add(new BasicNameValuePair("submit", "Report Kill"));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		try
		{
			HttpResponse response = Login.httpClient.execute(httpPost);
			InputStream is = response.getEntity().getContent();
	        String content = Functions.generateString(is);
	        if(content.contains("Eating that person won't help you.")) //means you're a zombie, cant eat yourself
	        {
	        	return true;
	        }
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void changePassword(View v) throws ClientProtocolException, IOException
	{
		String current_pass = current_password.getText().toString();
		String new_pass = new_password.getText().toString();
		String confirm_pass = confirm_password.getText().toString();
		
		if(current_pass.equals("Current Password"))
			current_pass = "";
		if(new_pass.equals("New Password"))
			new_pass = "";
		if(confirm_pass.equals("Confirm Password"))
			confirm_pass = "";
		
		if(!current_pass.equals(Login.currentPassword))
		{
			errors.add("Current password is incorrect");
		}
		
		if(!new_pass.equals(confirm_pass))
		{
			errors.add("Passwords don't mach");
		}
		
		if(new_pass.length() < 4)
		{
			errors.add("New password is too short");
		}
		
		if(errors.isEmpty())
		{
			HttpPost httpPost = new HttpPost(HvZ.siteURL + "/account.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("pass_original", current_pass));
		    nameValuePairs.add(new BasicNameValuePair("pass_new", new_pass));
		    nameValuePairs.add(new BasicNameValuePair("pass_confirm", confirm_pass));
		    nameValuePairs.add(new BasicNameValuePair("submit", "Change Password"));
		    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    Login.currentPassword = new_pass;
				
			Login.httpClient.execute(httpPost);
			
	    	//startActivity(new Intent(this, MyAccount.class));
	    	finish();
		}
		else
		{
			showDialog(0);
		}
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
	
	public void cancel(View v)
	{
		finish();
	}
}
