package com.example.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.Adapter.RecentConversationsAdapter;
import com.example.Models.ChatMessage;
import com.example.Models.User;
import com.example.chatfirebase.databinding.ActivityHomeBinding;
import com.example.listener.ConversionListener;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends BaseActivity implements ConversionListener {
    private ActivityHomeBinding binding;
    private PreferenceManage preferenceManage;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManage = new PreferenceManage(getApplicationContext());
        init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> {
            SignOut();
        });
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        binding.NameTxt.setText(preferenceManage.getString(Contants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManage.getString(Contants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        database.collection(Contants.KEY_CONVERSATIONS)
                .whereEqualTo(Contants.KEY_SENDER_ID, preferenceManage.getString(Contants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Contants.KEY_CONVERSATIONS)
                .whereEqualTo(Contants.KEY_RECEIVER_ID, preferenceManage.getString(Contants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

        private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String senderId = documentChange.getDocument().getString(Contants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Contants.KEY_RECEIVER_ID);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.senderId = senderId;
                        chatMessage.receiverId = receiverId;
                        if (preferenceManage.getString(Contants.KEY_USER_ID).equals(senderId)) {
                            chatMessage.conversionImage = documentChange.getDocument().getString(Contants.KEY_RECEIVER_IMAGE);
                            chatMessage.conversionName = documentChange.getDocument().getString(Contants.KEY_RECEIVER_NAME);
                            chatMessage.conversionId = documentChange.getDocument().getString(Contants.KEY_RECEIVER_ID);
                        } else {
                            chatMessage.conversionImage = documentChange.getDocument().getString(Contants.KEY_SENDER_IMAGE);
                            chatMessage.conversionName = documentChange.getDocument().getString(Contants.KEY_SENDER_NAME);
                            chatMessage.conversionId = documentChange.getDocument().getString(Contants.KEY_SENDER_ID);
                        }
                        chatMessage.message = documentChange.getDocument().getString(Contants.KEY_LAST_MESSAGE);
                        chatMessage.dateObject = documentChange.getDocument().getDate(Contants.KEY_TIMESTAMP);
                        conversations.add(chatMessage);

                    } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                        for (int i = 0; i < conversations.size(); i++) {
                            String senderId = documentChange.getDocument().getString(Contants.KEY_SENDER_ID);
                            String receiverId = documentChange.getDocument().getString(Contants.KEY_RECEIVER_ID);
                            if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                                conversations.get(i).message = documentChange.getDocument().getString(Contants.KEY_LAST_MESSAGE);
                                conversations.get(i).dateObject = documentChange.getDocument().getDate(Contants.KEY_TIMESTAMP);
                                break;
                            }
                        }
                    }
                }
                Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
                conversationsAdapter.notifyDataSetChanged();
                binding.conversationsRecyclerView.smoothScrollToPosition(0);
                binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        };

        private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Contants.KEY_COLLECTION_USERS).document(
                preferenceManage.getString(Contants.KEY_USER_ID)
        );
        documentReference.update(Contants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Tokend update fail"));
    }

    private void SignOut() {

        showToast("Signing out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Contants.KEY_COLLECTION_USERS).document(
                preferenceManage.getString(Contants.KEY_USER_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Contants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
                    preferenceManage.clear();
                    startActivity(new Intent(getApplicationContext(), SigInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Thoat Tai Khoan"));
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Contants.KEY_USER, user);
        startActivity(intent);
    }
}
