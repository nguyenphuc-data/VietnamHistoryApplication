// src/main/java/com/example/vietnamhistoryapplication/profile/ProfileOverviewFragment.java
package com.example.vietnamhistoryapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.vietnamhistoryapplication.forum.ForumActivity;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.utils.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileOverviewFragment extends Fragment {

    private CircleImageView ivProfilePhoto;  // ĐỔI SANG CircleImageView
    private TextView tvUserName, tvEmail;
    private Button btnEdit, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_overview_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnEdit.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));

        btnLogout.setOnClickListener(v -> signOut());

        view.findViewById(R.id.btnForum).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ForumActivity.class))
        );

        loadUserData(); // LẤY ẢNH + TÊN + EMAIL
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Không có người dùng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseUser.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String photoUrl = document.getString("photo"); // ẢNH TỪ users

                        // ƯU TIÊN: photo từ users → Google → mặc định
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            loadPhoto(photoUrl);
                        } else {
                            String googlePhoto = firebaseUser.getPhotoUrl() != null
                                    ? firebaseUser.getPhotoUrl().toString()
                                    : null;
                            if (googlePhoto != null && !googlePhoto.isEmpty()) {
                                loadPhoto(googlePhoto);
                            } else {
                                ivProfilePhoto.setImageResource(R.drawable.avatar);
                            }
                        }

                        tvUserName.setText(name != null && !name.trim().isEmpty() ? name : "Người dùng");
                        tvEmail.setText(email != null ? "Email: " + email : "Email: Chưa có");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Profile", "Lỗi tải dữ liệu", e);
                    Toast.makeText(getActivity(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPhoto(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(ivProfilePhoto);
    }

    private void signOut() {
        mAuth.signOut();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                requireActivity(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        );
        googleSignInClient.signOut();

        UserSession.clear();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.vietnamhistoryapplication.home.ProfileFragment.ProfileFragment())
                .commit();
    }
}