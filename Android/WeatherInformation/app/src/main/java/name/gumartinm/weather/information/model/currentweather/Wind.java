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
package name.gumartinm.weather.information.model.currentweather;

import java.io.Serializable;

public class Wind implements Serializable {
    private static final long serialVersionUID = -6895302757116888675L;
    private Number deg;
    private Number speed;

    public Number getDeg(){
        return this.deg;
    }
    public void setDeg(final Number deg){
        this.deg = deg;
    }
    public Number getSpeed(){
        return this.speed;
    }
    public void setSpeed(final Number speed){
        this.speed = speed;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Wind [deg=").append(this.deg).append(", speed=").append(this.speed)
        .append("]");
        return builder.toString();
    }
}
