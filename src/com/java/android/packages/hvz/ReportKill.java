package com.java.android.packages.hvz;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.java.android.packages.hvz.R;
import com.java.android.util.*;

public class ReportKill extends Activity
{
	private EditText tagID;
	private Spinner day;
	private Spinner feed1;
	private Spinner feed2;
	private TimePicker time;
	List<String[]> feedList = new ArrayList<String[]>();
	List<String> errors = new ArrayList<String>();
	
	public ReportKill()
	{
		super();
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if(Login.isLoggedIn())
			initiateLayout();
		else
			startActivityForResult(new Intent(this, Login.class), 1);
	}
	
	@SuppressWarnings("unchecked")
	private void initiateLayout()
	{
		setContentView(R.layout.report_kill);
		
		tagID = (EditText) findViewById(R.id.tag_id);
		day = (Spinner) findViewById(R.id.day);
		feed1 = (Spinner) findViewById(R.id.feed1);
		feed2 = (Spinner) findViewById(R.id.feed2);
		time = (TimePicker) findViewById(R.id.time);
		
		Functions.textTip(tagID, "Tag ID");
		
		try
		{
			HttpGet httpGet = new HttpGet(HvZ.siteURL + "/listZombies.php");
			HttpResponse response = Login.httpClient.execute(httpGet);
			InputStream is = response.getEntity().getContent();
	        String content = Functions.generateString(is);
	        
	        //parse JSON reponse "ID":"Display
	        feedList = JSONParser.parse(content);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//set the day's dropdown list
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.days, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    day.setAdapter(adapter);
	    day.setSelection(1); //set selection to Today for default
	    
	    //set the feeds' dropdown lists
	    ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
	    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    adapter2.add("");//add blank person
	    for(int i = 0; i < feedList.size(); i++)
	    {
	    	adapter2.add(feedList.get(i)[1]);
	    }
	    feed1.setAdapter(adapter2);
	    feed2.setAdapter(adapter2);
	}
	
	public void reportKill(View v) throws UnsupportedEncodingException
	{
		String tagID = this.tagID.getText().toString();
		tagID.toUpperCase();
		int day = this.day.getSelectedItemPosition();
		int hour = time.getCurrentHour();
		int minute = time.getCurrentMinute();
		int feed1Pos = this.feed1.getSelectedItemPosition();
		int feed2Pos = this.feed2.getSelectedItemPosition();
		String feed1;
		String feed2;
		Date d = new Date();
		
		if(feed1Pos == 0)
			feed1 = "";
		else
			feed1 = feedList.get(feed1Pos - 1)[0];
		if(feed2Pos == 0)
			feed2 = "";
		else
			feed2 = feedList.get(feed1Pos - 1)[0];
		
		if(tagID.equals(""))
		{
			errors.add("Please provide a victim");
		}
		if(tagID.equals(MyAccount.getTagID()))
		{
			errors.add("Eating yourself won't help you");
		}
		if((day == 1) && ((hour > d.getHours()) || ((hour == d.getHours()) && (minute > d.getMinutes()))))
		{
			errors.add("You can't eat people from the future");
		}
		
		if(errors.isEmpty())
		{
			HttpPost httpPost = new HttpPost(HvZ.siteURL + "/kill.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("victim_id", tagID));
	        nameValuePairs.add(new BasicNameValuePair("feed1", feed1));
	        nameValuePairs.add(new BasicNameValuePair("feed2", feed2));
	        nameValuePairs.add(new BasicNameValuePair("hour", String.valueOf(hour)));
	        nameValuePairs.add(new BasicNameValuePair("minute", String.valueOf(minute)));
	        nameValuePairs.add(new BasicNameValuePair("day", String.valueOf(day)));
	        nameValuePairs.add(new BasicNameValuePair("submit", "Report Kill"));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			try
			{
				HttpResponse response = Login.httpClient.execute(httpPost);
				InputStream is = response.getEntity().getContent();
		        String content = Functions.generateString(is);
		        if(content.contains("The ID number you entered could not be found."))
		        {
		        	errors.add("The ID number you entered could not be found");
		        }
		        if(content.contains("Eating that person won't help you."))
		        {
		        	errors.add("Victim already zombified");
		        }
		        if(content.contains("is dead."))
		        {
		        	errors.add("One or both of your feeds are dead");
		        }
		        
		        if(errors.isEmpty())
		        {
			        startActivity(new Intent(this, ReportKill.class));
			    	finish();
		        }
		        else
		        {
		        	showDialog(0);
		        }
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 1)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				initiateLayout();
			}
			else if(resultCode == Activity.RESULT_CANCELED)
			{
				finish();
			}
		}
	}
}