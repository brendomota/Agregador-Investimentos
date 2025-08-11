package br.unesp.rc.agregadorinvestimentos.service;

import org.springframework.stereotype.Service;

import br.unesp.rc.agregadorinvestimentos.controller.dto.StockDTO;
import br.unesp.rc.agregadorinvestimentos.entity.Stock;
import br.unesp.rc.agregadorinvestimentos.repository.StockRepository;

@Service
public class StockService {
    
    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public void createStock(StockDTO stockDTO) {
        
        var stock = new Stock(
            stockDTO.stockId(),
            stockDTO.description()
        );

        stockRepository.save(stock);
    }
}
