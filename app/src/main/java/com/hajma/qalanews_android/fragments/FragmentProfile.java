package com.hajma.qalanews_android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.qalanews_android.HomeActivity;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.SignActivity;
import com.hajma.qalanews_android.data.NewsProvider;
import com.hajma.qalanews_android.eventbus.DataEvent;
import com.hajma.qalanews_android.retrofit.login.User;

import org.greenrobot.eventbus.Subscribe;

public class FragmentProfile extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isLogin;
    private TextView txtProfileNameSurname;
    private TextView txtProfileTag;
    private TextView txtReadCount;
    private TextView txtSavedCount;
    private TextView txtSharedCount;
    private RelativeLayout relativeLayoutProfileRegister;
    private RelativeLayout relativeLayoutUserInfo;
    private RelativeLayout relativeLayoutCounts;
    private Button btnRegisterToProfileFragment;

    private ImageButton imageButtonExitProfile;
    private User user;
    private Uri CONTENT_URI = NewsProvider.CONTENT_URI_USERS;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        relativeLayoutUserInfo = v.findViewById(R.id.relativeLayoutUserInfo);
        relativeLayoutCounts = v.findViewById(R.id.relativeLayoutCounts);
        relativeLayoutProfileRegister = v.findViewById(R.id.relativeLayoutProfileRegister);
        btnRegisterToProfileFragment = v.findViewById(R.id.btnRegisterToProfileFragment);
        txtProfileNameSurname = v.findViewById(R.id.txtProfileNameSurname);
        txtProfileTag = v.findViewById(R.id.txtProfileTag);
        txtReadCount = v.findViewById(R.id.txtReadCount);
        txtSavedCount = v.findViewById(R.id.txtSavedCount);
        txtSharedCount = v.findViewById(R.id.txtSharedCount);




        imageButtonExitProfile = v.findViewById(R.id.imageButtonExitProfile);
        imageButtonExitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("username", "");
                editor.putBoolean("logined", false);
                editor.commit();
                restartHome();
            }
        });
        //Shared Preferences options
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        isLogin = sharedPreferences.getBoolean("logined", false);
        if(isLogin) {
            setUserInfo();
            txtProfileNameSurname.setText(user.getName());
            txtProfileTag.setText("@"+user.getUsername());
            txtReadCount.setText(""+user.getRead());
            txtSavedCount.setText(""+user.getSaved());
            txtSharedCount.setText(""+user.getShared());
            relativeLayoutProfileRegister.setVisibility(View.GONE);
        }else {
            relativeLayoutUserInfo.setVisibility(View.INVISIBLE);
            relativeLayoutCounts.setVisibility(View.INVISIBLE);
            imageButtonExitProfile.setVisibility(View.GONE);
            relativeLayoutProfileRegister.setVisibility(View.VISIBLE);
            btnRegisterToProfileFragment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SignActivity.class));
                }
            });
        }

        return v;
    }


    private void restartHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void setUserInfo() {

        String username = sharedPreferences.getString("username","");
        user = new User();
        Cursor cursor;
        String projection[] = {"name", "email", "username", "read", "saved", "shared"};
        String selection = "username = ?";
        String selectionArgs[] = {username};
        cursor = getActivity().getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, null);

        if(cursor != null && cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setRead(cursor.getInt(cursor.getColumnIndex("read")));
                user.setSaved(cursor.getInt(cursor.getColumnIndex("saved")));
                user.setShared(cursor.getInt(cursor.getColumnIndex("shared")));
            }
        }

    }

}
