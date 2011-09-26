package com.java.android.packages.hvz;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;

import com.java.android.packages.hvz.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HvZ extends Activity
{
	public static String siteURL;
	public static SharedPreferences settings;
	public static Activity main;
	
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Login.initiateHttpClient();
        settings = getPreferences(Activity.MODE_PRIVATE);
	    siteURL = settings.getString("site_url", "http://game.hvzsource.com");
	    if(siteURL.equals("") || siteURL.equals("http://") || !(siteURL.substring(0, 7).equals("http://")))
	    {
	    	setURL("http://game.hvzsource.com");
	    }
	    if(siteURL.equals("http://game.hvzsource.com"))
	    {
	    	startActivity(new Intent(this, Settings.class));
	    }
	    
	    this.main = getParent();
        
        setContentView(R.layout.main);
    }
    
    public void onResume()
    {
    	super.onResume();

    	final Button login = (Button) findViewById(R.id.login_button);
    	final Button logout = (Button) findViewById(R.id.logout_button);
    	final Button reportKill = (Button) findViewById(R.id.report_kill);
    	final Button players = (Button) findViewById(R.id.players);
    	final Button myAccount = (Button) findViewById(R.id.my_account);
    	final Button register = (Button) findViewById(R.id.register);
    	
    	if(Login.isLoggedIn())
    	{
    		myAccount.setVisibility(View.VISIBLE);
    		players.setVisibility(View.VISIBLE);
    		logout.setVisibility(View.VISIBLE);
    		reportKill.setVisibility(View.VISIBLE);
    		login.setVisibility(View.GONE);
    		//register.setVisibility(View.GONE);
    		
    		HttpGet httpGet = new HttpGet(HvZ.siteURL + "/kill.php");
			HttpClientParams.setRedirecting(httpGet.getParams(), false);
			try
			{
				HttpResponse response = Login.httpClient.execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 302)
				{
					//a status code of 302 means that it needs to redirect to game_no_start.php, therefore game closed
					reportKill.setEnabled(false);
					String location = response.getLastHeader("location").getValue().toString();
					if(location.equals("game_no_start.php"))
					{
						reportKill.setText("Report Kill: Game Not Started");
					}
					else if(location.equals("game_over.php"))
					{
						reportKill.setText("Report Kill: Game Over");
					}
				}
				else if(response.getStatusLine().getStatusCode() == 200)
				{
					//a status code of 200 means it is at kill.php, therefore game open
					if(MyAccount.isZombie())
					{
						reportKill.setEnabled(true);
						reportKill.setText("Report Kill");
					}
					else
					{
						reportKill.setEnabled(false);
						reportKill.setText("Report Kill: You're Still a Human");
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
    	else
    	{
    		reportKill.setVisibility(View.GONE);
    		myAccount.setVisibility(View.GONE);
    		players.setVisibility(View.GONE);
    		logout.setVisibility(View.GONE);
    		login.setVisibility(View.VISIBLE);
    		//register.setVisibility(View.VISIBLE);
    		
    		HttpGet httpGet = new HttpGet(HvZ.siteURL + "/register.php");
			HttpClientParams.setRedirecting(httpGet.getParams(), false);
			try
			{
				HttpResponse response = Login.httpClient.execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 302)
				{
					//a status code of 302 means that it needs to redirect to reg_closed.php, therefore registration closed
					//register.setEnabled(false);
					//register.setText("Registration Closed");
				}
				else if(response.getStatusLine().getStatusCode() == 200)
				{
					//a status code of 200 means it is at register.php, therefore registration open
					//register.setEnabled(true);
					//register.setText("Register");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
    }
    
    public void settings(View v)
    {
    	startActivity(new Intent(this, Settings.class));
    }
    
    public void reportKill(View v)
    {
    	startActivity(new Intent(this, ReportKill.class));
    }
    
    public void myAccount(View v)
    {
    	startActivity(new Intent(this, MyAccount.class));
    }
    
    public void login(View v)
    {
    	startActivity(new Intent(this, Login.class));
    }
    
    public void players(View v)
    {
    	startActivity(new Intent(this, Players.class));
    }
    
    public void logout(View v)
    {
    	Login.logout();
    	
    	final Button login = (Button) findViewById(R.id.login_button);
    	final Button logout = (Button) findViewById(R.id.logout_button);
    	final Button reportKill = (Button) findViewById(R.id.report_kill);
    	final Button myAccount = (Button) findViewById(R.id.my_account);
    	final Button players = (Button) findViewById(R.id.players);
    	
    	reportKill.setVisibility(View.GONE);
		myAccount.setVisibility(View.GONE);
		players.setVisibility(View.GONE);
		logout.setVisibility(View.GONE);
		login.setVisibility(View.VISIBLE);
    }
    
    public void register(View v)
    {
    	startActivity(new Intent(this, Register.class));
    }
    
    public static void setURL(String url)
    {
    	HvZ.siteURL = url;
    	SharedPreferences.Editor edit = HvZ.settings.edit();
    	edit.putString("site_url", url);
    	edit.commit();
    }
}