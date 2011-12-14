package de.android.test1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class NextActivity extends Activity {
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        String myCookie = CookieManager.getInstance().getCookie("192.168.1.34/userfront.php");
        setContentView(R.layout.main2);
    }
}
