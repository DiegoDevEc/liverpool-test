package com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex;

import com.liverpool.liverpooltest.domain.exception.PostalCodeNotFoundException;
import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto.CopomexInfo;
import com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto.CopomexResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CopomexAdapterTest {

    @Mock
    private CopomexClient copomexClient;

    @InjectMocks
    private CopomexAdapter copomexAdapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(copomexAdapter, "token", "test-token");
    }

    @Test
    void getAddressByPostalCode_shouldReturnAddress_whenValidPostalCode() {
        CopomexInfo info = buildInfo("Juárez", "Cuauhtémoc", "Ciudad de México", "Ciudad de México", "México");
        CopomexResponse response = buildResponse(false, info);

        when(copomexClient.getInfoByCp("06600", "test-token")).thenReturn(List.of(response));

        Address address = copomexAdapter.getAddressByPostalCode("06600");

        assertThat(address.getPostalCode()).isEqualTo("06600");
        assertThat(address.getMunicipality()).isEqualTo("Cuauhtémoc");
        assertThat(address.getState()).isEqualTo("Ciudad de México");
        assertThat(address.getCountry()).isEqualTo("México");
        assertThat(address.getNeighborhoods()).containsExactly("Juárez");
    }

    @Test
    void getAddressByPostalCode_shouldCollectMultipleNeighborhoods() {
        CopomexInfo info1 = buildInfo("Juárez", "Cuauhtémoc", "Ciudad de México", "Ciudad de México", "México");
        CopomexInfo info2 = buildInfo("Roma Norte", "Cuauhtémoc", "Ciudad de México", "Ciudad de México", "México");
        CopomexResponse response1 = buildResponse(false, info1);
        CopomexResponse response2 = buildResponse(false, info2);

        when(copomexClient.getInfoByCp("06600", "test-token")).thenReturn(List.of(response1, response2));

        Address address = copomexAdapter.getAddressByPostalCode("06600");

        assertThat(address.getNeighborhoods()).containsExactlyInAnyOrder("Juárez", "Roma Norte");
    }

    @Test
    void getAddressByPostalCode_shouldThrowPostalCodeNotFoundException_whenResponseIsEmpty() {
        when(copomexClient.getInfoByCp("00000", "test-token")).thenReturn(List.of());

        assertThatThrownBy(() -> copomexAdapter.getAddressByPostalCode("00000"))
                .isInstanceOf(PostalCodeNotFoundException.class)
                .hasMessageContaining("00000");
    }

    @Test
    void getAddressByPostalCode_shouldThrowPostalCodeNotFoundException_whenResponseHasError() {
        CopomexResponse errorResponse = buildResponse(true, null);

        when(copomexClient.getInfoByCp("99999", "test-token")).thenReturn(List.of(errorResponse));

        assertThatThrownBy(() -> copomexAdapter.getAddressByPostalCode("99999"))
                .isInstanceOf(PostalCodeNotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    void getAddressByPostalCode_shouldThrowPostalCodeNotFoundException_whenClientThrowsException() {
        when(copomexClient.getInfoByCp("06600", "test-token"))
                .thenThrow(new RuntimeException("Connection refused"));

        assertThatThrownBy(() -> copomexAdapter.getAddressByPostalCode("06600"))
                .isInstanceOf(PostalCodeNotFoundException.class)
                .hasMessageContaining("06600");
    }

    private CopomexInfo buildInfo(String settlement, String municipality, String city, String state, String country) {
        CopomexInfo info = mock(CopomexInfo.class);
        lenient().when(info.getSettlement()).thenReturn(settlement);
        lenient().when(info.getMunicipality()).thenReturn(municipality);
        lenient().when(info.getCity()).thenReturn(city);
        lenient().when(info.getState()).thenReturn(state);
        lenient().when(info.getCountry()).thenReturn(country);
        return info;
    }

    private CopomexResponse buildResponse(boolean error, CopomexInfo info) {
        CopomexResponse response = mock(CopomexResponse.class);
        when(response.isError()).thenReturn(error);
        if (!error) {
            when(response.getResponse()).thenReturn(info);
        }
        return response;
    }
}
