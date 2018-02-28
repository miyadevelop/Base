package com.lf.entry;

import android.content.Context;
import android.content.Intent;

public interface EntryMatcherInterface {
	
	public void goTo(Context context, Intent intent);
	
	public String getKey();
}
