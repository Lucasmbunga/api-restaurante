package com.lucas.api_restaurante.produto;

import com.lucas.api_restaurante.exceptions.NotFoundException;
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
@RequestMapping("/produtos")
public class ProdutoController {
    private ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> cadastrarProduto(@Valid @RequestBody ProdutoRequestDto produtoRequestDto) throws NotFoundException {
        this.produtoService.verificarCategoriaDoProduto(produtoRequestDto.idCategoria(),produtoRequestDto.nome());
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.cadastrarProduto(produtoRequestDto,"/produtos"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProdutoResponseDto>>> listarProdutos(@PageableDefault(page = 0,size =  10,direction = Sort.Direction.DESC,sort = "nome") Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.listarProdutos(pageable,"/produtos"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> buscarProdutoPorId(@PathVariable Long id) throws NotFoundException {
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.buscarProdutoPorId(id,"/produtos/"+id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponseDto>> atualizarProduto(@PathVariable Long id,@Valid @RequestBody ProdutoRequestDto produtoRequestDto) throws NotFoundException {
        this.produtoService.verificarCategoriaDoProduto(produtoRequestDto.idCategoria(),produtoRequestDto.nome());
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.atualizarProduto(id,produtoRequestDto,"/produtos/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirProduto(@PathVariable Long id) throws NotFoundException {
        this.produtoService.verificarProdutoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.excluirProduto(id,"/produtos/"+id));
    }

}
