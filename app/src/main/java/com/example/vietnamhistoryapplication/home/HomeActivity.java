package com.example.vietnamhistoryapplication.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.home.ExploreFragment.ExploreFragment;
import com.example.vietnamhistoryapplication.home.PersonFragment.PersonPeriodFragment;
import com.example.vietnamhistoryapplication.models.UserModel;
import com.example.vietnamhistoryapplication.profile.ProfileOverviewFragment;
import com.example.vietnamhistoryapplication.utils.UserSession;
import com.example.vietnamhistoryapplication.utils.AutoBrightnessManager;  // ← ĐÃ THÊM

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.vietnamhistoryapplication.home.PeriodFragment.PeriodFragment;
import com.example.vietnamhistoryapplication.home.GameFragment.GameFragment;
import com.example.vietnamhistoryapplication.home.ExploreFragment.ExploreFragment;
import com.example.vietnamhistoryapplication.home.ProfileFragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private AutoBrightnessManager autoBrightnessManager;   // ← ĐÃ THÊM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_activity);
//        //        ------------------------------
//        FirebaseAuth.getInstance().signOut(); // đăng xuất Firebase
//
////       xóa token Google Sign IN
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        googleSignInClient.signOut();
//
////------------------------------------
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item ->{
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if(itemId ==R.id.nav_period){
                selectedFragment = new PeriodFragment();
            }else if(itemId == R.id.nav_character){
                selectedFragment = new PersonPeriodFragment();
            }else if(itemId == R.id.nav_game){
                selectedFragment = new GameFragment();
            }else if(itemId == R.id.nav_explore){
                selectedFragment = new ExploreFragment();
            }else if(itemId ==R.id.nav_profile){
                UserModel user = UserSession.getCurrentUser();
                if (user != null) {
                    selectedFragment = new ProfileOverviewFragment();
                } else {
                    selectedFragment = new ProfileFragment();
                }
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
            }
            return true;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PeriodFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_period);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoBrightnessManager.start();   // Bật tự động sáng/tối
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoBrightnessManager.stop();    // Tắt để tiết kiệm pin
    }
}