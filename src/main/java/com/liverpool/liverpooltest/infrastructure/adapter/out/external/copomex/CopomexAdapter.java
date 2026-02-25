package com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex;

import com.liverpool.liverpooltest.domain.exception.PostalCodeNotFoundException;
import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.port.out.SepomexPort;
import com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto.CopomexInfo;
import com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto.CopomexResponse;
import com.liverpool.liverpooltest.infrastructure.config.CacheConfig;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CopomexAdapter implements SepomexPort {

    private final CopomexClient copomexClient;

    @Value("${copomex.api.token}")
    private String token;

    @Override
    @Cacheable(value = CacheConfig.ADDRESSES_CACHE, key = "#postalCode")
    public Address getAddressByPostalCode(String postalCode) {
        log.info("Cache miss — calling COPOMEX for postal code: {}", postalCode);
        List<CopomexResponse> responses;
        try {
            responses = copomexClient.getInfoByCp(postalCode, token);
        } catch (FeignException e) {
            log.error("COPOMEX HTTP error for postal code {} - status: {}, message: {}", postalCode, e.status(), e.getMessage());
            throw new PostalCodeNotFoundException(postalCode);
        } catch (Exception e) {
            log.error("Network error calling COPOMEX for postal code {}: {}", postalCode, e.getMessage(), e);
            throw new PostalCodeNotFoundException(postalCode);
        }

        if (responses == null || responses.isEmpty() || responses.get(0).isError()) {
            throw new PostalCodeNotFoundException(postalCode);
        }

        CopomexInfo first = responses.get(0).getResponse();
        List<String> neighborhoods = responses.stream()
                .filter(r -> !r.isError() && r.getResponse() != null)
                .map(r -> r.getResponse().getSettlement())
                .filter(s -> s != null && !s.isBlank())
                .toList();

        return Address.builder()
                .postalCode(postalCode)
                .municipality(first.getMunicipality())
                .state(first.getState())
                .city(first.getCity())
                .neighborhoods(neighborhoods)
                .country(first.getCountry())
                .build();
    }
}
