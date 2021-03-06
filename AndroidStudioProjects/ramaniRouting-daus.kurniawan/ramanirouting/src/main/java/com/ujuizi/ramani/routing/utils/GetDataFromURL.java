package com.ujuizi.ramani.routing.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ujuizi on 4/25/16.
 */
@SuppressWarnings("deprecation")
public class GetDataFromURL {

    public GetDataFromURL(final String urlString, final HttpQueryListener httpQueryListener){

        new AsyncTask<Void, Void, String>() {

            @SuppressWarnings("deprecation")
            @Override
            protected String doInBackground(Void... params) {
//                HttpURLConnection urlConnection = null;
                String result = "error";
                try {

                    result = getDataWithoutAsyntask(urlString);

                } catch (Exception e) {
                    result = "error";
                    e.printStackTrace();
                }
                Log.i("GetFromURL", "url : " + urlString);
                Log.i("GetFromURL", "result : " + result);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                httpQueryListener.onQueryReturn(s);
            }
//        }.execute();
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        if (waitToFinish) {
//            try {
//                httpTask.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    public GetDataFromURL(){

    }

    public String getDataWithoutAsyntask(String urlString){
        String result = "error";
        try {
            Response response = getResponse(urlString);
            if (response.isSuccessful()) {
                // notice string() call
                result = response.body().string().toString();
            }

        } catch (SocketTimeoutException e) {
            result = "time out";
            e.printStackTrace();

        } catch (Exception e) {
            result = "error";
            e.printStackTrace();
        }
        Log.i("GetFromURL", "url : " + urlString);
        Log.i("GetFromURL", "result : " + result);
        return result;
    }

    public Response getResponse(String urlString) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();

        Request request = new Request.Builder()
                .url(urlString)
                .build();
        try {
           return client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface HttpQueryListener {
        void onQueryReturn(String jsonString);
    }

}
