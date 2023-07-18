package com.example.movies;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.movies.adapter.NotificationAdapter;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.Notification;
import com.example.movies.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private List<Notification> notificationList;
    private NotificationAdapter notificationAdapter;
    private RequestQueue requestQueue;
    private RecyclerView notificationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ActionBar aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setTitle("Notifications");
            aBar.setDisplayHomeAsUpEnabled(true);
        }

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        //notificationRecyclerView.setHasFixedSize(true);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        notificationList = new ArrayList<>();
        fetchNotifications();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Fetch the notifications
    private void fetchNotifications() {

        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Fetching notifications...");
        mDialog.show();

        String  url = ApplicationConfig.BASE_URL.concat("getAllNotifications");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mDialog.dismiss();
                        for (int i = 0 ; i < response.length() ; i ++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String message = jsonObject.getString("message");

                                Notification notification = new Notification(title, message);
                                notificationList.add(notification);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        notificationAdapter = new NotificationAdapter(NotificationActivity.this , notificationList);

                        notificationRecyclerView.setAdapter(notificationAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        this.requestQueue.add(jsonArrayRequest);
    }
}