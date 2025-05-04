package com.example.dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ItemStorage {

    private static final String PREFS_NAME = "item_prefs";
    private static final String KEY_ITEMS = "item_list";

    public static void saveItemList(Context context, ArrayList<String> itemList) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString(KEY_ITEMS, json);
        editor.apply();
    }

    public static ArrayList<String> getItemList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ITEMS, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
