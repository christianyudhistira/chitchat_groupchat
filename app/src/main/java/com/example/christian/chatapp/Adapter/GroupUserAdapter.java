package com.example.christian.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.christian.chatapp.Model.User;
import com.example.christian.chatapp.R;

import java.util.List;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    private static final String TAG = "ngetes";

    public GroupUserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public GroupUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new GroupUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupUserAdapter.ViewHolder viewHolder, int i) {
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
//                Log.d(TAG, user.getUsername());

                // send userid to LocalBroadcastManager
                Intent intent = new Intent("custom-message");
                intent.putExtra("userid", user.getId());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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

