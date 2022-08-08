package ru.gsa.biointerface.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ContactAppConfig {

    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)),
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)),
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .serializationInclusion(JsonInclude.Include.NON_NULL);

        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
        module.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module);
    }
}
