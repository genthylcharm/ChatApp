package com.example.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfirebase.databinding.ActivitySigInBinding;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigInActivity extends AppCompatActivity {
    private ActivitySigInBinding binding;
    private PreferenceManage preferenceManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManage = new PreferenceManage(getApplicationContext());
        if (preferenceManage.getBoolean(Contants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySigInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SigUpActivity.class)));
        binding.LoginBtn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });

    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Contants.KEY_COLLECTION_USERS)
                .whereEqualTo(Contants.KEY_EMAIL, binding.emailEdt.getText().toString())
                .whereEqualTo(Contants.KEY_PASSWORD, binding.passwordEdt.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocumentChanges().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManage.putBoolean(Contants.KEY_IS_SIGNED_IN, true);
                        preferenceManage.putString(Contants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManage.putString(Contants.KEY_NAME, documentSnapshot.getString(Contants.KEY_NAME));
                        preferenceManage.putString(Contants.KEY_IMAGE, documentSnapshot.getString(Contants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });


    }

    // hien thi progressbar o buttonLogin
    private void loading(Boolean isloading) {
        if (isloading) {
            binding.LoginBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.LoginBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private Boolean isValidSignInDetails() {
        if (binding.emailEdt.getText().toString().trim().isEmpty()) {
            showToast("nhap email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEdt.getText().toString()).matches()) {
            showToast("khong dung email");
            return false;
        } else if (binding.passwordEdt.getText().toString().trim().isEmpty()) {
            showToast("nhap password");
            return false;
        } else {
            return true;
        }
    }
}
