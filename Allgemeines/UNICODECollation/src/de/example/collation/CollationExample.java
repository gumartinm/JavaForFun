package de.example.collation;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CollationExample {


    public static void main(final String[] args) {
        final String[] words = {"cote", "coté", "côte", "côté"};

        final List<String> wordsListES = new ArrayList<String>(Arrays.asList(words));
        final List<String> wordsListFR = new ArrayList<String>(Arrays.asList(words));

        final Collator es_ESCollator = Collator.getInstance(new Locale("es","ES"));
        final Collator fr_FRCollator = Collator.getInstance(Locale.FRANCE);

        Collections.sort(wordsListES, es_ESCollator);
        Collections.sort(wordsListFR, fr_FRCollator);

        System.out.println("Words list ES: " + wordsListES);
        System.out.println("Words list FR: " + wordsListFR);
    }

}
