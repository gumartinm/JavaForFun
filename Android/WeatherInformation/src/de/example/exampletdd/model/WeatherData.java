package de.example.exampletdd.model;


public class WeatherData {
    private final Main main;
    private final Wind wind;
    private final Rain rain;
    private final Coord coord;
    private final DataReceivingTime dataReceivingTime;
    private final StationName stationName;
    private final System system;
    private final Clouds clouds;
    private final Weather weather;
    private byte[] iconData;


    public static class Builder {
        //Optional parameters
        private Main mMain;
        private Wind mWind;
        private Rain mRain;
        private Coord mCoord;
        private DataReceivingTime mDataReceivingTime;
        private StationName mStationName;
        private System mSystem;
        private Clouds mClouds;
        private Weather mWeather;


        public Builder setMain(final Main main) {
            this.mMain = main;
            return this;
        }

        public Builder setWind(final Wind wind) {
            this.mWind = wind;
            return this;
        }

        public Builder setRain(final Rain rain) {
            this.mRain = rain;
            return this;
        }

        public Builder setCoord(final Coord coord) {
            this.mCoord = coord;
            return this;
        }

        public Builder setDataReceivingTime(
                final DataReceivingTime dataReceivingTime) {
            this.mDataReceivingTime = dataReceivingTime;
            return this;
        }

        public Builder setStationName(final StationName stationName) {
            this.mStationName = stationName;
            return this;
        }

        public Builder setSystem(final System system) {
            this.mSystem = system;
            return this;
        }

        public Builder setClouds(final Clouds clouds) {
            this.mClouds = clouds;
            return this;
        }

        public Builder setWeather(final Weather weather) {
            this.mWeather = weather;
            return this;
        }

        public WeatherData build() {
            return new WeatherData(this);
        }
    }

    private WeatherData(final Builder builder) {
        this.main = builder.mMain;
        this.wind = builder.mWind;
        this.rain = builder.mRain;
        this.coord = builder.mCoord;
        this.dataReceivingTime = builder.mDataReceivingTime;
        this.stationName = builder.mStationName;
        this.system = builder.mSystem;
        this.clouds = builder.mClouds;
        this.weather = builder.mWeather;
    }


    @Override
    public String toString() {
        final StringBuilder builder2 = new StringBuilder();
        builder2.append("WeatherData [main=").append(this.main)
        .append(", wind=").append(this.wind).append(", rain=")
        .append(this.rain).append(", coord=").append(this.coord)
        .append(", dataReceivingTime=").append(this.dataReceivingTime)
        .append(", stationName=").append(this.stationName)
        .append(", system=").append(this.system).append(", clouds=")
        .append(this.clouds).append(", weather=").append(this.weather)
        .append("]");
        return builder2.toString();
    }

    public Main getMain() {
        return this.main;
    }

    public Wind getWind() {
        return this.wind;
    }

    public Rain getRain() {
        return this.rain;
    }

    public Coord getCoord() {
        return this.coord;
    }

    public DataReceivingTime getDataReceivingTime() {
        return this.dataReceivingTime;
    }

    public StationName getStationName() {
        return this.stationName;
    }

    public System getSystem() {
        return this.system;
    }

    public Clouds getClouds() {
        return this.clouds;
    }

    public Weather getWeather() {
        return this.weather;
    }

    public void setIconData(final byte[] iconData) {
        this.iconData = iconData;
    }

    public byte[] getIconData() {
        return this.iconData;
    }

    public static class Main {
        private final double temp;
        // Minimum temperature
        private final double minTemp;
        // Maximum temperature
        private final double maxTemp;
        // Humidity in %
        private final double humidity;
        // Atmospheric pressure in kPa
        private final double pressure;

        public Main(final double temp, final double minTemp, final double maxTemp,
                final double humidity, final double pressure) {
            this.temp = temp;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
            this.humidity = humidity;
            this.pressure = pressure;
        }

        public double getTemp() {
            return this.temp;
        }

        public double getMinTemp() {
            return this.minTemp;
        }

        public double getMaxTemp() {
            return this.maxTemp;
        }

        public double getHumidity() {
            return this.humidity;
        }

        public double getPressure() {
            return this.pressure;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Main [temp=").append(this.temp)
            .append(", minTemp=").append(this.minTemp)
            .append(", maxTemp=").append(this.maxTemp)
            .append(", humidity=").append(this.humidity)
            .append(", pressure=").append(this.pressure).append("]");
            return builder.toString();
        }
    }

