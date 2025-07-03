package br.unesp.rc.agregadorinvestimentos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unesp.rc.agregadorinvestimentos.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {    
}
