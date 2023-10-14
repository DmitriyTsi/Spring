package com.tsyhanok.restapi.repository;

import com.tsyhanok.restapi.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserRepository extends CrudRepository<User, UUID> {
}
