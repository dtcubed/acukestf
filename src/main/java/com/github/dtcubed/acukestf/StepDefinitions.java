package com.github.dtcubed.acukestf;

import cucumber.api.java.en.Given;

/*
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
*/

public class StepDefinitions {


    /******************************************************************************************************************/
    @Given("^gherkin pass step$")
    public void gherkin_pass_step() throws Throwable {

        return;

    }
    /******************************************************************************************************************/
    @Given("^gherkin fail step$")
    public void gherkin_fail_step() throws Throwable {

        throw new Exception("Gherkin fail step");

    }
    /******************************************************************************************************************/
}



