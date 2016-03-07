package com.panchuang.locatedemo.receiver;



import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";
	private static final String MMS_LOCATION = "location:";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");

		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			Log.i(TAG,sender);
			String body = smsMessage.getMessageBody();
			
			Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
			if(body.startsWith(MMS_LOCATION)){
				body.replace(MMS_LOCATION,"");
				abortBroadcast();
			}
			
		}
	}

}
