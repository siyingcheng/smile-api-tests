package com.smile.apiobjects.address;

public record AddressDTO(
        Integer id,
        String fullAddress,
        String phone,
        Boolean isDefault
) {
}
