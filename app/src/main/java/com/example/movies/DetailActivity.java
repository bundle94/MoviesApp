package com.example.movies;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.movies.adapter.ReviewAdapter;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.Review;
import com.example.movies.model.ReviewRequest;
import com.example.movies.utils.RoundedBackgroundSpan;
import com.example.movies.utils.VolleySingleton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private View hiddenPanel;
    private View scrollView;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private RequestQueue requestQueue;
    private SpannableString spannableString;
    private Button sendButton;
    private Context context;
    private  int movieId;
    private EditText reviewEdittext;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Details");
        actionBar.setDisplayHomeAsUpEnabled(true);


        ImageView imageView = findViewById(R.id.poster_image);
        TextView rating_tv = findViewById(R.id.mRating);
        TextView title_tv = findViewById(R.id.mTitle);
        TextView overview_tv = findViewById(R.id.movervie_tv);
        TextView genre_tv = findViewById(R.id.genre_tv);
        TextView releaseDate_tv = findViewById(R.id.release_tv);
        TextView casts_tv = findViewById(R.id.cast_tv);

        hiddenPanel = findViewById(R.id.hidden_panel);
        scrollView = findViewById(R.id.scroll_view);
        fab = findViewById(R.id.review_fab);
        sendButton = findViewById(R.id.sendBtn);
        reviewEdittext = findViewById(R.id.reviewEdittext);
        context = this;

        recyclerView = findViewById(R.id.reviewrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        Bundle bundle = getIntent().getExtras();

        mTitle = bundle.getString("title");
        String mPoster = bundle.getString("poster");
        String mOverView = bundle.getString("overview");
        double mRating = bundle.getDouble("rating");
        movieId = bundle.getInt("id");
        String mreleaseDate = bundle.getString("releaseDate");
        String casts = bundle.getString("casts");

        Glide.with(this).load(mPoster).into(imageView);
        rating_tv.setText(Double.toString(mRating));
        title_tv.setText(mTitle);
        overview_tv.setText(mOverView);
        releaseDate_tv.setText("Released Date: "+ mreleaseDate);
        casts_tv.setText("Casts: "+ casts);


        String genre = bundle.getString("genre");

        String[] genreArray = genre.split(",");

        //Creating spans for genres
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < genreArray.length; i++) {
            spannableString = new SpannableString(genreArray[i]);
            spannableString.setSpan(new RoundedBackgroundSpan(Color.RED, Color.WHITE), 0, genreArray[i].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            if(i != 0) stringBuilder.append(" ");
            stringBuilder.append(spannableString);

        }
        genre_tv.setText(stringBuilder);

        reviewList = new ArrayList<>();
        fetchReviews();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Submit your review
                submitReview();
            }
        });

    }

    public  void submitReview() {

        if(reviewEdittext.getText().toString().equals("")) {
            Toast.makeText(DetailActivity.this, "Review field is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            //Initialize progress loader indicator
            ProgressDialog mDialog = new ProgressDialog(context);
            mDialog.setMessage("Submitting review...");
            mDialog.show();

            SharedPreferences preferences = getSharedPreferences(ApplicationConfig.APP_PREFERENCE_NAME, MODE_PRIVATE);
            int user_id = preferences.getInt("user_id", 0);
            String full_name = preferences.getString("full_name", ApplicationConfig.APP_ANONYMOUS_NAME);

            //Getting settings preference
            SharedPreferences settingsPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean anonymousSet = settingsPreference.getBoolean("anonymous", false);

            //Building request
            ReviewRequest request = new ReviewRequest();
            request.setMovie_id(movieId);
            request.setUser_id(user_id);
            request.setReview(reviewEdittext.getText().toString());
            request.setAnonymous(anonymousSet);

            //Converting request to json string
            ObjectMapper Obj = new ObjectMapper();
            String jsonStr = null;
            try {
                jsonStr = Obj.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            String url = ApplicationConfig.BASE_URL.concat("create");
            JSONObject jsonBody = null;
            try {
                jsonBody = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            mDialog.dismiss();
                            try {
                                if (response.getString("code").equals("00")) {
                                    Review review = new Review(full_name, movieId, reviewEdittext.getText().toString(), anonymousSet);
                                    reviewList.add(review);
                                    reviewAdapter.setFilteredList(reviewList);
                                    reviewEdittext.setText("");
                                } else {
                                    Toast.makeText(DetailActivity.this, "Unable to submit review", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDialog.dismiss();
                    error.printStackTrace();
                    //Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            this.requestQueue.add(jsonObjectRequest);
        }
    }

    public void fetchReviews() {
        //make Api call
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Fetching reviews...");
        mDialog.show();

        String  url = ApplicationConfig.BASE_URL.concat("getMovieReviews/"+movieId);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        mDialog.dismiss();
                        for (int i = 0 ; i < response.length() ; i ++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String fullName = jsonObject.getString("full_name");
                                int movieId = jsonObject.getInt("movie_id");
                                String review = jsonObject.getString("review");
                                boolean anonymous = jsonObject.getBoolean("anonymous");

                                Review reviewItem = new Review(fullName , movieId , review, anonymous);
                                reviewList.add(reviewItem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        reviewAdapter = new ReviewAdapter(DetailActivity.this , reviewList);

                        recyclerView.setAdapter(reviewAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        this.requestQueue.add(jsonArrayRequest);
    }

    public void slideUpDown(final View view) {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(view.VISIBLE);
            fab.hide();
        }
        else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_down);

            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            fab.show();
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,ApplicationConfig.SHARE_MOVIE_MESSAGE.concat(mTitle));
                sendIntent.setType("text/plain");
                Intent.createChooser(sendIntent,"Share via");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}