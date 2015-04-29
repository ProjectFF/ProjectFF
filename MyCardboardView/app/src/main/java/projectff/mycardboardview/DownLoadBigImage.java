package projectff.mycardboardview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by meyert on 27.04.2015.
 */
public class DownLoadBigImage extends AsyncTask<String, Integer, String> {
    private final String URL;
    ImageViewActivity myActivity;
    int count = 0;


    public DownLoadBigImage(String url, ImageViewActivity myactivity) {
        URL = url;
        myActivity = myactivity;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("PARAMS", params[0]);
        return downloadBitmap(URL);
    }

    @Override
    protected void onPreExecute() {
        myActivity.listView.setActivated(false);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        myActivity.pb.setProgress(progress[0]);
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String path) {
        if (isCancelled()) {
        }

        myActivity.listView.setActivated(true);
        myActivity.startViewer(path);
    }

    private String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String fname = "temp.jpg";
        File file = new File (myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap = Bitmap.createScaledBitmap(finalBitmap, 4096, 2048, false);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file.exists()){
            return file.getPath();
        }
        return null;
    }

    private String downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                String path = SaveImage(bitmap);
                return path;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

