package br.com.finances.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CacheEvictionService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final Logger logger = LoggerFactory.getLogger(CacheEvictionService.class);

	public CacheEvictionService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void evictCacheKeysForUser(String user) {
		// Cache that should change after any update:
		List<String> cacheList = List.of("summary-last-month", "summary-last-year", "getAllIncome", "getAllExpenses");
		for (String cache : cacheList) {
			String pattern = cache + "::" + user + "*";
			Set<String> keysToDelete = scanKeys(pattern);
			if (!keysToDelete.isEmpty()) {
				redisTemplate.delete(keysToDelete);
				logger.info("Evicting cache {} for user {}", cache, user);
			}
		}
	}

	private Set<String> scanKeys(String pattern) {
		Set<String> keys = new HashSet<>();
		var connection = redisTemplate.getConnectionFactory().getConnection();
		try (var cursor = connection.keyCommands().scan(ScanOptions.scanOptions().match(pattern).build())) {
			while (cursor.hasNext()) {
				keys.add(new String(cursor.next()));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error scanning Redis keys", e);
		}
		return keys;
	}
}