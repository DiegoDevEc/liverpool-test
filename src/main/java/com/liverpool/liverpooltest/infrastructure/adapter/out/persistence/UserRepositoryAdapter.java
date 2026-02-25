package com.liverpool.liverpooltest.infrastructure.adapter.out.persistence;

import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.model.User;
import com.liverpool.liverpooltest.domain.port.out.UserRepositoryPort;
import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.document.AddressDocument;
import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.document.UserDocument;
import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.repository.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final MongoUserRepository mongoUserRepository;

    @Override
    public User save(User user) {
        return toDomain(mongoUserRepository.save(toDocument(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return mongoUserRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(String id) {
        mongoUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoUserRepository.existsById(id);
    }

    private UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .paternalLastName(user.getPaternalLastName())
                .maternalLastName(user.getMaternalLastName())
                .email(user.getEmail())
                .address(Optional.ofNullable(user.getAddress()).map(toAddressDocument()).orElse(null))
                .build();
    }

    private User toDomain(UserDocument doc) {
        return User.builder()
                .id(doc.getId())
                .name(doc.getName())
                .paternalLastName(doc.getPaternalLastName())
                .maternalLastName(doc.getMaternalLastName())
                .email(doc.getEmail())
                .address(Optional.ofNullable(doc.getAddress()).map(toAddressDomain()).orElse(null))
                .build();
    }

    private Function<Address, AddressDocument> toAddressDocument() {
        return addr -> AddressDocument.builder()
                .postalCode(addr.getPostalCode())
                .municipality(addr.getMunicipality())
                .state(addr.getState())
                .city(addr.getCity())
                .neighborhoods(addr.getNeighborhoods())
                .country(addr.getCountry())
                .build();
    }

    private Function<AddressDocument, Address> toAddressDomain() {
        return addr -> Address.builder()
                .postalCode(addr.getPostalCode())
                .municipality(addr.getMunicipality())
                .state(addr.getState())
                .city(addr.getCity())
                .neighborhoods(addr.getNeighborhoods())
                .country(addr.getCountry())
                .build();
    }
}
