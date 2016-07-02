package com.example.weatherforecast.util;

import org.apache.http.HttpRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by misaki on 2016/7/1.
 */
public class HttpUtil {
    public interface  HttpCallbackListener{
     void    OnFinish(String response);
        void OnError(Exception e);

    }
        public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
            new Thread(new Runnable(){

                @Override
                public void run() {
                    HttpURLConnection httpURLConnection=null;
                    try {
                        URL url=new URL(address);

                            httpURLConnection= (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setConnectTimeout(8000);
                        httpURLConnection.setReadTimeout(8000);
                        InputStream inputStream=httpURLConnection.getInputStream();
                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder=new StringBuilder();
                        String line;
                        while((line=bufferedReader.readLine())!=null){
                            stringBuilder.append(line);
                        }
                        if(listener!=null){
                            listener.OnFinish(stringBuilder.toString());
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                        if(listener!=null){
                            listener.OnError(e);
                        }
                    }
                    finally {
                        if(httpURLConnection!=null){
                            httpURLConnection.disconnect();
                        }
                    }
                }
            }).start();

        }



}
