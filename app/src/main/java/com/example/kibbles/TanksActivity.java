package com.example.kibbles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TanksActivity extends AppCompatActivity {
    FirebaseFirestore db;

    ProgressBar PB_water;
    ProgressBar PB_kibbles;

    TextView TV_testDB;
    TextView TV_water_percent;
    TextView TV_kibbles_percent;

    String TAG;
    String TB_content;
    String water;
    String kibbles;
    String water_tank;
    String kibbles_tank;
    String timestamp;
    int kibbles_tank_int;
    int water_tank_int;
    int max_kibbles_tank;
    int max_water_tank;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanks);
        db = FirebaseFirestore.getInstance();
        TAG = "test";
        TB_content="";
        max_water_tank = 100;
        max_kibbles_tank = 100;

        TV_testDB = findViewById(R.id.TV_TestDB);
        TV_kibbles_percent = findViewById(R.id.TV_kibbles_percent);
        TV_water_percent = findViewById(R.id.TV_water_percent);

        getDatasFromFirebase();

        PB_water = findViewById(R.id.PB_water); // initiate the progress bar
        PB_water.setMax(water_tank_int); // 100 maximum value for the progress value
        PB_water.setProgress(50); // 50 default progress value for the progress bar
     //   PB_water.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        PB_kibbles = findViewById(R.id.PB_kibbles); // initiate the progress bar
        PB_kibbles.setMax(max_kibbles_tank); // 100 maximum value for the progress value
        PB_kibbles.setProgress(kibbles_tank_int); // 50 default progress value for the progress bar





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
                                timestamp = kibbles = (String)document.getString("timestamp");

                              //  kibbles = (String)document.getString("kibbles");
                                kibbles_tank = (String)document.getString("kibbles_tank");
                                kibbles_tank_int = Integer.parseInt(kibbles_tank);

                              //  water = (String)document.getString("water");
                               // water_tank = (String)document.getString("water_tank");
                               // water_tank_int = Integer.parseInt(water_tank);

                                TB_content = TB_content + "\n Date : " + timestamp + " Réservoir eau : " + water_tank + " Réservoir croquettes : " +kibbles_tank;
                                TV_testDB.setText(TB_content);
                                TV_water_percent.setText(water_tank_int*100/max_water_tank + " % ");
                                if(kibbles_tank_int == 0){
                                    TV_kibbles_percent.setText("Réservoir de croquettes plein");
                                }else if(kibbles_tank_int == 1){
                                    TV_kibbles_percent.setText("Réservoir de croquettes vide");
                                }


                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}