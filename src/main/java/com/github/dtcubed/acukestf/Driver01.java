package com.github.dtcubed.acukestf;


public class Driver01 {

    public static void main(String[] args) {

        System.out.println("Driver01() - hello world");

        if (args.length == 2) {

            String testSuiteFile      = args[0];
            String featureFileBaseDir = args[1];

        } else {

            String errMsg = "Expecting arguments <testSuiteFile> <featureFileBaseDir>";
            System.err.println(errMsg);
            System.exit(1);
        }

    }
}
