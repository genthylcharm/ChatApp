package com.example.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfirebase.databinding.ActivitySigUpBinding;
import com.example.utilities.Contants;
import com.example.utilities.PreferenceManage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SigUpActivity extends AppCompatActivity {
    private ActivitySigUpBinding binding;
    private PreferenceManage preferenceManage;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManage = new PreferenceManage(getApplicationContext());
        setListeners();
    }

    // xu ly listen
    private void setListeners() {
        binding.textLogIn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SigInActivity.class)));
        binding.registerBtn.setOnClickListener(v -> {
            if (isValidSigUpDetails()) {
                signUp();
            }
        });
        binding.layoutImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // xu li anh dai dien
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imgProfile.setImageBitmap(bitmap);
                            binding.addImageTxt.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    // xu li dang ki
    private void signUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Contants.KEY_NAME, binding.fullNameEdt.getText().toString());
        user.put(Contants.KEY_EMAIL, binding.emailEdt.getText().toString());
        user.put(Contants.KEY_PASSWORD, binding.passwordEdt.getText().toString());
        user.put(Contants.KEY_IMAGE, encodedImage);
        database.collection(Contants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManage.putBoolean(Contants.KEY_IS_SIGNED_IN, true);
                    preferenceManage.putString(Contants.KEY_USER_ID, documentReference.getId());
                    preferenceManage.putString(Contants.KEY_NAME, binding.fullNameEdt.getText().toString());
                    preferenceManage.putString(Contants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private boolean isValidSigUpDetails() {
        if (encodedImage == null) {
            showToast("Hãy chọn ảnh đại diện");
            return false;
        }

        String fullName = binding.fullNameEdt.getText().toString().trim();
        if (fullName.isEmpty()) {
            showToast("Hãy điền thông tin tên");
            return false;
        }

        String email = binding.emailEdt.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Hãy điền email");
            return false;
        } else if (!email.endsWith("@gmail.com")) {
            showToast("Email không hợp lệ. Vui lòng sử dụng địa chỉ email @gmail.com");
            return false;
        }

        String password = binding.passwordEdt.getText().toString().trim();
        if (password.isEmpty()) {
            showToast("Hãy điền mật khẩu");
            return false;
        } else if (password.length() < 6 || !password.matches(".*[!@#$%^&*()].*")) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất một ký tự đặc biệt");
            return false;
        }

        String confirmPassword = binding.cfPasswordEdt.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            showToast("Hãy điền xác nhận mật khẩu");
            return false;
        } else if (!password.equals(confirmPassword)) {
            showToast("Mật khẩu và xác nhận mật khẩu phải giống nhau");
            return false;
        }

        return true;
    }


    private void loading(Boolean isloading) {
        if (isloading) {
            binding.registerBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.registerBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }
}