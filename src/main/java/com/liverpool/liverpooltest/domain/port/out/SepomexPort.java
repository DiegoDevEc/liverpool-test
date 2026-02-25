package com.liverpool.liverpooltest.domain.port.out;

import com.liverpool.liverpooltest.domain.model.Address;

public interface SepomexPort {
    Address getAddressByPostalCode(String postalCode);
}
