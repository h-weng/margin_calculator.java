/*  
      This program was designed and developed by Hsiao Weng
      Copyright 2015, Common Development and Distribution License
      This code is open source
*/

//package name (the container for of all classes for the program)
package margin;

//import all Java util class library components
import java.util.*;
import java.io.*;

//declare public access specifier for trading class
public class trading {
   
    //declare and set Input and Output to null
    FileInputStream Input = null;
    FileOutputStream Output = null;

    //create an object of Scanner from imported class library
    protected Scanner sc = new Scanner(System.in);

    //declare public, static varaibles
    public static final String mybase = "USD";
    public static String base, quote; 

    //declare protected varaibles
    protected double price, pip_value, key_level;

    //declare private variables
    private int balance, leverage, margin_space, size, size_estimate;
    private double margin_base, eur_usd_base, mypip_value;
    private final int lot_round = 1000, margin_round = 10000, pips_tolerance = 23;
    private final double quote_ref = .0001;

    //array of strings with currency abbreviations; does not include all currency pairs
    String currencies[] = { "USD", "AUD", "CAD", "EUR", "CHF", "GBP", "NZD", "XAU", "XAG" };

    //constructor for trading class that passes a double parameter
    trading(double MinMax) {
        key_level = MinMax;
    }

    //method definition for clearing the screen; void return type
    void clear_screen() {
        //clears the entire screen
        System.out.print("\033[2J"); 
        //moves to the top of the screen
        System.out.print("\033[H"); 
    }

    //method definition for getting the balance and setting the leverage
    void make() {
        //call to other instance methods
        get_balance();
        set_leverage();
    }

    //method definition for setting the pair of curencies and their attributes
    void set_pairs() {
        //call to other instance methods
        pair_base();
        pair_quote();
    }

    //method definition for getting the balance
    void get_balance() {
        //pass an int through the Scanner and assign it to variable 
        try {
            System.out.print("\nEnter your current balance(no cents): ");
            balance = sc.nextInt();
            //catch non integer inputs and callback the get_balance method
        } catch(InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            get_balance();
        }
    }

    //method definition for setting the leverage
    void set_leverage() {
        //pass an int through the Scanner and assign it to variable 
        try {
            System.out.print("Enter the leverage ratio: ");
            leverage = sc.nextInt();   
            //catch non integer inputs and callback the set_leverage method
        } catch(InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            set_leverage();
        }
    }

    //method definition for writing currency pair to file
    void pairs_write() {
        //set Output to a file
        try {
            Output = new FileOutputStream("data.txt");
            //catch if no file is found
        } catch(FileNotFoundException e) {
            System.out.println("\n... Invalid file ...");
        } 

        //create an object from the OutputStreamWriter class passing in Output
        OutputStreamWriter Writer = new OutputStreamWriter(Output);

        //write the base and quote pair to the Writer object
        try {        
            Writer.write(base.toUpperCase() + "\n");
            Writer.write(quote.toUpperCase() + "\n");
            Writer.close();
            //catch if invalid
        } catch(IOException e) { 
            System.out.println("\nInvalid: \n" + e.getMessage());
        } 
    }

    //method definition for displaying usable pairs
    void pairs() {
        System.out.print("\n   2% (50):   EUR-USD   EUR-CAD   USD-CAD");
        System.out.print("\n   3% (33):   USD-CAD   NZD-USD   USD-CHF");
        System.out.print("\n   3% (33):   CAD-CHF   EUR-CHF   AUD-USD");
        System.out.println("\n   4% (25):   EUR-JPY   CAD-JPY   USD-JPY");     
    }

    //method definition for delcaring the base pair
    void pair_base() {
        int i = 0;
        boolean t;

        //ask for user input of currency base
        try {
            System.out.print("\n   Enter the base currency of pair.\n   (BASE/quote): ");
            base = sc.next();

             //check if the pair is in the array of usable currencies
            do {
                t = base.toUpperCase().equals(currencies[i]);
                i++;
            } while (t != true);
        //generate error if entry is not found and recurse method
        } catch (ArrayIndexOutOfBoundsException r) {
         System.out.println("\n... Invalid.  Enter valid value ...");
         pair_base();      
        }
    }

    //method definition for declaring the quote pair
    void pair_quote() {
        //declare scope variables
        int i = 0;
        boolean t;

        //ask for user input of currency quote
        try {
            System.out.print("\n   Enter the quote currency of pair.\n   (base/QUOTE): ");
            quote = sc.next();

            //check if the pair is in the array of usable currencies
            do {
                t = base.toUpperCase().equals(currencies[i]);
                i++;
            } while (t != true);
        //generate error if entry is not found and recurse method  
        } catch (ArrayIndexOutOfBoundsException r) {
            System.out.println("\n... Invalid.  Enter valid value ...");
            pair_quote();      
        }     
    }

