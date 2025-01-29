package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.entities.Pratica;
import it.epicode.patronato_gestionale.entities.Appuntamento;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.repositories.PraticaRepository;
import it.epicode.patronato_gestionale.repositories.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PraticaRepository praticaRepository;

    @Autowired
    private AppuntamentoRepository appuntamentoRepository;

    public Map<StatoPratica, Long> getPraticheByStato() {
        List<Pratica> pratiche = praticaRepository.findAll();
        return pratiche.stream()
                .collect(Collectors.groupingBy(Pratica::getStato, Collectors.counting()));
    }

    public long getTotalPratiche() {
        return praticaRepository.count();
    }

    public long getTotalAppuntamenti() {
        return appuntamentoRepository.count();
    }

    public long getAppuntamentiQuestoMese() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        return appuntamentoRepository.findAll().stream()
                .filter(a -> !a.getDataOra().toLocalDate().isBefore(startOfMonth) &&
                        !a.getDataOra().toLocalDate().isAfter(endOfMonth))
                .count();
    }
}