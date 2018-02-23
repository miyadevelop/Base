package com.lf.controler.tools.download.helper;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class LoadParam {

	private HashMap<String, String> mPostParams = new HashMap<String, String>();

	public LoadParam addPostParams(String key, String value)
	{
		mPostParams.put(key, value);
		return this;
	}


	private HashMap<String, String> mParams = new HashMap<String, String>();

	public LoadParam addParams(String key, String value)
	{
		mParams.put(key, value);
		return this;
	}

	
	public LoadParam addParams(Uri uri)
	{
		if(null == uri)
			return this;
		Set<String> names = uri.getQueryParameterNames();
		if(names.size() > 0)
		{
			for(String name : names)
			{
				addParams(name, uri.getQueryParameter(name));
			}
		}
		return this;
	}

	private HashMap<String, String> mHeadParams = new HashMap<String, String>();

	public LoadParam addHeadParams(String key, String value)
	{
		mHeadParams.put(key, value);
		return this;
	}


	public String toString()
	{
		String ret = "";
		for (Entry<String, String> entry : mParams.entrySet()) {  
			ret = ret + entry.getKey() + entry.getValue() + "-";
		}  
		for (Entry<String, String> entry : mPostParams.entrySet()) {  
			ret = ret + entry.getKey() + entry.getValue() + "-";
		}  
		for (Entry<String, String> entry : mHeadParams.entrySet()) {  
			ret = ret + entry.getKey() + entry.getValue() + "-";
		}  
		return ret;
	}


	public HashMap<String, String> getHeadParams() {
		return mHeadParams;
	}


	public HashMap<String, String> getParams() {
		return mParams;
	}

	public HashMap<String, String> getPostParams() {
		return mPostParams;
	}
}
