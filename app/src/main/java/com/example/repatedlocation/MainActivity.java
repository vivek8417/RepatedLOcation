package com.example.repatedlocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    Button get_location;
    public static TextView location_textView;
    public TextView status_textView;
    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 1001;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        get_location = findViewById(R.id.get_location);
        location_textView= findViewById(R.id.textView);
        status_textView=findViewById(R.id.textView2);

        Constraints constraints=new Constraints.Builder().setRequiresCharging(true).build();
        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(MyWorker.class,15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag("First work")
                .build();
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                workManager.enqueueUniquePeriodicWork("First work", ExistingPeriodicWorkPolicy.REPLACE,request);
            }
        });
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.getWorkInfoByIdLiveData(request.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {

                if(workInfo!=null)
                {
                    if(workInfo.getState().isFinished())
                    {
                        Data data=workInfo.getOutputData();
                        String output =data.getString(MyWorker.WORK_RESULT);
                        status_textView.append(output+"\n");
                    }
                }
                String status =workInfo.getState().name();
                status_textView.append(status +"\n");
            }
        });
    }
}