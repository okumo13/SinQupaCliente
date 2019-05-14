package com.sinqupa.cliente.entity;

public class DistanceObject {
    private String text;
    private Integer value;

    public DistanceObject() {
    }

    public DistanceObject(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return text == null ? " " : text;
    }
}
