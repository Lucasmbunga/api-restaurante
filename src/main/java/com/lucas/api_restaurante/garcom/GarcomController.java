package com.lucas.api_restaurante.garcom;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garcons")
public class GarcomController {

    private final GarcomService garcomService;
    public GarcomController(GarcomService garcomService) {
        this.garcomService = garcomService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GarcomResponseDto>> registrarGarcom(@RequestBody @Valid GarcomCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(garcomService.registrarGarcom(dto,"/api/garcons/"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<GarcomResponseDto>>> listarGarcom(@PageableDefault(page = 0,size =  10,direction = Sort.Direction.ASC,sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(garcomService.listarGarcons(pageable,"/garcons/"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GarcomResponseDto>> buscarGarcomPorId(@PathVariable Long id) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(garcomService.buscarGarcomPorId(id,"/garcons/"+id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GarcomResponseDto>> editarGarcom(@PathVariable Long id, @RequestBody @Valid GarcomCreateDto dto) {
        return ResponseEntity.ok(garcomService.editarDadosDoGarcom(id,dto,"/garcons/"+id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> excluirGarcom(@PathVariable Long id) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(garcomService.excluirGarcom(id,"/garcons/"+id));
    }
}
