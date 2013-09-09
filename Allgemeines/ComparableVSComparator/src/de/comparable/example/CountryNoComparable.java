package de.comparable.example;

public class CountryNoComparable {
    private final int countryId;
    private final String countryName;

    public CountryNoComparable(final int countryId, final String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    public int getCountryId() {
        return this.countryId;
    }

    public String getCountryName() {
        return this.countryName;
    }
}
