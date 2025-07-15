package br.unesp.rc.agregadorinvestimentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.unesp.rc.agregadorinvestimentos.entity.AccountStock;
import br.unesp.rc.agregadorinvestimentos.entity.AccountStockId;

@Repository 
public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {
    
}
