package br.unesp.rc.agregadorinvestimentos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.unesp.rc.agregadorinvestimentos.client.dto.BrapiReponseDTO;

@FeignClient(name = "BrapiClient", url = "https://brapi.dev/")
public interface BrapiClient {
    
    @GetMapping("/api/quote/{stockId}")
    BrapiReponseDTO getQuote(@RequestParam("token") String token,
                            @RequestParam("stockId") String stockId);
    
}
