package com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.repository;

import com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<UserDocument, String> {
}
