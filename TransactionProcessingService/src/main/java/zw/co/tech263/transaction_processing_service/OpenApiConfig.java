package zw.co.tech263.transaction_processing_service;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI(){
        return new OpenAPI()
                .info(new Info().title("Banking transactions service").version("0.0.1").description("Banking transaction related activity for Equals Banking"));
    }

}
