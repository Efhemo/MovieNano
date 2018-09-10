package com.efhemo.movienano.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.efhemo.movienano.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> taskFavourite;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase mDb= AppDatabase.getsInstance(this.getApplication());

        //we want it to survive through rotation changes when accessing data
        taskFavourite = mDb.taskDao().loadAllFavouriteMovie();

    }

    public LiveData<List<Movie>> getTask(){
        return taskFavourite;
    }

}
