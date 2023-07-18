package com.example.movies.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.movies.adapter.MovieAdapter;
import com.example.movies.R;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.Movie;
import com.example.movies.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<Movie> movieList;
    private Context context;
    private SearchView searchView;
    private MovieAdapter movieAdapter;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Movies");
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.clearFocus();
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        // Refresh  the layout
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        //Clear the moviesList for the new record that will be fetched
                        movieList = new ArrayList<>();
                        fetchMovies();

                        //Set this to false to avoid multiple refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        requestQueue = VolleySingleton.getmInstance(context).getRequestQueue();

        movieList = new ArrayList<>();
        fetchMovies();
    }

    //Search filter implementation
    private void filterList(String text){
        if(!text.isEmpty()) {
            List<Movie> filteredMovieList = new ArrayList<>();
            for (Movie movie : movieList) {
                if (movie.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredMovieList.add(movie);
                }
            }

            if (filteredMovieList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
            } else {
                movieAdapter.setFilteredList(filteredMovieList);
            }
        }
    }

    //Fetch the movies
    private void fetchMovies() {

        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("Fetching movies...");
        mDialog.show();

        String url = ApplicationConfig.BASE_URL.concat("getAll");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

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

                            movieAdapter = new MovieAdapter(context , movieList);

                            recyclerView.setAdapter(movieAdapter);
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