    public static class Wind {
        // Wind speed in mps
        private final double speed;
        // Wind direction in degrees (meteorological)
        private final double deg;
        // speed of wind gust
        private final double gust;
        // Wind direction
        private final double var_beg;
        // Wind direction
        private final double var_end;

        public Wind(final double speed, final double deg, final double gust,
                final double var_beg, final double var_end) {
            this.speed = speed;
            this.deg = deg;
            this.gust = gust;
            this.var_beg = var_beg;
            this.var_end = var_end;
        }

        public double getSpeed() {
            return this.speed;
        }

        public double getDeg() {
            return this.deg;
        }

        public double getGust() {
            return this.gust;
        }

        public double getVar_beg() {
            return this.var_beg;
        }

        public double getVar_end() {
            return this.var_end;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Wind [speed=").append(this.speed).append(", deg=")
            .append(this.deg).append(", gust=").append(this.gust)
            .append(", var_beg=").append(this.var_beg)
            .append(", var_end=").append(this.var_end).append("]");
            return builder.toString();
        }
    }

    public static class Rain {
        // Period
        private final String time;
        // Precipitation volume for period
        private final double ammount;

        public Rain(final String time, final double ammount) {
            this.time = time;
            this.ammount = ammount;
        }

        public String getTime() {
            return this.time;
        }

        public double getAmmount() {
            return this.ammount;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Rain [time=").append(this.time)
            .append(", ammount=").append(this.ammount).append("]");
            return builder.toString();
        }

    }

    public static class Coord {
        // City location
        private final double longitude;
        private final double latitude;

        public Coord(final double longitude, final double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public double getLongitude() {
            return this.longitude;
        }

        public double getLatitude() {
            return this.latitude;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Coord [longitude=").append(this.longitude)
            .append(", latitude=").append(this.latitude).append("]");
            return builder.toString();
        }
    }

    public static class DataReceivingTime {
        // Time of data receiving in unixtime GMT
        private final double time;

        public DataReceivingTime(final double time) {
            this.time = time;
        }

        public double getTime() {
            return this.time;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("DataReceivingTime [time=").append(this.time)
            .append("]");
            return builder.toString();
        }
    }

    public static class StationName {
        private final String name;

        public StationName(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("StationName [name=").append(this.name).append("]");
            return builder.toString();
        }
    }

    public static class System {
        private final String country;
        // Unixtime GMT
        private final long sunRiseTime;
        // Unixtime GMT
        private final long sunSetTime;
        private final double message;

        public System(final String country, final long sunRiseTime,
                final long sunSetTime, final double message) {
            this.country = country;
            this.sunRiseTime = sunRiseTime;
            this.sunSetTime = sunSetTime;
            this.message = message;
        }

        public String getCountry() {
            return this.country;
        }

        public long getSunRiseTime() {
            return this.sunRiseTime;
        }

        public long getSunSetTime() {
            return this.sunSetTime;
        }

        public double getMessage() {
            return this.message;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("System [country=").append(this.country)
            .append(", sunRiseTime=").append(this.sunRiseTime)
            .append(", sunSetTime=").append(this.sunSetTime)
            .append(", message=").append(this.message).append("]");
            return builder.toString();
        }
    }

    public static class Clouds {
        // Cloudiness in %
        private final double cloudiness;

        public Clouds(final double cloudiness) {
            this.cloudiness = cloudiness;
        }

        public double getCloudiness() {
            return this.cloudiness;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Clouds [cloudiness=").append(this.cloudiness)
            .append("]");
            return builder.toString();
        }
    }

    public static class Weather {
        private final int weatherId;
        private final String main;
        private final String description;
        private final String icon;

        public Weather(final int weatherId, final String main,
                final String description, final String icon) {
            this.weatherId = weatherId;
            this.main = main;
            this.description = description;
            this.icon = icon;
        }

        public int getWeatherId() {
            return this.weatherId;
        }

        public String getMain() {
            return this.main;
        }

        public String getDescription() {
            return this.description;
        }

        public String getIcon() {
            return this.icon;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Weather [weatherId=").append(this.weatherId)
            .append(", main=").append(this.main)
            .append(", description=").append(this.description)
            .append(", icon=").append(this.icon).append("]");
            return builder.toString();
        }
    }
}
