package com.hajma.qalanews_android.retrofit;

import com.hajma.qalanews_android.Constants;

public class ApiUtils {

    public static UserDAOInterface getUserDAOInterface() {
        return RetrofitClient.getClient(Constants.BASE_URL).create(UserDAOInterface.class);
    }


}
