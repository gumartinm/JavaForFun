package de.example.exampletdd.fragment.specific;

public class WeatherSpecificDataEntry {
    private final String header;
    private final String body;

    public WeatherSpecificDataEntry(final String header, final String body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return this.header;
    }

    public String getBody() {
        return this.body;
    }

}
