package com.kevinmarc;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class FlashlightActivity extends Activity {

/******************************************************************************************************************************************
 * Class Variables START
 ******************************************************************************************************************************************/

    private boolean previousBrightnessModeisAutomatic;
    private float previousBrightness;
    private boolean deviceHasFlash;
    private Camera camera;
    private ScreenBroadcastReceiver screenBroadcastReciever;

/******************************************************************************************************************************************
 * Class Variables END & Life Cycle Methods START
 ******************************************************************************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        screenBroadcastReciever = new ScreenBroadcastReceiver(this);
        registerReceiver(screenBroadcastReciever, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        
        deviceHasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            previousBrightnessModeisAutomatic = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE) 
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

        } catch (Settings.SettingNotFoundException e) { 
            previousBrightnessModeisAutomatic = false;    
        }

        
        Log.d("abc", "" + getApplicationContext());
        
        if (deviceHasFlash) {

            /* Set flag to keep screen on. */
            this.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            /* Device has camera flash, implement flashlight via camera flash. */
            setScreenBrightness(0.0F);

            /* Turn on Flash. */
            camera = Camera.open();
            Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();

        } else {

            /* Set flag to enable full screen and keep screen on. */
            this.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            /*
             * Device does not have camera flash, implement flashlight via all
             * white screen.
             */
            setScreenBrightness(1.0F);
            findViewById(android.R.id.content).setBackgroundColor(0xFFFFFFFF);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("tag", "INSIDE_ON_STOP");


        /* Turn off flash if applicable. */
        if (deviceHasFlash) {
            camera.stopPreview();
            camera.release();
        }

        /*
         * Return brightness mode to previous state, and reset screen brightness
         * to previous intensity.
         */

        if (previousBrightnessModeisAutomatic) {
            Settings.System.putInt(getContentResolver(),
                                   Settings.System.SCREEN_BRIGHTNESS_MODE,
                                   Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

        } else {
            WindowManager.LayoutParams layoutParams = this.getWindow()
                    .getAttributes();
            layoutParams.screenBrightness = previousBrightness;
            getWindow().setAttributes(layoutParams);
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d("tag", "INSIDE_ON_DESTORY");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("tag", "INSIDE_ON_PAUSE");
    }

    
    
/******************************************************************************************************************************************
 * Life Cycle Methods END & Public Methods START
 ******************************************************************************************************************************************/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        Log.d("tag", "INSIDE_ON_KEY_DOWN");
        
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.i("Home Button", "Clicked");
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("Back Button", "Clicked");
            finish();
        }
        return false;
    }

/******************************************************************************************************************************************
 * Public Methods END & Private Methods START
 ******************************************************************************************************************************************/

    private void setScreenBrightness(float intensity) {


        if (previousBrightnessModeisAutomatic) {
            Settings.System.putInt(getContentResolver(),
                                   Settings.System.SCREEN_BRIGHTNESS_MODE,
                                   Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        /* Store current screen brightness and dim screen. */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        previousBrightness = layoutParams.screenBrightness; // stores screen
                                                            // brightness before
                                                            // dim
        layoutParams.screenBrightness = intensity;
        getWindow().setAttributes(layoutParams);

    }

/******************************************************************************************************************************************
 * Private Methods END
 ******************************************************************************************************************************************/

}