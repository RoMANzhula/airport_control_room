package org.romanzhula.plane.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import utils.helpers.MessageConverter;

@Configuration
public class MessageConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new MessageConverter();
    }

}
