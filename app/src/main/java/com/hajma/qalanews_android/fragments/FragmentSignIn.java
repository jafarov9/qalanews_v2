package com.hajma.qalanews_android.fragments;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.qalanews_android.HomeActivity;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.data.NewsProvider;
import com.hajma.qalanews_android.retrofit.ApiUtils;
import com.hajma.qalanews_android.retrofit.UserDAOInterface;
import com.hajma.qalanews_android.retrofit.login.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSignIn extends Fragment {

    private EditText etEmailLogin;
    private EditText etPasswordLogin;
    private Button btnLogin;
    private UserDAOInterface userDIF;
    private   Uri CONTENT_URI = NewsProvider.CONTENT_URI_USERS;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String token;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_in, container, false);

        //Shared Preferences options
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userDIF = ApiUtils.getUserDAOInterface();

        etEmailLogin = view.findViewById(R.id.etEmailLogin);
        etPasswordLogin = view.findViewById(R.id.etPasswordLogin);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextControl()) {
                    postLogin();
                }
            }
        });


        return view;
    }

    private void postLogin() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        userDIF.postLogin(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        User user = new User();
                        user.setEmail(jsonObject.getJSONObject("success").getJSONObject("user").getString("email"));
                        user.setName(jsonObject.getJSONObject("success").getJSONObject("user").getString("name"));
                        user.setUsername(jsonObject.getJSONObject("success").getJSONObject("user").getString("username"));
                        user.setVerified(jsonObject.getJSONObject("success").getJSONObject("user").getBoolean("verified"));
                        token = jsonObject.getJSONObject("success").getString("token");

                        if(token != null) {
                            editor.putString("token", token);
                            editor.putBoolean("logined", true);
                            editor.commit();
                            if (userIsExistsInDatabase(user.getUsername())) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.login_success), Toast.LENGTH_LONG).show();
                            }else {
                                insertUserToDatabase(user);
                                Toast.makeText(getActivity(), getResources().getString(R.string.login_success), Toast.LENGTH_LONG).show();
                            }
                        }

                        restartHome();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.you_are_not_registered), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            }
        });


    }

    private boolean editTextControl() {
        if(etEmailLogin.getText().toString().trim().isEmpty()) {
            etEmailLogin.setError("This field is required");
            return false;
        }

        if(etPasswordLogin.getText().toString().trim().isEmpty()) {
            etPasswordLogin.setError("This field is required");
            return false;
        }

        return true;
    }

    private void restartHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    //insert user sqlite database
    public void insertUserToDatabase(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());

        Uri uri = getActivity().getContentResolver().insert(CONTENT_URI, values);
        editor.putString("username", user.getUsername());
        editor.commit();
    }

    public boolean  userIsExistsInDatabase(String username) {

        Cursor cursor;

        String projection[] = {"username"};
        String selection = "username = ?";
        String selectionArgs[] = {username};
        cursor = getActivity().getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, null);

        if(cursor != null && cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                editor.putString("username", cursor.getString(cursor.getColumnIndex("username")));
                editor.commit();
                return true;
            }
        }
        return false;
    }
}
