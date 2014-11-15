package com.weather.information.service;

import com.weather.information.R;

import java.util.HashMap;
import java.util.Map;

public enum IconsList {
    ICON_01d("01d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_clear;
        }
    },
    // TODO: I am sometimes receiving this code, there is no documentation about it on the
    // openweathermap site.... But it exists!!! Some day, try to find out more information about it.
    // see: http://openweathermap.org/img/w/01dd.png
    ICON_01dd("01dd") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_clear;
        }
    },
    ICON_01n("01n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_clear_night;
        }
    },
    ICON_02d("02d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_few_clouds;
        }
    },
    ICON_02n("02n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_few_clouds_night;
        }
    },
    ICON_03d("03d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_few_clouds;
        }
    },
    ICON_03n("03n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_few_clouds;
        }
    },
    ICON_04d("04d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_overcast;
        }
    },
    ICON_04n("04n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_overcast;
        }
    },
    ICON_09d("09d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_showers;
        }
    },
    ICON_09n("09n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_showers;
        }
    },
    ICON_10d("10d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_showers_scattered;
        }
    },
    ICON_10n("10n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_showers_scattered;
        }
    },
    ICON_11d("11d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_storm;
        }
    },
    ICON_11n("11n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_storm;
        }
    },
    ICON_13d("13d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_snow;
        }
    },
    ICON_13n("13n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_snow;
        }
    },
    ICON_50d("50d") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_fog;
        }
    },
    ICON_50n("50n") {
        @Override
        public int getResourceDrawable() {
            return R.drawable.weather_fog;
        }
    };

    private final String icon;
    // Map with every enum constant. Class variable initializer. JLS§12.4.2
    // Executed in textual order.
    private static final Map<String, IconsList> codeMap = new HashMap<String, IconsList>();

    // Static initializer. JLS§12.4.2 Executed in textual order.
    static {
        for (final IconsList code : IconsList.values()) {
            codeMap.put(code.getIcon(), code);
        }
    }

    private IconsList(final String icon) {
        this.icon = icon;
    }

    public static final IconsList getIcon(final String icon) {
        return codeMap.get(icon);
    }

    private String getIcon() {
        return this.icon;
    }

    public abstract int getResourceDrawable();
}
