package com.github.dtcubed.acukestf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

/*
import java.util.regex.Pattern;
import java.util.regex.Matcher;
*/

import java.util.*;


import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utilities {

    /*****************************************************************************************************************/
    public static boolean build_run_feature_file(String testSuiteFile, String featureFileBaseDir, String runDirectory)
        throws Throwable
    {
        // NOTE: in the code below, an example from "Mkyong.com" is useful:
        // http://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/

        boolean localDebug = false;

        String cummulativeFFcontents = "";

        String runFeatureFile = runDirectory + "run.feature";

        try {

            // Get a list of a Feature Files
            List<File> file_list = get_feature_files(featureFileBaseDir);

            // Add the contents of each Feature to one cummulative String variable.
            for (File feature_file : file_list) {

                if (localDebug) {

                    System.out.println("Canonical Path: " + feature_file.getCanonicalPath());
                    System.out.println("Relative Path : " + feature_file.getPath());
                }

                // Read the entire file into a string using Apache Commons IO per:
                // http://abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
                cummulativeFFcontents += FileUtils.readFileToString(new File(feature_file.getPath()));
                // Add two (2) MSFT "newline" representations (with a leading whitespace) to ensure
                // separation between the last Scenario in one file from the Feature line in the next.
                // The leading whitespace also is there to ensure that we can isolate the last Scenario
                // in the last Feature file processed.
                cummulativeFFcontents += " \r\n \r\n";

            }

            if (localDebug) {

                System.out.println("START CUMMULATIVE FF CONTENTS: [");
                System.out.println(cummulativeFFcontents);
                System.out.println("] END CUMMULATIVE FF CONTENTS");
            }

            // Prepare to write out a new Run Feature File
            File fileRunDirectory = new File(runDirectory);
            File fileRunFeatureFile = new File(runFeatureFile);

            // Ensure that the Run Directory already exists.
            if (!(fileRunDirectory.isDirectory())) {

                String errorMessage = String.format("Run Directory: [%s] does NOT already exist", runDirectory);
                throw new Exception(errorMessage);

            }

            // Ensure that the Run Feature File does not already exist.
            if (fileRunFeatureFile.exists()) {

                String errorMessage = String.format("Run Feature File: [%s] ALREADY exists", runFeatureFile);
                throw new Exception(errorMessage);

            } else {

                // Create the Run Feature File
                fileRunFeatureFile.createNewFile();
            }


            FileWriter fw     = new FileWriter(fileRunFeatureFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            /******************** Start going through Suite File here *************/
            String fileContents = FileUtils.readFileToString(new File(testSuiteFile));

            JSONObject json_object = new JSONObject(fileContents);

            String suiteName           = json_object.getJSONObject("suite").getString("name");
            boolean suiteProduction    = json_object.getJSONObject("suite").getBoolean("production");
            boolean suiteStopOnFailure = json_object.getJSONObject("suite").getBoolean("stop_on_failure");
            String suiteVersion        = json_object.getJSONObject("suite").getString("version");

            if (localDebug) {

                System.out.println("Suite Name           : [" + suiteName + "]");
                System.out.println("Suite Production Flag: [" + Boolean.toString(suiteProduction) + "]");
                System.out.println("Suite Stop On Failure: [" + Boolean.toString(suiteStopOnFailure) + "]");
                System.out.println("Suite Version        : [" + suiteVersion + "]");

            }

            // Write Feature: line out into the Run Feature File
            bw.write("Feature: ");
            bw.write(suiteName);
            bw.write("\r\n");
            bw.write("\r\n");

            // Now, handle the Test Cases. Loop through them in the order presented in the JSON file.
            JSONArray testCase = json_object.getJSONArray("cases");

            for(int testCaseIndex = 0; testCaseIndex < testCase.length(); testCaseIndex++) {

                String testCaseName        = testCase.getJSONObject(testCaseIndex).getString("name");
                // To Extract the scenario, we need the "at sign" added.
                String testCaseNameWithTag = "@" + testCaseName;
                int testCaseCount      = testCase.getJSONObject(testCaseIndex).getInt("count");

                // Continue on to the next Test Case if the "count" is less than or equal to "0".
                if (testCaseCount <= 0) {

                    continue;
                }

                String [] scenario = extract_scenario(cummulativeFFcontents, testCaseNameWithTag);

                // Write out the desired number of copies of the extracted scenario.
                for(int repetitionCount = 0; repetitionCount < testCaseCount; repetitionCount++) {

                    for (int lineNumber = 0; lineNumber < scenario.length; lineNumber++) {

                        String myLine = scenario[lineNumber];
                        // Write into the Run Feature File
                        bw.write(myLine);
                        // Write MSFT "newline";
                        bw.write("\r\n");
                        //System.out.println(myLine);
                    }
                    // Write an additional MSFT "newline" after each Scenario.
                    bw.write("\r\n");
                }

            }
            bw.close();
            /******************** End going through Suite File here *************/

        } catch (Exception e) {

            e.printStackTrace();
            String errorMessage = String.format("Problem processing: [%s]", featureFileBaseDir);
            throw new Exception(errorMessage);
        }

        return true;
    }
    /*****************************************************************************************************************/
    public static boolean execute_run_feature_file(String runDirectory) throws Throwable
    {
        boolean localDebug = true;

        String runFeatureFile         = runDirectory + "run.feature";

        String cucumberPlugInValueOne = "junit:" + runDirectory + "run-output.xml";
        String cucumberPlugInValueTwo = "json:" + runDirectory + "run-output.json";

        if (localDebug) {

            System.out.println("Executing Feature File : [" + runFeatureFile + "]");
            System.out.println("Cucumber Plugin Value 1: [" + cucumberPlugInValueOne + "]");
            System.out.println("Cucumber Plugin Value 2: [" + cucumberPlugInValueTwo + "]");

        }

        CucumberCliApiWrapper.executeFeatureFile(runFeatureFile, cucumberPlugInValueOne, cucumberPlugInValueTwo);

        return true;
    }
    /*****************************************************************************************************************/
    public static boolean execute_test_cases(String testSuiteFile, String featureFileBaseDir) throws Throwable
    {

        boolean localDebug = false;

        byte testCaseReturnCode;

        if (localDebug) {

            System.out.println("Processing Test Suite: [" + testSuiteFile + "]");
        }

        try {

            // Read the entire file into a string using Apache Commons IO per:
            // http://abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
            String fileContents = FileUtils.readFileToString(new File(testSuiteFile));

            if (localDebug) {

                System.out.println("START TEST SUITE FILE CONTENTS: [");
                System.out.println(fileContents);
                System.out.println("] END TEST SUITE FILE CONTENTS");
            }

            // Now, we are going to convert the raw file contents to a JSONObject and ensure that there
            // is one (and only one) Gherkin "TAG" for each of the named Test Cases in the JSON file.
            JSONObject json_object = new JSONObject(fileContents);

            String suiteName        = json_object.getJSONObject("suite").getString("name");
            boolean suiteProduction = json_object.getJSONObject("suite").getBoolean("production");
            String suiteVersion     = json_object.getJSONObject("suite").getString("version");

            if (localDebug) {

                System.out.println("Suite Name           : [" + suiteName + "]");
                System.out.println("Suite Production Flag: [" + Boolean.toString(suiteProduction) + "]");
                System.out.println("Suite Version        : [" + suiteVersion + "]");

            }

            // Now, handle the Test Cases. Loop through them in the order presented in the JSON file.
            JSONArray testCase = json_object.getJSONArray("cases");

            for(int testCaseIndex = 0; testCaseIndex < testCase.length(); testCaseIndex++) {

                String testCaseName        = testCase.getJSONObject(testCaseIndex).getString("name");
                // The input HashMap still has the "at sign" in its keys
                String testCaseNameWithTag = "@" + testCaseName;
                int testCaseCount      = testCase.getJSONObject(testCaseIndex).getInt("count");

                if (localDebug) {

                    System.out.println("Test Case Name : [" + testCaseName + "]");
                    System.out.println("Test Case Count: [" + Integer.toString(testCaseCount) + "]");

                }

                testCaseReturnCode = CucumberCliApiWrapper.doit(testCaseNameWithTag, featureFileBaseDir);

                if (localDebug) {

                    System.out.println("TEST CASE EXIT STATUS: [" + Byte.toString(testCaseReturnCode) + "]");

                }

            }

        } catch (Throwable e) {

            e.printStackTrace();
            String errorMessage = String.format("Problem processing: [%s]", testSuiteFile);
            System.err.println(errorMessage);
        }

        return true;

    }
    /*****************************************************************************************************************/
    public static String[] extract_scenario(String cummulativeFFcontents, String gerkinTag) {

        boolean localDebug = false;

        boolean scenarioStartFound = false;
        boolean scenarioEndFound = false;

        int scenarioStartLine = 0;
        int scenarioEndLine   = 0;


        // Split the Cummulative Feature File contents into a bunch of lines using the MSFT
        // "newline" convention. If this ends up running on a UNIX system,
        // TODO: revisit EOL code.
        String [] line = cummulativeFFcontents.split("\r\n");

        for(int lineNumber = 0; lineNumber < line.length; lineNumber++) {

            String myLine = line[lineNumber];

            if (localDebug) {

                String message = String.format("LINE[%06d]:[%s]", lineNumber, myLine);
                System.out.println(message);
            }

            // TODO: The matching for Scenario Start will definitely have to be re-visited.
            // TODO: We may have to put in logic to scoop up any non-empy lines _before_ the tag that matched.
            if (!(scenarioStartFound)) {

                // The regexp below implies that the Start Of Scenario matching Gherkin tag should be
                // on a line of its own before the Scenario to execute.
                if (myLine.matches("^\\s*" + gerkinTag + "\\s*$")) {

                        if (localDebug) {

                            String message = String.format("SCENARIO START FOUND - LINE[%06d]", lineNumber);
                            System.out.println(message);
                        }

                        scenarioStartFound = true;
                        scenarioStartLine = lineNumber;
                }


            } else {

                // Zero or more whitespace.
                if (myLine.matches("^\\s*$")) {

                    if (localDebug) {

                        String message = String.format("SCENARIO END FOUND - LINE[%06d]", lineNumber);
                        System.out.println(message);
                    }

                    scenarioEndFound = true;
                    scenarioEndLine = lineNumber;

                    break;
                }
            }
        }

        // TODO: implement logic to scoop up non-empty lines that occur before scenarioStartLine.
        if (scenarioStartFound && scenarioEndFound) {

            String [] extractedScenario = Arrays.copyOfRange(line, scenarioStartLine, scenarioEndLine);
            return extractedScenario;

        } else {

            String message = String.format("SCENARIO NOT EXTRACTED FOR GHERKIN TAG: [%s]", gerkinTag);
            String [] scenarioNotExtracted = {message};
            return scenarioNotExtracted;
        }

    }
    /*****************************************************************************************************************/
    public static String get_datetime_string()
    {
        Date date  = new Date();
        // Using the date/time out to the millisecond.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss-SSS");

        String str2return = dateFormat.format(date);

        return str2return;
    }
    /*****************************************************************************************************************/
    public static HashMap<String, Integer> get_feature_file_tags(String featureFile) throws Exception
    {
        boolean localDebug = false;

        // Good link for Java HashMap: http://www.dotnetperls.com/hashmap
        HashMap<String, Integer> tagCount = new HashMap<String, Integer>();

        if (localDebug) {

            System.out.println("Processing: [" + featureFile + "]");
        }

        try {

            // Read the entire file into a string using Apache Commons IO per:
            // http://abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
            String fileContents = FileUtils.readFileToString(new File(featureFile));

            if (localDebug) {

                System.out.println("START FEATURE FILE CONTENTS: [");
                System.out.println(fileContents);
                System.out.println("] END FEATURE FILE CONTENTS");
            }

            // Split the Feature Into A Bunch Of Lines of "tokens", eliminating all "whitespace".
            String [] line = fileContents.split("\\s+");

            for(int lineNumber = 0; lineNumber < line.length; lineNumber++) {

                String myToken = line[lineNumber];

                if (localDebug) {

                    String message = String.format("LINE[%05d]:[%s]", lineNumber, myToken);
                    System.out.println(message);
                }

                if (myToken.matches("^@.*")) {

                    if (tagCount.containsKey(myToken)) {

                        // Since the Hash key already exists, store the incremented count.
                        tagCount.put(myToken, (tagCount.get(myToken) + 1));

                    } else {

                        // Since this is the first time we've seen this tag, store the count of 1.
                        tagCount.put(myToken, 1);
                    }

                    if (localDebug) {

                        String message = String.format("MATCH TOKEN:[%s] COUNT:[%d]", myToken, tagCount.get(myToken));
                        System.out.println(message);

                    }

                }
            }

        } catch (IOException e) {

            e.printStackTrace();
            String errorMessage = String.format("Problem processing: [%s]", featureFile);
            throw new Exception(errorMessage);
        }

        return tagCount;

    }
    /*****************************************************************************************************************/
    public static List<File> get_feature_files(String featureFileBaseDir) throws IOException {

        boolean localDebug = false;

        // Tip:
        // http://www.avajava.com/tutorials/lessons/how-do-i-get-all-files-with-certain-extensions-in-a-directory-including-subdirectories.html
        File dir = new File(featureFileBaseDir);
        String[] extensions = new String[] { "feature" };

        if (localDebug) {

            System.out.println("Getting all .feature files in " + dir.getCanonicalPath() + " and any subdirectories");
        }

        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

        if (localDebug) {

            for (File file : files) {

                System.out.println("file: " + file.getCanonicalPath());
            }
        }

        return files;

    }
    /*****************************************************************************************************************/
    public static HashMap<String, Integer> get_feature_files_tag_count(String featureFileBaseDir) throws Exception
    {
        boolean localDebug = false;

        // Good link for Java HashMap: http://www.dotnetperls.com/hashmap
        HashMap<String, Integer> tagCount = new HashMap<String, Integer>();

        HashMap<String, Integer> tempHashMap;

        // Get list of Feature Files
        // Foreach Feature File, return a HashMap consisting of each Gerkin "tag" used and a usage count
        // Merge the individual file HashMap with the "master" HashMap declared above.
        try {

            List<File> file_list = get_feature_files(featureFileBaseDir);

            for (File feature_file : file_list) {

                if (localDebug) {

                    System.out.println("Canonical Path: " + feature_file.getCanonicalPath());
                    System.out.println("Relative Path : " + feature_file.getPath());
                }

                tempHashMap = get_feature_file_tags(feature_file.getPath());

                // Get keys of the tempHashMap.
                Set<String> keys = tempHashMap.keySet();

                // Now, loop over keys.
                for (String key : keys) {

                    // See if "tagCount" already contains the key in question.
                    if (tagCount.containsKey(key)) {

                        int totalCount = tempHashMap.get(key) + tagCount.get(key);

                        // Now, store total back into "tagCount".
                        tagCount.put(key, totalCount);

                        if (localDebug) {

                            String msg = String.format("TAG COUNT KEY: [%s] VALUE: [%d]", key, totalCount);
                            System.out.println(msg);
                        }

                    } else {

                        // Since the key didn't previously exist in "tagCount", store the new key and value.
                        tagCount.put(key, tempHashMap.get(key));

                        if (localDebug) {

                            String msg = String.format("TAG COUNT KEY: [%s] VALUE: [%d]", key, tempHashMap.get(key));
                            System.out.println(msg);
                        }

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            String errorMessage = String.format("Problem processing: [%s]", featureFileBaseDir);
            throw new Exception(errorMessage);
        }

        return(tagCount);

    }
    /*****************************************************************************************************************/
    public static String get_random_string()
    {
        Date date  = new Date();
        Random rng = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss-SSS");

        // Concatenate the formatted data/time (out to the millisecond) with a
        // pseudo-random number. This forms a "random enough" string.

        String str2return = dateFormat.format(date) + Integer.toString(rng.nextInt());

        return str2return;
    }
    /*****************************************************************************************************************/
    public static boolean test_suite_ok_to_execute(String testSuiteFile, HashMap<String, Integer> tagCount) {

        boolean localDebug = false;

        int testCaseErrorCounter = 0;

        if (localDebug) {

            System.out.println("Processing Test Suite: [" + testSuiteFile + "]");
        }

        try {

            // Read the entire file into a string using Apache Commons IO per:
            // http://abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
            String fileContents = FileUtils.readFileToString(new File(testSuiteFile));

            if (localDebug) {

                System.out.println("START TEST SUITE FILE CONTENTS: [");
                System.out.println(fileContents);
                System.out.println("] END TEST SUITE FILE CONTENTS");
            }

            // Now, we are going to convert the raw file contents to a JSONObject and ensure that there
            // is one (and only one) Gherkin "TAG" for each of the named Test Cases in the JSON file.
            JSONObject json_object = new JSONObject(fileContents);

            String suiteName        = json_object.getJSONObject("suite").getString("name");
            boolean suiteProduction = json_object.getJSONObject("suite").getBoolean("production");
            String suiteVersion     = json_object.getJSONObject("suite").getString("version");

            if (localDebug) {

                System.out.println("Suite Name           : [" + suiteName + "]");
                System.out.println("Suite Production Flag: [" + Boolean.toString(suiteProduction) + "]");
                System.out.println("Suite Version        : [" + suiteVersion + "]");

            }

            // Now, handle the Test Cases. Loop through them in the order presented in the JSON file.
            JSONArray testCase = json_object.getJSONArray("cases");

            for(int testCaseIndex = 0; testCaseIndex < testCase.length(); testCaseIndex++) {

                String testCaseName        = testCase.getJSONObject(testCaseIndex).getString("name");
                // The input HashMap still has the "at sign" in its keys
                String testCaseNameWithTag = "@" + testCaseName;
                int testCaseCount          = testCase.getJSONObject(testCaseIndex).getInt("count");

                if (localDebug) {

                    System.out.println("Test Case Name      : [" + testCaseName + "]");
                    System.out.println("Test Case Loop Count: [" + Integer.toString(testCaseCount) + "]");

                }

                // See if a Gherkin Feature Tag Exists For This Test Case
                if (tagCount.containsKey(testCaseNameWithTag)) {

                    int occurrenceCount = tagCount.get(testCaseNameWithTag);

                    // For a Test Case, we are insisting that a unique TAG be used to specify it.
                    if (occurrenceCount != 1) {

                        String errorMessage = String.format("Found: [%d] Gherkin TAGs For: [%s]", occurrenceCount, testCaseName);
                        System.err.println(errorMessage);
                        testCaseErrorCounter += 1;

                    }

                } else {

                    String errorMessage = String.format("No Gherkin TAG Found For: [%s]", testCaseName);
                    System.err.println(errorMessage);
                    testCaseErrorCounter += 1;

                }
            }

            if (testCaseErrorCounter == 0) {

                return true;

            } else {

                return false;

            }

        } catch (Exception e) {

            e.printStackTrace();
            String errorMessage = String.format("Problem processing: [%s]", testSuiteFile);
            System.err.println(errorMessage);
        }

        return false;
    }
    /*****************************************************************************************************************/
    public static void main(String [ ] args)
    {
        String rs = get_random_string();

        System.out.println(rs);

    }
    /*****************************************************************************************************************/
}


