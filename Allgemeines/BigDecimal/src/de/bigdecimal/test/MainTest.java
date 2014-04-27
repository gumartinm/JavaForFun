package de.bigdecimal.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/*
 * REMEMBER, double/float THE IN MEMORY VALUES ARE NOT USUALLY WHAT YOU EXPECT
 * BECAUSE OF THE IEE 754. SO, IF DECIMALS ARE IMPORTANT FOR YOU (LIKE WITH CURRENCY)
 * NEVER USE double/float IN ANY POINT OF YOUR APPLICATION.
 * 
 */

/*
 * NICE EXPLANATIONS ABOUT WHEN TO USE DOUBLE/FLOAT AND WHEN BIGDECIMAL
 *
 * 1.
 * http://stackoverflow.com/questions/2545567/in-net-how-do-i-choose-between-a-decimal-and-a-double
 * I usually think about natural vs artificial quantities.
 *
 * Natural quantities are things like weight, height and time. These will never be measured absolutely
 * accurately, and there's rarely any idea of absolutely exact arithmetic on it: you shouldn't generally
 * be adding up heights and then making sure that the result is exactly as expected.
 * Use double for this sort of quantity. Doubles have a huge range, but limited precision;
 * they're also extremely fast.
 *
 * The dominant artificial quantity is money. There is such a thing as "exactly $10.52", and if you
 * add 48 cents to it you expect to have exactly $11. Use decimal for this sort of quantity.
 * Justification: given that it's artificial to start with, the numbers involved are artificial
 * too, designed to meet human needs - which means they're naturally expressed in base 10.
 * Make the storage representation match the human representation. decimal doesn't have the range of double,
 * but most artificial quantities don't need that extra range either. It's also slower than double, but
 * I'd personally have a bank account which gave me the right answer slowly than a wrong answer quickly :)
 *
 * For a bit more information, I have articles on .NET binary floating point types and the .NET decimal type.
 * (Note that decimal is a floating point type too - but the "point" in question is a decimal point, not a binary point.)
 * 
 * FROM ME:
 * BUT BE CAREFUL BECAUSE IF YOU HAVE MATH OPERATIONS WITH NATURAL QUANTITIES YOU COULD FINISH HAVING
 * -0.0 or NaN, AND I GUESS YOU DO NOT WANT TO SHOW TO THE USER A WEIGHT OF -0.0 or NaN :/ SO DEPENDING ON
 * THE NATURAL QUANTITIES (IF THEY ARE SMALL OR BIG NUMBERS) AND THE MATH OPERATIONS, PERHAPS
 * YOU COULD WANT TO USE A FIXED-POINT NUMBER (LIKE BigDecimal IN JAVA OR decimal IN C#)
 * SO, THIS IS A BIT COMPLICATED: PERHAPS EVEN IF YOU FINISH HAVING -0.0, YOU COULD DO SOMETHING
 * LIKE THIS:
 * public static boolean isNegative(double d) {
 *    return Double.compare(d, 0.0) < 0;
 * }
 * AND IN THAT CASE YOU COULD SHOW 0.0 INSTEAD OF -0.0. ALL DEPENDS ON WHAT ARE YOU DOING.
 * I GUESS IF YOU DO NOT KNOW ANYTHING ABOUT THE NUMBERS YOU ARE USING YOU MUST USE
 * FIXED-POINT NUMBER BigDecimal, decimal OR BUILDING YOURSELF SOME INTEGER WHERE THE LAST 4 NUMBERS
 * COULD BE DECIMALS AND THEN YOU ALWAYS *10000 AND /10000. THIS IS THE FASTEST SOLUTION BUT
 * I DO NOT THINK IT IS THE BEST. THE BEST (IMHO) IF YOU KNOW NOTHIG IS BigDecimal AND decimal
 * (FIXED-POINT NUMBER)
 * 
 * 2.
 * SEE: http://randomascii.wordpress.com/2012/02/13/dont-store-that-in-a-float/
 * WITH VIDEO GAMES WHEN DECIMALS ARE IMPORTANT (FOR EXAMPLE WHEN CALCULATING FPS) YOU MIGHT
 * WANT TO USE DOUBLE. BUT AT THE END (IMHO) THE BEST WOULD BE A FIXED-POINT NUMBER (LIKE BigDecimal IN
 * JAVA OR decimal IN C#)
 * 
 * 3. (fixed-point number)
 * SEE: http://home.comcast.net/~tom_forsyth/blog.wiki.html#OffendOMatic link "A Matter of precision"
 * YOU MUST ALWAYS USE FIXED-POINT NUMBER!!!!
 * 
 * (note to 1.)
 * 
 */

