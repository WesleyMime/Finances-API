package br.com.finances.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.security.Principal;
import java.util.Collections;

@Configuration
public class CacheConfig {

	Logger logger = LoggerFactory.getLogger(CacheConfig.class);

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
				.transactionAware()
				.withInitialCacheConfigurations(Collections.singletonMap("predefined",
						RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()))
				.build();
	}

	@CacheEvict(cacheNames = {"summary-last-year", "monthOverMonthComparisonTakeaway", "financialBalanceTakeaway",
			"spendingByCategoryLastMonthTakeaway", "spendingByCategoryYearTakeaway", "savingsTakeaway", "getAll"},
			key = "#principal.name")
	public void evictClientCache(Principal principal) {
		logger.info("Evicting cache for user: {}", principal.getName());
	}
}
