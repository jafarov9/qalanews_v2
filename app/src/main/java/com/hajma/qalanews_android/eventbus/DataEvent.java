package com.hajma.qalanews_android.eventbus;

public class DataEvent {

    public static final int FRAGMENT_PROFILE_ID = 1;
    public static final int FRAGMENT_SAVED_ID = 2;

     public static class SearchQuery {
        private String searchquery;

        public SearchQuery(String searchquery) {
            this.searchquery = searchquery;
        }

        public String getSearchquery() {
            return searchquery;
        }

        public void setSearchquery(String searchquery) {
            this.searchquery = searchquery;
        }
    }

    public static class LanguageID {
         private int languageID;

        public LanguageID(int languageID) {
            this.languageID = languageID;
        }

        public int getLanguageID() {
            return languageID;
        }

        public void setLanguageID(int languageID) {
            this.languageID = languageID;
        }
    }

    public static class CallSavedProfileFragmentUpdate {
         private int response;
         private int fragmentID;


        public CallSavedProfileFragmentUpdate(int response, int fragmentID) {
            this.response = response;
            this.fragmentID = fragmentID;
        }

        public int getFragmentID() {
            return fragmentID;
        }

        public void setFragmentID(int fragmentID) {
            this.fragmentID = fragmentID;
        }

        public int getResponse() {
            return response;
        }

        public void setResponse(int response) {
            this.response = response;
        }
    }

    public static class LoginInfo {
         private boolean logined;

        public LoginInfo(boolean logined) {
            this.logined = logined;
        }

        public boolean isLogined() {
            return logined;
        }

        public void setLogined(boolean logined) {
            this.logined = logined;
        }
    }
}
