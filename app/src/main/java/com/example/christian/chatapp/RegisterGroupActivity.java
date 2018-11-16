package com.example.christian.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.christian.chatapp.Adapter.GroupUserAdapter;
import com.example.christian.chatapp.Adapter.UserAdapter;
import com.example.christian.chatapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterGroupActivity extends AppCompatActivity {

    private static final String TAG = "ngetes";

    MaterialEditText groupsubject;
    Button btn_register;

    FirebaseUser firebaseUser;

    private RecyclerView recyclerView;
    private GroupUserAdapter groupUserAdapter;
    private List<User> mUsers;
    private List<String> groupMember;

    // receiver of userid
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String userId = intent.getStringExtra("userid");

            // remove userid if selected twice
            if (groupMember.contains(userId)) {
                groupMember.remove(userId);
            } else {
                groupMember.add(userId);
            }

//            Log.d(TAG, String.valueOf(groupMember.size()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
//        Log.d(TAG, "on create");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        groupsubject = findViewById(R.id.groupsubject);
        btn_register = findViewById(R.id.btn_register);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsers = new ArrayList<>();
        groupMember = new ArrayList<>();
        groupMember.add(firebaseUser.getUid());

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupSubject = groupsubject.getText().toString();

                if (TextUtils.isEmpty(groupSubject)) {
                    Toast.makeText(RegisterGroupActivity.this, "group subject cannot empty", Toast.LENGTH_SHORT).show();
                } else {
                    registerGroup(groupSubject);
                }
            }
        });

        // register LocalBroadcastManager to receive userid
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));

        readUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "on Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "on Destroy");
        // unregister LocalBroadcastManager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void readUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                groupUserAdapter = new GroupUserAdapter(RegisterGroupActivity.this, mUsers);
                recyclerView.setAdapter(groupUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerGroup(final String groupSubject) {
        Log.d(TAG, "group name: " + groupSubject);

        for (int x = 0; x<groupMember.size(); x++) {
            Log.d(TAG, "member: " + groupMember.get(x));
        }

        // store members into users child of Threads Table
        DatabaseReference threadReference = FirebaseDatabase.getInstance().getReference("Threads");
        final String key = threadReference.push().getKey();
        Log.d(TAG, "key: "+key);

        Map<String, Object> threadUpdates = new HashMap<>();
        final Long timestamp = System.currentTimeMillis();
        for (int x=0; x<groupMember.size(); x++) {
            threadUpdates.put("/" + key + "/users/" + groupMember.get(x), timestamp);
        }
        threadUpdates.put("/" + key + "/details/" + "type", 2);
        threadUpdates.put("/" + key + "/details/" + "username", groupSubject);
        threadUpdates.put("/" + key + "/details/" + "creationDate", timestamp);

        threadReference.updateChildren(threadUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
                        Map<String, Object> postValues = new HashMap<>();
                        postValues.put(key, System.currentTimeMillis());

                        // store group name into threads child of Users Table
                        Map<String, Object> childUpdates = new HashMap<>();
                        for (int x=0; x<groupMember.size(); x++) {
                            childUpdates.put("/" + groupMember.get(x) + "/threads/" + key, timestamp);
                        }
                        usersReference.updateChildren(childUpdates);

                    }
                });

        Intent intent = new Intent(RegisterGroupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
