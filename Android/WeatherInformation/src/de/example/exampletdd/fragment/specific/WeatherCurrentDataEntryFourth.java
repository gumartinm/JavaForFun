package de.example.exampletdd.fragment.specific;

public class WeatherCurrentDataEntryFourth {
    private final String morningTemp;
    private final String dayTemp;
    private final String eveTemp;
    private final String nightTemp;

    public WeatherCurrentDataEntryFourth(final String morningTemp, final String dayTemp,
            final String eveTemp, final String nightTemp) {
        this.morningTemp = morningTemp;
        this.dayTemp = dayTemp;
        this.eveTemp = eveTemp;
        this.nightTemp = nightTemp;
    }

    public String getMorningTemp() {
        return this.morningTemp;
    }

    public String getDayTemp() {
        return this.dayTemp;
    }

    public String getEveTemp() {
        return this.eveTemp;
    }

    public String getNightTemp() {
        return this.nightTemp;
    }

}
