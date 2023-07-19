package com.example.movies.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.example.movies.adapter.PopularAdapter;
import com.example.movies.R;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.Movie;
import com.example.movies.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PopularFragment extends Fragment {

    RecyclerView recyclerView;
    private Context context;
    private RequestQueue requestQueue;
    private List<Movie> movieList;
    private PopularAdapter popularAdapter;
    ImageView poster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);
        getActivity().setTitle("Popular Movies");
        recyclerView = (RecyclerView) view.findViewById(R.id.popularRecycler);
        poster = (ImageView) view.findViewById(R.id.poster_image);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));


        requestQueue = VolleySingleton.getmInstance(context).getRequestQueue();

        movieList = new ArrayList<>();
        fetchPopularMovies();
    }

    //Fetch the movies
    private void fetchPopularMovies() {

        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("Fetching popular movies...");
        mDialog.show();

        String url = ApplicationConfig.BASE_URL.concat("popularMovies");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Random rand = new Random();
                        // Setting the upper bound to generate the
                        // random numbers in specific range
                        int upperbound = response.length();
                        int int_random = rand.nextInt(upperbound);
                        String selectedPoster = null;
                        try {
                            JSONObject random = response.getJSONObject(int_random);
                            selectedPoster = random.getString("poster");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Glide.with(context).load(selectedPoster.split("\\|")[1]).into(poster);
                        mDialog.dismiss();
                        for (int i = 0 ; i < response.length() ; i ++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String overview = jsonObject.getString("overview");
                                String poster = jsonObject.getString("poster");
                                Double rating = jsonObject.getDouble("rating");
                                int id = jsonObject.getInt("id");
                                String genre = jsonObject.getString("genre");
                                String releaseDate = jsonObject.getString("release_date");
                                String casts = jsonObject.getString("casts");

                                Movie movie = new Movie(title , poster , overview , rating, id, genre, releaseDate, casts);
                                movieList.add(movie);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            popularAdapter = new PopularAdapter(context , movieList);

                            recyclerView.setAdapter(popularAdapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Put the request in volley queue
        requestQueue.add(jsonArrayRequest);
    }
}