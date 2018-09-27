package com.efhemo.movienano.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.efhemo.movienano.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<Movie>> taskFavourite;

    //get data from database
    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase mDb= AppDatabase.getsInstance(this.getApplication());

        //we want it to survive through rotation changes when accessing data
        Log.d(LOG_TAG, " Movie App Retriving from the data base");
        taskFavourite = mDb.taskDao().loadAllFavouriteMovie();

    }

    public LiveData<List<Movie>> getTask(){
        return taskFavourite;
    }

}
