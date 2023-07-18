package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.movies.config.ApplicationConfig;
import com.example.movies.model.User;
import com.example.movies.utils.VolleySingleton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateActivity extends AppCompatActivity {

    private TextView signInLink;
    private Button createAccountButton;
    private RequestQueue requestQueue;
    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        signInLink = findViewById(R.id.signInLink);
        createAccountButton = findViewById(R.id.createAccountBtn);
        name = findViewById(R.id.fullname);
        email = findViewById(R.id.createEmail);
        password = findViewById(R.id.createPassword);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        signInLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(CreateActivity.this, LoginActivity.class));
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    public void createAccount() {
        //Initializing progress  indicator
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Creating account...");
        mDialog.show();

        //Building request
        User user = new User();
        user.setFull_name(name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());

        //Converting request to json string
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = Obj.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //Making API call
        String  url = ApplicationConfig.BASE_URL.concat("createAccount");
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
                            if(response.getString("code").equals("00")) {
                                Toast.makeText(CreateActivity.this, ApplicationConfig.ACCOUNT_CREATION_SUCCESS_MSG, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(CreateActivity.this, LoginActivity.class));

                            }
                            else {
                                Toast.makeText(CreateActivity.this, ApplicationConfig.ACCOUNT_CREATION_SUCCESS_MSG, Toast.LENGTH_LONG).show();
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