package com.example.Adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.User;
import com.example.chatfirebase.databinding.ItemContainerUserBinding;
import com.example.listener.UserListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHoder> {
    private final List<User> users;
    private final UserListener userListener;

    public UserAdapter(List<User> users,UserListener userListener) {
        this.users = users;
        this.userListener= userListener;
    }

    @NonNull
    @Override
    public UserViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new UserViewHoder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHoder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHoder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        UserViewHoder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.name);
            binding.textmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImager(user.image));
            binding.getRoot().setOnClickListener(v-> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImager(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
