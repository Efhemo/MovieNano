package com.efhemo.movienano;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efhemo.movienano.model.Movie;

public class DetailActivity extends AppCompatActivity {

    public final static String EXTRA_MOVIE = "extraMovie";

    Movie movie;
    String backDropPath, movieName, overview, rating, dateOfRelease;
    int movie_id;
    private TextView textViewOverview;
    private TextView textViewDate;
    private ImageView imageViewBackDrop;
    private Toolbar toolbar;
    private TextView textViewUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        initializeView();

        if(intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)){
            movie = getIntent().getParcelableExtra(DetailActivity.EXTRA_MOVIE);

            backDropPath = movie.getPosterPath();
            movieName = movie.getOriginalTitle();
            overview = movie.getOverview();
            rating = Double.toString(movie.getVoteAverage());
            dateOfRelease = movie.getReleaseDate();
            movie_id = movie.getId();

            String poster = "https://image.tmdb.org/t/p/w500" + backDropPath;
            Glide.with(this).load(poster).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewBackDrop);

            textViewOverview.setText(overview);
            textViewDate.setText(dateOfRelease);
            textViewUserRating.setText(rating);
            setSupportActionBar(toolbar);
        }


    }

    void initializeView(){
        toolbar = findViewById(R.id.toolbar);
        textViewOverview = findViewById(R.id.overview_tv);
        textViewDate = findViewById(R.id.date_tv);
        imageViewBackDrop = findViewById(R.id.image_header);
        textViewUserRating = findViewById(R.id.user_rating);

    }

    private void closeOnError(){
        finish();
        Toast.makeText(this, R.string.not_available_movie, Toast.LENGTH_SHORT).show();
    }
}
