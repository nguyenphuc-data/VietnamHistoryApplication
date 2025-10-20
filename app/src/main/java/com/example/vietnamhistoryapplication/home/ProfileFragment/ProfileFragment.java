package com.example.vietnamhistoryapplication.home.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.home.HomeActivity;
import com.example.vietnamhistoryapplication.profile.ProfileOverviewFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    // Launcher để thay thế startActivityForResult
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(requireContext());
        mAuth = FirebaseAuth.getInstance();
        // ---- Google ----
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // từ Firebase console
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

        // ---- Facebook ----
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override public void onCancel() {}
            @Override public void onError(@NonNull FacebookException error) {
                Toast.makeText(requireContext(), "Lỗi Facebook: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);


        view.findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });


        view.findViewById(R.id.btnFaceBook).setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(
                    this, Arrays.asList("email", "public_profile")
            );
        });

        return view;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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
                        Toast.makeText(requireContext(), "Đăng nhập Facebook thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkAndCreateUser(FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // 🔹 Người dùng mới → tạo mới document
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", firebaseUser.getUid());
                        userData.put("name", firebaseUser.getDisplayName());
                        userData.put("email", firebaseUser.getEmail());
                        userData.put("photo", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "");
                        userData.put("bio", "");
                        userData.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(firebaseUser.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Tạo tài khoản mới thành công", Toast.LENGTH_SHORT).show();
                                    moveToProfileOverview();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Lỗi tạo tài khoản mới: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        // 🔹 Người dùng đã tồn tại → chuyển luôn
                        moveToProfileOverview();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi truy cập Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private void moveToProfileOverview() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProfileOverviewFragment())
                .addToBackStack(null)
                .commit();
    }
}
