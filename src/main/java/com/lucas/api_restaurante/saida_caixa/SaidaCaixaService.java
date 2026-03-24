package com.lucas.api_restaurante.saida_caixa;

import com.lucas.api_restaurante.caixa.CaixaRepository;
import com.lucas.api_restaurante.contas_a_pagar.ContasAPagarRepository;
import com.lucas.api_restaurante.contas_a_pagar.EstadoPagamento;
import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.turno.StatusTurno;
import com.lucas.api_restaurante.turno.Turno;
import com.lucas.api_restaurante.turno.TurnoRepository;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaidaCaixaService {
    private final SaidaCaixaRepository saidaCaixaRepository;
    private final TurnoRepository turnoRepository;
    private final CaixaRepository caixaRepository;
    private final ContasAPagarRepository contasRepository;

    public ApiResponse<List<SaidaCaixa>> obterTodasSaidasCaixa(Pageable pageable, String path) {
        List<SaidaCaixa> saidasCaixas = saidaCaixaRepository.findAll(pageable).getContent();

        return ResponseUtil.sucess(saidasCaixas, "Sucesso", path);
    }

    public ApiResponse<List<SaidaCaixa>> obterSaidasDiaria(String path) throws NotFoundException {
        var turnoAtivo = this.obterTurnoAtivo();
        List<SaidaCaixa> saidasCaixas = saidaCaixaRepository.findByTurno(turnoAtivo);
        return ResponseUtil.sucess(saidasCaixas, "Sucesso", path + turnoAtivo.getId() + "/saidas");
    }

    public ApiResponse<List<SaidaCaixa>> obterSaidasPorTurno(Long idTurno, String path) throws NotFoundException {
        var turno = turnoRepository.findById(idTurno).orElseThrow(() -> new NotFoundException("Não foi encontrado um turno com id " + idTurno));
        List<SaidaCaixa> saidasCaixas = saidaCaixaRepository.findByTurno(turno);
        return ResponseUtil.sucess(saidasCaixas, "Sucesso", path);
    }

    @Transactional
    public ApiResponse<SaidaCaixa> darSaidaCaixa(SaidaCaixaRequestDto saidaCixaRequest) throws NotFoundException {
        var turnoAtivo = this.obterTurnoAtivo();
        var caixa = caixaRepository.findByTurno(turnoAtivo).orElseThrow(() -> new NotFoundException("Caixa não encontrada para o turno especificado"));

        if (saidaCixaRequest.valor().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("O valor da saída não pode ser menor ou igual a zero");

        }
        else if (caixa.getEntradas().isEmpty() || caixa.getTotalEntradas().compareTo(saidaCixaRequest.valor()) < 0) {

            throw new RuntimeException("O valor que está na caixa  não  é suficiente para efetuar esta operação");
        }

        var novaSaida = new SaidaCaixa();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        novaSaida.setTurno(turnoAtivo);
        novaSaida.setCaixa(caixa);
        novaSaida.setData(LocalDateTime.now());
        novaSaida.setValor(saidaCixaRequest.valor());
        novaSaida.setDescricao(saidaCixaRequest.descricao() + ". Autor: " + ((Usuario) auth.getPrincipal()).getNome());

        if (saidaCixaRequest.idContaAPagar() != null) {
            var contaAPagar = contasRepository.findById(saidaCixaRequest.idContaAPagar()).orElseThrow(() -> new NotFoundException("Não foi encontrada uma conta a pagar com id" + saidaCixaRequest.idContaAPagar()));
            novaSaida.setContasAPagar(contaAPagar);

            if (saidaCixaRequest.valor().equals(contaAPagar.getValorAPagar())) {
                contaAPagar.setEstado(EstadoPagamento.PAGO);
            } else {
                contaAPagar.setValorAPagar(contaAPagar.getValorAPagar().subtract(saidaCixaRequest.valor()));
            }
            contasRepository.save(contaAPagar);
        }

        var saidaRegistrada = saidaCaixaRepository.save(novaSaida);

        caixa.registrarSaida(saidaRegistrada);
        caixaRepository.save(caixa);

        return ResponseUtil.sucess(saidaRegistrada, "Sucesso", "");
    }

    public Turno obterTurnoAtivo() throws NotFoundException {
        return turnoRepository.findByStatus(StatusTurno.ABERTO).orElseThrow(() -> new NotFoundException("Nenhum turno está aberto."));
    }

}
