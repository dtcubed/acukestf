package com.github.dtcubed.acukestf;

import cucumber.api.cli.Main;

public class CucumberCliApiWrapper {

    /*****************************************************************************************************************/
    public static void doit(String tagsValue, String featureDirectory) throws Throwable {


        boolean localDebug = true;

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

        String cukeArgv[] = {
                "--glue", glueSwitchValue,
                "--plugin", "json",
                "--tags", tagsValue,
                "--monochrome",
                featureDirectory};

        Main.main(cukeArgv);

    }
    /*****************************************************************************************************************/

}
