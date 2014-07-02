package de.example.tryfinally;

public class TryFinallyMain {
    static String result;

    public static void main(final String[] args) {
        System.out.println("Calling go");
        final String st = returnAndFinally();
        System.out.println("Back from go: " + st);
        System.out.println("Surprise!! " + result);

        try {
            System.out.println(noCatchAndFinally());
        } catch (final Exception e) {
            System.out.println("Second catch.");
            e.printStackTrace();
        }
    }

    public static String returnAndFinally() {
        result = "NOK";

        try {
            messingAround();
            return result;
        }
        catch (final Exception ex) {
            System.out.println("Entered catch: " + result);
            result = "OK";
            //This is the return statement that this method will execute in case of any Exception class or subclass is thrown.
            return result;
        }
        finally {
            //You will not see FINALLY in the "Back from go:" statement.
            //From stackoverflow:
            //http://stackoverflow.com/questions/421797/what-really-happens-in-a-try-return-x-finally-x-null-statement.
            // * Code before return statement is executed
            // * Expression in return statement is evaluated
            // * finally block is executed
            // * Result evaluated in step 2 is returned

            result = "FINALLY";
            System.out.println("Entered finally: " + result);
            //NEVER USE return in finally block because you can loose the stack if some exception is thrown from the try block!!!
            //You finish with this return instead of the previous one.
            //http://stackoverflow.com/questions/48088/returning-from-a-finally-block-in-java
            //return result;
        }
    }

    public static void messingAround() throws Exception {
        throw(new Exception());
    }


    public static String noCatchAndFinally() throws Exception {
        try {
            messingAround();
            return "You will not see me";
        }
        finally {
            try
            {
                //Catching this exception does not swallow the previous one (if there was one)
                messingAroundReturns();
            }
            catch (final Exception e) {
                System.out.println("First catch.");
                e.printStackTrace();
            }
            //NEVER USE return in finally block because you can loose the stack if some exception is thrown from the try block!!!
            //http://stackoverflow.com/questions/48088/returning-from-a-finally-block-in-java
            // return
            // "Do not do this. You are loosing the exception thrown by messingAround method!!!";
        }
    }

    public static void messingAroundReturns() throws Exception {
        throw(new Exception());
    }
}
