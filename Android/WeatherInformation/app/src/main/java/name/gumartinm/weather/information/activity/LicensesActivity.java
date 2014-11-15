package name.gumartinm.weather.information.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import name.gumartinm.weather.information.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class LicensesActivity extends Activity {
    private static final String TAG = "LicensesActivity";
    private WebView mWebView;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_licenses);

        mWebView = (WebView) this.findViewById(R.id.weather_licenses);
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(this.getString(R.string.weather_licenses_title));

        final String googlePlayServices = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this.getApplicationContext());
        try {
            final StringBuilder stringBuilder = this.loadData();
            stringBuilder.append(googlePlayServices).append("</pre>").append("</body>").append("</html>");
            mWebView.loadDataWithBaseURL(null, stringBuilder.toString(), "text/html", "UTF-8", null);
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "Load data error", e);
        } catch (final IOException e) {
            Log.e(TAG, "Load data error", e);
        }
    }

    private StringBuilder loadData() throws UnsupportedEncodingException, IOException {
        final InputStream inputStream = this.getResources().openRawResource(R.raw.licenses);
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            try {
                final BufferedReader reader = new BufferedReader(inputStreamReader);
                try {
                    final StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                    return stringBuilder;
                } finally {
                    reader.close();
                }
            } finally {
                inputStreamReader.close();
            }
        } finally {
            inputStream.close();
        }
    }
}
