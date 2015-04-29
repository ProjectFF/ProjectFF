package projectff.mycardboardview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.vrtoolkit.cardboard.CardboardActivity;

import org.rajawali3d.cardboard.RajawaliCardboardRenderer;
import org.rajawali3d.cardboard.RajawaliCardboardView;


public class MainActivity extends CardboardActivity implements View.OnKeyListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String image = intent.getStringExtra("image");

        Log.d("STRING", image);

        String spheremapPath = image; //savedInstanceState.getString("image");

        RajawaliCardboardView view = new RajawaliCardboardView(this);
        setContentView(view);
        setCardboardView(view);

        RajawaliCardboardRenderer renderer = new MyCardboardRenderer(this, spheremapPath);
        view.setRenderer(renderer);
        view.setSurfaceRenderer(renderer);

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK ){

                Intent intent = new Intent(this, ImageViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
