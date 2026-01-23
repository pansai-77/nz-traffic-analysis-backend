package io.github.pansai.traffic.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    public GroupedOpenApi userInfoApi(){
        return GroupedOpenApi.builder()
                .group("userInfo")
                .pathsToMatch("/api/userInfo")
                .build();
   }
}
