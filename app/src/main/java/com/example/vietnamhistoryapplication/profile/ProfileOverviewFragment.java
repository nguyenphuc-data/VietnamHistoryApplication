package com.example.vietnamhistoryapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.vietnamhistoryapplication.models.UserModel;
import com.example.vietnamhistoryapplication.profile.EditProfileActivity;
import com.example.vietnamhistoryapplication.home.ProfileFragment.ProfileFragment;
import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.utils.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileOverviewFragment extends Fragment {

    private ImageView ivProfilePhoto;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        btnLogout.setOnClickListener(v -> signOut());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        loadUserData();
    }

    private void loadUserData() {
        UserModel currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "Không có người dùng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String photoUrl = document.getString("photo");
                                String email = document.getString("email");

                                tvUserName.setText(name != null ? name : "Không có tên");
                                tvEmail.setText(email != null ? "Email: " + email : "Email: Không có thông tin");

                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(photoUrl)
                                            .apply(new RequestOptions()
                                                    .placeholder(R.drawable.placeholder)
                                                    .error(R.drawable.error)
                                                    .transform(new CircleCrop()))
                                            .into(ivProfilePhoto);
                                } else {
                                    ivProfilePhoto.setImageResource(R.drawable.placeholder);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("ProfileFragment", "Lỗi tải dữ liệu: ", task.getException());
                            Toast.makeText(getActivity(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signOut() {
        UserSession.clear();
        requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
    }
}