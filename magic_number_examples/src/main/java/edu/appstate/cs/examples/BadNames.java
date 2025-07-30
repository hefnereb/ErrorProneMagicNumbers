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
     BadNames b = new BadNames();
     BadNames ccc = new BadNames();
     ccc.foo(m);
     b.foo(m);
   }
 }