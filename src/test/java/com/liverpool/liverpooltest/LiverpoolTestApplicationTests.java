package com.liverpool.liverpooltest;

import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.repository.MongoUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cache.type=simple",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration," +
                "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration," +
                "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration"
})
class LiverpoolTestApplicationTests {

    @TestConfiguration
    static class TestCacheConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @MockitoBean
    private MongoUserRepository mongoUserRepository;

    @Test
    void contextLoads() {
    }

}
