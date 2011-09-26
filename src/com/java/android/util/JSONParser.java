package com.java.android.util;

import java.util.ArrayList;
import java.util.List;

public class JSONParser
{
	public static List<String[]> parse(String s)
	{
		int i;
		boolean inBracket = false;
		boolean inFirstVariable = true;
		boolean inComma = false;
		boolean inQuote = false;
		List<String[]> list = new ArrayList<String[]>();
		String[] array = new String[2];
		String variable = new String();
		
		for(i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == '{' && !inQuote)
			{
				inBracket = true;
			}
			else if(s.charAt(i) == '"')
			{
				inQuote = !inQuote;
				if(inFirstVariable && !inQuote)
				{
					array[0] = variable;
					variable = new String();
				}
				else if(!inQuote)
				{
					array[1] = variable;
					variable = new String();
					list.add(array);
					array = new String[2];
				}
			}
			else if(s.charAt(i) == ':' && !inQuote)
			{
				inFirstVariable = false;
			}
			else if(s.charAt(i) == '}' && !inQuote)
			{
				inBracket = false;
			}
			else if(s.charAt(i) == ',' && !inQuote)
			{
				inFirstVariable = true;
			}
			else
			{
				if(inQuote)
				{
					variable = variable + s.charAt(i);
				}
			}
		}
		
		return list;
	}
}
