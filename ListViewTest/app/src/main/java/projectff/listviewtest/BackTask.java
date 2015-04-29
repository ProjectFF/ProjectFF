package projectff.listviewtest;

import android.os.AsyncTask;

/**
 * Created by meyert on 28.04.2015.
 */
public class BackTask extends AsyncTask<String, Integer, String[]> {

    public BackTask() {
        super();
    }

    @Override
    protected String[] doInBackground(String... string) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String[] s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
