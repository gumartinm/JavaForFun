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

        Collator es_ESCollator = Collator.getInstance(new Locale("es", "ES"));
        final Collator fr_FRCollator = Collator.getInstance(Locale.FRANCE);

        // String.compareTo and String.compareToIgnoreCase are using UNICODE
        // values directly
        //final String test = "GUS";
        //test.compareToIgnoreCase("gus");

        es_ESCollator.setStrength(Collator.TERTIARY);
        es_ESCollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        Collections.sort(wordsListES, es_ESCollator);
        fr_FRCollator.setStrength(Collator.TERTIARY);
        fr_FRCollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        Collections.sort(wordsListFR, fr_FRCollator);

        System.out.println("Words list ES: " + wordsListES);
        System.out.println("Words list FR: " + wordsListFR);

        // Shouldn't it be the same word in German?
        // CHECK MONO RESULTS, IT IS NOT THE SAME!!!! O.o
        // Why Mono and Java give me different results? WTF!!! :(
        System.out.println("strasse");
        Collator de_DECollator = Collator.getInstance(new Locale("de", "DE"));
        de_DECollator.setStrength(Collator.TERTIARY);
        de_DECollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        int result = de_DECollator.compare("strasse", "straße");
        System.out.println("German result: " + result);
        es_ESCollator = Collator.getInstance(new Locale("es", "ES"));
        es_ESCollator.setStrength(Collator.TERTIARY);
        es_ESCollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        // Neither in German nor in Spanish they are the same word. I do not
        // understand collations :(
        result = es_ESCollator.compare("strasse", "straße");
        System.out.println("Spanish result: " + result);

        // Shouldn't it be the same word in German?
        // IN THIS CASE I GET THE SAME RESULTS USING MONO :)
        System.out.println("koennen");
        de_DECollator = Collator.getInstance(new Locale("de", "DE"));
        de_DECollator.setStrength(Collator.TERTIARY);
        de_DECollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        result = de_DECollator.compare("können", "koennen");
        System.out.println("German result: " + result);
        es_ESCollator = Collator.getInstance(new Locale("es", "ES"));
        es_ESCollator.setStrength(Collator.TERTIARY);
        es_ESCollator.setDecomposition(Collator.FULL_DECOMPOSITION);
        // Neither in German nor in Spanish they are the same word. I do not
        // understand collations :(
        result = es_ESCollator.compare("können", "koennen");
        System.out.println("Spanish result: " + result);
    }

}
