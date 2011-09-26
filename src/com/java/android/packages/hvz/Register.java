package com.java.android.packages.hvz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.java.android.util.Functions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends Activity
{
	private EditText firstName;
	private EditText lastName;
	private EditText username;
	private EditText password;
	private EditText confirm_password;
	private EditText email;
	private CheckBox oz_opt;
	private List<String> errors = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		firstName = (EditText) findViewById(R.id.fname);		
		lastName = (EditText) findViewById(R.id.lname);		
		username = (EditText) findViewById(R.id.reg_username);		
		password = (EditText) findViewById(R.id.reg_password);		
		confirm_password = (EditText) findViewById(R.id.reg_confirm_password);		
		email = (EditText) findViewById(R.id.email);
		oz_opt = (CheckBox) findViewById(R.id.oz_opt);

		Functions.textTip(firstName, "First Name");
		Functions.textTip(lastName, "Last Name");
		Functions.textTip(username, "Username");
		Functions.textTip(password, "Password", true);
		Functions.textTip(confirm_password, "Confirm Password", true);
		Functions.textTip(email, "Email");
	}
	
	public void register(View v) throws ClientProtocolException, IOException
	{
		String firstName = this.firstName.getText().toString();
		String lastName = this.lastName.getText().toString();
		String username = this.username.getText().toString();
		String password = this.password.getText().toString();
		String confirm_password = this.confirm_password.getText().toString();
		String email = this.email.getText().toString();
		String oz_opt;
		
		if(username.length() < 4)
		{
			errors.add("Username is too short.");
		}
		
		if(password.length() < 4)
		{
			errors.add("Password is too short.");
		}
		
		if(!password.equals(confirm_password))
		{
			errors.add("Passwords do not match.");
		}
		
		if(this.oz_opt.isChecked())
		{
			oz_opt = "oz";
		}
		else
		{
			oz_opt = "no";
		}
		
		if(errors.isEmpty())
		{
			HttpPost httpPost = new HttpPost(HvZ.siteURL + "/account.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("firstname", firstName));
		    nameValuePairs.add(new BasicNameValuePair("lastname", lastName));
		    nameValuePairs.add(new BasicNameValuePair("username", username));
		    nameValuePairs.add(new BasicNameValuePair("password1", password));
		    nameValuePairs.add(new BasicNameValuePair("password2", confirm_password));
		    nameValuePairs.add(new BasicNameValuePair("email_address", email));
		    nameValuePairs.add(new BasicNameValuePair("oz_opt", oz_opt));
		    nameValuePairs.add(new BasicNameValuePair("submit", "Register"));
		    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
			Login.httpClient.execute(httpPost);
			
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