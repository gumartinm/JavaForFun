package de.example.exampletdd.fragment.specific;

public class SpecificDataEntryThird {
    private final String humidityValue;
    private final String pressureValue;
    private final String windValue;
    private final String rainValue;
    private final String cloudsValue;

    public SpecificDataEntryThird(final String humidityValue, final String pressureValue,
            final String windValue, final String rainValue, final String cloudsValue) {
        this.humidityValue = humidityValue;
        this.pressureValue = pressureValue;
        this.windValue = windValue;
        this.rainValue = rainValue;
        this.cloudsValue = cloudsValue;
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

}
