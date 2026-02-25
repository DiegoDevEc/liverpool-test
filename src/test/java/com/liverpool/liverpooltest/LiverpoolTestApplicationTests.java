package com.liverpool.liverpooltest;

import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.repository.MongoUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cache.type=none",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration," +
                "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration," +
                "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration," +
                "org.springframework.boot.data.redis.autoconfigure.RedisAutoConfiguration," +
                "org.springframework.boot.data.redis.autoconfigure.RedisRepositoriesAutoConfiguration"
})
class LiverpoolTestApplicationTests {

    @MockitoBean
    private MongoUserRepository mongoUserRepository;

    @Test
    void contextLoads() {
    }

}
