package com.github.dtcubed.acukestf;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;

//import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;


public class Utilities {


    /*****************************************************************************************************************/
    public static void get_feature_file_tags_ORIG(String featureFile)
    {
        boolean localDebug = true;

        if (localDebug) {

            System.out.println("Processing: [" + featureFile + "]");
        }



        // Use SPLIT

        //String regexpTag   = "^.*\\(@\\S+\\)\\s+.*$";

        // String regexpTag   = ".*(TEST-CASE-00\\d+).*";

        // Pattern patternTag = Pattern.compile(regexpTag, Pattern.DOTALL);


        String regexpTag   = "^@\\(\\S+\\)\\$";

        // String regexpTag   = ".*(TEST-CASE-00\\d+).*";

        Pattern patternTag = Pattern.compile(regexpTag);

        try {

            // Read the entire file into a string using Apache Commons IO per:
            // http://abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
            String fileContents = FileUtils.readFileToString(new File(featureFile));

            if (localDebug) {

                System.out.println("START: [");

                System.out.println(fileContents);

                System.out.println("] ************************************ END");

            }

            // String [] line = fileContents.split("@");

            String [] line = fileContents.split("\\s+");

            System.out.println("============================= START ===========================");
            for(int i = 0; i < line.length; i++) {

                String myLine = line[i];

                System.out.println("LINE: [" + myLine + "]");

                if (myLine.matches("^@.*")) {

                    System.out.println("STRING MATCHES: [" + myLine + "]");

                }

                Matcher matcherTag = patternTag.matcher(myLine);

                if (matcherTag.find()) {

                    String tag = matcherTag.group(1);

                    System.out.println("MATCHING TAG MATCHER: [" + tag + "]");

                }

            }
            System.out.println("============================= END ===========================");


               /*
             System.out.println("Line 0");

             System.out.println(line[0]);

             System.out.println("Line 1");

             System.out.println(line[1]);

             Matcher matcherTag = patternTag.matcher(fileContents);

             while (matcherTag.find()) {

                 //String tag = matcherTag.group(1);

                 String tag = matcherTag.group(1);

                 System.out.println("MATCHING TAG: [" + tag + "]");
             }
             */


        } catch (IOException e) {

            e.printStackTrace();
        }

    }
    /*****************************************************************************************************************/
    public static HashMap<String, Integer> get_feature_file_tags(String featureFile) throws Exception
    {
        boolean localDebug = true;

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
    public static HashMap<String, Integer> get_feature_files_tag_count(String featureFileBaseDir)
    {
        boolean localDebug = true;

        // Good link for Java HashMap: http://www.dotnetperls.com/hashmap
        HashMap<String, Integer> tagCount = new HashMap<String, Integer>();

        // Get list of Feature Files
        // Foreach Feature File, return a HashMap consisting of each Gerkin "tag" used and a usage count
        // Merge the individual file HashMap with the "master" HashMap declared above.

        try {

            List<File> file_list = get_feature_files(featureFileBaseDir);

            for (File feature_file : file_list) {

                if (localDebug) {

                    // System.out.println("Canonical Path: " + feature_file.getCanonicalPath());
                    System.out.println("Relative Path : " + feature_file.getPath());
                }
                get_feature_file_tags(feature_file.getPath());

            }

        } catch (Exception e) {

            System.err.println(e.getMessage());
        }


        tagCount.put("TEST-CASE-001", 1);
        tagCount.put("TEST-CASE-002", 2);

        return(tagCount);

    }
    /*****************************************************************************************************************/
    public static String get_random_string()
    {
        Date date  = new Date();
        Random rng = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss-SSS-");

        // Concatenate the formatted data/time (out to the millisecond) with a
        // pseudo-random number. This forms a "random enough" string.

        String str2return = dateFormat.format(date) + Integer.toString(rng.nextInt());

        return str2return;
    }
    /*****************************************************************************************************************/
    public static void main(String [ ] args)
    {
        String rs = get_random_string();

        System.out.println(rs);

    }
}
