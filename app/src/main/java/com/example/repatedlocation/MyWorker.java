package com.example.repatedlocation;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MyWorker extends Worker {
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "MainActivity";
    public static final String WORK_RESULT = "work_result";
    int LOCATION_REQUEST_CODE = 1001;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("check", "doWork: ");
        Data data = getInputData();
        String desc = data.getString(MainActivity.MESSAGE_STATUS);
        displayNotification("My Location","Location Updated");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getLastLocation();
        }
        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();

        
        return Result.success(outputData);
    }



    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    displayNotification("My Location","Location Updated");
                    Log.d(TAG, "onSuccess: "+location.toString());
                    Log.d(TAG, "onSuccess: "+location.getLatitude());
                    Log.d(TAG, "onSuccess: "+location.getLongitude());

                    MainActivity.location_textView.setText("Longitude "+location.getLongitude()+'\n'+"Latitude"+location.getLatitude());

                }
                else
                {
                    Log.d(TAG, "onSuccess: location was null....");
                }

            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+e.getLocalizedMessage() );
            }
        });
    }

    private void displayNotification(String task, String desc)
    {
        NotificationManager manager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel("channelId","channelName",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"channelId")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1,builder.build());

    }
}
