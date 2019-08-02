package cf.poorcoder.driverapplication;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cf.poorcoder.driverapplication.Services.ExampleService;
import cf.poorcoder.driverapplication.Services.MyAlarm;
import cf.poorcoder.driverapplication.Views.CustomTypefaceSpan;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Toolbar toolbar;
    TextView toolbarTitle;
    LocationManager mLocationManager;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    Context context = this;
    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    CardView profileCard,attendenceCard,notificationCard,absentCard,rectifyCard;
    int hours,mins;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //create new intent for service
    Intent mServiceIntent;
    private ExampleService service;
    Context ctx;

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font_app.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        setUpToolBar();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profileCard = (CardView) findViewById(R.id.profile_card);
        attendenceCard = (CardView) findViewById(R.id.attendence_card);
        notificationCard = (CardView) findViewById(R.id.notification_card);
        absentCard = (CardView) findViewById(R.id.absentCard);
        rectifyCard = (CardView) findViewById(R.id.rectify_card);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        attendenceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AttendenceActivity.class);
                startActivity(intent);
            }
        });


        rectifyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RectifyActivity.class);
                startActivity(intent);
            }
        });


        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent);
            }
        });

        absentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AbsentStudentActivity.class);
                startActivity(intent);
            }
        });

       //fetchLocation();

        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.setItemIconTintList(null);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case (R.id.home):
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.alarm):
                        drawerLayout.closeDrawers();
                        Intent intentAlarm = new Intent(MainActivity.this,AlarmActivity.class);
                        startActivity(intentAlarm);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.rectify):
                        drawerLayout.closeDrawers();
                        Intent intentRectify = new Intent(MainActivity.this,RectifyActivity.class);
                        startActivity(intentRectify);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.notification):
                        drawerLayout.closeDrawers();
                        Intent intentNotify = new Intent(MainActivity.this,NotificationActivity.class);
                        startActivity(intentNotify);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.attendence):
                        drawerLayout.closeDrawers();
                        Intent intentAttendence = new Intent(MainActivity.this,AttendenceActivity.class);
                        startActivity(intentAttendence);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.profile):
                        drawerLayout.closeDrawers();
                        Intent intentProfile = new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(intentProfile);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;

                }
                return false;
            }
        });


        service = new ExampleService(getCtx());
        String input = "Hello";
        mServiceIntent = new Intent(getCtx(), service.getClass());

        if (!isMyServiceRunning(service.getClass())) {
            if (!isMyServiceRunning(ExampleService.class)){
                startService(mServiceIntent);
            }

        }
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.profile):
                Intent intentP = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intentP);
                break;
            case (R.id.alarm):
                Intent intentC = new Intent(MainActivity.this,AlarmActivity.class);
                startActivity(intentC);
                break;
        }
        return true;
    }

    void setUpToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/font.ttf");
        toolbarTitle.setTypeface(type);
        actionBarDrawerToggle =  new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void fetchLocation() {


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double latittude = location.getLatitude();
                                Double longitude = location.getLongitude();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                HashMap<String,Double> hash = new HashMap<String, Double>();
                                Random random = new Random();
                                hash.put("longitude",longitude);
                                hash.put("latittude",latittude);
                                firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).update((Map)hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                //user_location.setText("Latitude = "+latittude + "\nLongitude = " + longitude);

                            }
                        }
                    });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{

            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    //calling this method to destroy the service to create again after destroying the activity

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}
