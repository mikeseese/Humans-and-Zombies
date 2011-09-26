package com.java.android.packages.hvz;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.java.android.util.Functions;
import com.java.android.util.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Players extends Activity
{
	List<String[]> players = new ArrayList<String[]>();
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        if(Login.isLoggedIn())
			initiateLayout();
		else
			startActivityForResult(new Intent(this, Login.class), 1);
    }
	
	private void initiateLayout()
	{
		setContentView(R.layout.players);
		
		try
		{
			HttpGet httpGet = new HttpGet(HvZ.siteURL + "/players.php");
			HttpResponse response = Login.httpClient.execute(httpGet);
			InputStream is = response.getEntity().getContent();
	        String content = Functions.generateString(is);
	        
	        //parse data
	        content = content.substring(content.indexOf("<table width=100% border>")); //remove previous junk
	        content = content.substring(0, content.indexOf("</table>")); //remove following junk
	        content = content.substring(content.indexOf("</tr>") + 5); //remove initial table contents
	        while(content.contains("</tr>"))
	        {
	        	String player = content.substring(0, content.indexOf("</tr>"));
	        	content = content.substring(content.indexOf("</tr>"));
	        	
	        	player = player.substring(player.indexOf("</td>") + 5); //remove image details
	        	
	        	String name = player.substring(4, player.indexOf("</td>"));
	        	player = player.substring(player.indexOf("</td>") + 5); //remove name details
	        	
	        	String state;
	        	if(player.charAt(4) == 'H')
	        		state = "Human";
	        	else
	        		state = "Zombie";
	        	player = player.substring(player.indexOf("</td>") + 5); //remove state details
	        	
	        	String[] current = new String[2];
	        	current[0] = name;
	        	current[1] = state;
	        	players.add(current);
	        }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
