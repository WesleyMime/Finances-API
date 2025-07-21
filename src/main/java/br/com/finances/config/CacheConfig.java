package br.com.finances.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Configuration
public class CacheConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		LocalDate now = LocalDate.now();
		int dayOfMonth = now.getDayOfMonth();
		int lengthOfMonth = now.lengthOfMonth();
		int daysToEndOfMonth = dayOfMonth - lengthOfMonth + 1; // To not expire on last day and to not be zero.

		RedisCacheConfiguration endOfMonthTtlExpirationDefaults =
				RedisCacheConfiguration.defaultCacheConfig()
						.entryTtl(Duration.ofDays(daysToEndOfMonth))
						.disableCachingNullValues();
		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(endOfMonthTtlExpirationDefaults)
				.build();
	}

	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
