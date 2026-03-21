package com.lucas.api_restaurante.categoria;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public ApiResponse<CategoriaResponseDto> cadastrarCategoria(CategoriaRequestDto categoriaRequestDto, String path) {
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome(categoriaRequestDto.nome());
        novaCategoria.setTipoCategoria(categoriaRequestDto.tipoCategoria());

        Categoria categoriaSalva = categoriaRepository.save(novaCategoria);
        CategoriaResponseDto categoriaResponseDto = new CategoriaResponseDto(categoriaSalva.getId(), categoriaSalva.getNome(), categoriaSalva.getTipoCategoria());
        return ResponseUtil.sucess(categoriaResponseDto, "Categoria criado com sucesso", path);

    }

    public ApiResponse<List<Categoria>> listarCategorias(Pageable pageable, String path) {
        return ResponseUtil.sucess(categoriaRepository.findAll(pageable).getContent(), "Sucesso", path);
    }

    public ApiResponse<Categoria> buscarCategoriaPorId(Long id, String path) throws NotFoundException {
        if (!categoriaEncontrada(id)) {
            throw new NotFoundException("Não foi encontrada um categoria com id" + id);
        }

        Categoria categoria = categoriaRepository.findById(id).get();
        return ResponseUtil.sucess(categoria, "Sucess", path);
    }

    public boolean categoriaEncontrada(Long id) {
        return categoriaRepository.existsById(id);
    }

    public ApiResponse<Categoria> atualizarCategoria(Long id, CategoriaRequestDto categoriaRequestDto, String path) throws NotFoundException {
        var categoriaProcurada = categoriaRepository.findById(id);
        if (!categoriaProcurada.isPresent()) {
            throw new NotFoundException("Não foi encontrada uma categoria com id" + id);
        }
        Categoria categoriaEncontrada = categoriaProcurada.get();
        categoriaEncontrada.setNome(
                !categoriaRequestDto.nome().isBlank() ?
                        categoriaRequestDto.nome() : categoriaEncontrada.getNome());
        categoriaEncontrada.setTipoCategoria(!categoriaRequestDto.tipoCategoria().equals(null) ?
                categoriaRequestDto.tipoCategoria() : categoriaEncontrada.getTipoCategoria());

        Categoria categoriaAtualizada = categoriaRepository.save(categoriaEncontrada);
        return ResponseUtil.sucess(categoriaAtualizada, "Categoria atualizada com sucesso!", path);

    }

    public ApiResponse<Void> excluirCategoria(Long id, String path) {
        categoriaRepository.deleteById(id);
        return ResponseUtil.sucess("Categoria excluída com sucesso", path);
    }
}
