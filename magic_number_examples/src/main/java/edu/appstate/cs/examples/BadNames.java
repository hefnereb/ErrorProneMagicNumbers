/*
 * A sample file for the checker to process.
 */

 package edu.appstate.cs.examples;

 public class BadNames {
   public void foo(String s) {
     System.out.println(s);
   }
 
   public void thisMethodNameIsRidiculouslyLongAndShouldAlsoBeFlaggedByOurPluginBecauseItKeepsGoing() {
     BadNames thisOtherNameIsWayTooLongAndShouldBeFlaggedByTHEPluginBecauseItIsOverFifty = new BadNames();
     thisOtherNameIsWayTooLongAndShouldBeFlaggedByTHEPluginBecauseItIsOverFifty.foo("bad name");
     BadNames aaaaaaa = new BadNames();
     aaaaaaa.foo("bad name");
   }
 
   public static void main(String[] args) {
     String m = "This is a message";
     int d = 5;
     int dd = 1; 
     if (d > 3)
     {
        System.out.println("d is greater than 3");
     }
     else if (dd < 2)
     {
        System.out.println("dd is less than 2");
     }
     else
     {
        System.out.println("d is not greater than 3 and dd is not less than 2");
     }

     BadNames b = new BadNames();
     BadNames ccc = new BadNames();
     ccc.foo(m);
     b.foo(m);
   }
 }