package com.lucas.api_restaurante.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucas.api_restaurante.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario user) {
        try{
            Algorithm algorithm=Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Api-auth")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.gerarDataDeExpiracaoDeToken())
                    .sign(algorithm);
        }
        catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar Token");
        }
    }

    public String validarToken(String token) {
       try{
           Algorithm algorithm=Algorithm.HMAC256(secret);
           return JWT.require(algorithm)
                   .withIssuer("Api-auth")
                   .build()
                   .verify(token)
                   .getSubject();
       }
       catch (JWTVerificationException exception){
           return null;
       }

    }
    private Instant gerarDataDeExpiracaoDeToken() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("+01:00"));
    }

}
