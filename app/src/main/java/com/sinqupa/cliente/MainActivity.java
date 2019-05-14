package com.sinqupa.cliente;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sinqupa.cliente.utility.Utility;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions())
                requestPermissions();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AlarmFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_alarm:
                    selectedFragment = new AlarmFragment();
                    break;
                case R.id.navigation_map:
                    selectedFragment = new MapFragment();
                    break;
                case R.id.navigation_contact:
                    selectedFragment = new ContactFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    private boolean checkPermissions() {
        for(String permission : Utility.PERMISSIONS){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void requestPermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : Utility.PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                remainingPermissions.add(permission);
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), Utility.REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        if(requestCode == Utility.REQUEST_PERMISSIONS_REQUEST_CODE){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.fragment_container),Utility.PERMISSION_TEXT,Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(ContextCompat.getColor(this,R.color.colorDelete));
                        snackbar.setAction(Utility.OK, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions();
                            }
                        });
                        snackbar.show();
                    }
                    return;
                }
            }
        }
    }
}
