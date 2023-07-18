package com.example.movies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context , List<Review> reviews){
        this.context = context;
        reviewList = reviews;
    }

    public void setFilteredList(List<Review> filteredList) {
        this.reviewList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review , parent , false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Review review = reviewList.get(position);
        if(review.isAnonymous()) {
            holder.name.setText(ApplicationConfig.APP_ANONYMOUS_NAME);
        }
        else {
            holder.name.setText(review.getFullName());
        }
        holder.review.setText(review.getReview());

        /*holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , DetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title" , movie.getTitle());
                bundle.putString("overview" , movie.getOverview());
                bundle.putString("poster" , movie.getPoster());
                bundle.putDouble("rating" , movie.getRating());

                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder{

        TextView name , review;
        ConstraintLayout constraintLayout;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_tv);
            review = itemView.findViewById(R.id.review_tv);
            constraintLayout = itemView.findViewById(R.id.main_layout2);
        }
    }
}
