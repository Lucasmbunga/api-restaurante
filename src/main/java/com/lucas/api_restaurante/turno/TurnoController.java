package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.caixa.CaixaAberturaRequestDto;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {
    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<Turno>>> listarTurnos(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "data") Pageable pageable){
        return ResponseEntity.ok(turnoService.listarTurnos(pageable,"/turnos"));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<TurnoResponseDto>> abrirTurno(@RequestBody CaixaAberturaRequestDto caixaRequest) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(turnoService.abrirTurno(caixaRequest.valorInicial()));
    }

    @PostMapping("/fecho")
    @Transactional
    public ResponseEntity<ApiResponse<FechoTurnoResponseDto>> fecharTurno() throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(turnoService.fecharTurno());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.excluirTurno(id));
    }
}
