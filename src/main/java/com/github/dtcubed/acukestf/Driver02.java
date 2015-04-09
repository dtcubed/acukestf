package com.github.dtcubed.acukestf;


import java.io.File;

import java.util.HashMap;

public class Driver02 {

    public static void main(String[] args) {

        String errorMsg;
        String message;

        if (args.length == 2) {

            String testSuiteFile      = args[0];
            String featureFileBaseDir = args[1];

            String runBaseDirectory = "support/run/";

            String runId = Utilities.get_datetime_string();

            String runDirectory = runBaseDirectory + runId + "/";

            message = String.format("%s: [%s] %s: [%s] %s: [%s]",
                                     "Test Suite File", testSuiteFile,
                                     "Feature File Base Dir", featureFileBaseDir,
                                     "Run Dir", runDirectory);

            System.out.println(message);

            File fileRunBaseDirectory = new File(runBaseDirectory);

            File fileRunDirectory     = new File(runDirectory);


            // Ensure that the base directory exists.
            if (fileRunBaseDirectory.isDirectory()) {

                // Ensure that the proposed run directory does NOT exist (as either a directory or a file).
                // Since the proposed run directory is based on a time-stamp down to the millisecond, we have a
                // problem if it does already exist.
                if (!(fileRunDirectory.exists())) {

                    // Now, make the creation attempt.
                    if (!(fileRunDirectory.mkdir())) {

                        errorMsg = String.format("Failed to create directory: [%s]", runDirectory);
                        System.err.println(errorMsg);
                        System.exit(1);
                    }
                }
                else {

                    errorMsg = String.format("Proposed Run Directory: [%s] already exists!", runDirectory);
                    System.err.println(errorMsg);
                    System.exit(1);
                }

            }
            else {

                errorMsg = String.format("Run Base Directory: [%s] does not already exist!", runBaseDirectory);
                System.err.println(errorMsg);
                System.exit(1);
            }


            try {

                HashMap<String, Integer> tagCount = Utilities.get_feature_files_tag_count(featureFileBaseDir);

                if (Utilities.test_suite_ok_to_execute(testSuiteFile, tagCount)) {

                    if (Utilities.build_run_feature_file(testSuiteFile, featureFileBaseDir, runDirectory)) {

                        Utilities.execute_run_feature_file(runDirectory);

                    } else {

                        System.err.println("Problem Building Run Feature File");

                    }

                } else {

                    System.err.println("Suite is NOT OK to execute!");
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


