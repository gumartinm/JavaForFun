package de.android.mobiads;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MobiAdsLoginActivity extends Activity {
    private static final String TAG = "MobiAdsLoginActivity";
    private static final String SETCOOKIEFIELD = "Set-Cookie";
    private StrictMode.ThreadPolicy currentPolicy;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        setContentView(R.layout.login);

    }

    public void onClickLogin(final View v) {
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText username = (EditText) findViewById(R.id.username);
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(getResources().getString(R.string.url_login_web_service));
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        final List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);


        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, getResources().getString(R.string.encoded_web_service));
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, getResources().getString(R.string.user_agent_web_service));
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        //TODO: RESTful Web Service must use JSON instead of signin array :(
        formParams.add(new BasicNameValuePair("signin[username]", username.getText().toString()));
        formParams.add(new BasicNameValuePair("signin[password]", password.getText().toString()));
        try {
            httpEntity = new UrlEncodedFormEntity(formParams, getResources().getString(R.string.encoded_web_service));
            httpPost.setEntity(httpEntity);
            httpResponse = httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "Error while encoding POST parameters.", e);
            //finally block runs just before returning from this code and that is what we want.
            return;
        } catch (final ClientProtocolException e) {
            Log.e(TAG, "Error while executing HTTP client connection.", e);
            createErrorDialog(R.string.error_dialog_connection_error);
            //finally block runs just before returning from this code and that is what we want.
            return;
        } catch (final IOException e) {
            Log.e(TAG, "Error while executing HTTP client connection.", e);
            createErrorDialog(R.string.error_dialog_connection_error);
            //finally block runs just before returning from this code and that is what we want.
            return;
        } finally {
            //this code is used always even when returning from one of the above blocks.
            httpClient.getConnectionManager().shutdown();
        }

        if (httpResponse != null) {
            switch (httpResponse.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    String cookie = httpResponse.getLastHeader(SETCOOKIEFIELD).getValue();
                    if (cookie != null) {
                        cookie = cookie.split(";")[0];
                        //Go to the next activity
                        StrictMode.setThreadPolicy(currentPolicy);
                        Cookie.setCookie(cookie);
                        this.finish();
                    } else {
                        Log.e(TAG, "There must be a weird issue with the server because... There is not cookie!!!!");
                        createErrorDialog(R.string.error_dialog_connection_error);
                    }
                    break;
                case HttpStatus.SC_UNAUTHORIZED:
                    //Username or password is incorrect
                    createErrorDialog(R.string.error_dialog_userpwd_error);
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    //What the heck are you doing?
                    createErrorDialog(R.string.error_dialog_userpwd_error);
                    break;
                default:
                    Log.e(TAG, "Error while retrieving the HTTP status line.");
                    createErrorDialog(R.string.error_dialog_connection_error);
                    break;
            }
        }
        else {
            Log.e(TAG, "No response? This should never have happened.");
            createErrorDialog(R.string.error_dialog_connection_error);
        }
    }

    public void onClickLocalAds(final View v) {
        final Intent intent = new Intent("android.intent.action.MOBIADSLIST").
                setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.list.MobiAdsListActivity"));
        this.startActivity(intent);
    }

    private void createErrorDialog(final int title) {
        final DialogFragment newFragment = ErrorDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "errorDialog");
    }

    public void doPositiveClick() {
        StrictMode.setThreadPolicy(currentPolicy);
        finish();
    }

    public void doNegativeClick() {

    }

    public static class ErrorDialogFragment extends DialogFragment {

        public static ErrorDialogFragment newInstance(final int title) {
            final ErrorDialogFragment frag = new ErrorDialogFragment();
            final Bundle args = new Bundle();

            args.putInt("title", title);
            frag.setArguments(args);

            return frag;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(title)
            .setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int whichButton) {

                }
            }
                    )
                    .create();
        }
    }
}
