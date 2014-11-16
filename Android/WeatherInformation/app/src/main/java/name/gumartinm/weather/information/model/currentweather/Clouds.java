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

public class Clouds implements Serializable {
    private static final long serialVersionUID = 3034435739326030899L;
    private Number all;

    public Number getAll(){
        return this.all;
    }
    public void setAll(final Number all){
        this.all = all;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Clouds [all=").append(this.all).append("]");
        return builder.toString();
    }
}
