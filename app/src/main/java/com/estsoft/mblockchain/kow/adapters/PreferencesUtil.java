package com.estsoft.mblockchain.kow.adapters;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by joeylee on 2016-11-22.
 */

@SuppressWarnings("static-access")
public class PreferencesUtil{

    /**
     * set Preference
     *
     */
    public static void setPreferences(Context context, String key, String value) {
        SharedPreferences p = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * get Preference
     *
     */
    public static String getPreferences(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        p = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        return p.getString(key, "");
    }
}