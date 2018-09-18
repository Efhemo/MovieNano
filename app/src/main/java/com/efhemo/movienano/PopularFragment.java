package com.efhemo.movienano;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.efhemo.movienano.api.ApiClient;
import com.efhemo.movienano.api.Service;
import com.efhemo.movienano.model.Movie;
import com.efhemo.movienano.model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, MovieRecyclerAdapter.OnListClickListener {

    private MovieRecyclerAdapter movieRecyclerAdapter;
    private RecyclerView recyclerView;
    private TextView textViewNoMovies;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String API_KEY = BuildConfig.TheMovieDBAPIKEY;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = getLayoutInflater().inflate(R.layout.fragment_recyclerview, container, false);

        movieRecyclerAdapter = new MovieRecyclerAdapter(getContext(), this );
        recyclerView = view.findViewById(R.id.rc_view);
        textViewNoMovies = view.findViewById(R.id.no_movie);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(movieRecyclerAdapter);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.sort_order_key));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.
                getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void loadJsonResultPopular(){

        try {

            Service service = ApiClient.getApiClient().create(Service.class);

            //isApiKeyAvailable();
            Call<MoviesResponse> moviesResponseCall = service.getPopularMovies(API_KEY);
            moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> results = response.body().getResults();
                    if(!results.isEmpty()){
                        textViewNoMovies.setVisibility(View.GONE);
                    }
                    movieRecyclerAdapter.setTaskMovie(results);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void loadJsonResultTopRated(){

        try {

            Service service = ApiClient.getApiClient().create(Service.class);
            //isApiKeyAvailable();
            Call<MoviesResponse> moviesResponseCall = service.getTopRatedMovies(API_KEY);
            moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> results = response.body().getResults();
                    if(!results.isEmpty()){
                        textViewNoMovies.setVisibility(View.GONE);
                    }
                    movieRecyclerAdapter.setTaskMovie(results);

                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        String prefValue = sharedPreferences.getString(s, getString(R.string.popularity) );

        if(prefValue.equals(getString(R.string.top_rated))){
            loadJsonResultTopRated();
        }else loadJsonResultPopular();
    }

    @Override
    public void onListClick(Movie movie) {
        Intent intent = new Intent(this.getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

}
