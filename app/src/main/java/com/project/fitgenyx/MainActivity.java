package com.project.fitgenyx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button logout;
    private EditText reps;
    private EditText weight;
    private Button savebtn;
    private Button showDataBtn;
    private TextView activitiesListTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        reps = findViewById(R.id.reps);
        weight = findViewById(R.id.weight);
        savebtn = findViewById(R.id.repsBtn);
        showDataBtn = findViewById(R.id.showDataBtn);
        activitiesListTxt = findViewById(R.id.activities_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // get the uid of the user that is currently logged in
        String uid;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = "";
        }


        // logout the current user and redirect them to the start page
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> activity = new HashMap<>();
                activity.put("reps", reps.getText().toString());
                activity.put("weight", weight.getText().toString());
                activity.put("create_time", FieldValue.serverTimestamp());

                db.collection("userData")
                        .document(uid)
                        .collection("activities")
                        .add(activity)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Exercise added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Exercise not added successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        showDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // retrieve all activities logged by the current user sorted by the time they were entered
                db.collection("userData")
                        .document(uid)
                        .collection("activities")
                        .orderBy("create_time")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    activitiesListTxt.setText("");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.e("Document ", document.getId() + " => " + document.getData());
                                        activitiesListTxt.append(document.getData().toString() + "\n");
                                    }
                                }
                            }
                        });
            }
        });


//        Map<String, Object> city = new HashMap<>();
//        city.put("name", "Winter Haven");
//        city.put("state", "florida");
//        city.put("country", "usa");
//
//        db.collection("cities").document("jsr").set(city).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "values added", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        Map<String, Object> data = new HashMap<>();
//        data.put("capital", false);

//        db.collection("cities").document("jsr").set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "merge successful", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("name", "Tokyo");
//        data.put("capital", "Japan");
//        db.collection("cities").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "values added successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        DocumentReference ref = FirebaseFirestore.getInstance().collection("cities").document("jsr");
//        ref.update("capital", true);

//        DocumentReference docRef = FirebaseFirestore.getInstance().collection("cities").document("jsr");
    }
}