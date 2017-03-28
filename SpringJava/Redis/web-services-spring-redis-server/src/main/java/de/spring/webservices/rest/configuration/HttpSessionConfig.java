package de.spring.webservices.rest.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableRedisHttpSession 
public class HttpSessionConfig {
	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.password}")
	private String password;
	
	@Value("${spring.redis.port}")
	private int port;
	
    private ClassLoader loader;

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
    	LettuceConnectionFactory config = new LettuceConnectionFactory(hostName, port);
    	config.setPassword(password);
    	
    	return config;
    }

    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return mapper;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }


}