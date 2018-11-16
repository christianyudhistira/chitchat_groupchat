package com.example.christian.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.christian.chatapp.Adapter.ChatAdapter;
import com.example.christian.chatapp.Model.Thread;
import com.example.christian.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Thread> mThreads;
    private List<String> threadsId;
    Map<String, Object> data;
    HashSet<Thread> hset;

    private static final String TAG = "ngetes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mThreads = new ArrayList<>();
        threadsId = new ArrayList<>();
        hset = new HashSet<>();
        data = new HashMap<>();

//        readThread();
        chatAdapter = new ChatAdapter(getContext(), mThreads);
        //chatAdapter = new ChatAdapter(getContext(), new ArrayList<Thread>(hset));
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(chatAdapter);

//        if (chatAdapter == null) {
//            chatAdapter = new ChatAdapter(getContext(), mThreads);
//            //chatAdapter = new ChatAdapter(getContext(), new ArrayList<Thread>(hset));
//            recyclerView.setAdapter(null);
//            recyclerView.setAdapter(chatAdapter);
//        }

        readThread();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "chat fragments start");
        // tadinya di sini
//        mThreads.clear();
//        threadsId.clear();
//        chatAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(chatAdapter);
//        for (int i = 0; i < mThreads.size(); i++) {
//            Log.d(TAG, "a: " + mThreads.get(i).getUsername());
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "chat fragments destroy");
    }

    private void readThread() {
        Log.d(TAG, "reading Thread");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("threads");


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // check if node exist
                if (dataSnapshot.exists()) {
                    //Map<String, Object> d = (Map<String, Object>) dataSnapshot.getValue();
                    mThreads.clear();
                    threadsId.clear();
                    data.clear();

                    data.putAll((Map<String, Object>) dataSnapshot.getValue());
                    int i = 0;
                    int mapSize = data.size();
                    Log.d(TAG, "mapSze: " + mapSize);
                    for (Map.Entry<String, Object> entry : data.entrySet()) {

                        Log.d(TAG, "-------- entry: " + entry.getKey());

                        FirebaseDatabase.getInstance()
                                .getReference("Threads")
                                .child(entry.getKey()).child("details")
                                .orderByChild(entry.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //mThreads.add(dataSnapshot.getValue(Thread.class));
                                        //hset.add(dataSnapshot.getValue(Thread.class));
                                        //mThreads = new ArrayList<Thread>(hset);
                                        if (!mThreads.contains(dataSnapshot.getValue(Thread.class))) {
                                            Log.d(TAG, "BELUM ADA");
                                            mThreads.add(dataSnapshot.getValue(Thread.class));
                                            chatAdapter.notifyDataSetChanged();

                                        } else {
                                            Log.d(TAG, "UDAH ADA");
                                        }
                                        Log.d(TAG, "TAi: ---------");
//                                        for (int i = 0; i < mThreads.size(); i++) {
//                                            Log.d(TAG, "-> " + mThreads.get(i).getUsername());
//                                        }
//                                        Iterator<Thread> setIterator = hset.iterator();
//                                        while(setIterator.hasNext()){
//                                            Log.d(TAG, "-> " + setIterator.next().getUsername());
//                                        }
                                        Log.d(TAG, "---------------");
                                        //chatAdapter = new ChatAdapter(getContext(), mThreads);
                                        //recyclerView.setAdapter(chatAdapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                    Log.d(TAG, "------------------");


                    //data = (Map<String, Object>) dataSnapshot.getValue();


//                    mThreads.clear();
//                    threadsId.clear();
//                    final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
//
//                    for (Map.Entry<String, Object> entry : data.entrySet()) {
////                        FirebaseDatabase.getInstance().getReference("Threads").child(entry.getKey()).child("details")
////                                .addListenerForSingleValueEvent(new ValueEventListener() {
////                                    @Override
////                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                        Thread threadDetail = dataSnapshot.getValue(Thread.class);
////                                        mThreads.add(threadDetail);
////
////                                        chatAdapter = new ChatAdapter(getContext(), mThreads);
////                                        recyclerView.setAdapter(chatAdapter);
////                                    }
////
////                                    @Override
////                                    public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                    }
////                                });
//                        threadsId.add(entry.getKey());
//                        Log.d(TAG, entry.getKey());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}