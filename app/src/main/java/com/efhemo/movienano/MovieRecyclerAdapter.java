package com.efhemo.movienano;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.efhemo.movienano.model.Movie;

import java.util.List;

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.RecyclerViewHolder> {

    private List<Movie> movieList;

    private OnListClickListener onListClickListener;
    public interface OnListClickListener{
       void onListClick(Movie movie);
    }

    private Context context;
    public MovieRecyclerAdapter(Context context, OnListClickListener onListClickListener) {
        this.context = context;
        this.onListClickListener = onListClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        String imageUrl = movieList.get(holder.getAdapterPosition()).getPosterPath();
        Glide.with(context).load("https://image.tmdb.org/t/p/w200/"+imageUrl)
                .fitCenter()
                /*.diskCacheStrategy(DiskCacheStrategy.ALL)*/
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(movieList == null){
            return 0;
        }
        return movieList.size();
    }

    public void setTaskMovie(List<Movie> movieList){
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.poster_path_image);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            onListClickListener.onListClick(movieList.get(getAdapterPosition()));
        }
    }
}
