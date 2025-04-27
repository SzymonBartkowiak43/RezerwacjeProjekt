package com.example.systemrezerwacji.features.redis;

import com.example.systemrezerwacji.BaseIntegrationTest;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


public class RedisSalonIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS;

    @Autowired
    CacheManager cacheManager;

    @SpyBean
    SalonFacade salonFacade;

    static {
        REDIS = new GenericContainer<>("redis").withExposedPorts(6379);
        REDIS.start();
    }

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS::getHost);
        registry.add("spring.redis.port", () -> REDIS.getFirstMappedPort().toString());
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.cache.redis.time-to-live", () -> "PT1S");
    }


//    @Test
//    public void should_save_salons_to_cache_and_then_invalidate_by_time_to_live() throws Exception {
//        // step 1: should save salons to the cache
//        // given && when
//        mockMvc.perform(get("/salons")
//                .contentType(MediaType.APPLICATION_JSON_VALUE));
//
//        // then
//        verify(salonFacade, times(1)).getAllSalons();
//        assertThat(cacheManager.getCacheNames().contains("salons")).isTrue();
//
//        // step 2: cache should be invalidated
//        await()
//                .atMost(Duration.ofSeconds(4))
//                .pollInterval(Duration.ofSeconds(1))
//                .untilAsserted(() -> {
//                            mockMvc.perform(get("/salons")
//                                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                            );
//                            verify(salonFacade, atLeast(2)).getAllSalons();
//                        }
//                );
//    }


}
