package com.lucas.api_restaurante.acompanhante;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcompanhanteService {

    private final AcompanhanteRepository acompanhanteRepository;

    public AcompanhanteService(AcompanhanteRepository acompanhanteRepository) {
        this.acompanhanteRepository = acompanhanteRepository;
    }

    public ApiResponse<List<Acompanhante>> listarAcompanhantes(Pageable pageable,String path) {
        return ResponseUtil.sucess(acompanhanteRepository.findAll(pageable).getContent(),"Sucesso",path);
    }

    public ApiResponse<Acompanhante> buscarAcompanhantePorId(Long id,String path) throws RecursoNaoEncontradoException{
        if(!acompanhanteRepository.findById(id).isPresent()){
            throw new RecursoNaoEncontradoException("Não foi encontrado um acompanhante com id "+id);
        }

        return ResponseUtil.sucess(acompanhanteRepository.findById(id).get(),"Sucesso",path+id);
    }

    public ApiResponse<Acompanhante> cadastrarAcompanhante(Acompanhante companhante,String path) {
        Acompanhante novoAcompanhante=new Acompanhante();
        novoAcompanhante.setNome(companhante.getNome());
        novoAcompanhante.setPreco(companhante.getPreco());
        novoAcompanhante.setDescricao(companhante.getDescricao());
        var acompanhanteCadastrado=acompanhanteRepository.save(novoAcompanhante);

        return ResponseUtil.sucess(acompanhanteCadastrado,"Sucesso",path+acompanhanteCadastrado.getId());
    }

    public ApiResponse<Acompanhante> editarAcompanhante(Long id,Acompanhante acompanhante,String path) throws RecursoNaoEncontradoException{
        if(!acompanhanteRepository.findById(id).isPresent()){
            throw new RecursoNaoEncontradoException("Não foi encontrado um acompanhante com id"+id);
        }
        var acompanhanteExistente=acompanhanteRepository.findById(id).get();
        acompanhanteExistente.setNome(!(acompanhante.getNome().isBlank())?acompanhante.getNome():acompanhanteExistente.getNome());
        acompanhanteExistente.setPreco((acompanhante.getPreco()!=null)?acompanhante.getPreco():acompanhanteExistente.getPreco());
        acompanhanteExistente.setDescricao(!(acompanhante.getDescricao().isBlank())?acompanhante.getDescricao():acompanhanteExistente.getDescricao());

        var acompanhanteEditado=acompanhanteRepository.save(acompanhanteExistente);

        return ResponseUtil.sucess(acompanhanteEditado,"Sucesso",path+id);
    }

    public ApiResponse<Void> deletarAcompanhante(Long id,String path) throws RecursoNaoEncontradoException{
        if(!acompanhanteRepository.findById(id).isPresent()){
            throw new RecursoNaoEncontradoException("Não foi encontrado um acompanhante com id"+id);
        }

        acompanhanteRepository.deleteById(id);
        return ResponseUtil.sucess("Sucesso",path+id);
    }
}
