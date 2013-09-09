package de.comparable.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * Comparable: to be used by objects which want to be compared.
 * </p>
 * <p>
 * Comparator: to be used by sort algorithms to change the sort comparison or
 * when trying to sort objects which do not implement the Comparable interface
 * because they did not intend to be compared.
 * </p>
 */
public class Example {

    public static void main(final String[] args) {
        final Country indiaCountry = new Country(1, "India");
        final Country chinaCountry = new Country(4, "China");
        final Country nepalCountry = new Country(3, "Nepal");
        final Country bhutanCountry = new Country(2, "Bhutan");
        final Country bhutanCountryAgain = new Country(2, "Bhutan");

        // 1. Using Comparable.
        List<Country> listOfCountries = new ArrayList<Country>();
        listOfCountries.add(indiaCountry);
        listOfCountries.add(chinaCountry);
        listOfCountries.add(nepalCountry);
        listOfCountries.add(bhutanCountry);
        listOfCountries.add(bhutanCountryAgain);

        System.out.println("Before Sort  : ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country = listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "||"
                    + "Country name: " + country.getCountryName());
        }

        Collections.sort(listOfCountries);

        System.out.println("After Sort  : ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country = listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "|| "
                    + "Country name: " + country.getCountryName());
        }


        // 2. Using Comparator (the same result)
        listOfCountries = new ArrayList<Country>();
        listOfCountries.add(indiaCountry);
        listOfCountries.add(chinaCountry);
        listOfCountries.add(nepalCountry);
        listOfCountries.add(bhutanCountry);
        listOfCountries.add(bhutanCountryAgain);

        System.out.println("Before Sort by id : ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country=listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "||"
                    + "Country name: " + country.getCountryName());
        }

        Collections.sort(listOfCountries,new CountrySortByIdComparator());

        System.out.println("After Sort by id: ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country=listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "|| "
                    + "Country name: " + country.getCountryName());
        }


        // 3. Using Comparator (with new sort)
        // Country objects could not be designed to be compared, but by means of
        // Comparator interface we could compare them even if the were not
        // designed to be compared.
        final CountryNoComparable indiaCountryNoComparable = new CountryNoComparable(1, "India");
        final CountryNoComparable chinaCountryNoComparable = new CountryNoComparable(4, "China");
        final CountryNoComparable nepalCountryNoComparable = new CountryNoComparable(3, "Nepal");
        final CountryNoComparable bhutanCountryNoComparable = new CountryNoComparable(2, "Bhutan");
        final CountryNoComparable bhutanCountryAgainNoComparable = new CountryNoComparable(2, "Bhutan");

        final List<CountryNoComparable> listOfCountriesNoComparable = new ArrayList<CountryNoComparable>();
        listOfCountriesNoComparable.add(indiaCountryNoComparable);
        listOfCountriesNoComparable.add(chinaCountryNoComparable);
        listOfCountriesNoComparable.add(nepalCountryNoComparable);
        listOfCountriesNoComparable.add(bhutanCountryNoComparable);
        listOfCountriesNoComparable.add(bhutanCountryAgainNoComparable);

        System.out.println("Before Sort by name : ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country = listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "||"
                    + "Country name: " + country.getCountryName());
        }

        Collections.sort(listOfCountriesNoComparable, new Comparator<CountryNoComparable>() {

            @Override
            public int compare(final CountryNoComparable o1, final CountryNoComparable o2) {
                // We can compare even if CountryNoComparable was not designed for it :)
                return o1.getCountryName().compareTo(o2.getCountryName());
            }
        });

        System.out.println("After Sort by name: ");
        for (int i = 0; i < listOfCountries.size(); i++) {
            final Country country=listOfCountries.get(i);
            System.out.println("Country Id: " + country.getCountryId() + "|| "
                    + "Country name: " + country.getCountryName());
        }
    }
}
