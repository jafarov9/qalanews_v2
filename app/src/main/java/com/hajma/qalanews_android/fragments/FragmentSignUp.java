package com.hajma.qalanews_android.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class FragmentSignUp extends Fragment {

    private EditText etUsernameSignUp;
    private EditText etNameSurnameSignUp;
    private EditText etEmailSignUp;
    private EditText etPasswordFirstSignUp;
    private EditText etPasswordSecondSignUp;
    private Button buttonSign;
    private UserDAOInterface userDIF;
    private Uri CONTENT_URI = NewsProvider.CONTENT_URI_USERS;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String token;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        userDIF = ApiUtils.getUserDAOInterface();

        //Shared Preferences options
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etUsernameSignUp = view.findViewById(R.id.etUsernameSignUp);
        etNameSurnameSignUp = view.findViewById(R.id.etNameSurnameSignUp);
        etEmailSignUp = view.findViewById(R.id.etEmailSignUp);
        etPasswordFirstSignUp = view.findViewById(R.id.etPasswordSignUp);
        etPasswordSecondSignUp = view.findViewById(R.id.etRePasswordSignUp);
        buttonSign = view.findViewById(R.id.btnSignUp);

        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextControl()) {
                    postRegister();
                }
            }
        });
        return view;
    }

    private void postRegister() {

        String username = etUsernameSignUp.getText().toString().trim();
        String password = etPasswordFirstSignUp.getText().toString().trim();
        String c_password = etPasswordSecondSignUp.getText().toString();
        String email = etEmailSignUp.getText().toString().trim();
        String name = etNameSurnameSignUp.getText().toString().trim();


        userDIF.postRegister(username, password, c_password, name, email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        User user = new User();
                        user.setEmail(jsonObject.getJSONObject("success").getString("email"));
                        user.setName(jsonObject.getJSONObject("success").getString("name"));
                        user.setUsername(jsonObject.getJSONObject("success").getString("username"));
                        user.setVerified(jsonObject.getJSONObject("success").getBoolean("verified"));
                        token = jsonObject.getJSONObject("success").getString("token");

                        if(token != null) {
                            editor.putString("token", token);
                            editor.putBoolean("logined", true);
                            editor.commit();
                            insertUserToDatabase(user);
                            Toast.makeText(getActivity(), getResources().getString(R.string.login_success), Toast.LENGTH_LONG).show();

                        }

                        restartHome();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.username_or_email_are_exists), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            }
        });


    }

    private boolean editTextControl() {
        if(etEmailSignUp.getText().toString().trim().isEmpty()) {
            etEmailSignUp.setError("This field is required");
            return false;
        }

        if(etPasswordFirstSignUp.getText().toString().trim().isEmpty()) {
            etPasswordFirstSignUp.setError("This field is required");
            return false;
        }

        if(etPasswordSecondSignUp.getText().toString().trim().isEmpty()) {
            etPasswordSecondSignUp.setError("This field is required");
            return false;
        }

        if(etUsernameSignUp.getText().toString().trim().isEmpty()) {
            etUsernameSignUp.setError("This field is required");
            return false;
        }

        if(etNameSurnameSignUp.getText().toString().trim().isEmpty()) {
            etPasswordFirstSignUp.setError("This field is required");
            return false;
        }

        String s1 = etPasswordFirstSignUp.getText().toString();
        String s2 = etPasswordSecondSignUp.getText().toString();

        if(!s1.equals(s2)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.check_second_password), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    private void restartHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }


}
