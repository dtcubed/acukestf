package com.github.dtcubed.acukestf;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Utilities {

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
        boolean localDebug = true;

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
