package de.comparable.example;

import java.util.Comparator;

public class CountrySortByIdComparator implements Comparator<Country> {

    @Override
    public int compare(final Country country1, final Country country2) {
        return (country1.getCountryId() < country2.getCountryId()) ? -1
                : (country1.getCountryId() > country2.getCountryId()) ? 1 : 0;
    }

}
