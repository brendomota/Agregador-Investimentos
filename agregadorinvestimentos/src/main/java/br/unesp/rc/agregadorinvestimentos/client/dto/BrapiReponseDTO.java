package br.unesp.rc.agregadorinvestimentos.client.dto;

import java.util.List;

public record BrapiReponseDTO (List<StockDTO> results) {
    
}
