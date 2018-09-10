package com.efhemo.movienano;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.efhemo.movienano.model.Review;

import java.util.List;

public class ReviewFragment extends DialogFragment {

    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();

    public ReviewFragment() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.review_main);
        dialog.setTitle("Review");
        setStyle(R.style.Dialog_FullScreen, android.R.style.Theme);

        RecyclerView recyclerView = dialog.findViewById(R.id.list_review);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        TextView textViewrev = dialog.findViewById(R.id.review_text1);


        GenRCAdapter genRCAdapter = new GenRCAdapter(getContext());
        recyclerView.setAdapter(genRCAdapter);


        List<Review> reviewList = getArguments().getParcelableArrayList("list");
        if(reviewList == null){
            Log.d(LOG_TAG, "Empty ReviewList");
        }
        textViewrev.setVisibility(View.GONE);
        genRCAdapter.setTaskReview(reviewList);
        Log.d(LOG_TAG, "Not Empty ReviewList");

        return dialog;
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
