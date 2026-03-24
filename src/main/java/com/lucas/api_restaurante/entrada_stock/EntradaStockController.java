package com.lucas.api_restaurante.entrada_stock;

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
@RequestMapping("/stock/entradas")
public class EntradaStockController {
    private final EntradaStockService entradaStockService;

    public EntradaStockController(EntradaStockService entradaStockService) {
        this.entradaStockService=entradaStockService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EntradaStockResponseDto>>> obterTodasEntradasStock(@PageableDefault Pageable pageable)  {
        return ResponseEntity.ok(entradaStockService.obterTodasEntradasStock(pageable,"/stock/entradas"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EntradaStockResponseDto>> registrarEntrada(@Valid @RequestBody EntradaStockRegisterDto entradaStockRegisterDto) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(entradaStockService.registrarEntradaStock(entradaStockRegisterDto,"/stock/entradas/"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EntradaStockResponseDto>> editarEntradaStock(@PathVariable Long id,@Valid @RequestBody EntradaStockRegisterDto entradaStockRegisterDto) throws NotFoundException {
        return ResponseEntity.ok(entradaStockService.editarEntradaStock(id, entradaStockRegisterDto,"/stock/entradas/"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<EntradaStockResponseDto>> excluirEntradaStock(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(entradaStockService.excluirEntradaStock(id));
    }
}
