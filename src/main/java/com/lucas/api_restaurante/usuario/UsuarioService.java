package com.lucas.api_restaurante.usuario;

import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.telefone.Telefone;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).getContent();
    }

    public UserDetails buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);

    }

    public ApiResponse<Usuario> registrarUsuario(UsuarioRegisterDto usuario, String path) {
        var novoUsuario=new Usuario();
        novoUsuario.setEmail(usuario.email());
        String senhaEncriptada=new BCryptPasswordEncoder().encode(usuario.senha());

        novoUsuario.setSenha(senhaEncriptada);
        novoUsuario.setNome(usuario.nome());
        novoUsuario.setTipoUsuario(usuario.tipoUsuario());
        List<Telefone>  telefones=usuario.telefones().stream().map(telefone -> new Telefone(telefone,novoUsuario)).collect(Collectors.toList());
        novoUsuario.setTelefones(telefones);

        var usuarioRegistrado=usuarioRepository.save(novoUsuario);
        return ResponseUtil.sucess(usuarioRegistrado,"Usuário registrado com sucesso",path+"/"+usuarioRegistrado.getId());
    }

    public ApiResponse<Void> excluirUsuario(Long id,String path) {
        usuarioRepository.deleteById(id);
        return ResponseUtil.sucess("Usuário excluído com sucesso.",path);
    }
}
