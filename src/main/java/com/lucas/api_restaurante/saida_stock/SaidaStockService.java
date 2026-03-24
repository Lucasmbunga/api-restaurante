package com.lucas.api_restaurante.saida_stock;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.produto.ProdutoRepository;
import com.lucas.api_restaurante.produto_stock.ProdutoStockRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SaidaStockService {

    private final SaidaStockRepository saidaStockRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoStockRepository produtoStockRepository;

    public SaidaStockService(SaidaStockRepository saidaStockRepository,ProdutoRepository produtoRepository,ProdutoStockRepository produtoStockRepository) {
        this.saidaStockRepository = saidaStockRepository;
        this.produtoRepository = produtoRepository;
        this.produtoStockRepository = produtoStockRepository;
    }


    public ApiResponse<Page<SaidaStockResponseDto>> obterSaidasStock(Pageable pageable,String path) {
        Page<SaidaStockResponseDto> saidaStockResponseDtos=saidaStockRepository.findAll(pageable).map(this::responseDto);

        return ResponseUtil.sucess(saidaStockResponseDtos,"Sucesso",path);
    }
    @Transactional
    public ApiResponse<SaidaStockResponseDto> registrarSaidaStock( SaidaStockRegisterDto saidaStockRegisterDto,String path) throws NotFoundException {
        var produto=produtoRepository.findById(saidaStockRegisterDto.idProduto()).orElseThrow(()->new NotFoundException("Não foi encontrado um produto com id "+saidaStockRegisterDto.idProduto()));
        SaidaStock novaSaidaStock=new SaidaStock();
        novaSaidaStock.setProduto(produto);
        novaSaidaStock.setQuantidade(saidaStockRegisterDto.quantidade());

        var saidaStockRegistrada=saidaStockRepository.save(novaSaidaStock);

        var produtoStock=produtoStockRepository.findByProduto(produto).orElseThrow(()->new NotFoundException("Produto com id "+produto.getId()+" não tem stock registrado."));

        if(produtoStock.getQuantidadeDisponivel()<0){
            throw new IllegalStateException("Não foi possível efetuar esta operação porque o produto "+produto.getNome()+" não tem stock disponível");
        }
        else if( produtoStock.getQuantidadeDisponivel()< saidaStockRegisterDto.quantidade()){
            throw new IllegalStateException("O produto "+produto.getNome()+" não tem stock suficiente. Stock disponível:"+produtoStock.getQuantidadeDisponivel());
        }
        produtoStock.setQuantidadeDisponivel(produtoStock.getQuantidadeDisponivel()- saidaStockRegistrada.getQuantidade());
        produtoStockRepository.save(produtoStock);

        return ResponseUtil.sucess(responseDto(saidaStockRegistrada),"Sucesso",path);

    }

    @Transactional
    public void darBaixaNoStock( SaidaStockRegisterDto saidaStockRegisterDto) throws NotFoundException {
        var produto=produtoRepository.findById(saidaStockRegisterDto.idProduto()).orElseThrow(()->new NotFoundException("Não foi encontrado um produto com id "+saidaStockRegisterDto.idProduto()));
        SaidaStock novaSaidaStock=new SaidaStock();
        novaSaidaStock.setProduto(produto);
        novaSaidaStock.setQuantidade(saidaStockRegisterDto.quantidade());

        var saidaStockRegistrada=saidaStockRepository.save(novaSaidaStock);

        var produtoStock=produtoStockRepository.findByProduto(produto).orElseThrow(()->new NotFoundException("Produto com id "+produto.getId()+" não tem stock registrado."));

        if(produtoStock.getQuantidadeDisponivel()<0){
            throw new IllegalStateException("Não foi possível efetuar esta operação porque o produto "+produto.getNome()+" não tem stock disponível");
        }
        else if( produtoStock.getQuantidadeDisponivel()< saidaStockRegisterDto.quantidade()){
            throw new IllegalStateException("O produto "+produto.getNome()+" não tem stock suficiente. Stock disponível:"+produtoStock.getQuantidadeDisponivel());
        }
        produtoStock.setQuantidadeDisponivel(produtoStock.getQuantidadeDisponivel()- saidaStockRegistrada.getQuantidade());
        produtoStockRepository.save(produtoStock);
    }

    public SaidaStockResponseDto responseDto(SaidaStock saidaStock){
        var produto=produtoRepository.findById(saidaStock.getProduto().getId()).get();

        return new SaidaStockResponseDto(produto.getNome(),saidaStock);
    }
}
