package com.lucas.api_restaurante.garcom;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.telefone.Telefone;
import com.lucas.api_restaurante.usuario.TipoUsuario;
import com.lucas.api_restaurante.usuario.UsuarioService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GarcomService {

    private final GarcomRepository garcomRepository;
    private final UsuarioService usuarioService;

    public GarcomService(GarcomRepository garcomRepository, UsuarioService usuarioService) {
        this.garcomRepository = garcomRepository;
        this.usuarioService = usuarioService;
    }

    public GarcomResponseDto entityToDto(Garcom garcom) {
        return new GarcomResponseDto(
                garcom.getId(),
                garcom.getNome(),
                garcom.getEmail(),
                garcom.getCargo(),
                garcom.getTelefones().stream().map(Telefone::getNumeroTelefone).toList()
        );
    }

    public ApiResponse<GarcomResponseDto> registrarGarcom(GarcomCreateDto garcomCreateDto, String path) {
        if (!(usuarioService.buscarUsuarioPorEmail(garcomCreateDto.email()) == null)) {
            throw new RuntimeException("Já existe um usuário cadastrado com email " + garcomCreateDto.email());
        }
        Garcom novoGarcom = new Garcom();
        novoGarcom.setNome(garcomCreateDto.nome());
        novoGarcom.setEmail(garcomCreateDto.email());

        String senhaEncriptada = new BCryptPasswordEncoder().encode(garcomCreateDto.senha());
        novoGarcom.setSenha(senhaEncriptada);

        List<Telefone> telefones = garcomCreateDto.telefones().stream().map(fone -> new Telefone(fone, novoGarcom)).collect(Collectors.toList());
        novoGarcom.setTelefones(telefones);
        novoGarcom.setTipoUsuario(TipoUsuario.GARCOM);
        novoGarcom.setCargo(garcomCreateDto.cargo());
        var garcomRegistrado = garcomRepository.save(novoGarcom);
        return ResponseUtil.sucess(this.entityToDto(garcomRegistrado), "Garçom registrado com sucesso", path);

    }

    public ApiResponse<List<GarcomResponseDto>> listarGarcons(Pageable pageable, String path) {
        List<GarcomResponseDto> listaDeGarcons = garcomRepository.findAll(pageable)
                .map(this::entityToDto)
                .getContent();
        return ResponseUtil.sucess(listaDeGarcons, "Sucesso", path);
    }

    public ApiResponse<GarcomResponseDto> buscarGarcomPorId(Long id, String path) throws RecursoNaoEncontradoException {
        if (garcomRepository.findById(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Não foi Encontrado um Garçom com id " + id);
        }
        return ResponseUtil.sucess(this.entityToDto(garcomRepository.findById(id).get()), "Sucesso", path);
    }

    public ApiResponse<GarcomResponseDto> editarDadosDoGarcom(Long id, GarcomCreateDto garcomCreateDto, String path) {

        if (garcomRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Falha ao editar o garcom. Porque não foi encontrado um garçom com id " + id);
        }

        var garcomEncontrado = garcomRepository.findById(id).get();
        garcomEncontrado.setNome(garcomCreateDto.nome());
        garcomEncontrado.setEmail(garcomCreateDto.email());
        garcomEncontrado.setSenha(garcomCreateDto.senha());

        List<Telefone> telefones = garcomCreateDto.telefones().stream().map(fone -> new Telefone(fone, garcomEncontrado)).collect(Collectors.toList());
        garcomEncontrado.setTelefones(telefones.isEmpty() ? garcomEncontrado.getTelefones() : telefones);
        garcomEncontrado.setTipoUsuario(TipoUsuario.GARCOM);
        garcomEncontrado.setCargo(garcomCreateDto.cargo());
        var garcomEditado = garcomRepository.save(garcomEncontrado);

        return ResponseUtil.sucess(this.entityToDto(garcomEditado), "Sucesso", path);
    }

    public ApiResponse<Void> excluirGarcom(Long id, String path) {
        if (!garcomRepository.existsById(id)) {
            throw new RuntimeException("Erro ao excluir Garçom com id " + id + ". Não foi encontrado um Garçom com este id ");
        }
        garcomRepository.deleteById(id);

        return ResponseUtil.sucess("Sucesso", path);
    }
}
