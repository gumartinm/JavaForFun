package com.weather.information.test;

import android.content.Intent;
import android.widget.Button;

public class WeatherInformationActivityUnitTest extends
        ActivityUnitTestCase<WeatherInformationActivity> {

    private WeatherInformationActivity activity;

    public WeatherInformationActivityUnitTest() {
        super(WeatherInformationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Intent intent = new Intent(this.getInstrumentation().getTargetContext(),
                WeatherInformationActivity.class);
        this.startActivity(intent, null, null);
        this.activity = this.getActivity();
    }

    public void testIntentTriggerViaOnClick() {
        final int buttonweather = com.weather.information.R.id.buttonweather;
        final Button view = (Button) this.activity.findViewById(buttonweather);
        assertNotNull("Button Weather not allowed to be null", view);

        view.performClick();

        // TouchUtils cannot be used, only allowed in
        // InstrumentationTestCase or ActivityInstrumentationTestCase2

        // Check the intent which was started
        final Intent triggeredIntent = this.getStartedActivityIntent();
        assertNotNull("Intent was null", triggeredIntent);
        final String data = triggeredIntent.getDataString();

        assertEquals("Incorrect data passed via the intent",
                "http://gumartinm.name", data);
    }
}
