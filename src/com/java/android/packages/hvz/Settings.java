package com.java.android.packages.hvz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.java.android.packages.hvz.R;
import com.java.android.util.Functions;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends Activity
{
	private EditText siteURL;
	private List<String> errors = new ArrayList<String>();
	
	public Settings()
	{
		super();
	}
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        siteURL = (EditText) findViewById(R.id.site_url);

        siteURL.setText(HvZ.siteURL);
    }
	
	public void saveSettings(View v) throws IOException
	{
        String url = siteURL.getText().toString();
        
        if(url.equals(""))
        {
        	errors.add("URL can't be empty");
			showDialog(0);
        	return;
        }
        else if(!(url.substring(0, 7).equals("http://")))
        {
        	errors.add("URL needs to begin with 'http://'");
        	showDialog(0);
        	return;
        }
        else if(url.substring(7).equals(""))
        {
        	errors.add("URL after 'http://' can't be empty");
        	showDialog(0);
        	return;
        }
        
        if(url.contains("\n"))
        {
        	url = url.replaceAll("\n", "");
        }
        
        if(Login.isLoggedIn() && !url.equals(HvZ.siteURL)) //logout if user changes site url in mid-login
        {
        	Login.logout();
        }
        
		HvZ.setURL(url);
		
		finish();
	}
	
	public void cancel(View v)
	{
		finish();
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
}