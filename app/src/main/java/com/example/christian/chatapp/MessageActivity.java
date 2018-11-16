package com.example.christian.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.christian.chatapp.Adapter.MessageAdapter;
import com.example.christian.chatapp.Model.Message;
import com.example.christian.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference usersReference;
    DatabaseReference threadsReference;

    ImageButton btnSend;
    EditText textSend;

    MessageAdapter messageAdapter;
    List<Message> mMessage;

    RecyclerView recyclerView;

    Intent intent;

    private static final String TAG = "ngetes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btn_send);
        textSend = findViewById(R.id.text_send);

        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        threadsReference = FirebaseDatabase.getInstance().getReference("Threads");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        final String userId = intent.getStringExtra("userid");
        final String threadId = intent.getStringExtra("threadid");

//        Log.d(TAG, threadId);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = textSend.getText().toString();
                if(!payload.equals("")){
                    sendMessage(fUser.getUid(), threadId, payload);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        usersReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profileImage);
                }

                readMessages(fUser.getUid(), userId, user.getImageURL(), threadId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String sender, final String threadId, String payload) {
        DatabaseReference reference = threadsReference.child(threadId).child("messages");

        Message message = new Message(sender, payload); // new message object
        reference.push().setValue(message);
    }

    private void readMessages(final String myid, final String userid, final String imageurl, final String threadId) {
        mMessage = new ArrayList<>();
        DatabaseReference reference = threadsReference.child(threadId).child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    mMessage.add(message);

                    messageAdapter = new MessageAdapter(MessageActivity.this, mMessage, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
