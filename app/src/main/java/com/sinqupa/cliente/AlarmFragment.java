package com.sinqupa.cliente;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sdsmdg.tastytoast.TastyToast;
import com.sinqupa.cliente.entity.Alarm;
import com.sinqupa.cliente.entity.DistanceObject;
import com.sinqupa.cliente.entity.SoundObject;
import com.sinqupa.cliente.service.LocationUpdatesService;
import com.sinqupa.cliente.utility.Utility;
import java.util.ArrayList;

public class AlarmFragment extends Fragment {
    private Button btnSave;
    private LinearLayout lytSound,lytDistance,lytUbication;
    private Switch swActive;
    private TextView lblSound,lblDistance,lblUbication;
    private MediaPlayer md;
    private Integer soundPosition,newSoundPosition = Utility.DEFAULT_INDEX_SOUND,distancePosition,newDistancePosition = Utility.DEFAULT_INDEX_DISTANCE;
    private ConexionSQLiteHelper conn;
    private Alarm alarm;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private Marker mDestinationMarker;
    private LatLng mDestinationLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm,container,false);
        conn = new ConexionSQLiteHelper(getContext(), Utility.DATABASE, null, Utility.VERSION);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        swActive = (Switch)view.findViewById(R.id.swActive);
        lytSound = (LinearLayout) view.findViewById(R.id.lytSound);
        lytDistance = (LinearLayout) view.findViewById(R.id.lytDistance);
        lytUbication = (LinearLayout) view.findViewById(R.id.lytUbication);
        lblSound = (TextView)view.findViewById(R.id.lblSound);
        lblDistance = (TextView)view.findViewById(R.id.lblDistance);
        lblUbication = (TextView)view.findViewById(R.id.lblUbication);

        alarm = new Alarm();
        findAllAlarms();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existsAlarm()){
                    updateAlarm(alarm);
                }else {
                    insertAlarm(alarm);
                    TastyToast.makeText(getContext(), "Datos Guardados", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                }
                findAllAlarms();
            }
        });

        lytSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogSound();
            }
        });

        lytDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogDistance();
            }
        });

        lytUbication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions())
                    customDialogMap();
            }
        });

        swActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existsAlarm()){
                     if (swActive.isChecked()){
                        updateActivedAlarm(alarm.getAlarmID(),Utility.TRUE);
                        Utility.distance = alarm.getDistance();
                        Utility.locationCustomer = new Location(Utility.TITLE_MARKER_CUSTOMER);
                        Utility.locationCustomer.setLatitude(Double.parseDouble(alarm.getLatitude()));
                        Utility.locationCustomer.setLongitude(Double.parseDouble(alarm.getLongitude()));
                        //if (checkPermissions())
                        requestLocationUpdates();
                    }else {
                        updateActivedAlarm(alarm.getAlarmID(),Utility.FALSE);
                        //if (checkPermissions())
                        removeLocationUpdates();
                    }

                }else {
                    if (swActive.isChecked())
                        TastyToast.makeText(getContext(), "Ingresar Datos", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            }
        });
        return view;
     }

    //Metodo para mostrar la Pantalla de Dialogo de los Sonidos
    private void customDialogSound() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_dialog_sound, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(dialogLayout);

        //----------------- Llenamos el ListView de Sonido -----------------//
        ListView listViewSound = (ListView) dialogLayout.findViewById(R.id.lvSound);
        RingtoneManager ringtoneManager = new RingtoneManager(getContext());
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        final Cursor alarmsCursor = ringtoneManager.getCursor();
        ArrayList<SoundObject> arrayListSound = new ArrayList<>();
        SoundObject soundObject;
        int index = 0;
        while (alarmsCursor.moveToNext()) {
            soundObject = new SoundObject();
            soundObject.setText(ringtoneManager.getRingtone(index).getTitle(getContext()));
            soundObject.setUri(ringtoneManager.getRingtoneUri(index));
            arrayListSound.add(soundObject);
            index++;
        }
        alarmsCursor.close();
        final ArrayAdapter<SoundObject> adapterSound = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, arrayListSound);
        listViewSound.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewSound.setAdapter(adapterSound);
        //----------------- Fin de Llenamos el ListView de Sonido -----------------//

        if (existsAlarm()) {
            for (int i = 0; i < listViewSound.getAdapter().getCount(); i++ ){
                if (alarm.getSound().equals(adapterSound.getItem(i).getText())) {
                    listViewSound.setItemChecked(i, true);
                    break;
                }
            }
        } else {
            listViewSound.setItemChecked(newSoundPosition, true);
        }
        listViewSound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                isPlayingRingtone();
                md = MediaPlayer.create(getContext(), adapterSound.getItem(position).getUri());
                md.start();
                soundPosition = position;
            }
        });
        alertDialog.setPositiveButton(Utility.ADD, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newSoundPosition = soundPosition;
                alarm.setSound(adapterSound.getItem(newSoundPosition).getText());
                alarm.setUri(adapterSound.getItem(newSoundPosition).getUri());
                lblSound.setText(alarm.getSound());
                isPlayingRingtone();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton(Utility.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isPlayingRingtone();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialogSound = alertDialog.create();
        dialogSound.show();
    }

    //Metodo para mostrar la Pantalla de Dialogo de Google Map
    private void customDialogMap() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_dialog_map, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(dialogLayout);
        alertDialog.setPositiveButton(Utility.ADD, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alarm.setLatitude(String.valueOf(mDestinationLocation.latitude));
                alarm.setLongitude(String.valueOf(mDestinationLocation.longitude));
                onDestroyViewMap();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton(Utility.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onDestroyViewMap();
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialogMap = alertDialog.create();
        dialogMap.show();
        mSupportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                if (mGoogleMap != null){
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mGoogleMap.setMyLocationEnabled(true);
                    if (existsAlarm()){
                        //Cargamos el marcador con la Ubicación
                        LatLng destinationPosition = new LatLng(Double.parseDouble(alarm.getLatitude()),Double.parseDouble(alarm.getLongitude()));
                        MarkerOptions markerOptionsDestination = new MarkerOptions();
                        markerOptionsDestination.position(destinationPosition);
                        markerOptionsDestination.title(Utility.TITLE_MARKER_EMPLOYEE);
                        //markerOptionsDestination.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_house));
                        mDestinationMarker = mGoogleMap.addMarker(markerOptionsDestination);
                    }

                    mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng destinationPosition) {
                            if (mDestinationMarker != null)
                                mDestinationMarker.remove();

                            mDestinationLocation = destinationPosition;
                            //Colocamos el Marcador con la Ubicación
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(destinationPosition);
                            markerOptions.title(Utility.TITLE_MARKER_EMPLOYEE);
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_house));
                            mDestinationMarker = mGoogleMap.addMarker(markerOptions);
                        }
                    });
                }
            }
        });
    }

    //Metodo para mostrar la Pantalla de Dialogo de las Repeticiones
    private void customDialogDistance() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_dialog_distance, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(dialogLayout);

        //----------------- Llenamos el ListView de Repeticiones -----------------//
        final ListView listViewDistance = (ListView) dialogLayout.findViewById(R.id.lvDistance);
        ArrayList<DistanceObject> arrayListDistance = new ArrayList<>();
        arrayListDistance.add(new DistanceObject("5 Metros",5));
        arrayListDistance.add(new DistanceObject("10 Metros",10));
        arrayListDistance.add(new DistanceObject("15 Metros",15));
        arrayListDistance.add(new DistanceObject("20 Metros",20));
        arrayListDistance.add(new DistanceObject("25 Metros",25));
        arrayListDistance.add(new DistanceObject("30 Metros",30));

        final ArrayAdapter<DistanceObject> adapterDistance = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, arrayListDistance);
        listViewDistance.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewDistance.setAdapter(adapterDistance);
        //----------------- Fin de Llenamos el ListView de Repeticiones -----------------//

        if (existsAlarm()) {
            for (int i = 0; i < listViewDistance.getAdapter().getCount(); i++ ){
                if (alarm.getDistance().equals(adapterDistance.getItem(i).getText())) {
                    listViewDistance.setItemChecked(i, true);
                    break;
                }
            }
        } else {
            listViewDistance.setItemChecked(newDistancePosition, true);
        }

        listViewDistance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                distancePosition = position;
            }
        });

        alertDialog.setPositiveButton(Utility.ADD, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newDistancePosition = distancePosition;
                alarm.setDistance(adapterDistance.getItem(newDistancePosition).getValue());
                lblDistance.setText(String.valueOf(alarm.getDistance()) + " Metros");
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton(Utility.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialogRepeat = alertDialog.create();
        dialogRepeat.show();
    }

    //Metodo para verificar si el Ringtone se esta Reproduciendo
    private void isPlayingRingtone(){
        if (md != null) {
            if (md.isPlaying())
                md.stop();
            md = null;
        }
    }

    private void insertAlarm(Alarm alarm) {
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utility.FIELD_ACTIVED,alarm.getActived());
        values.put(Utility.FIELD_DISTANCE,alarm.getDistance());
        values.put(Utility.FIELD_SOUND, alarm.getSound());
        values.put(Utility.FIELD_URI,alarm.getUri().toString());
        values.put(Utility.FIELD_LATITUDE,alarm.getLatitude());
        values.put(Utility.FIELD_LONGITUDE,alarm.getLongitude());
        db.insert(Utility.TABLE_ALARM, null, values);
        db.close();
    }

    private void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parameters = {alarm.getAlarmID().toString()};
        ContentValues values = new ContentValues();
        values.put(Utility.FIELD_DISTANCE,alarm.getDistance());
        values.put(Utility.FIELD_SOUND, alarm.getSound());
        values.put(Utility.FIELD_URI,alarm.getUri().toString());
        values.put(Utility.FIELD_LATITUDE,alarm.getLatitude());
        values.put(Utility.FIELD_LONGITUDE,alarm.getLongitude());
        db.update(Utility.TABLE_ALARM, values, Utility.FIELD_IDALARM + "=?", parameters);
        db.close();
    }

    private void updateActivedAlarm(Integer idAlarm,Integer isActive) {
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parameters = {idAlarm.toString()};
        ContentValues values = new ContentValues();
        values.put(Utility.FIELD_ACTIVED, isActive);
        db.update(Utility.TABLE_ALARM, values, Utility.FIELD_IDALARM + "=?", parameters);
        db.close();
    }

    private boolean existsAlarm() {
        boolean exists = false;
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery(Utility.QUERY_SELECT_ALL + Utility.TABLE_ALARM, null);
        if (cursor.moveToFirst()) {
            exists = true;
        }
        db.close();
        cursor.close();
        return exists;
    }

    private void findAllAlarms() {
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery(Utility.QUERY_SELECT_ALL + Utility.TABLE_ALARM, null);
        while (cursor.moveToNext()) {
            alarm = new Alarm();
            alarm.setAlarmID(cursor.getInt(cursor.getColumnIndex(Utility.FIELD_IDALARM)));
            alarm.setDistance(cursor.getInt(cursor.getColumnIndex(Utility.FIELD_DISTANCE)));
            alarm.setActived(cursor.getInt(cursor.getColumnIndex(Utility.FIELD_ACTIVED)));
            alarm.setSound(cursor.getString(cursor.getColumnIndex(Utility.FIELD_SOUND)));
            alarm.setUri(Uri.parse(cursor.getString(cursor.getColumnIndex(Utility.FIELD_URI))));
            alarm.setLatitude(cursor.getString(cursor.getColumnIndex(Utility.FIELD_LATITUDE)));
            alarm.setLongitude(cursor.getString(cursor.getColumnIndex(Utility.FIELD_LONGITUDE)));
        }
        db.close();
        cursor.close();
    }

    //Metodo para eliminar los fragment del Google Map
    private void onDestroyViewMap() {
        if (mSupportMapFragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(mSupportMapFragment).commit();
    }

    private boolean checkPermissions() {
        for(String permission : Utility.PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(getContext(),permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void requestLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(new Intent(getContext(), LocationUpdatesService.class));
        }
        getActivity().startService(new Intent(getContext(), LocationUpdatesService.class));
    }

    private void removeLocationUpdates() {
        getActivity().stopService(new Intent(getContext(), LocationUpdatesService.class));
    }
}
