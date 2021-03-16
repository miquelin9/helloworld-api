package com.modeling;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class MainClass {
    public static void main(String[] args) {
        System.out.println("Running API test...");
        Quarkus.run(args);
    }
}
