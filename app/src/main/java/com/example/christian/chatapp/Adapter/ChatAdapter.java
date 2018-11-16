package com.example.christian.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private List<Thread> mThreads;

    private static final String TAG = "ngetes";

    public ChatAdapter(Context mContext, List<Thread> mThreads) {
        this.mContext = mContext;
        this.mThreads = mThreads;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, viewGroup, false);

        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        final Thread thread = mThreads.get(i);
        viewHolder.username.setText(thread.getUsername());
        viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);

        // video 6
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open new activity for single chat
//                Intent intent = new Intent(mContext, MessageActivity.class);
//                intent.putExtra("userid", receiverUserId);
//                intent.putExtra("threadid", threadId);
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThreads.size();
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
