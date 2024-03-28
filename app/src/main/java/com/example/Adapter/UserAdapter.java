package com.example.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfirebase.databinding.ItemContainerUserBinding;

public class UserAdapter {
    class UserViewHoder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        UserViewHoder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;

        }
    }

    private Bitmap getUserImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
