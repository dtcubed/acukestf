package com.github.dtcubed.acukestf;

import cucumber.api.cli.Main;

public class CucumberCliApiWrapper {

    /*****************************************************************************************************************/
    public static byte doit(String tagsValue, String featureDirectory) throws Throwable {


        boolean localDebug = false;

        ACukesTFPropertiesSingleton acukestfps = ACukesTFPropertiesSingleton.getInstance();

        String glueSwitchValue = acukestfps.getPropertyValue("CUCUMBER_CLI_API_GLUE_SWITCH_VALUE");

        if (glueSwitchValue == null) {

            // If the value hasn't been specified in the correct .properties file, set the
            // "glue" switch to point to a "reasonable" place.
            glueSwitchValue = "classpath:com.github.dtcubed.acukestf";
        }

        if (localDebug) {

            System.out.println("Feature Directory:[" + featureDirectory + "]");
            System.out.println("Glue Switch Value:[" + glueSwitchValue + "]");
            System.out.println("Tags Switch Value:[" + tagsValue + "]");

        }

        /*
        Example Argv for the Cucumber API CLI

        String cukeArgv[] = {
                "--glue", "classpath:com.github.dtcubed.acukestf",
                "--plugin", "json",
                "--tags", "@TEST-CASE-007",
                "--monochrome",
                "support/features"};
        */

        /* TODO: more work in this area.
           For both the json and junit plugin, there seems to be the option to use an argument like this:
           junit:run/runid/output.xml       or     json:run/runid/output.json
         */
        String cukeArgv[] = {
                "--glue", glueSwitchValue,
                "--plugin", "json",
                "--tags", tagsValue,
                "--monochrome",
                featureDirectory};


        //Main.main(cukeArgv);
        // Use the Main.run() method since it doesn't exit.
        // I got the second argument from the de-compiled Class file, Main.class.

        byte exitstatus = Main.run(cukeArgv, Thread.currentThread().getContextClassLoader());

        return exitstatus;

    }
    /*****************************************************************************************************************/
    /*****************************************************************************************************************/
    public static byte executeFeatureFile(String featureFile, String pluginValue, String pluginValueTwo)
            throws Throwable {

        boolean localDebug = false;

        ACukesTFPropertiesSingleton acukestfps = ACukesTFPropertiesSingleton.getInstance();

        String glueSwitchValue = acukestfps.getPropertyValue("CUCUMBER_CLI_API_GLUE_SWITCH_VALUE");

        if (glueSwitchValue == null) {

            // If the value hasn't been specified in the correct .properties file, set the
            // "glue" switch to point to a "reasonable" place.
            glueSwitchValue = "classpath:com.github.dtcubed.acukestf";
        }

        if (localDebug) {

            System.out.println("Feature File     :[" + featureFile + "]");
            System.out.println("Glue Switch Value:[" + glueSwitchValue + "]");
            System.out.println("Plugin Value     :[" + pluginValue + "]");

        }

        /*
        Example Argv for the Cucumber API CLI

        String cukeArgv[] = {
                "--glue", "classpath:com.github.dtcubed.acukestf",
                "--plugin", "json",
                "--tags", "@TEST-CASE-007",
                "--monochrome",
                "support/features"};
        */

        /* TODO: more work in this area.
           For both the json and junit plugin, there seems to be the option to use an argument like this:
           junit:run/runid/output.xml       or     json:run/runid/output.json
         */
        String cukeArgv[] = {
                "--glue", glueSwitchValue,
                "--plugin", pluginValue,
                "--plugin", pluginValueTwo,
                "--monochrome",
                featureFile};


        //Main.main(cukeArgv);
        // Use the Main.run() method since it doesn't exit.
        // I got the second argument from the de-compiled Class file, Main.class.

        byte exitstatus = Main.run(cukeArgv, Thread.currentThread().getContextClassLoader());

        return exitstatus;

    }
    /*****************************************************************************************************************/

}
