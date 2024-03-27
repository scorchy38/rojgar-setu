package com.up.rojgarsetu.Notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.EmployeeMainActivity;
import com.up.rojgarsetu.MainActivity;

import org.json.JSONObject;

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.

    Context context2;

    public NotificationOpenedHandler(Context context) {
        context2 = context;
    }
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        Log.d("OSNotificationPayload", "result.notification.payload.toJSONObject().toString(): " + result.notification.payload.toJSONObject().toString());


        if (data != null) {
            customKey = data.optString("key", null);
            if (customKey != null) {
                Log.d("OneSignalExample", "customkey set with value: " + customKey);
                if(customKey.equalsIgnoreCase("employer")){
                    String path = data.optString("path", null);
                    Intent intent = new Intent(context2, MainActivity.class);
                    SharedPreferences.Editor editor = context2.getSharedPreferences("NotificationEmployerApply", Context.MODE_PRIVATE).edit();
                    editor.putString("id",path);
                    editor.commit();
              //      intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context2.startActivity(intent);
                }else if(customKey.equalsIgnoreCase("employee")){
                    String path = data.optString("path", null);
                    String id = data.optString("id", null);
                    Intent intent = new Intent(context2, EmployeeMainActivity.class);
                    SharedPreferences.Editor editor = context2.getSharedPreferences("NotificationCheckJob", Context.MODE_PRIVATE).edit();
                    editor.putString("ownerUserID",id);
                    editor.putString("documentNumber",path);
                    editor.putString("jobOffered","-1");
                    editor.commit();
              //      intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context2.startActivity(intent);
                }
            }
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.d("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.


        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
    }
}
