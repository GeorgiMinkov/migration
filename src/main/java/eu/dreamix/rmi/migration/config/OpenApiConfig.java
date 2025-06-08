package eu.dreamix.rmi.migration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderMigrationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("REST API for the Order Service - Migrated from RMI")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Dreamix Team")
                                .email("support@dreamix.eu"))
                        .license(new License()
                                .name("Internal Use Only")
                                .url("https://dreamix.eu/licenses/internal")));
    }
}
