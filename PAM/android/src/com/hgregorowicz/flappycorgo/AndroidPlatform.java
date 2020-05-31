package com.hgregorowicz.flappycorgo;
import android.app.Activity;
import android.widget.Toast;

import com.hgregorowicz.flappycorgo.*;

public class AndroidPlatform implements Interfejs {

    private Activity context;
    public AndroidPlatform (Activity context) {
        this.context = context;
    }
    @Override
    public void toastHighscore() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "NEW HIGHSCORE!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
