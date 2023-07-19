package com.example.movies.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movies.DetailActivity;
import com.example.movies.R;
import com.example.movies.model.Movie;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MovieHolder>{
    private Context context;
    private List<Movie> movieList;

    public PopularAdapter(Context context , List<Movie> movies){
        this.context = context;
        movieList = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular , parent , false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

        Movie movie = movieList.get(position);
        holder.rating.setText(movie.getRating().toString());
        holder.title.setText(movie.getTitle());
        Glide.with(context).load(movie.getPoster().split("\\|")[0]).into(holder.imageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , DetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title" , movie.getTitle());
                bundle.putString("overview" , movie.getOverview());
                bundle.putString("poster" , movie.getPoster().split("\\|")[1]);
                bundle.putDouble("rating" , movie.getRating());
                bundle.putInt("id", movie.getId());
                bundle.putString("genre", movie.getGenre());
                bundle.putString("releaseDate", movie.getReleaseDate());
                bundle.putString("casts", movie.getCasts());

                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title , overview , rating;
        ConstraintLayout constraintLayout;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.name_txt);
            rating = itemView.findViewById(R.id.id_txt);
            constraintLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
