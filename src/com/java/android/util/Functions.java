package com.java.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class Functions
{
	public static void textTip(EditText editText, String defaultText)
	{
		textTip(editText, defaultText, false);
	}
	
	public static void textTip(final EditText editText, final String defaultText, final boolean password)
	{
		editText.setTextColor(Color.parseColor("#808080"));
		
		editText.setOnFocusChangeListener(new OnFocusChangeListener()
		{
		    public void onFocusChange(View v, boolean hasFocus)
		    {
		    	if(hasFocus)
		    	{
		    		if(editText.getText().toString().equals(defaultText))
		    		{
			    		if(password)
			    		{
			    			editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			    			editText.setTransformationMethod(new PasswordTransformationMethod());
			    		}
			    		editText.setText("");
			    		editText.setTextColor(Color.parseColor("#000000"));
		    		}
		    	}
		    	else
		    	{
			    	if(editText.getText().toString().equals(""))
			    	{
			    		editText.setInputType(InputType.TYPE_CLASS_TEXT);
			    		editText.setTransformationMethod(null);
			    		editText.setText(defaultText);
			    		editText.setTextColor(Color.parseColor("#808080"));
			    	}
		    	}
		    }
		});
	}
	
	public static String generateString(InputStream stream)
	{
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();

		try
		{
			String cur;
			while ((cur = buffer.readLine()) != null)
			{
				sb.append(cur + "\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return sb.toString();
	}
}
