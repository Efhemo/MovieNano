package com.efhemo.movienano;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efhemo.movienano.model.Review;
import com.efhemo.movienano.model.Trailer;

import java.util.List;

public class GenRCAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final static String LOG_TAG = GenRCAdapter.class.getSimpleName();
    private List<Trailer> trailers;
    private List<Review> reviewList;
    private Context context;

    public GenRCAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(trailers !=null){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
            return new TrailerViewHolder(view);

        }else if(reviewList !=null){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
            return new ReviewHolder(view);
        }else {
            Log.d(LOG_TAG, "no view holder");
            return null;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if(trailers !=null){
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
            Trailer trailer = trailers.get(holder.getAdapterPosition());
            String imageResult = trailer.getKey();
            Glide.with(context).load("https://img.youtube.com/vi/"+imageResult+"/0.jpg")
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(trailerViewHolder.imageViewTrailer);
            trailerViewHolder.textViewTrailerName.setText(trailer.getName());

        }else if(reviewList !=null){

            ReviewHolder reviewHolder = (ReviewHolder)holder;
            Review reviewsModel = reviewList.get(holder.getAdapterPosition());
            reviewHolder.textViewAuthor.setText(reviewsModel.getAuthor());
            reviewHolder.textViewReview.setText(reviewsModel.getContent());
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {

        if(trailers !=null){
            return trailers.size();
        }else if(reviewList !=null){
            return reviewList.size();
        }else {
            Log.d(LOG_TAG, "no size of list");
            return 0;
        }
    }

    public void setTask(List<Trailer> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void setTaskReview(List<Review> reviewList){
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageViewTrailer;
        TextView textViewTrailerName;


        public TrailerViewHolder(View itemView) {
            super(itemView);

            imageViewTrailer = itemView.findViewById(R.id.thumbnail_imageView);
            textViewTrailerName = itemView.findViewById(R.id.video_name);
            imageViewTrailer.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            String videoKey = trailers.get(getAdapterPosition()).getKey();
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+videoKey+"")));
            Log.d("Video", "Video Playing...");
        }
    }

    public class ReviewHolder extends RecyclerView.ViewHolder{

        TextView textViewAuthor;
        TextView textViewReview;
        public ReviewHolder(View itemView) {
            super(itemView);

            textViewAuthor = itemView.findViewById(R.id.authors_name);
            textViewReview = itemView.findViewById(R.id.authors_review);
        }
    }
}
