package com.krishna.petshop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    BottomNavigationViewHelper bottomNavigationViewHelper;
    Fragment fragment;
    Intent intent;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = new Preferences(this);


        if (preferences.getUserType().equals("Seller")) {
            loadFragment(new AddPet());
        }else {
            loadFragment(new PetList());
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        fragment = new PetList();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_library:
                        fragment = new AddPet();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_mypet:
                        fragment = new MyPet();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_profile:
                        fragment = new Profile();
                        loadFragment(fragment);
                        return true;
                }

                return false;
            }
        });
    }

    void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //  transaction.addToBackStack(null);
        transaction.commit();
    }
}
