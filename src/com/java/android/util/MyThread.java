package com.java.android.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;

import com.java.android.packages.hvz.HvZ;

public class MyThread extends Thread
{
	public String s1;
	public String s2;
	
	public MyThread(String s1, String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
	}
}
