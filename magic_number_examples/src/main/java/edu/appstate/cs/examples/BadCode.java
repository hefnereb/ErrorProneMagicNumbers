package edu.appstate.cs.examples;

public class BadCode {
    public static void someMethod() {
        int y = 1;
        if (y == 25) {
            System.out.println("I'm here");
        }

        if (Math.random() < 0.5) {
            System.out.println("I'm here 2");
        } else {
            System.out.println("I'm here 3");
        }
    }
}
