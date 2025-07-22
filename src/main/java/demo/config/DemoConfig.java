package demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration that builds a {@link ChatClient} for DeepSeek-R1.
 * DeepSeek-R1 提供 OpenAI 兼容的接口，故直接复用 {@link DeepSeekChatModel}。
 * <p>
 * 配置项示例：
 * deepseek:
 * base-url: https://api.deepseek.com/v1
 * api-key:  YOUR_KEY
 * model:    deepseek-chat-r1
 */
@Configuration
public class DemoConfig {

    @Bean
    public ChatClient deepSeekChatClient(@Value("${spring.ai.deepseek.api-key}") String apiKey,
                                         @Value("${spring.ai.deepseek.base-url}") String baseUrl,
                                         @Value("${spring.ai.deepseek.model:deepseek-chat-r1}") String model) {

        return new DefaultChatClientBuilder(DeepSeekChatModel.builder()
                .deepSeekApi(DeepSeekApi.builder()
                        .apiKey(new SimpleApiKey(apiKey))
                        .build())
                .defaultOptions(DeepSeekChatOptions.builder()
                        .model(model)
                        .build())
                .build(), ObservationRegistry.NOOP, null)
                .build();
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 设置value序列化方式
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }


}
