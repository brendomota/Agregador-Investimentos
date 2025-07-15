package br.unesp.rc.agregadorinvestimentos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unesp.rc.agregadorinvestimentos.entity.BillingAddress;

@Repository
public interface BillingAddressRepository extends JpaRepository<BillingAddress, UUID> {
    
}
