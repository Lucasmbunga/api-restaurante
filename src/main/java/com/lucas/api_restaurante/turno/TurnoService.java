package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.caixa.CaixaAberturaResponseDto;
import com.lucas.api_restaurante.caixa.CaixaRepository;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.saidacaixa.SaidaCaixa;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
        List<Turno> turnos = turnoRepository.findAll(pageable).getContent();
        return ResponseUtil.sucess(turnos, "Sucesso", path);
    }

    public ApiResponse<Turno> buscarTurnoPorId(Long id, String path) throws RecursoNaoEncontradoException {
        if (turnoRepository.findById(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Não foi encontrado um turno com id " + id);
        }
        return ResponseUtil.sucess(turnoRepository.findById(id).get(), "Sucesso", path);
    }

    @Transactional
    public ApiResponse<TurnoResponseDto> abrirTurno(BigDecimal valorInicial) throws RecursoNaoEncontradoException {

        if (turnoRepository.findByStatus(StatusTurno.ABERTO).isPresent()) {
            throw new IllegalStateException("Erro ao tentar abrir o Turno. Já tem um turno aberto.");
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
        var garcomLogado = SecurityContextHolder.getContext().getAuthentication();

        if (garcomLogado == null) {
            throw new RecursoNaoEncontradoException("Garçom não logado");
        }
        var responsavelPelaAbertura = (Usuario) garcomLogado.getPrincipal();
        TurnoResponseDto turnoResponse = new TurnoResponseDto(turnoAberto.getId(), turnoAberto.getData(), turnoAberto.getHoraAbertura(), turnoAberto.getStatus(), responsavelPelaAbertura.getNome(), new CaixaAberturaResponseDto(novaCaixa.getId(), novaCaixa.getValorInicial()));
        return ResponseUtil.sucess(turnoResponse, "Sucesso", "");
    }

    public ApiResponse<Optional<Turno>> obterTurnoAtivo() {
        var turnoAtivo = turnoRepository.findByStatus(StatusTurno.ABERTO);
        if (turnoAtivo.isEmpty()) {
            throw new RuntimeException("Nenhuma turno aberto.");
        }
        return ResponseUtil.sucess(turnoAtivo, "Sucesso", "");
    }

    @Transactional
    public ApiResponse<FechoTurnoResponseDto> fecharTurno() throws RecursoNaoEncontradoException {

        var turnoAtivo = this.obterTurnoAtivo().data()
                .orElseThrow(()->new RecursoNaoEncontradoException("Nenhuma turno aberto."));

        turnoAtivo.setStatus(StatusTurno.FECHADO);
        turnoAtivo.setHoraFecho(LocalTime.now());

        var caixaAberta = caixaRepository.findByTurno(turnoAtivo).orElseThrow(()->new RecursoNaoEncontradoException("Caixa não encontrado."));

        BigDecimal totalEntradas;
        BigDecimal faturamentoLiquido;

        if (!caixaAberta.getEntradas().isEmpty() && !caixaAberta.getSaidas().isEmpty()) {

            totalEntradas = this.calcularTotalDeEntradas(caixaAberta);
            var totalSaidas=this.calcularTotalSaidas(caixaAberta);

            faturamentoLiquido = totalEntradas.subtract(totalSaidas);
            caixaAberta.setFaturamentoLiquido(faturamentoLiquido);
        } else if (!caixaAberta.getEntradas().isEmpty()) {

            faturamentoLiquido = calcularTotalDeEntradas(caixaAberta);
            caixaAberta.setFaturamentoLiquido(faturamentoLiquido);
        }

        var caixaFechada = caixaRepository.save(caixaAberta);
        var turnoFechado = turnoRepository.save(turnoAtivo);
        return ResponseUtil.sucess(new FechoTurnoResponseDto(turnoFechado, caixaFechada), "Sucesso", "");

    }
    public BigDecimal calcularTotalDeEntradas(Caixa caixaAberta){
        return caixaAberta.getEntradas()
                .stream().map(entrada -> entrada.getPagamento().getValorPago())
                .reduce(BigDecimal.ZERO,BigDecimal::add);

    }
    public BigDecimal calcularTotalSaidas(Caixa caixaAberta){
        return caixaAberta.getSaidas()
                .stream().map(SaidaCaixa::getValor)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public ApiResponse<Void> excluirTurno(Long id) {
        turnoRepository.deleteById(id);
        return ResponseUtil.sucess("Sucesso", "");
    }

}
