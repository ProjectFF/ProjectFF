package projectff.mycardboardview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;

import org.rajawali3d.cardboard.RajawaliCardboardView;
import org.rajawali3d.surface.RajawaliSurfaceView;


import com.example.inputmanagercompat.InputManagerCompat;
import com.example.inputmanagercompat.InputManagerCompat.InputDeviceListener;
import com.google.vrtoolkit.cardboard.CardboardActivity;

public class RajawaliVRExampleActivity extends CardboardActivity implements InputDeviceListener {
 
	RajawaliVRExampleRenderer renderer;
    public RajawaliSurfaceView rajawaliSurface;
	private InputManagerCompat mInputManager;
	private InputDevice dev;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputManager = InputManagerCompat.Factory.getInputManager(this);
		mInputManager.registerInputDeviceListener(this, null);
        renderer = new RajawaliVRExampleRenderer(this);

        RajawaliCardboardView view = new RajawaliCardboardView(this);

        setContentView(view);
        setCardboardView(view);

        view.setRenderer(renderer);
        view.setSurfaceRenderer(renderer);
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		
		renderer.onKeyDown(keyCode, event);
		return true;
	};
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		renderer.onKeyUp(keyCode, event);
		return true;
	};
	
	@Override
	public void onInputDeviceAdded(int deviceId) {
		// TODO Auto-generated method stub
		dev = InputDevice.getDevice(deviceId);
		Log.d("deviceName", dev.getName());
	}

	@Override
	public void onInputDeviceChanged(int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInputDeviceRemoved(int deviceId) {
		// TODO Auto-generated method stub
		
	}
}