package de.comparable.example;


public class Country implements Comparable<Country> {
    private final int countryId;
    private final String countryName;

    public Country(final int countryId, final String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    @Override
    public int compareTo(final Country country) {
        return (this.countryId < country.countryId) ? -1
                : (this.countryId > country.countryId) ? 1 : 0;
    }

    public int getCountryId() {
        return this.countryId;
    }

    public String getCountryName() {
        return this.countryName;
    }

}