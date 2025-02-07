package it.epicode.patronato_gestionale.controllers;

import it.epicode.patronato_gestionale.enums.FatturaStato;
import it.epicode.patronato_gestionale.enums.StatoPratica;
import it.epicode.patronato_gestionale.services.DashboardService;
import it.epicode.patronato_gestionale.services.FatturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private FatturaService fatturaService;

    // 🔹 Pratiche - Statistiche
    @GetMapping("/pratiche-stato")
    public ResponseEntity<Map<StatoPratica, Long>> getPraticheByStato() {
        return ResponseEntity.ok(dashboardService.getPraticheByStato());
    }

    @GetMapping("/totale-pratiche")
    public ResponseEntity<Long> getTotalPratiche() {
        return ResponseEntity.ok(dashboardService.getTotalPratiche());
    }

    @GetMapping("/totale-appuntamenti")
    public ResponseEntity<Long> getTotalAppuntamenti() {
        return ResponseEntity.ok(dashboardService.getTotalAppuntamenti());
    }

    @GetMapping("/appuntamenti-mese")
    public ResponseEntity<Long> getAppuntamentiQuestoMese() {
        return ResponseEntity.ok(dashboardService.getAppuntamentiQuestoMese());
    }

    // 🔹 Fatture - Statistiche
    @GetMapping("/totale-fatture")
    public ResponseEntity<Long> getTotaleFatture() {
        long totaleFatture = fatturaService.getAllFatture().size();
        return ResponseEntity.ok(totaleFatture);
    }

    @GetMapping("/fatture-stato")
    public ResponseEntity<Map<FatturaStato, Long>> getFatturePerStato() {
        return ResponseEntity.ok(fatturaService.getFatturePerStato());
    }
}