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
package name.gumartinm.weather.information.fragment.overview;

import android.graphics.Bitmap;

public class OverviewEntry {
    private final String dateName;
    private final String dateNumber;
    private final String maxTemp;
    private final String minTemp;
    private final Bitmap picture;

    public OverviewEntry(final String dateName, final String dateNumber,
            final String maxTemp, final String minTemp,
            final Bitmap picture) {
        this.dateName = dateName;
        this.dateNumber = dateNumber;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.picture = picture;
    }

    public String getDateName() {
        return this.dateName;
    }

    public String getDateNumber() {
        return this.dateNumber;
    }

    public String getMaxTemp() {
        return this.maxTemp;
    }

    public String getMinTemp() {
        return this.minTemp;
    }

    public Bitmap getPicture() {
        return this.picture;
    }
}
