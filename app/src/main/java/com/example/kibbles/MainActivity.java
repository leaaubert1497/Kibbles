package com.example.kibbles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String TAG;
    String TB_content;
    String water;
    String kibbles;
    String timestamp;

    Button btn_tanks;
    Button btn_mode_manuel;
    Button btn_mode_auto;
    Button btn_refillKibbles;
    Button btn_refillWater;

    TextView TV_kibbleContent;
    TextView TV_waterContent;
    TextView TV_curentContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        btn_tanks = findViewById(R.id.btn_tanks);
        btn_mode_manuel = findViewById(R.id.btn_mode_manuel);
        btn_mode_auto = findViewById(R.id.btn_mode_auto);
        btn_refillKibbles = findViewById(R.id.btn_refillKibbles);
        btn_refillWater = findViewById(R.id.btn_refillWater);

        TV_curentContent = findViewById(R.id.TV_curentContent);
        TV_waterContent = findViewById(R.id.TV_waterContent);
        TV_kibbleContent = findViewById(R.id.TV_kibblesContent);

        getDatasFromFirebase();
        btn_tanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTanksActivity = new Intent(getApplicationContext(), TanksActivity.class);
                startActivity(intentTanksActivity);
            }
        });

        btn_mode_manuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modePost("manuel");
            }
        });

        btn_mode_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modePost("auto");
            }
        });

        btn_refillKibbles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refillPost("refill_kibbles");
            }
        });

        btn_refillWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refillPost("refill_water");
            }
        });

    }

    public void refillPost(String action){
        String postUrl = "<Node-RED IP>/refill";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("action", action);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    public void modePost(String mode){
        String postUrl = "<Node-RED IP>/mode";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("mode", mode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void getDatasFromFirebase(){
        db.collection("gamelles").orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                timestamp = (String)document.getString("timestamp");

                                kibbles = (String)document.getString("kibbles");
                                water = (String)document.getString("water");

                                TV_waterContent.setText(water + " mL");
                                TV_kibbleContent.setText(kibbles + " g");
                                TV_curentContent.setText("Contenu actuel (maj : " +timestamp);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}