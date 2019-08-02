package cf.poorcoder.driverapplication.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cf.poorcoder.driverapplication.AlarmActivity;
import cf.poorcoder.driverapplication.MainActivity;

/**
 * Created by Belal on 8/29/2017.
 */

//class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {

    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        //you can check the log that it is fired
        //Here we are actually not doing anything
        //but you can do any task here that you want to be done at a specific time everyday
        Log.d("MyAlarmBelal", "Alarm just fired");
        Toast.makeText(context,"ALARM",Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context, MainActivity.class));
    }

}