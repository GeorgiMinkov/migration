package eu.dreamix.rmi.migration.cucumber.config;

import eu.dreamix.rmi.migration.MigrationApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring configuration for Cucumber tests.
 * This class connects Cucumber with the Spring test context.
 */
@CucumberContextConfiguration
@SpringBootTest(
    classes = MigrationApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CucumberSpringConfiguration {
    // No additional code needed - this class serves as a configuration holder
}
