package com.lucas.api_restaurante.cliente;

import com.lucas.api_restaurante.endereco.Endereco;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.telefone.Telefone;
import com.lucas.api_restaurante.usuario.TipoUsuario;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    private ClienteResponseDto entityToDto(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefones().stream().map(Telefone::getNumeroTelefone).toList(),
                cliente.getNif(),
                cliente.getEndereco()
        );
    }

    public ApiResponse<List<ClienteResponseDto>> listarClientes(Pageable pageable, String path) {
        List<ClienteResponseDto> listaDeClientes = clienteRepository.findAll(pageable).getContent()
                .stream()
                .map(this::entityToDto)
                .toList();

        return ResponseUtil.sucess(listaDeClientes, "Sucesso", path);
    }

    public ApiResponse<ClienteResponseDto> buscarClientePorId(Long id, String path) {
        if (clienteRepository.findById(id).isEmpty()) {
            return null;
        }
        ClienteResponseDto clienteResponse = clienteRepository.findById(id)
                .map(this::entityToDto)
                .get();
        return ResponseUtil.sucess(clienteResponse, "Sucesso", path + clienteResponse.id());
    }

    public ApiResponse<ClienteResponseDto> registrarCliente(ClienteRequestDto clienteRequestDto, String path) {
        if (clienteRepository.findByEmail(clienteRequestDto.email()).isPresent()) {
            throw new RuntimeException("Já existe um cliente cadastrado com email " + clienteRequestDto.email());
        }
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(clienteRequestDto.nome());
        novoCliente.setEmail(clienteRequestDto.email());
        novoCliente.setSenha(new BCryptPasswordEncoder().encode(clienteRequestDto.senha()));
        novoCliente.setNif(clienteRequestDto.nif());
        novoCliente.setTipoUsuario(TipoUsuario.CLIENTE);

        Endereco endereco = new Endereco(clienteRequestDto.cidade(), clienteRequestDto.bairro(), clienteRequestDto.zona(), clienteRequestDto.rua());
        endereco.setCliente(novoCliente);
        novoCliente.setEndereco(Arrays.asList(endereco));

        List<Telefone> telefones = clienteRequestDto.telefones().stream().map(telefone -> new Telefone(telefone, novoCliente)).collect(Collectors.toList());
        novoCliente.setTelefones(telefones);

        ClienteResponseDto clienteRegistrado = this.entityToDto(clienteRepository.save(novoCliente));

        return ResponseUtil.sucess(clienteRegistrado, "Sucesso", path + clienteRegistrado.id());
    }

    public ApiResponse<ClienteResponseDto> editarDadosDoCliente(Long id, ClienteRequestDto clienteDto, String path) {
        if (!clienteRepository.findById(id).isPresent()) {
            throw new RuntimeException("Não foi encontrado um cliente com id " + id);
        }
        try {

            var clienteEncontrado = clienteRepository.findById(id).get();
            clienteEncontrado.setNome(!(clienteDto.nome().isBlank()) ? clienteDto.nome() : clienteEncontrado.getNome());
            clienteEncontrado.setEmail(!(clienteDto.email().isBlank()) ? clienteDto.email() : clienteEncontrado.getEmail());
            clienteEncontrado.setSenha(!(clienteDto.senha().isBlank()) ? clienteDto.senha() : clienteEncontrado.getSenha());
            List<Telefone> telefones = clienteDto.telefones().stream().map(telefone -> new Telefone(telefone, clienteEncontrado)).collect(Collectors.toList());

            clienteEncontrado.setTelefones(!(telefones.isEmpty()) ? telefones : clienteEncontrado.getTelefones());
            clienteEncontrado.setNif(!(clienteDto.nif().isBlank()) ? clienteDto.nif() : clienteEncontrado.getNif());

            ClienteResponseDto clienteEditado = this.entityToDto(clienteRepository.save(clienteEncontrado));

            return ResponseUtil.sucess(clienteEditado, "Sucesso", path + clienteEditado.id());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
        return null;
    }

    public ApiResponse<Void> excluirCliente(Long id, String path) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Não foi encontrado um cliente com id " + id);
        }
        clienteRepository.deleteById(id);
        return ResponseUtil.sucess("Sucesso", path);
    }

}