    //method definition for calculating the margin
    void calculate() {
        clear_screen();
        //declare scope variables
        double size_lots, margin_spc;
        int spc_round = 30;

        //calculate pip value and space for magin call
        try{
            //use this to calculate size and pip value if quote is equal to USD
            if(mybase.equals(quote.toUpperCase()) == true) {
                //call to methods to get values
                mybase_is_quote();
                margin_space();

                //set margin_spc to value from margin_space() times mypip_value
                margin_spc = margin_space * mypip_value;
                margin_spc = margin_spc - (margin_spc % spc_round);

                size_lots = ((balance - margin_spc) / margin_base)  * leverage;
                //cast size to int 
                size = (int) size_lots;
                //estimate size by subtracting modulus of a round number
                size_estimate = size - (size % lot_round);
                //set pip_value
                pip_value = quote_ref * size_estimate;

                //call to print_calculation method
                print_calculation(); 
            //use this to calculate size and pip value if base is equal to CAD, and quote is equal to EUR
            } else if (currencies[3].equals(base.toUpperCase()) == true 
                     && currencies[2].equals(quote.toUpperCase()) == true) {
                //call to methods to get values
                myquote_is_cad();
                margin_space();

                //set margin_spc to value from margin_space() times mypip_value
                margin_spc = margin_space * mypip_value;
                margin_spc = margin_spc - (margin_spc % spc_round);

                size_lots = ((balance - margin_spc) / eur_usd_base ) * leverage;
                //cast size to int 
                size = (int) size_lots;
                //estimate size by subtracting modulus of a round number
                size_estimate = size - (size % lot_round);
                //set pip_value
                pip_value = quote_ref * size_estimate * margin_base;

                //call to print_calculation method  
                print_calculation();   
            //in all other cases use this to calculate size and pip value
            } else {
                //call to methods to get values
                mybase_is_base();
                margin_space();

                //set margin_spc to value from margin_space() times mypip_value
                margin_spc = margin_space * mypip_value;

                margin_spc = margin_spc - (margin_spc % spc_round);

                size_lots = ((balance - margin_spc) / (eur_usd_base))  * leverage;
                //cast size to int 
                size = (int) size_lots;
                //estimate size by subtracting modulus of a round number
                size_estimate = size - (size % lot_round);
                //set pip_value
                pip_value = quote_ref * size_estimate * (1/margin_base);

                //call to print_calculation method
                print_calculation();         
            }      
        //generate error if input is invalid and recurse method  
        } catch(InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            calculate();
        }
    }

    //method definition for finding the margin space
    void margin_space() {
        //declare scope variables
        double margin_pips_convert, raw_pips;
        try {
            raw_pips = Math.abs(price - key_level);
            margin_pips_convert = (margin_round * raw_pips);

            margin_space = (int) margin_pips_convert  + pips_tolerance; 

            System.out.print("\n... Stop distance (pips): " + margin_space + 
                    " (unadjusted: " + (int) margin_pips_convert + ") ...");
        //generate error if input is invalid and recurse method 
        } catch (InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            margin_space();              
        }  
    }

    //method definition for calculating the lot size if base is usd
    void mybase_is_base() {
        //declare scope variables
        double  size_lots, eur_usd_price;

        try {
            System.out.print("\n   Enter current price of "+ base.toUpperCase() + "/" 
                    + quote.toUpperCase() + ": ");
            price = sc.nextDouble();

            System.out.print("   Enter current price for EUR/USD: " );
            eur_usd_price = sc.nextDouble();
            //set margin_base to base price   
            margin_base = price;
            eur_usd_base = eur_usd_price;

            //size of of lot is balance divided by EUR/USD times leverage
            size_lots = (balance / eur_usd_base) * leverage;
            //cast size to int 
            size = (int) size_lots;
            //value of a pip
            mypip_value = quote_ref * size * (1/margin_base);
            //generate error if input is invalid and recurse method   
        } catch (InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            mybase_is_base();      
        } 
    }

    //method definition for calculating the lot size if quote is usd
    void mybase_is_quote() {
        //declare scope variables
        double size_lots, quote_price;
        try {
            System.out.print("\n   Enter current price of " + base.toUpperCase() 
                    + "/" + quote.toUpperCase() +": ");
            price = sc.nextDouble();

            //set margin_base to base price
            margin_base = price;

            //size of of lot is balance divided by margin_base times leverage
            size_lots = (balance / margin_base) * leverage;
            //cast size to int 
            size = (int) size_lots;
            //value of a pip
            mypip_value = quote_ref * size;
        //generate error if input is invalid and recurse method   
        } catch (InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            mybase_is_quote();    
        }
    }

    //method definition for calculating the lot size if quote is cad
    void myquote_is_cad() {
        //declare scope variables
        double size_lots, quote_price = 0.0, eur_usd_price = 0.0;

        //ask for the current price of */CAD and price of EUR/USD
        try {
            System.out.print("\n   Enter current price of " + base.toUpperCase()
                    + "/" + quote.toUpperCase() +": ");
            price = sc.nextDouble();

            System.out.print("   Enter current price for " + mybase.toUpperCase() 
                    + "/" + quote.toUpperCase() + ": " );
            quote_price = sc.nextDouble();

            System.out.print("   Enter current price for EUR/USD: " );
            eur_usd_price = sc.nextDouble();

            //set margin_base to 1/(*/CAD)
            margin_base = 1/quote_price;
            eur_usd_base = eur_usd_price;

            //size of of lot is balance divided by EUR/USD times leverage
            size_lots = (balance / eur_usd_base) * leverage;
            //cast size to int 
            size = (int) size_lots;
            //value of a pip
            mypip_value = quote_ref * margin_base * size;
            //catch any invalid inputs and recurse the method
        } catch (InputMismatchException e) {
            sc.next();
            System.out.println("\n... Invalid.  Enter valid values ...");
            myquote_is_cad();    
        } 
    }

    //method definition for printing the calculations on the screen
    void print_calculation() {
        //if size is positive then print calculations
        if (size > 0) {
            System.out.print("\n... optimium number of units: " 
                    + size_estimate +  " (unadjusted: " + size + ") ...");
            System.out.printf("\n... point in percentage value: " 
                    + "%.4f", pip_value);
            System.out.println(" ...");
            //otherwise not enough to cover tolerance between margin and stop loss
        } else {
            System.out.println("\n... Not enough to cover margin space ...\n");
        }  
    }
}