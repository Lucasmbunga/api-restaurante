package com.lucas.api_restaurante.saida_stock;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock/saidas")
public class SaidaStockController {
    private final SaidaStockService saidaStockService;

    public SaidaStockController(SaidaStockService saidaStockService) {
        this.saidaStockService = saidaStockService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SaidaStockResponseDto>>> obterSaidasStock(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(saidaStockService.obterSaidasStock(pageable,"/stock/saidas"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaidaStockResponseDto>> registrarSaidaStock(@Valid @RequestBody SaidaStockRegisterDto saidaStockRegisterDto) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(saidaStockService.registrarSaidaStock(saidaStockRegisterDto,"/stock/saidas/"));
    }
}
