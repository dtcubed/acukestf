package com.github.dtcubed.acukestf;

import java.util.HashMap;

public class Driver01 {

    public static void main(String[] args) {

        String message;

        System.out.println("Driver01() - hello world");

        if (args.length == 2) {

            String testSuiteFile      = args[0];
            String featureFileBaseDir = args[1];

            message = String.format("Test Suite File: [%s] Feature File Base Dir: [%s]", testSuiteFile, featureFileBaseDir);
            System.out.println(message);

            HashMap<String, Integer> tagCount = Utilities.get_feature_files_tag_count(featureFileBaseDir);

            int a = tagCount.get("TEST-CASE-001");
            int b = tagCount.get("TEST-CASE-002");

            System.out.println(a);
            System.out.println(b);


        } else {

            String errMsg = "Expecting arguments <testSuiteFile> <featureFileBaseDir>";
            System.err.println(errMsg);
            System.exit(1);
        }

    }
}
