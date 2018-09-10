package com.efhemo.movienano.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class ListConverter implements Serializable {

    @TypeConverter
    public String fromList (List<Integer> integerList){
        if(integerList == null){
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        return gson.toJson(integerList, type);
    }

    @TypeConverter
    public List<Integer> toList (String listInteger){
        if(listInteger == null){
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(listInteger, type);
    }
}
