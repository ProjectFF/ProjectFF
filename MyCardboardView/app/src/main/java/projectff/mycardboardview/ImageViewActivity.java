package projectff.mycardboardview;

import android.app.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class ImageViewActivity extends Activity implements View.OnKeyListener {

    String[] images = new String[10];
    ProgressBar pb;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);

        images[0] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_153555.jpg";
        images[1] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_152709.jpg";
        images[2] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_151147.jpg";
        images[3] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_142425.jpg";
        images[4] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_141832.jpg";
        images[5] = "http://mu5ic.de/spheremaps/tmb/PANO_20150426_132905.jpg";
        images[6] = "http://mu5ic.de/spheremaps/tmb/PANO_20150425_112210.jpg";
        images[7] = "http://mu5ic.de/spheremaps/tmb/PANO_20150425_104145.jpg";
        images[8] = "http://mu5ic.de/spheremaps/tmb/PANO_20150424_213854.jpg";
        images[9] = "http://mu5ic.de/spheremaps/tmb/PANO_20150424_122951.jpg";

        ArrayList<ListItem> listData = getListData();

        listView = (ListView) findViewById(R.id.custom_list);
        listView.setAdapter(new CustomListAdapter(this, listData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                ListItem newsData = (ListItem) listView.getItemAtPosition(position);
                Log.d("NewsData", newsData.getHeadline());

                String url = newsData.getUrl().replace("tmb", "big");

                new DownLoadBigImage(url , ImageViewActivity.this).execute(url);



            }
        });
    }



    public void startViewer(String Path)
    {
        Intent intent = new Intent(ImageViewActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("image", Path);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        this.finish();
        return true;
    }

    private ArrayList<ListItem> getListData() {

        ArrayList<ListItem> listMockData = new ArrayList<ListItem>();


        for (int i = 0; i < images.length; i++) {
            ListItem newsData = new ListItem();
            newsData.setUrl(images[i]);
            newsData.setHeadline(images[i]);
            newsData.setReporterName("Thomas");
            newsData.setDate("Date");
            listMockData.add(newsData);
        }
       return listMockData;

    }


}

