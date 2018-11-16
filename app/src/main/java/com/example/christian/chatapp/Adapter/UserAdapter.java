package com.example.christian.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.christian.chatapp.MessageActivity;
import com.example.christian.chatapp.Model.Thread;
import com.example.christian.chatapp.Model.User;
import com.example.christian.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    private static final String TAG = "ngetes";

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
        }

        // video 6
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String senderUserId = currentUser.getUid();
                final String receiverUserId = user.getId();
                final String threadId = (senderUserId.compareTo(receiverUserId) < 0 ? senderUserId.concat(receiverUserId) : receiverUserId.concat(senderUserId));

                DatabaseReference threadsReference = FirebaseDatabase.getInstance().getReference("Threads").child(threadId).child("users");

                // set users who participate in the single chat
                Map<String, Object> userMap = new HashMap<>();
                Long timestamp = System.currentTimeMillis();
                userMap.put(senderUserId, timestamp);
                userMap.put(receiverUserId, timestamp);

                threadsReference.setValue(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
                                Map<String, Object> threadMap = new HashMap<>();
                                threadMap.put(threadId, System.currentTimeMillis());

                                // set chat room where sender join
                                usersReference.child(senderUserId).child("threads").updateChildren(threadMap);
                                // set chat room where receiver join
                                usersReference.child(receiverUserId).child("threads").updateChildren(threadMap);
                            }
                        });

                // open new activity for single chat
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", receiverUserId);
                intent.putExtra("threadid", threadId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
