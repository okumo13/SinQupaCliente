package com.sinqupa.cliente.utility;

import android.Manifest;
import android.location.Location;
import android.net.Uri;

public class Utility {
    public static int distance;
    public static Location locationCustomer;
    public static Location locationEmployee;
    public static Uri uriNotification;

    public static final int FALSE = 0;
    public static final int TRUE = 1;
    public static final int DEFAULT_INDEX_SOUND = 0;
    public static final int DEFAULT_INDEX_DISTANCE = 0;
    public static final int NOTIFICATION_ID = 1008;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 101;
    public static final String CHANNEL_ID = "NOTIFICACION";
    public static final String PERMISSION_TEXT="Permisos Requeridos para SinQupa";
    public static final String[] PERMISSIONS = { Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECEIVE_BOOT_COMPLETED};
    private static final String PACKAGE_NAME = "com.sinqupa.cliente";
    public static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +".started_from_notification";
    public static final String TITLE_MARKER_CUSTOMER = "Mi Ubicacion";
    public static final String TITLE_MARKER_EMPLOYEE = "Ubicacion Chofer";

    //----------------- Variables para los Botones de las  Ventanas de Dialogo -----------------//
    public static final String ADD = "Agregar";
    public static final String CANCEL = "Cancelar";
    public static final String OK = "Ok";
    //----------------- Fin Variables para los Botones de las  Ventanas de Dialogo -----------------//

    //----------------- Variables para BD -----------------//
    public static final String DATABASE = "SinQupa";
    public static final Integer VERSION = 1;
    public static final String TABLE_ALARM = "Alarm";
    public static final String FIELD_IDALARM = "idAlarm";
    public static final String FIELD_DISTANCE = "ditance";
    public static final String FIELD_ACTIVED = "actived";
    public static final String FIELD_SOUND = "sound";
    public static final String FIELD_URI= "uri";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String QUERY_SELECT_ALL = "SELECT * FROM ";
    //----------------- Fin Variables para BD -----------------//

    //----------------- Variable para Crear la Estructura de la BD -----------------//
    public static final String CREATE_TABLE_ALARM =
            "CREATE TABLE " + TABLE_ALARM +
                    "(" +
                    FIELD_IDALARM     + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_DISTANCE    + " INTEGER,"                              +
                    FIELD_ACTIVED     + " INTEGER,"                           +
                    FIELD_SOUND       + " TEXT,"                              +
                    FIELD_URI         + " TEXT,"                              +
                    FIELD_LATITUDE    + " TEXT,"                              +
                    FIELD_LONGITUDE   + " TEXT"                               +
                    ")";
    //----------------- Fin Variable para Crear la Estructura de la BD -----------------//
}
