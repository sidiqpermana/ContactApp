package com.nbs.ppm.contactapp.util;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class AppUrl {
    public static String BASE_URL = "http://sidiqpermana.com/codelab/index.php?k=test&c=";
    public enum ApiAction{
        CREATE, UPDATE, DELETE, VIEW
    }

    public static String getUrl(ApiAction action){
        String url = null;
        switch (action){
            case VIEW:
                url = BASE_URL + "1";
                break;

            case CREATE:
                url = BASE_URL + "2";
                break;

            case UPDATE:
                url = BASE_URL + "3";
                break;

            case DELETE:
                url = BASE_URL + "4";
                break;


        }
        return url;
    }
}
