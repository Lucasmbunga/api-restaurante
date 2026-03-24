package com.lucas.api_restaurante.usuario;

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
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public  UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "nome") Pageable pageable){
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Usuario>> cadastrar(@Valid @RequestBody UsuarioRegisterDto usuario){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(usuario,"/usuarios"));

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.excluirUsuario(id,""));
    }
}
