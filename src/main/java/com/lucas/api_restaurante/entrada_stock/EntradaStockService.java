package com.lucas.api_restaurante.entrada_stock;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.produto.ProdutoRepository;
import com.lucas.api_restaurante.produto_stock.ProdutoStockRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EntradaStockService {

    private final EntradaStockRepository entradaStockRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoStockRepository produtoStockRepository;

    public EntradaStockService(EntradaStockRepository entradaStockRepository,ProdutoRepository produtoRepository,ProdutoStockRepository produtoStockRepository) {
        this.entradaStockRepository = entradaStockRepository;
        this.produtoRepository = produtoRepository;
        this.produtoStockRepository = produtoStockRepository;
    }

    public ApiResponse<Page<EntradaStockResponseDto>> obterTodasEntradasStock(Pageable pageable,String path)  {

        Page<EntradaStockResponseDto> entradaStocks = entradaStockRepository.findAll(pageable).map(this::responseDto);
        return ResponseUtil.sucess(entradaStocks,"Sucesso",path);
    }

    public ApiResponse<EntradaStockResponseDto> registrarEntradaStock(EntradaStockRegisterDto entradaStockRegisterDto, String path) throws NotFoundException {
        var produto=produtoRepository.findById(entradaStockRegisterDto.idProduto()).orElseThrow(()->new NotFoundException("Não foi encontrado um produto com id"+ entradaStockRegisterDto.idProduto()));

        EntradaStock novaEntradaStock=new EntradaStock();
        novaEntradaStock.setProduto(produto);
        novaEntradaStock.setDataValidade(entradaStockRegisterDto.dataValidade());
        novaEntradaStock.setQuantidade(entradaStockRegisterDto.quantidade());
        novaEntradaStock.setData(LocalDate.now());

        var entradaStockRegistrada=entradaStockRepository.save(novaEntradaStock);
        var produtoStock=produtoStockRepository.findByProduto(produto).orElseThrow(()->new NotFoundException("O produto "+produto.getNome()+" não tem stock registrado."));

        produtoStock.setQuantidadeDisponivel(produtoStock.getQuantidadeDisponivel()+entradaStockRegistrada.getQuantidade());
        produtoStockRepository.save(produtoStock);

        return ResponseUtil.sucess(responseDto(entradaStockRegistrada),"Entrada registrada no stock com sucesso",path+entradaStockRegistrada.getId());

    }

    public ApiResponse<EntradaStockResponseDto> editarEntradaStock(Long id, EntradaStockRegisterDto entradaStockRegisterDto, String path) throws NotFoundException {
        var produto=produtoRepository.findById(entradaStockRegisterDto.idProduto()).orElseThrow(()->new NotFoundException("Não foi encontrado um produto com id"+ entradaStockRegisterDto.idProduto()));
        var entradaExistente=entradaStockRepository.findById(id).orElseThrow(()->new NotFoundException("Não foi encontrada uma entrada no stock com id "+id));

        entradaExistente.setProduto(produto);
        entradaExistente.setQuantidade(entradaStockRegisterDto.quantidade());
        entradaExistente.setDataValidade(entradaStockRegisterDto.dataValidade());

        var entradaStockEditada=entradaStockRepository.save(entradaExistente);

        var produtoStock=produtoStockRepository.findByProduto(produto).orElseThrow(()->new NotFoundException("O produto "+produto.getNome()+" não tem stock registrado."));
        produtoStock.setQuantidadeDisponivel(entradaStockEditada.getQuantidade());

        produtoStockRepository.save(produtoStock);

        return ResponseUtil.sucess(responseDto(entradaStockEditada),"Sucesso",path+entradaStockEditada.getId());
    }

    public ApiResponse<EntradaStockResponseDto> excluirEntradaStock(Long id) throws NotFoundException {
       var entradaStock=entradaStockRepository.findById(id).orElseThrow(()->new NotFoundException("Não foi encontrada uma entrada no stock com id"+id));
       entradaStockRepository.delete(entradaStock);

       return ResponseUtil.sucess(responseDto(entradaStock),"Sucesso",null);

    }

    public EntradaStockResponseDto responseDto(EntradaStock entradaStock)  {
        var produto=produtoRepository.findById(entradaStock.getProduto().getId()).get();

        return new EntradaStockResponseDto(produto.getNome(),entradaStock);
    }
}
