package com.example.android.vinter_1.data;

/**
 * Created by Daniel Ibanez on 2016-10-02.
 */

public class Patient {

    private String mName;
    private int mEntrada;
    private String mNotes;

    public Patient(String name) {
        mName = name;
    }

    public int getEntrada() {
        return mEntrada;
    }

    public void setEntrada(int entrada) {
        mEntrada = entrada;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "mEntrada=" + mEntrada +
                ", mName='" + mName + '\'' +
                ", mNotes='" + mNotes + '\'' +
                '}';
    }
}
