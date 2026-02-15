package com.lucas.api_restaurante.acompanhante;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acompanhantes")
public class AcompanhanteController {

    public final AcompanhanteService acompanhanteService;

    public AcompanhanteController(AcompanhanteService acompanhanteService) {
        this.acompanhanteService = acompanhanteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Acompanhante>>> listarAcompanhantes(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "nome") Pageable pageable){
        return ResponseEntity.ok(acompanhanteService.listarAcompanhantes(pageable,"/api/acompanhantes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Acompanhante>> buscarAcompanhantePorId(@PathVariable Long id) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(acompanhanteService.buscarAcompanhantePorId(id,"/api/acompanhantes/"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<Acompanhante>> cadastrarAcompanhante(@RequestBody Acompanhante acompanhante){
        return ResponseEntity.status(HttpStatus.CREATED).body(acompanhanteService.cadastrarAcompanhante(acompanhante,"/api/acompanhantes/"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Acompanhante>> editarAcompanhante(@PathVariable Long id, @RequestBody Acompanhante acompanhante) throws RecursoNaoEncontradoException{
        return ResponseEntity.ok(acompanhanteService.editarAcompanhante(id,acompanhante,"/api/acompanhantes/"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> excluirAcompanhante(@PathVariable Long id) throws RecursoNaoEncontradoException{
        return ResponseEntity.ok(acompanhanteService.deletarAcompanhante(id,"/api/acompanhantes/"));
    }
}
