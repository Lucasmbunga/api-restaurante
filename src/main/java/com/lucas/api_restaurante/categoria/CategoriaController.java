package com.lucas.api_restaurante.categoria;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<Categoria>>> listarCategorias(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoriaService.listarCategorias(pageable,"/categorias"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> buscarCategoriaPorId(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoriaService.buscarCategoriaPorId(id,"/categorias/"+id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> criarCategoria(@Valid @RequestBody CategoriaRequestDto categoriaRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.cadastrarCategoria(categoriaRequestDto,"/categorias"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDto categoriaRequestDto) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoriaService.atualizarCategoria(id,categoriaRequestDto,"/categorias/"+id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirCategoria(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(categoriaService.excluirCategoria(id,"/categorias"));
    }
}