public class MainTest {

    public static void main(final String[] args) {
        /**
         * WARNING: if you write 165.01499999999998 after compiling this code
         * javac will write "for you" the value 165.015 in your bytecode :/
         * Use: javap -verbose BigDecimal/bin/de/bigdecimal/test/MainTest You will see 165.015d
         * instead of something closer to what you would have using double.
         * For example, gcc with that value writes in your assembly code 4064A07AE147AE14. By the way
         * 165.015d and 165.01499999999998 are the same in memory: 4064A07AE147AE14. They
         * are adjacent values and because of that javac and Double.toString transform it to 165.015d.
         * 
         * IF YOU READ THE Double.toString JAVADOC YOU WILL SEE THAT THE JAVA DEVELOPERS OF DOUBLE.TOSTRING
         * DECIDED TO SHOW ALWAYS THE SMALLEST ADJACENT VALUE. THIS DECISION IS NOT GOOD OR BAD,
         * IF FOR YOU IT IS A PROBLEM IT IS BECAUSE YOU SHOULD NOT HAVE USED SINCE THE FIRST
         * VERY MOMENT DOUBLE OR FLOATS. IF DECIMALS ARE IMPORTANT FOR YOU MUST USE BigDecimal.
         * SO, IF YOU THINK DOUBLE.TOSTRING IS WRONG, IT IS NOT THE PROBLEM OF DOUBLE.TOSTRING
         * IT IS YOUR PROBLEM BECAUSE YOU ARE WORKING WHEN DOUBLE/FLOAT WHEN YOU SHOULD HAVE
         * WORKED WITH BigDecimal.
         **/

        /**
         * Double.toString TRANSFORMS 165.01499999999998 in 165.015d the same as javac does :/
         */

        /**
         * If decimals are important for you never use double/float.
         **/

        /**
         * new BigDecimal(double) tries to represent the real value of a float/double. When I write
         * real value I mean the value in memory, which should use IEE 754.
         * new BigDecimal(String) tries to represent what the user expects to see.
         * 
         * If you debug this code you will see that new BigDecimal(double) and new BigDecimal(String)
         * are storing different values. The first one extracts (using Double.doubleToLongBits)
         * the in memory value of some double/float and stores it in its internal instance variables.
         * The second one takes the value in the string and stores it in its internal instance
         * variables, it does no try to extract the in memory value (the IEE 754 value)
         **/

        final BigDecimal fromString = new BigDecimal("165.01499999999998");
        /**
         * 1. I write: "165.01499999999998"
         * 2. javac writes: string "165.01499999999998"
         * 3. BigDecimal stores: 165.01499999999998 (the String value without modifications)
         */

        /**
         * 1. extracts the stored value in BigDecimal
         */
        System.out.println("fromString (BigDecimal stores the String value without modifications): " + fromString.toString());
        /**
         * 1. extracts the stored value in BigDecimal
         * 2. transform that value in double using  FloatingDecimal.readJavaFormatString(s).doubleValue();
         *    I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         *    even if it does 165.0149999999999863575794734060764312744140625 then if we want to represent it we must use
         *    Double.toString and it ALWAYS TRANSFORM IT to 165.015 :(
         * 3. create string with Double.toString() it ends up with 165.015 :(
         * 
         */
        System.out.println("fromString using Double.toString (println): " + fromString.doubleValue());

        /**
         * Double.toHexString takes a double value and by means of Double.doubleToLongBits
         * extracts the in memory values: (sign, mantissa and exponent)
         * I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         * even if it does 165.0149999999999863575794734060764312744140625 it does not matter because
         * 165.015, 165.01499999999998, 165.0149999999999863575794734060764312744140625 have the same in
         * memory values: 165.0149999999999863575794734060764312744140625
         */
        System.out.println("fromString hexadecimal (IEE 754 value): " + Double.toHexString(fromString.doubleValue()));

        /**
         * SCALE 2, HALF_UP we have 165.01499999999998
         * SCALE 2: 165.01
         * HALF_UP: 0.499999999998 <---- less than 0.5 then we should see as result 165.01
         */
        System.out.println("fromString rounding two: " + fromString.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("fromString rounding two using Double.toString: " + fromString.setScale(2, RoundingMode.HALF_UP).doubleValue());
        /**
         * SCALE 4, HALF_UP we have 165.01499999999998
         * SCALE 4: 165.0149
         * HALF_UP: 0.9999999998 <---- more than 0.5 then we should see as result 165.0150
         */
        System.out.println("fromString rounding four: " + fromString.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("fromString rounding four using Double.toString: " + fromString.setScale(4, RoundingMode.HALF_UP).doubleValue());
        

        /**
         * 1. I write: 165.01499999999998
         * 2. javac writes: string 165.015
         * 3. BigDecimal stores: 165.0149999999999863575794734060764312744140625 (the IEE 754 value,
         * the in memory value) It uses Double.doubleToLongBits. As we know the in memory
         * value of 165.015 is 165.0149999999999863575794734060764312744140625 and it is the same
         * as the in memory value for 165.01499999999998
         */
        final BigDecimal fromDouble = new BigDecimal(165.01499999999998);

        /**
         * 1. extracts the stored value in BigDecimal and shows it as string.
         */
        System.out.println("fromDouble (BigDecimal stores the in memory value): " + fromDouble.toString());

        /**
         * 1. extracts the stored value in BigDecimal
         * 2. transform that value in double using  FloatingDecimal.readJavaFormatString(s).doubleValue();
         *    I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         *    even if it does 165.0149999999999863575794734060764312744140625 then if we want to represent it we must use
         *    Double.toString and it ALWAYS TRANSFORM IT to "165.015" (Double.toString removes adjacent values
         *    and shows the "smallest" adjacent value. :(
         * 3. create string with Double.toString() it ends up with "165.015" :(
         * 
         */
        System.out.println("fromDouble using Double.toString (println): " + fromDouble.doubleValue());

        /**
         * Double.toHexString takes a double value and by means of Double.doubleToLongBits
         * extracts the in memory values: (sign, mantissa and exponent)
         * I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         * even if it does 165.015 it does not matter because 165.015 has the same in
         * memory value as 165.01499999999998: 165.0149999999999863575794734060764312744140625
         */
        System.out.println("fromDouble hexadecimal (IEE 754 value): " + Double.toHexString(fromDouble.doubleValue()));
        System.out.println("fromDouble rounding two: " + fromDouble.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("fromDouble rounding two using Double.toString: " + fromDouble.setScale(2, RoundingMode.HALF_UP).doubleValue());
        System.out.println("fromDouble rounding four: " + fromDouble.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("fromDouble rounding four using Double.toString: " + fromDouble.setScale(4, RoundingMode.HALF_UP).doubleValue());

        /**
         * 1. I write: 165.01499999999998
         * 2. javac writes: string 165.015
         * 3. BigDecimal stores: 165.015 Even if javac did not take away decimals, because
         *    BigDecimal.valueOf is using Double.toString it will end up with 165.015 instead
         *    of 165.01499999999998. This BigDecimal uses the constructor new BigDecimal(String)
         *    which stores the value in String without transformations, the problem is
         *    there is Double.toString transformation before the constructor BigDecimal(String)
         *    IMHO this sucks, I do not understand why someone would want to use in this
         *    way BigDecimal, for example Apache MathUtils is using it for the round method
         *    and as you can see Double.toString applies some transformations, which in many cases
         *    could break our results...
         *    I would never use BigDecimal.valueOf, what I would try always to use is
         *    new BigDecimal(String) in the edges of my application (I would receive numbers
         *    as strings from the devices reading for example prices), then I would work in my
         *    whole application with BigDecimal and at the end (to show values to user) I would
         *    use BigDecimal.setScale(2, RoundingMode.HALF_UP).toString() IN THIS WAY MY APP
         *    WILL NEVER FAIL AND EVERYTHING WILL WORK AS EXPECTED!!!!
         */
        final BigDecimal fromValueOf = BigDecimal.valueOf(165.01499999999998);

        /**
         * 1. extracts the stored value in BigDecimal and shows it as string.
         *    I expected to see the same as in fromDouble.toString() but I WAS WRONG BECAUSE
         *    BigDecimal STORES DIFFERENT VALUES DEPENDING ON THE CONSTRUCTOR WE USE.
         */
        System.out.println("fromValueOf (BigDecimal stores the Double.toString(double) value "
                + "without modifications, it is Double.toString what would remove adjacent values. "
                + "In this case, even before Double.toString, javac removed adjacent values and in byte code "
                + "we have 165.015. Anyhow even without javac removing values, Double.toString would have "
                + "finished with 165.015 instead of 165.01499999999998 WHEN DECIMALS ARE A PROBLEM, YOU MUST NO USE "
                + "DOUBLE/FLOAT IF YOU ARE USING THEM, YOU ARE DOING IT WRONG): " + fromValueOf.toString());

        /**
         * 1. extracts the stored value in BigDecimal
         * 2. transform that value in double using  FloatingDecimal.readJavaFormatString(s).doubleValue();
         *    I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         *    even if it does 165.0149999999999863575794734060764312744140625 then if we want to represent
         *    it we must use Double.toString and it ALWAYS TRANSFORM IT to 165.015 :(
         * 3. create string with Double.toString() it ends up with 165.015 :(
         * 
         */
        System.out.println("fromValueOf using Double.toString (println): " + fromValueOf.doubleValue());

        /**
         * Double.toHexString takes a double value and by means of Double.doubleToLongBits
         * extracts the in memory values: (sign, mantissa and exponent)
         * I do not know what FloatingDecimal.readJavaFormatString(s).doubleValue() returns but
         * even if it does 165.0149999999999863575794734060764312744140625 it does not matter because
         * 165.015 has the same in memory value: 165.0149999999999863575794734060764312744140625
         */
        System.out.println("fromValueOf hexadecimal (IEE 754 value): " + Double.toHexString(fromValueOf.doubleValue()));
        System.out.println("fromValueOf rounding two: " + fromValueOf.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("fromValueOf rounding two using Double.toString: " + fromValueOf.setScale(2, RoundingMode.HALF_UP).doubleValue());
        System.out.println("fromValueOf rounding four: " + fromValueOf.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("fromValueOf rounding four using Double.toString: " + fromValueOf.setScale(4, RoundingMode.HALF_UP).doubleValue());


        /**
         * 1. Double.toString ALWAYS TAKES AWAY DECIMALS (if they are not useful, as in our case
         *    the in memory value is the same for 165.015 and for 165.01499999999998)
         * 2. BigDecimal STORES DIFFERENT VALUES DEPENDING ON WHAT CONSTRUCTOR WE USE.
         *    BECAUSE OF THAT BigDecimal.toString RETURNS DIFFERENT VALUES DEPENDING ON THE
         *    CONSTRUCTOR.
         * 3. BE CAREFUL, DEPENDING ON WHAT YOU WANT TO DO YOU MIGHT WANT TO USE BigDecimal(double)
         *    instead of BigDecimal(String)
         *    BigDecimal(String): stores the String as a BigDecimal value
         *    BigDecimal(double): stores the in memory value of that double as a BigDecimal value.
         * 4. JUST IN THE MOMENT YOU CONVERT DOUBLE TO STRING THERE ARE PROBLEMS IF YOU WANT
         *    TO KEEP ALL YOUR DECIMALS. IN MEMORY:
         *    - 165.015 is 165.0149999999999863575794734060764312744140625
         *    - 165.01499999999998 is  165.0149999999999863575794734060764312744140625
         *    SO IF DOUBLE.TOSTRING SHOWS 165.015 IT IS NOT WRONG, THE PROBLEM IS JUST IN THE MOMENT
         *    YOU USE double YOUR REAL DECIMALS DISSAPEAR BECAUSE THE IEE 754 VALUE IS NOT WHAT YOU EXPECT!!!!
         * 5. If you use double or float, some times will be impossible to retrieve the value
         *    you wrote because the in memory value (the IEE 754) is not what you expect. Because
         *    of that if Double.toString removes some decimals, it is not wrong because the in memory
         *    value is the same for adjacent values, it does not know what you wanted to see,
         *    it just know what there is in memory. Besides, the Java developers of Double.toString
         *    decided to remove decimals when having adjacent values and to show to the user
         *    the "smallest" adjacent value. That is why if you use
         *    Double.toString(165.0149999999999863575794734060764312744140625) you finish having
         *    "165.015" instead of "165.0149999999999863575794734060764312744140625".
         */

        /**
         * AND OF COURSE, NEVER USE FLOAT OR DOUBLE IF THE DECIMALS ARE IMPORTANT FOR YOU!!!!!!!
         * THAT IS WHY YOU MUST NEVER USE FLOAT/DOUBLE WITH CURRENCY. ROUND,DIVISION,SUM,REST
         * OPERATIONS WILL BE WRONG FOR SURE IF YOU USE FLOAT/DOUBLE. THE WHOLE APPLICATION MUST USE
         * BigDecimal WITH CURRENCY, JUST IN THE MOMENT YOU USE DOUBLE/FLOAT FOR ROUND,DIVISION,SUM,
         * REST OPERATIONS YOUR APPLICATION WILL BE BROKEN.
         * 
         * AND NEVER EVER TRY TO USE double == double / double != double / float == float /
         * float != float
         */


        /**
         * I GUESS FOR SOME APPLICATION THE BEST WOULD BE TO USE IN THE EDGES (FOR EXAMPLE
         * SOME DEVICE READING PRICES AND RETURNING PRICES AS STRINGS) STRING, THEN WE COULD USE
         * STRING AS INPUT PARAMETER FOR BigDecimal, (WE WOULD USE new BigDecimal(String))
         * THE WHOLE APPLICATION WOULD WORK WITH BigDecimal FOR CURRENCY AND IN THIS WAY NOTHING
         * WRONG SHOULD HAPPEN.
         */


        final BigDecimal fromFunnyString = new BigDecimal("165.015");
        System.out.println("fromFunnyString (BigDecimal stores the String value without modifications): " + fromFunnyString.toString());
        System.out.println("fromFunnyString using Double.toString (println): " + fromFunnyString.doubleValue());
        System.out.println("fromFunnyString hexadecimal (IEE 754 value): " + Double.toHexString(fromFunnyString.doubleValue()));
        System.out.println("fromFunnyString rounding two: " + fromFunnyString.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("fromFunnyString rounding two using Double.toString: " + fromFunnyString.setScale(2, RoundingMode.HALF_UP).doubleValue());
        System.out.println("fromFunnyString rounding four: " + fromFunnyString.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("fromFunnyString rounding four using Double.toString: " + fromFunnyString.setScale(4, RoundingMode.HALF_UP).doubleValue());


        final BigDecimal fromFunnyDouble = new BigDecimal(165.015);
        System.out.println("fromFunnyDouble (BigDecimal stores the in memory value): " + fromFunnyDouble.toString());
        System.out.println("fromFunnyDouble using Double.toString (println): " + fromFunnyDouble.doubleValue());
        System.out.println("fromFunnyDouble hexadecimal (IEE 754 value): " + Double.toHexString(fromFunnyDouble.doubleValue()));

        /**
         * BigDecimal stores the in memory value, and it will work with it. That is the reason
         * we end up having "unexpected" results in the next two cases.
         * There is not 165.015 value for IEE 754. The in memory value is: 165.0149999999999863575794734060764312744140625
         */
        System.out.println("(YOU DID NOT EXPECT THIS, DID YOU?) fromFunnyDouble rounding two: " + fromFunnyDouble.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("(YOU DID NOT EXPECT THIS, DID YOU?) fromFunnyDouble rounding two using Double.toString: " + fromFunnyDouble.setScale(2, RoundingMode.HALF_UP).doubleValue());
        System.out.println("(YOU DID NOT EXPECT THIS, DID YOU?) fromFunnyDouble rounding four: " + fromFunnyDouble.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("(YOU DID NOT EXPECT THIS, DID YOU?) fromFunnyDouble rounding four using Double.toString: " + fromFunnyDouble.setScale(4, RoundingMode.HALF_UP).doubleValue());


        final BigDecimal fromFunnyValueOf = BigDecimal.valueOf(165.015);
        System.out.println("fromFunnyValueOf (BigDecimal stores the Double.toString(double) value "
                + "without modifications, Double.toString removes adjacent values. In this case "
                + "the Double.toString(165.015) is 165.015 but in other cases it could be a problem if "
                + "Double.toString removes our decimals. Anyhow WHEN DECIMALS ARE A PROBLEM, YOU MUST NO USE "
                + "DOUBLE/FLOAT IF YOU ARE USING THEM, YOU ARE DOING IT WRONG): " + fromFunnyValueOf.toString());
        System.out.println("fromFunnyValueOf using Double.toString (println): " + fromFunnyValueOf.doubleValue());
        System.out.println("fromFunnyValueOf hexadecimal (IEE 754 value): " + Double.toHexString(fromFunnyValueOf.doubleValue()));
        System.out.println("fromFunnyValueOf rounding two: " + fromFunnyValueOf.setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println("fromFunnyValueOf rounding two using Double.toString: " + fromFunnyValueOf.setScale(2, RoundingMode.HALF_UP).doubleValue());
        System.out.println("fromFunnyValueOf rounding four: " + fromFunnyValueOf.setScale(4, RoundingMode.HALF_UP).toString());
        System.out.println("fromFunnyValueOf rounding four using Double.toString: " + fromFunnyValueOf.setScale(4, RoundingMode.HALF_UP).doubleValue());

        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#################");
        System.out.println("DecimalFormat: " + tempFormatter.format(165.01499999999998));
        System.out.println("String.format (printf style): " + String.format(Locale.US, "%.20f", 165.01499999999998));
    }

}
