package com.example.vietnamhistoryapplication.home.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.models.UserModel;
import com.example.vietnamhistoryapplication.profile.ProfileOverviewFragment;
import com.example.vietnamhistoryapplication.utils.UserSession;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private TextView tvRegister;

    public static UserModel currentUserModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogle = view.findViewById(R.id.btnGoogle);
        tvRegister = view.findViewById(R.id.tvRegister);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Toast.makeText(requireContext(), "Google Sign-in thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnLogin.setOnClickListener(v -> {
            String username = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                loginWithUsername(username, password);
            }
        });

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        tvRegister.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loginWithUsername(String username, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(requireContext(), "Không tìm thấy tài khoản này", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    String storedPassword = document.getString("password");

                    if (storedPassword != null && storedPassword.equals(password)) {
                        Toast.makeText(requireContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        String uid = document.getString("uid");
                        String name = document.getString("name");
                        String usern = document.getString("username");
                        String email = document.getString("email");
                        String photo = document.getString("photo");
                        String bio = document.getString("bio");
                        Long createdAtLong = document.getLong("createdAt");
                        long createdAt = createdAtLong != null ? createdAtLong : System.currentTimeMillis();

                        UserModel user = new UserModel(
                                uid,
                                name,
                                usern,
                                email,
                                photo,
                                bio,
                                createdAt
                        );
                        saveUserToModel(user);

                        moveToProfileOverview();
                    } else {
                        Toast.makeText(requireContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAndCreateUser(user);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndCreateUser(FirebaseUser firebaseUser) {
        db.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", firebaseUser.getUid());
                        userData.put("name", firebaseUser.getDisplayName());
                        userData.put("username", null);
                        userData.put("email", firebaseUser.getEmail());
                        userData.put("photo", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);
                        userData.put("bio", "");
                        userData.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(firebaseUser.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Tạo tài khoản mới thành công", Toast.LENGTH_SHORT).show();

                                    UserModel user = new UserModel(
                                            firebaseUser.getUid(),
                                            firebaseUser.getDisplayName(),
                                            null,
                                            firebaseUser.getEmail(),
                                            firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                            "",
                                            (long) userData.get("createdAt")
                                    );
                                    saveUserToModel(user);

                                    moveToProfileOverview();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Lỗi tạo tài khoản mới: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        String uid = documentSnapshot.getString("uid");
                        String name = documentSnapshot.getString("name");
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        String photo = documentSnapshot.getString("photo");
                        String bio = documentSnapshot.getString("bio");
                        Long createdAtLong = documentSnapshot.getLong("createdAt");
                        long createdAt = createdAtLong != null ? createdAtLong : System.currentTimeMillis();

                        UserModel user = new UserModel(
                                uid,
                                name,
                                username,
                                email,
                                photo,
                                bio,
                                createdAt
                        );
                        saveUserToModel(user);
                        moveToProfileOverview();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi truy cập Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveUserToModel(UserModel user) {
        currentUserModel = user;
        UserSession.setCurrentUser(user);
    }

    private void moveToProfileOverview() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ProfileOverviewFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
