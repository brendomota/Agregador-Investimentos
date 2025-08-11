package br.unesp.rc.agregadorinvestimentos.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.unesp.rc.agregadorinvestimentos.client.BrapiClient;
import br.unesp.rc.agregadorinvestimentos.controller.dto.AccountStockDTO;
import br.unesp.rc.agregadorinvestimentos.controller.dto.AccountStockResponseDTO;
import br.unesp.rc.agregadorinvestimentos.entity.AccountStock;
import br.unesp.rc.agregadorinvestimentos.entity.AccountStockId;
import br.unesp.rc.agregadorinvestimentos.repository.AccountRepository;
import br.unesp.rc.agregadorinvestimentos.repository.AccountStockRepository;
import br.unesp.rc.agregadorinvestimentos.repository.StockRepository;

@Service
public class AccountService {

    @Value("#{environment.TOKEN}")
    private String TOKEN;

    private AccountRepository accountRepository;
    private StockRepository stockRepository;
    private AccountStockRepository accountStockRepository;
    private BrapiClient brapiClient;

    public AccountService(AccountRepository accountRepository, StockRepository stockRepository, AccountStockRepository accountStockRepository, BrapiClient brapiClient) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.accountStockRepository = accountStockRepository;
        this.brapiClient = brapiClient;
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

    public List<AccountStockResponseDTO> listStocks(String accountId) {
        
        var account = accountRepository.findById(UUID.fromString(accountId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return account.getAccountStocks().stream().map(as -> new AccountStockResponseDTO(
            as.getStock().getStockId(), as.getQuantity(), 
            getTotal(as.getQuantity(), as.getStock().getStockId()))).toList();
        
    }

    private double getTotal(Integer quantity, String stockId) {
        
        var response = brapiClient.getQuote(TOKEN, stockId);

        var price = response.results().getFirst().regularMarketPrice();

        return price * quantity;
    }

    
}
