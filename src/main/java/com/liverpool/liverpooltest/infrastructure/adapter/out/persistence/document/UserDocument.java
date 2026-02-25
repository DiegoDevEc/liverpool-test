package com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @Id
    private String id;
    private String name;
    private String paternalLastName;
    private String maternalLastName;
    @Indexed(unique = true)
    private String email;
    private AddressDocument address;
}
