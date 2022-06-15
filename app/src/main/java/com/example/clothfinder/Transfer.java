package com.example.clothfinder;

import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;

import java.io.*;
import java.util.ArrayList;

public class Transfer {
    private String result = null;

    public String getResult() {
        return result;
    }

    //1.이미지 송신
    public void sendImageToServer(Intent intent, ContentResolver Resolver){
        Bitmap bitmap = CreateBitmap(intent, Resolver); //비트맵 만들기
        ArrayList<Integer> imageByte = bitmapToByteArray(bitmap); // BitMap to ByteArray(0~255)

        // URL 설정.
        String url = "SERVER_URL";

        //params 설정
        ContentValues values = new ContentValues();
        values.put("binImage", imageByte.toString());

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }

    //1.1 비트맵 만들기
    public Bitmap CreateBitmap(Intent intent, ContentResolver Resolver){
        Bitmap bitmap = null;
        Uri uri = intent.getData();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(Resolver, uri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(Resolver, uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //1.2 비트맵을 (int)바이트배열로 변경
    public ArrayList<Integer> bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        ArrayList<Integer> imageList = new ArrayList<Integer>();
        for (byte i : byteArray){
            imageList.add(i & 0xff);
        }
        return imageList;
    }

    public void sendStringToServer(String selectedClass) {
        String url = "SERVER_URL";

        //params 설정
        ContentValues values = new ContentValues();
        values.put("selectedClass", selectedClass);

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }

    //0.http 통신을 위한 클래스
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }// end NetworkTask
}
