package com.lucas.api_restaurante.mesa;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {
    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Mesa>>> listarMesas(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "numeroMesa") Pageable pageable){
        return ResponseEntity.ok(mesaService.listarMesas(pageable,"/api/mesas"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Mesa>> buscarMesaPorId(@PathVariable Long id) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(mesaService.buscarMesaPorId(id,"/api/mesas/"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Mesa>> cadastrarMesa(@RequestBody Mesa mesa){
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.cadastrarMesa(mesa,"/api/mesas/"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Mesa>> editarMesa(@PathVariable Long id, @RequestBody Mesa mesa) throws RecursoNaoEncontradoException{
        return ResponseEntity.ok(mesaService.editarMesa(id,mesa,"/api/mesas/"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirMesa(@PathVariable Long id) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(mesaService.excluirMesa(id,"/api/mesas/"));
    }
 }
