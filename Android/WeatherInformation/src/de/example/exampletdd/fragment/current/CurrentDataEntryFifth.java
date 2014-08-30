package de.example.exampletdd.fragment.current;

public class CurrentDataEntryFifth {
    private final String sunRiseTime;
    private final String sunSetTime;
    private final String humidityValue;
    private final String pressureValue;
    private final String windValue;
    private final String rainValue;
    private final String cloudsValue;
    private final String feelsLike;
    private final String feelsLikeUnits;
    private final String snowValue;

    public CurrentDataEntryFifth(final String sunRiseTime, final String sunSetTime,
            final String humidityValue, final String pressureValue, final String windValue,
            final String rainValue, final String feelsLike, final String feelsLikeUnits,
            final String snowValue,
            final String cloudsValue) {
        this.sunRiseTime = sunRiseTime;
        this.sunSetTime = sunSetTime;
        this.humidityValue = humidityValue;
        this.pressureValue = pressureValue;
        this.windValue = windValue;
        this.rainValue = rainValue;
        this.feelsLike = feelsLike;
        this.feelsLikeUnits = feelsLikeUnits;
        this.snowValue = snowValue;
        this.cloudsValue = cloudsValue;
    }

    public String getSunRiseTime() {
        return this.sunRiseTime;
    }

    public String getSunSetTime() {
        return this.sunSetTime;
    }

    public String getFeelsLike() {
        return this.feelsLike;
    }

    public String getFeelsLikeUnits() {
        return this.feelsLikeUnits;
    }

    public String getHumidityValue() {
        return this.humidityValue;
    }

    public String getPressureValue() {
        return this.pressureValue;
    }

    public String getWindValue() {
        return this.windValue;
    }

    public String getRainValue() {
        return this.rainValue;
    }

    public String getCloudsValue() {
        return this.cloudsValue;
    }

    public String getSnowValue() {
        return this.snowValue;
    }
}
