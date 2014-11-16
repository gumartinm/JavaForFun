/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.gumartinm.weather.information.test;

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
        final int buttonweather = name.gumartinm.weather.information.R.id.buttonweather;
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
