package com.lucas.api_restaurante.produto;

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
@RequestMapping("/api/produtos")
public class ProdutoController {
    private ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> cadastrarProduto(@Valid @RequestBody ProdutoRequestDto produtoRequestDto) throws RecursoNaoEncontradoException {
        this.produtoService.verificarCategoriaDoProduto(produtoRequestDto.idCategoria(),produtoRequestDto.nome());
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.cadastrarProduto(produtoRequestDto,"/api/produtos"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProdutoResponseDto>>> listarProdutos(@PageableDefault(page = 0,size =  10,direction = Sort.Direction.DESC,sort = "nome") Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.listarProdutos(pageable,"/api/produtos"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> buscarProdutoPorId(@PathVariable Long id) throws RecursoNaoEncontradoException {
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.buscarProdutoPorId(id,"/api/produtos/"+id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> atualizarProduto(@PathVariable Long id,@Valid @RequestBody ProdutoRequestDto produtoRequestDto) throws RecursoNaoEncontradoException {
        this.produtoService.verificarCategoriaDoProduto(produtoRequestDto.idCategoria(),produtoRequestDto.nome());
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.atualizarProduto(id,produtoRequestDto,"/api/produtos/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirProduto(@PathVariable Long id) throws RecursoNaoEncontradoException {
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.excluirProduto(id,"/api/produtos/"+id));
    }
}
