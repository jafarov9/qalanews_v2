package com.hajma.qalanews_android.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserDAOInterface {

    @POST("/api/login")
    @FormUrlEncoded
    Call<ResponseBody> postLogin(@Field("email") String email, @Field("password") String password);

    @POST("/api/register")
    @FormUrlEncoded
    Call<ResponseBody> postRegister(@Field("username") String username,
                                    @Field("password") String password,
                                    @Field("c_password") String c_password,
                                    @Field("name") String name,
                                    @Field("email") String email);


    @Headers({"Accept: application/json"})
    @POST("/api/save-news?newsId=1")
    Call<ResponseBody> savenews(@Query("newsId") int id, @Header("Authorization") String token);

    @Headers({"Accept: application/json"})
    @POST("/api/saved-news?page=1")
    Call<ResponseBody> savedNews(@Query("page") int page, @Header("Authorization") String token);

}
