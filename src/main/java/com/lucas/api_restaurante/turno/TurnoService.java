package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.caixa.CaixaRepository;
import com.lucas.api_restaurante.entradacaixa.EntradaCaixa;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.saidacaixa.SaidaCaixa;
import com.lucas.api_restaurante.usuario.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {
    private final TurnoRepository turnoRepository;
    private final CaixaRepository caixaRepository;

    public TurnoService(TurnoRepository turnoRepository, CaixaRepository caixaRepository) {
        this.turnoRepository = turnoRepository;
        this.caixaRepository = caixaRepository;
    }

    public ApiResponse<List<Turno>> listarTurnos(Pageable pageable, String path) {
        List<Turno> turnos = turnoRepository.findAll();
        return ResponseUtil.sucess(turnos, "Sucesso", path);
    }

    public ApiResponse<Turno> buscarTurnoPorId(Long id, String path) throws RecursoNaoEncontradoException {
        if (turnoRepository.findById(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Não foi encontrado um turno com id " + id);
        }
        return ResponseUtil.sucess(turnoRepository.findById(id).get(), "Sucesso", path);
    }

    public ApiResponse<TurnoResponseDto> abrirTurno(BigDecimal valorInicial) {
        List<Turno> turnos = turnoRepository.findAll()
                .stream().filter(turno -> turno.getStatus() == StatusTurno.ABERTO).toList();

        if (!turnos.isEmpty()) {
            throw new RuntimeException("Erro ao tentar abrir o Turno. Já tem um turno aberto.");
        }
        Turno novoTurno = new Turno();
        novoTurno.setHoraAbertura(LocalTime.now());
        novoTurno.setData(LocalDate.now());
        novoTurno.setStatus(StatusTurno.ABERTO);

        var turnoAberto = turnoRepository.save(novoTurno);

        var novaCaixa = new Caixa();
        novaCaixa.setTurno(turnoAberto);
        novaCaixa.setValorInicial(valorInicial);

        var caixaAberta = caixaRepository.save(novaCaixa);
        var responsavelPelaAbertura = (Usuario) SecurityContextHolder.getContext().getAuthentication();
        TurnoResponseDto turnoResponse = new TurnoResponseDto(turnoAberto.getId(), turnoAberto.getData(), turnoAberto.getHoraAbertura(), turnoAberto.getStatus(), responsavelPelaAbertura.getNome(), novaCaixa);
        return ResponseUtil.sucess(turnoResponse, "Sucesso", "");
    }

    public ApiResponse<Turno> obterTurnoAtivo() {
        var turnoAtivo = turnoRepository.findAll().stream().filter(turno -> turno.getStatus() == StatusTurno.ABERTO).toList().getFirst();
        if (turnoAtivo == null) {
            throw new RuntimeException("Nenhuma turno aberto.");
        }
        return ResponseUtil.sucess(turnoAtivo, "Sucesso", "");
    }

    public ApiResponse<TurnoResponseDto> fecharTurno() {
        var turnoAtivo = this.obterTurnoAtivo().data();

        turnoAtivo.setStatus(StatusTurno.FECHADO);
        turnoAtivo.setHoraFecho(LocalTime.now());
        turnoRepository.save(turnoAtivo);
        var caixaAberta=caixaRepository.findAll()
                .stream().filter(caixa -> caixa.getTurno().getId().equals(turnoAtivo.getId())).findFirst().get();

        /*

        BigDecimal entradasDoDia=caixaAberta.getEntradas().stream().reduce((entrada1,entrada2)-> {
            int i = entrada1.getPagamento().getValorPago() + entrada2.getPagamento().getValorPago();
            return i;
        }).get();
        List<SaidaCaixa> saidasDoDia=caixaAberta.getSaidas();
        BigDecimal faturamentoLiquido=entradasDoDia.stream().map(entrada->e)
        caixaAberta.setFaturamentoLiquido();

        */

        return ResponseUtil.sucess(null, "Sucesso", "");
    }

}
