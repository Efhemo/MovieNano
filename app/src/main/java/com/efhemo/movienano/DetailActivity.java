package com.efhemo.movienano;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efhemo.movienano.api.ApiClient;
import com.efhemo.movienano.api.Service;
import com.efhemo.movienano.database.AppDatabase;
import com.efhemo.movienano.database.AppExecutors;
import com.efhemo.movienano.model.Movie;
import com.efhemo.movienano.model.Review;
import com.efhemo.movienano.model.ReviewResponse;
import com.efhemo.movienano.model.Trailer;
import com.efhemo.movienano.model.TrailerResponse;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public final static String EXTRA_MOVIE = "extraMovie";


    Movie movie;
    String backDropPath, movieName, overview, rating, dateOfRelease;
    int movie_id;
    private ExpandableTextView textViewDescript;
    private TextView textViewDate;
    private ImageView imageViewBackDrop;
    private Toolbar toolbar;
    private GenRCAdapter genRCAdapter;
    private ImageView imageViewPosterDetails;
    private RatingBar ratingBar;
    private TextView textViewTitle;
    private LinearLayout linearLayout;
    private TextView textViewText;
    private TextView textViewName;
    private CoordinatorLayout coordinatorLayout;
    private TextView textViewSeeMore;
    public FloatingActionButton floatingActionButton;

    private AppDatabase appDatabase;
    private SharedPreferences sharedPref;
    private boolean isFavourite;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        initializeView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();

        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            movie = getIntent().getParcelableExtra(DetailActivity.EXTRA_MOVIE);

            backDropPath = movie.getBackdropPath();
            movieName = movie.getOriginalTitle();
            overview = movie.getOverview();
            rating = Double.toString(movie.getVoteAverage());
            dateOfRelease = movie.getReleaseDate();
            movie_id = movie.getId();

            double rating = movie.getVoteAverage();

            String poster = "https://image.tmdb.org/t/p/w500" + backDropPath;
            Glide.with(this).load(poster).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewBackDrop);

            Glide.with(this).
                    load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .centerCrop().into(imageViewPosterDetails);

            ratingBar.setRating((float) rating);
            textViewDescript.setText(overview);
            textViewTitle.setText(movie.getOriginalTitle());
            textViewDate.setText(dateOfRelease);
            setSupportActionBar(toolbar);
            toolbar.setTitle(movieName);


        }

        RecyclerView recyclerView = findViewById(R.id.video_rc_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        genRCAdapter = new GenRCAdapter(this);

        recyclerView.setAdapter(genRCAdapter);

        appDatabase = AppDatabase.getsInstance(getApplicationContext());

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        isFavourite = sharedPref.getBoolean(String.valueOf(movie_id), false);
        editor = sharedPref.edit();
        favouriteFeature(isFavourite);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickedFavouriteFeature();
            }
        });

        loadVideo();

        loadReviews();

    }

    void initializeView() {
        toolbar = findViewById(R.id.toolbar);

        //Viewa for Key Details
        ratingBar = findViewById(R.id.ratingBar);
        imageViewPosterDetails = findViewById(R.id.poster_details);
        textViewTitle = findViewById(R.id.movie_title);
        textViewDate = findViewById(R.id.release_date);

        imageViewBackDrop = findViewById(R.id.image_header);
        textViewDescript = findViewById(R.id.expand_text_view);
        linearLayout = findViewById(R.id.trailer_layout);

        //Views for Review
        textViewText = findViewById(R.id.review_text);
        textViewName = findViewById(R.id.reviewers_name);
        coordinatorLayout = findViewById(R.id.review_layout_id);
        textViewSeeMore = findViewById(R.id.see_more_review);

        floatingActionButton = findViewById(R.id.fab);
    }

    void deleteFavouriteMovie(){

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.taskDao().oneMovie(movie_id);
                Log.v(LOG_TAG, movie_id+" id deleted" );
            }
        });
    }


    void clickedFavouriteFeature() {

        if (isFavourite) {
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(R.color.whiteCo)));

            deleteFavouriteMovie();

            isFavourite = false;
            editor.putBoolean(String.valueOf(movie_id), isFavourite).commit();
            Toast.makeText(this, "Delete as Favourite", Toast.LENGTH_SHORT).show();

        } else if (!isFavourite) {
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(R.color.colorAccent)));
            insertMovie();
            isFavourite = true;
            editor.putBoolean(String.valueOf(movie_id), isFavourite).commit();
            Toast.makeText(this, "Save as Favourite", Toast.LENGTH_SHORT).show();
        }
    }


    void insertMovie() {

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.taskDao().insertMovie(movie);
                int identification   = movie.getIdentification();
                Log.v(LOG_TAG, "id of "+identification );
                sharedPref.edit().putInt("id",identification);
            }
        });
    }

    void loadVideo() {

        Service service = ApiClient.getApiClient().create(Service.class);
        Call<TrailerResponse> trailerCall =
                service.getMovieTrailer(movie_id, MainActivity.API_KEY);
        trailerCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                List<Trailer> trailerList = response.body().getResults();

                if (trailerList.isEmpty()) {
                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                genRCAdapter.setTask(trailerList);

                Log.d(LOG_TAG, "list of trailer dilivered");
                Toast.makeText(DetailActivity.this, "Trailer Available", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.d(LOG_TAG, "list of trailer not delivered");

            }
        });
    }

    void loadReviews() {
        Service service = ApiClient.getApiClient().create(Service.class);
        Call<ReviewResponse> reviewResponseCall = service.
                getReviewResponse(movie_id, MainActivity.API_KEY);

        reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                ReviewResponse reviewResponse = response.body();
                List<Review> reviewList = reviewResponse.getReviewList();

                seeMoreReview(reviewList);
                if (reviewList.isEmpty()) {
                    coordinatorLayout.setVisibility(View.GONE);
                    return;
                }
                Review review = reviewList.get(0);
                textViewText.setText(review.getContent());
                textViewName.setText(review.getAuthor());
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });

    }

    void seeMoreReview(final List<Review> list) {

        if (list.size() < 1) {
            textViewSeeMore.setVisibility(View.GONE);
        }
        textViewSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", (ArrayList<Review>) list);
                ReviewFragment reviewFragment = new ReviewFragment();
                reviewFragment.setArguments(bundle);

                reviewFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);

                reviewFragment.show(getSupportFragmentManager(), "reviewer");
            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.not_available_movie, Toast.LENGTH_SHORT).show();
    }

    void favouriteFeature(boolean isFavourite) {

        if (isFavourite) {
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(R.color.colorAccent)));

        } else if (!isFavourite) {
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(R.color.whiteCo)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
