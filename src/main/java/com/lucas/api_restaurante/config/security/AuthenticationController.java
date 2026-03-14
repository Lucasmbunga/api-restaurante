package com.lucas.api_restaurante.config.security;

import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    private final
    TokenService tokenService;

    public AuthenticationController(TokenService tokenService) {

        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationRequestDto.email(), authenticationRequestDto.senha());

        System.out.println("UsernamePassword: " + usernamePassword);

        var auth = this.authenticationManager.authenticate(usernamePassword);
        Usuario usuario = (Usuario) auth.getPrincipal();
        if (usuario != null) {
            var token = tokenService.gerarToken(usuario);

            return ResponseEntity.ok(new TokenResponseDto(usuario.getEmail(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
