package com.example.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.Adapter.UserAdapter;
import com.example.Models.User;
import com.example.chatfirebase.databinding.ActivityUsersBinding;
import com.example.listener.UserListener;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
    private ActivityUsersBinding binding;
    private PreferenceManage preferenceManage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManage = new PreferenceManage(getApplicationContext());
        setLisetners();
        getUser();

    }

    private void setLisetners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUser() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Contants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManage.getString(Contants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Contants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Contants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Contants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Contants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UserAdapter userAdapter = new UserAdapter(users, this);
                            binding.userRecyclerView.setAdapter(userAdapter);
                            binding.userRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.erroMessageTxt.setText(String.format("%s", "no user available"));
        binding.erroMessageTxt.setText(View.VISIBLE);
    }

    private void loading(Boolean isloading) {
        if (isloading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Contants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}