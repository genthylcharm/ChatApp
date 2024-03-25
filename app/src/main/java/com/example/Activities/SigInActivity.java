package com.example.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfirebase.databinding.ActivitySigInBinding;

public class SigInActivity extends AppCompatActivity {
    private ActivitySigInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SigUpActivity.class)));

    }
    private void signIn(){

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
