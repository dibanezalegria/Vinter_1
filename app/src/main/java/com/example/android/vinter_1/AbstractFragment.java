package com.example.android.vinter_1;

import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * All my fragments extend this abstract class. This allows me to call saveToDatabase from
 * TestActivity using polymorphism
 */
public abstract class AbstractFragment extends Fragment {

    public abstract boolean saveToDatabase();
    public abstract void helpDialog();
    public abstract void notesDialog();

}
