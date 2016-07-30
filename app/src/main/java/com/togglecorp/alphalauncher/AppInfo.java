package com.togglecorp.alphalauncher;

import android.graphics.drawable.Drawable;

public class AppInfo {

    public CharSequence label;
    public CharSequence name;
    public Drawable icon;

    public AppInfo(CharSequence label, CharSequence name, Drawable icon) {
        this.label = label;
        this.name = name;
        this.icon = icon;
    }

}
