package com.lucas.api_restaurante.produto;

import com.lucas.api_restaurante.categoria.CategoriaRepository;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ApiResponse<ProdutoResponseDto> cadastrarProduto(ProdutoRequestDto produtoRequestDto, String path) throws RecursoNaoEncontradoException {

        var categoria = categoriaRepository.findById(produtoRequestDto.idCategoria()).get();
        Produto novoProduto = new Produto();
        novoProduto.setNome(produtoRequestDto.nome());
        novoProduto.setPrecoCompra(produtoRequestDto.precoCompra());
        novoProduto.setPrecoVenda(produtoRequestDto.precoVenda());
        novoProduto.setTipoProduto(produtoRequestDto.tipoProduto());
        novoProduto.setCategoria(categoria);

        var produtoSalvo = produtoRepository.save(novoProduto);
        ProdutoResponseDto produtoResponseDto = new ProdutoResponseDto(produtoSalvo.getId(),produtoSalvo.getNome(), produtoSalvo.getPrecoCompra(), produtoSalvo.getPrecoVenda(), produtoSalvo.getTipoProduto());
        return ResponseUtil.sucess(produtoResponseDto, "Produto cadastrado com sucesso", path);

    }

    public ApiResponse<List<ProdutoResponseDto>> listarProdutos(Pageable pageable, String path) {
        List<ProdutoResponseDto> produtos = produtoRepository.findAll(pageable)
                .map(produto ->
                        new ProdutoResponseDto(produto.getId(),produto.getNome(), produto.getPrecoCompra(), produto.getPrecoVenda(), produto.getTipoProduto())
                ).toList();
        return ResponseUtil.sucess(produtos, "Sucess", path);
    }

    public ApiResponse<ProdutoResponseDto> buscarProdutoPorId(Long id, String path) {

        var produto = produtoRepository.findById(id).get();
        var produtoResponse = new ProdutoResponseDto(produto.getId(),produto.getNome(), produto.getPrecoCompra(), produto.getPrecoVenda(), produto.getTipoProduto());
        return ResponseUtil.sucess(produtoResponse, "Sucess", path);
    }

    public ApiResponse<ProdutoResponseDto> atualizarProduto(Long id, ProdutoRequestDto produtoRequestDto, String path) {
        var produtoExistente = produtoRepository.findById(id).get();
        var categoria = categoriaRepository.findById(produtoRequestDto.idCategoria()).get();
        produtoExistente.setNome(!produtoRequestDto.nome().isBlank() ? produtoRequestDto.nome() : produtoExistente.getNome());

        produtoExistente.setPrecoCompra(
                !produtoRequestDto.precoCompra().equals(null) ? produtoRequestDto.precoCompra() : produtoExistente.getPrecoCompra());

        produtoExistente.setPrecoVenda(!produtoRequestDto.precoVenda().equals(null) ?
                produtoRequestDto.precoVenda() : produtoExistente.getPrecoVenda());

        produtoExistente.setTipoProduto(!produtoRequestDto.tipoProduto().equals(null) ?
                produtoRequestDto.tipoProduto() : produtoExistente.getTipoProduto());
        produtoExistente.setCategoria(categoria);

        var produtoAtualizado = produtoRepository.save(produtoExistente);

        var produtoResponse = new ProdutoResponseDto(produtoAtualizado.getId(),produtoAtualizado.getNome(),
                produtoAtualizado.getPrecoCompra(), produtoAtualizado.getPrecoVenda(), produtoAtualizado.getTipoProduto());

        return ResponseUtil.sucess(produtoResponse, "Sucess", path);

    }

    public void verificarProdutoPorId(Long id) throws RecursoNaoEncontradoException {
        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Não foi encontrado um produto com id " + id);
        }
    }

    public void verificarCategoriaDoProduto(Long id, String nomeProduto) throws RecursoNaoEncontradoException {
        var categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()) {
            throw new RecursoNaoEncontradoException("Não foi encontrada um categoria com id " +
                    id + "especificado para o produto " + nomeProduto);
        }
    }

    public ApiResponse<Void> excluirProduto(Long id, String path) {
        produtoRepository.deleteById(id);
        return ResponseUtil.sucess("Produto excluído com sucesso",path);
    }
}
