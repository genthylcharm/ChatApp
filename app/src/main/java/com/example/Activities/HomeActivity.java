package com.example.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfirebase.databinding.ActivityHomeBinding;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private PreferenceManage preferenceManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManage = new PreferenceManage(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
    }
    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v-> {
            SignOut();
        });
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

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Contants.KEY_COLLECTION_USERS).document(
                preferenceManage.getString(Contants.KEY_USER_ID)
        );
        documentReference.update(Contants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Tokend update ok"))
                .addOnFailureListener(e -> showToast("Tokend update fail"));
    }

    private void SignOut() {
        showToast("Signing out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Contants.KEY_COLLECTION_USERS).document(
                preferenceManage.getString(Contants.KEY_USER_ID)
        );
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Contants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates). addOnSuccessListener(unused -> {
            preferenceManage.clear();
            startActivity(new Intent(getApplicationContext(),SigInActivity.class));
            finish();
                })
                .addOnFailureListener(e -> showToast("Thoat Tai Khoan"));
    }


}