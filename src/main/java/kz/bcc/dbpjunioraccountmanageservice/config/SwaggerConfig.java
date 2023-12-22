package kz.bcc.dbpjunioraccountmanageservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
/*@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)*/
@OpenAPIDefinition(
        info = @Info(title = "JB ACCOUNT API", version = "v1"),
        //security = @SecurityRequirement(name = "bearerAuth"),
        servers = {
                @Server(url = "http://localhost:8005",
                        description = "Local"),

                @Server(url = "dev"),

                @Server(url = "prod")}
)

public class SwaggerConfig {

}
