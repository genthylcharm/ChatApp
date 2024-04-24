package com.example.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.Adapter.ChatAdapter;
import com.example.Models.ChatMessage;
import com.example.Models.User;
import com.example.chatfirebase.databinding.ActivityChatBinding;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManage preferenceManage;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();

    }


    private void init() {
        preferenceManage = new PreferenceManage(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages, getBitmapFromEncodedString(receiverUser.image),
                preferenceManage.getString(Contants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void senMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Contants.KEY_SENDER_ID, preferenceManage.getString(Contants.KEY_USER_ID));
        message.put(Contants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Contants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Contants.KEY_TIMESTAMP, new Date());
        database.collection(Contants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
        if (conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Contants.KEY_SENDER_ID, preferenceManage.getString(Contants.KEY_USER_ID));
            conversion.put(Contants.KEY_SENDER_NAME, preferenceManage.getString(Contants.KEY_SENDER_NAME));
            conversion.put(Contants.KEY_SENDER_IMAGE, preferenceManage.getString(Contants.KEY_SENDER_IMAGE));
            conversion.put(Contants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Contants.KEY_RECEIVER_NAME, receiverUser.image);
            conversion.put(Contants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Contants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Contants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }

    private void listenAvailabilityOfReceiver() {
        database.collection(Contants.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if (value.getLong(Contants.KEY_AVAILABILITY) != null) {
                            int availability = Objects.requireNonNull(
                                    value.getLong(Contants.KEY_AVAILABILITY)
                            ).intValue();
                            isReceiverAvailable = availability == 1;
                        }
                        receiverUser.token = value.getString(Contants.KEY_FCM_TOKEN);
                    }
                    if (isReceiverAvailable) {
                        binding.textAvailability.setVisibility(View.VISIBLE);
                    } else {
                        binding.textAvailability.setVisibility(View.GONE);
                    }

                }
        );
    }

    private void listenMessages() {
        database.collection(Contants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Contants.KEY_SENDER_ID, preferenceManage.getString(Contants.KEY_USER_ID))
                .whereEqualTo(Contants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Contants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Contants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Contants.KEY_RECEIVER_ID, preferenceManage.getString(Contants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Contants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Contants.KEY_SENDER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Contants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Contants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Contants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if (conversionId == null) {
            checkForConversion();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Contants.KEY_USER);
        binding.NameTxt.setText(receiverUser.name);
    }

    private void setListeners() {

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> senMessage());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd,yyyy -hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Contants.KEY_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference ->
                        conversionId = documentReference.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference = database.collection(Contants.KEY_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Contants.KEY_LAST_MESSAGE, message,
                Contants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManage.getString(Contants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManage.getString(Contants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Contants.KEY_CONVERSATIONS)
                .whereEqualTo(Contants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Contants.KEY_RECEIVER_ID, receiverUser)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocumentChanges().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            String conversionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }


    public void btnCall(View view) {
        Intent intent = new Intent(ChatActivity.this, CallActivity.class);
        startActivity(intent);
    }
}