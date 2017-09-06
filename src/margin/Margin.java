/*  
      This program was designed and developed by Hsiao Weng
      Copyright 2015, Common Development and Distribution License
      This code is open source
*/

//package name (the container for of all classes for the program)
package margin;

//import Java util class library components
import java.util.InputMismatchException;
import java.util.Scanner;

//declare public access specifier for Margin class
public class Margin {
     
   //declare static variable of type double
   static double ref_min_max;
   
   //main method: program execution starts here
   public static void main(String[] args) {
      //call to clear_screen method
      clear_screen(); 
      System.out.println("\n... Margin Calculator 1.0 ...");
      pairs();
      calc_margin();  
   }
   
   //method definition for clearing the screen; static type specifier, void return type
   static void clear_screen() {
         //clears the entire screen
        System.out.print("\033[2J");
        //moves to the top of the screen
        System.out.print("\033[H"); 
    }
    
   //method definition for calcuating margin
   static void calc_margin() {
      
      //create an object of Scanner from imported class library
      Scanner cs = new Scanner(System.in);
      
      //pass a double through the Scanner and assign it to variable  
      try {
         System.out.print("\nEnter stop loss reference: ");
         ref_min_max = cs.nextDouble();
      
      //catch non double inputs and callback the calcMargin method
      } catch (InputMismatchException e) {
         System.out.println("\nInvalid.  Enter valid values.");
         calc_margin();
      }
      
      //create an object from the trading class with input as parameter
      trading obj = new trading(ref_min_max);
      
      //call trading class methods on the object
      obj.set_pairs();
      obj.make();
      obj.calculate();
 }
   
     //method definition for displaying usable pairs
   static void pairs() {
        System.out.print("\n   2% (50):   EUR-USD   EUR-CAD   USD-CAD");
        System.out.print("\n   3% (33):   NZD-USD   USD-CHF   AUD-USD");
        System.out.print("\n   3% (33):   CAD-CHF   EUR-CHF");
        System.out.println("\n   4% (25):   EUR-JPY   CAD-JPY   USD-JPY");     
   }
   
}
