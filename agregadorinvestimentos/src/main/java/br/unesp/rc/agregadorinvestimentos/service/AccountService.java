package br.unesp.rc.agregadorinvestimentos.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.unesp.rc.agregadorinvestimentos.controller.dto.AccountStockDTO;
import br.unesp.rc.agregadorinvestimentos.entity.AccountStock;
import br.unesp.rc.agregadorinvestimentos.entity.AccountStockId;
import br.unesp.rc.agregadorinvestimentos.repository.AccountRepository;
import br.unesp.rc.agregadorinvestimentos.repository.AccountStockRepository;
import br.unesp.rc.agregadorinvestimentos.repository.StockRepository;

@Service
public class AccountService {
    
    private AccountRepository accountRepository;
    private StockRepository stockRepository;
    private AccountStockRepository accountStockRepository;

    public AccountService(AccountRepository accountRepository, StockRepository stockRepository, AccountStockRepository accountStockRepository) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.accountStockRepository = accountStockRepository;
    }

    public void associateStock(String accountId, AccountStockDTO dto) {

        var account = accountRepository.findById(UUID.fromString(accountId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var stock = stockRepository.findById(dto.stockId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var id = new AccountStockId(account.getAccountId(), stock.getStockId());

        var entity = new AccountStock(
            id,
            account,
            stock,
            dto.quantity()
        );

        accountStockRepository.save(entity);
    }

    
}
