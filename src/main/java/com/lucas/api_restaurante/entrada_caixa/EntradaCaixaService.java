package com.lucas.api_restaurante.entrada_caixa;

import com.lucas.api_restaurante.caixa.CaixaRepository;
import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.pagamento.Pagamento;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.turno.Turno;
import com.lucas.api_restaurante.turno.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntradaCaixaService {

    private final EntradaCaixaRepository entradaCaixaRepository;
    private final TurnoRepository turnoRepository;
    private final CaixaRepository caixaRepository;

    public EntradaCaixaService(EntradaCaixaRepository entradaCaixaRepository, TurnoRepository turnoRepository, CaixaRepository caixaRepository) {
        this.entradaCaixaRepository = entradaCaixaRepository;
        this.turnoRepository = turnoRepository;
        this.caixaRepository = caixaRepository;
    }

    @Transactional
    public void darEntradaCaixa(Pagamento pagamento, Turno turno) {
        var novaEntradaCaixa = new EntradaCaixa();

        var caixa = caixaRepository.findByTurno(turno).orElseThrow(()->new RuntimeException("Caixa não encontrada para o turno selecionado"));

        novaEntradaCaixa.setCaixa(caixa);
        novaEntradaCaixa.setTurno(turno);
        novaEntradaCaixa.setPagamento(pagamento);
        novaEntradaCaixa.setData(LocalDateTime.now());
        novaEntradaCaixa.setDescricao("Pagamento de Pedido");
        var entradaRegistrada= entradaCaixaRepository.save(novaEntradaCaixa);

        caixa.darEntradaNaCaixa(entradaRegistrada);
        caixaRepository.save(caixa);
    }

    public ApiResponse<List<EntradaCaixa>> listarTodasEntradasCaixa(Pageable pageable, String path) {
        List<EntradaCaixa> listaDeEntradasCaixa = entradaCaixaRepository.findAll(pageable).getContent();

        return ResponseUtil.sucess(listaDeEntradasCaixa, "Sucesso", path);
    }

    public ApiResponse<List<EntradaCaixa>> listarEntradasCaixaPorTurno(Long idTurno, String path) throws NotFoundException {
        var turno = turnoRepository.findById(idTurno).orElseThrow(() -> new NotFoundException("Turno não encontrado"));
        var caixa = caixaRepository.findByTurno(turno).orElseThrow(()->new RuntimeException("Caixa não encontrada para o turno selecionado"));
        List<EntradaCaixa> entradasDoTurno = caixa.getEntradas();

        return ResponseUtil.sucess(entradasDoTurno, "Sucesso", path);
    }

    public ApiResponse<List<EntradaCaixa>> listarEntradasCaixaPorData(LocalDate data) {
        List<EntradaCaixa> entradasFiltradas = entradaCaixaRepository.findAll()
                .stream().filter(entradaCaixa -> entradaCaixa.getData().toLocalDate().equals(data)).toList();

        return ResponseUtil.sucess(entradasFiltradas, "Sucesso", "");
    }

}
