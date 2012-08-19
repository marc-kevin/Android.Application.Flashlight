package com.kevinmarc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenBroadcastReceiver extends BroadcastReceiver {

	private FlashlightActivity flashlightActivity;
	
	public ScreenBroadcastReceiver(FlashlightActivity flashlightActivity)
	{
		
		this.flashlightActivity = flashlightActivity;
	}
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	flashlightActivity.finish();
    }
}
