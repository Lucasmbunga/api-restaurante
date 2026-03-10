package com.lucas.api_restaurante.entradacaixa;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/entradas-caixa")
public class EntradasCaixaController {
    private EntradaCaixaService entradaCaixaService;

    public EntradasCaixaController(EntradaCaixaService entradaCaixaService) {
        this.entradaCaixaService = entradaCaixaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EntradaCaixa>>> listarTodasEntradasCaixa(@PageableDefault(page = 0,size=10) Pageable pageable){
        return ResponseEntity.ok(entradaCaixaService.listarTodasEntradasCaixa(pageable,"/entradas-caixa"));
    }

    @GetMapping("/{idTurno}/entradas-caixa")
    public ResponseEntity<ApiResponse<List<EntradaCaixa>>> listarEntradasCaixaPorTurno(@PathVariable Long idTurno)throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(entradaCaixaService.listarEntradasCaixaPorTurno(idTurno,idTurno+"/entradas-caixa"));
    }
    @GetMapping("/filtrar-por-data")
    public ResponseEntity<ApiResponse<List<EntradaCaixa>>> listarEntradasCaixaPorData(@RequestParam LocalDate data){
        return ResponseEntity.ok(entradaCaixaService.listarEntradasCaixaPorData(data));
    }
}
