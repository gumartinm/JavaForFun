package de.android.test1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Test1Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onClickOk(View v) {
        
    }
}