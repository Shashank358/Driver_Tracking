package cf.poorcoder.driverapplication.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cf.poorcoder.driverapplication.AlarmActivity;

import cf.poorcoder.driverapplication.MainActivity;
import cf.poorcoder.driverapplication.R;

import static cf.poorcoder.driverapplication.Services.App.CHANNEL_ID;


public class ExampleService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String input = "Hello";

    int hours,mins;

    public ExampleService(Context applicationContext) {
        super();

    }
    public ExampleService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Driver Application")
                .setContentText(input)
                .setContentInfo("Running in Background")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchLocation();
                //getAlarm();

                firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("alarm").document("alarmTime").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hours = Integer.parseInt(documentSnapshot.get("hour").toString());
                        mins = Integer.parseInt(documentSnapshot.get("min").toString());

                        Calendar calendar = Calendar.getInstance();
                        Calendar calendar2 = Calendar.getInstance();

                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                hours, mins);

                        if(calendar.equals(calendar2))
                        {
                            startActivity(notificationIntent);
                        }
                    }
                });

                handler.postDelayed(this, 60000);
            }
        },60000);

        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);

    }

    private void fetchLocation() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            }
            else {
            // Permission has already been granted

            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Logic to handle location object
                        Double latittude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        HashMap<String,Double> hash = new HashMap<String, Double>();
                        Random random = new Random();
                        hash.put("longitude",longitude);
                        hash.put("latittude",latittude);

                        double dist = distFrom(latittude,longitude,30.7641843,76.574308);

                        hash.put("distance",dist);


                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c);

                        firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).update((Map)hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        HashMap<String,String> hm = new HashMap<String, String>();
                        hm.put("Date",formattedDate);
                        if(dist < 1000)
                        {
                            hm.put("attendence","present");
                        }

                        firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("Attencence").document(formattedDate).set(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });


                    }
                }
            });

        }

    }

    private void getAlarm()
    {
        firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("alarm").document("alarmTime").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                hours = Integer.parseInt(documentSnapshot.get("hour").toString());
                mins = Integer.parseInt(documentSnapshot.get("min").toString());

                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();

                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        hours, mins);

                if(calendar.before(calendar2))
                {
                    setAlarm(calendar.getTimeInMillis());
                }
            }
        });
    }


    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, MyAlarm.class);
        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        //Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();

    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}