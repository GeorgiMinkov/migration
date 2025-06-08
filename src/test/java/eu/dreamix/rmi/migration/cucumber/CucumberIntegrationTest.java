package eu.dreamix.rmi.migration.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Integration test runner for Cucumber tests.
 * This will find and run all feature files in the classpath.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    glue = {
        "eu.dreamix.rmi.migration.cucumber.steps",
        "eu.dreamix.rmi.migration.cucumber.config"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-pretty",
        "json:target/cucumber-reports/CucumberTestReport.json"
    }
)
public class CucumberIntegrationTest {
    // This class serves as an entry point for Cucumber tests
}
