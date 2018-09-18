package com.efhemo.movienano;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.efhemo.movienano.database.MainViewModel;
import com.efhemo.movienano.model.Movie;

import java.util.List;

public class FavouriteFragment extends Fragment implements MovieRecyclerAdapter.OnListClickListener {

    LiveData<List<Movie>> movieListLiveData;
    private TextView textViewNoMovie;
    private MovieRecyclerAdapter movieRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        textViewNoMovie = rootView.findViewById(R.id.no_movie);
        RecyclerView recyclerView = rootView.findViewById(R.id.rc_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieRecyclerAdapter = new MovieRecyclerAdapter(getContext(), this );
        recyclerView.setAdapter(movieRecyclerAdapter);

        retrieveTask();

        getActivity().setTitle(getResources().getString(R.string.favourite));
        return rootView;
    }

    //get data from database
    private void retrieveTask() {

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movieListLiveData = mainViewModel.getTask();

        movieListLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> popularEntries) {

                if (popularEntries == null) {
                    textViewNoMovie.setText(getResources().getString(R.string.is_available_favourite));
                }else
                    textViewNoMovie.setVisibility(View.GONE);
                    movieRecyclerAdapter.setTaskMovie(popularEntries);

            }
        });
    }

    @Override
    public void onListClick(Movie movie) {

        Intent intent = new Intent(this.getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);

    }
}
