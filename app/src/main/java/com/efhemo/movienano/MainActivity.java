package com.efhemo.movienano;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.efhemo.movienano.api.ApiClient;
import com.efhemo.movienano.api.Service;
import com.efhemo.movienano.database.MainViewModel;
import com.efhemo.movienano.model.Movie;
import com.efhemo.movienano.model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieRecyclerAdapter.OnListClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private MovieRecyclerAdapter movieRecyclerAdapter;
    private RecyclerView recyclerView;
    private TextView textViewNoMovie;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String API_KEY = "95b230b9dc5ca4b835cdb00a1aef6270"/*BuildConfig.TheMovieDBAPIKEY*/;

    LiveData<List<Movie>> movieListLiveData;
    private static final String BUNDLE_RECYCLER_LAYOUT = "ketkey";
    private int savedposition;
    private GridLayoutManager gridLayoutManager;
    private int lastFirstVisilePosition;
    private int mPosition = 0;

            Fragment fragment;
    private Bundle bundle;
    private Parcelable liststate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //pageadapter
        setSupportActionBar(toolbar);
        /*ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);

        MoviePageAdapter moviePageAdapter = new MoviePageAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(moviePageAdapter);


        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);*/
        //end pageadapter

        textViewNoMovie = findViewById(R.id.no_movie);
        recyclerView = findViewById(R.id.rc_view);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        movieRecyclerAdapter = new MovieRecyclerAdapter(this, this );
        recyclerView.setAdapter(movieRecyclerAdapter);
        //recyclerView.setSaveEnabled(true);
        this.setTitle(getResources().getString(R.string.favourite));

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.sort_order_key));

    }

    @Override
    protected void onPause() {
        super.onPause();
        bundle = new Bundle();
        Parcelable liststate = recyclerView.getLayoutManager().onSaveInstanceState();
        bundle.putParcelable(BUNDLE_RECYCLER_LAYOUT, liststate);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (liststate != null){
            gridLayoutManager.onRestoreInstanceState(liststate);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
        /*lastFirstVisilePosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        outState.putInt("SAVED_INT_POS_FAV", lastFirstVisilePosition);
        Log.d(LOG_TAG, lastFirstVisilePosition + " savedSAvedInStance");*/
        liststate = gridLayoutManager.onSaveInstanceState();
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, liststate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState !=null){
            liststate = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }

    //observe the data change from database
    private void  retrieveTask() {
        //final List<Movie> popularEntry = new ArrayList<>();

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getTask().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> popularEntries) {


                if (popularEntries == null) {
                    textViewNoMovie.setText(getResources().getString(R.string.is_available_favourite));
                }else {

                    textViewNoMovie.setVisibility(View.GONE);
                    movieRecyclerAdapter.setTaskMovie(popularEntries);
                    Log.d(LOG_TAG, " savedSAvedInStance" +mPosition);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void loadJsonResultPopular(){

        try {

            Service service = ApiClient.getApiClient().create(Service.class);

            //isApiKeyAvailable();
            Call<MoviesResponse> moviesResponseCall = service.getPopularMovies(API_KEY);
            moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> results = response.body().getResults();
                    if(!results.isEmpty()){
                        textViewNoMovie.setVisibility(View.GONE);
                    }
                    movieRecyclerAdapter.setTaskMovie(results);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        textViewNoMovie.setVisibility(View.GONE);
                    }
                    movieRecyclerAdapter.setTaskMovie(results);

                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String prefValue = sharedPreferences.getString(key, getString(R.string.popularity) );

        if(prefValue.equals(getString(R.string.top_rated))){
            loadJsonResultTopRated();
            //loadReadToprated();
        }else if(prefValue.equals(getString(R.string.popularity))){
                //loadReadPopular();
                loadJsonResultPopular();
        }else {
            retrieveTask();
        }
        //pref();
    }

    private void pref(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefValue = preferences.getString(
                this.getString(R.string.top_rated),
                this.getString(R.string.popularity)
        );

        if(prefValue.equals(getString(R.string.top_rated))){
            loadJsonResultTopRated();
            //loadReadToprated();
        }else if(prefValue.equals(getString(R.string.popularity))){
                //loadReadPopular();
                loadJsonResultPopular();
            }
    }

}
