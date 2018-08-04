package com.aldarjose.shilo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SavePhotoTask extends AsyncTask<byte[], String, String> {

    private Context mContext;
    public SavePhotoTask (Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(byte[]... jpeg) {

        File photo=new File(Environment.getExternalStorageDirectory(), "photo.jpg");

        if (photo.exists()) {
            photo.delete();
            Log.d("Shilo", "del", new Throwable(photo.getPath()));
        }

        try {
            Context context;
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(jpeg[0]);
            fos.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            mContext.sendBroadcast(mediaScanIntent);

            Log.d("Shilo", "saved");
        }
        catch (java.io.IOException e) {
            Log.e("Shilo", "Exception in photoCallback", e);
        }

        return(null);
    }
}