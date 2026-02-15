package com.lucas.api_restaurante.mesa;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {
    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public ApiResponse<List<Mesa>> listarMesas(Pageable pageable, String path) {
        return ResponseUtil.sucess(mesaRepository.findAll(pageable).getContent(),"Sucesso",path);
    }
    public ApiResponse<Mesa> buscarMesaPorId(Long id,String path) throws RecursoNaoEncontradoException {
        if(!mesaRepository.findById(id).isPresent()){
            throw new RecursoNaoEncontradoException("Não foi encontrada uma mesa com id "+id);
        }

        return ResponseUtil.sucess(mesaRepository.findById(id).get(),"Sucesso",path+id);
    }

    public ApiResponse<Mesa> cadastrarMesa(Mesa mesa,String path){
        var novaMesa=new Mesa();
        novaMesa.setNumeroMesa(mesa.getNumeroMesa());
        novaMesa.setCapacidade(mesa.getCapacidade());
        novaMesa.setEstaOcupada(false);
        var mesaCadastrada=mesaRepository.save(novaMesa);
        return ResponseUtil.sucess(mesaCadastrada,"Mesa cadastrada",path+mesaCadastrada.getId());
    }

    public ApiResponse<Mesa> editarMesa(Long id,Mesa mesa,String path)throws RecursoNaoEncontradoException{
        if(!mesaRepository.findById(id).isPresent()){
            throw new RecursoNaoEncontradoException("Não foi encontrada uma mesa com id "+id);
        }

        var mesaExistente=mesaRepository.findById(id).get();
        mesaExistente.setNumeroMesa((mesa.getNumeroMesa()!=null)?mesa.getNumeroMesa():mesaExistente.getNumeroMesa());
        mesaExistente.setCapacidade((mesa.getCapacidade()!=null)?mesa.getCapacidade():mesaExistente.getCapacidade());
        mesaExistente.setEstaOcupada((mesa.getEstaOcupada()!=null)?mesa.getEstaOcupada():mesaExistente.getEstaOcupada());

        var mesaEditada=mesaRepository.save(mesaExistente);

        return ResponseUtil.sucess(mesaEditada,"Mesa atualizada",path+id);
    }

    public ApiResponse<Void> excluirMesa(Long id,String path)throws RecursoNaoEncontradoException{
        if(mesaRepository.findById(id).isEmpty()){
            throw new RecursoNaoEncontradoException("Não foi encontrada um mesa com id "+id);
        }
        mesaRepository.deleteById(id);
        return ResponseUtil.sucess("Mesa excluída com sucesso!",path+id);
    }
}
