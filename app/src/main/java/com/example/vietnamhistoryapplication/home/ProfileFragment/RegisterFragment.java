package com.example.vietnamhistoryapplication.home.ProfileFragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vietnamhistoryapplication.R;
import com.google.firebase.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterFragment extends Fragment {

    private EditText etName, etUsername, etPassword, etEmail;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.register_fragment, container, false);

        etName = view.findViewById(R.id.etName);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvBackToLogin = view.findViewById(R.id.tvBackToLogin);
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        tvBackToLogin.setOnClickListener(v -> goToLogin());

        return view;
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Kiểm tra bắt buộc
        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tài khoản");
            etUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            etPassword.requestFocus();
            return;
        }

        // Kiểm tra email (nếu có)
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        // Kiểm tra username trùng
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        etUsername.setError("Tên tài khoản đã được sử dụng");
                        etUsername.requestFocus();
                    } else {
                        // Nếu không trùng -> tạo mới
                        String uid = UUID.randomUUID().toString();
                        long createdAt = System.currentTimeMillis();

                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", uid);
                        user.put("name", name);
                        user.put("username", username);
                        user.put("password", password);
                        user.put("email", TextUtils.isEmpty(email) ? null : email);
                        user.put("photo", null);
                        user.put("createdAt", createdAt);

                        db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> showSuccessPopup())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra tài khoản: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void goToLogin() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void showSuccessPopup() {
        if (getContext() == null) return;

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("🎉 Đăng ký thành công")
                .setMessage("Tài khoản của bạn đã được tạo thành công!\nBạn có thể đăng nhập ngay bây giờ.")
                .setCancelable(false)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    dialog.dismiss();
                    goToLogin();
                })
                .show();
    }
}
