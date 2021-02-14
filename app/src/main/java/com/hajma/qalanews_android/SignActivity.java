package com.hajma.qalanews_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.fragments.FragmentSignIn;
import com.hajma.qalanews_android.fragments.FragmentSignUp;

public class SignActivity extends AppCompatActivity {

    private Button btnSignUpFragment;
    private Button btnSignInFragment;
    private FrameLayout signFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        btnSignInFragment = findViewById(R.id.btnSignInFragment);
        btnSignUpFragment = findViewById(R.id.btnSignUpFragment);

        signFragmentContainer = findViewById(R.id.sign_fragment_container);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .add(R.id.sign_fragment_container, new FragmentSignUp())
                .commit();



        btnSignUpFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.sign_fragment_container, new FragmentSignUp())
                        .commit();

            }
        });

        btnSignInFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.sign_fragment_container, new FragmentSignIn())
                        .commit();
            }
        });


    }

    public void closeSignActivity(View view) {
        finish();
    }
}
