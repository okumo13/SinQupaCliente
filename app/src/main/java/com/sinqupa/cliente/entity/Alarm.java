package com.sinqupa.cliente.entity;

import android.net.Uri;

public class Alarm {
    private Integer alarmID;
    private Integer distance;
    private Integer actived;
    private String sound;
    private Uri uri;
    private String latitude;
    private String longitude;

    public Alarm() {
    }

    public Alarm(Integer alarmID, Integer distance, Integer actived, String sound, Uri uri, String latitude, String longitude) {
        this.alarmID = alarmID;
        this.distance = distance;
        this.actived = actived;
        this.sound = sound;
        this.uri = uri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(Integer alarmID) {
        this.alarmID = alarmID;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getActived() {
        return actived;
    }

    public void setActived(Integer actived) {
        this.actived = actived;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
