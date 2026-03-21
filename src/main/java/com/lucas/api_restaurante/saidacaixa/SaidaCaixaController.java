package com.lucas.api_restaurante.saidacaixa;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saidas-caixa")
public class SaidaCaixaController {
    private final SaidaCaixaService saidaCaixaService;

    public SaidaCaixaController(SaidaCaixaService saidaCaixaService) {
        this.saidaCaixaService = saidaCaixaService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SaidaCaixa>>> listarTodasSaidas(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "data")Pageable pageable) {
        return ResponseEntity.ok(saidaCaixaService.obterTodasSaidasCaixa(pageable,"/saidas-caixa/all"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<SaidaCaixa>>> obterSaidasDiaria()throws NotFoundException {
        return ResponseEntity.ok(saidaCaixaService.obterSaidasDiaria("/turnos/"));
    }

    @GetMapping("/{idTurno}")
    public ResponseEntity<ApiResponse<List<SaidaCaixa>>> obterSaidasPorTurno(@PathVariable Long idTurno)throws NotFoundException {
        return ResponseEntity.ok(saidaCaixaService.obterSaidasPorTurno(idTurno,"/turnos/"+idTurno+"/saidas-caixa"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<SaidaCaixa>> registrarSaida(@RequestBody SaidaCaixaRequestDto requestDto) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(saidaCaixaService.darSaidaCaixa(requestDto));
    }
}
