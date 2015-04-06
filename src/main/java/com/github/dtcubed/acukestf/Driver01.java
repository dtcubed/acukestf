package com.github.dtcubed.acukestf;

import java.util.HashMap;

public class Driver01 {

    public static void main(String[] args) {

        String message;

        if (args.length == 2) {

            String testSuiteFile      = args[0];
            String featureFileBaseDir = args[1];

            message = String.format("Test Suite File: [%s] Feature File Base Dir: [%s]", testSuiteFile, featureFileBaseDir);
            System.out.println(message);

            try {

                HashMap<String, Integer> tagCount = Utilities.get_feature_files_tag_count(featureFileBaseDir);

                if (Utilities.test_suite_ok_to_execute(testSuiteFile, tagCount)) {

                    Utilities.execute_test_cases(testSuiteFile, featureFileBaseDir);

                } else {

                    System.out.println("Suite is NOT OK to execute!");
                }

            } catch (Throwable e) {

                System.err.println(e.getStackTrace());

            }

        } else {

            String errMsg = "Expecting arguments <testSuiteFile> <featureFileBaseDir>";
            System.err.println(errMsg);
            System.exit(1);
        }

    }
}

