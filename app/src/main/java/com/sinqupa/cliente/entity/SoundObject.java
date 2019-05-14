package com.sinqupa.cliente.entity;

import android.net.Uri;

public class SoundObject {
    private String text;
    private Uri uri;

    public SoundObject() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String toString()
    {
        return text == null ? " " : text;
    }
}
