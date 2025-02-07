package it.epicode.patronato_gestionale.services;

import it.epicode.patronato_gestionale.dto.FatturaRequest;
import it.epicode.patronato_gestionale.entities.Fattura;
import it.epicode.patronato_gestionale.enums.FatturaStato;
import it.epicode.patronato_gestionale.repositories.FatturaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class FatturaService {

    @Autowired
    private FatturaRepository fatturaRepository;

    public Page<Fattura> getFatturePaginate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fatturaRepository.findAll(pageable);
    }

    public List<Fattura> getAllFatture() {
        return fatturaRepository.findAll();
    }

    public Fattura getFatturaById(Long id) {
        return fatturaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fattura non trovata con ID: " + id));
    }

    @Transactional
    public Fattura createFattura(FatturaRequest request) {
        if (fatturaRepository.existsByNumero(request.getNumero())) {
            throw new IllegalArgumentException("Esiste già una fattura con questo numero.");
        }

        Fattura fattura = new Fattura();
        fattura.setNumero(request.getNumero());
        fattura.setDataEmissione(request.getDataEmissione());
        fattura.setImporto(request.getImporto());
        fattura.setDescrizione(request.getDescrizione());
        fattura.setNome(request.getNome());
        fattura.setCognome(request.getCognome());
        fattura.setCodiceFiscale(request.getCodiceFiscale());
        fattura.setIndirizzo(request.getIndirizzo());
        fattura.setTelefono(request.getTelefono());
        fattura.setEmail(request.getEmail());
        fattura.setStato(request.getStato());

        return fatturaRepository.save(fattura);
    }

    @Transactional
    public Fattura updateFattura(Long id, FatturaRequest request) {
        Fattura fattura = getFatturaById(id);

        if (fatturaRepository.existsByNumero(request.getNumero()) && !fattura.getNumero().equals(request.getNumero())) {
            throw new IllegalArgumentException("Il numero della fattura è già in uso.");
        }

        fattura.setNumero(request.getNumero());
        fattura.setDataEmissione(request.getDataEmissione());
        fattura.setImporto(request.getImporto());
        fattura.setDescrizione(request.getDescrizione());
        fattura.setNome(request.getNome());
        fattura.setCognome(request.getCognome());
        fattura.setCodiceFiscale(request.getCodiceFiscale());
        fattura.setIndirizzo(request.getIndirizzo());
        fattura.setTelefono(request.getTelefono());
        fattura.setEmail(request.getEmail());
        fattura.setStato(request.getStato());

        return fatturaRepository.save(fattura);
    }

    @Transactional
    public void deleteFattura(Long id) {
        if (!fatturaRepository.existsById(id)) {
            throw new EntityNotFoundException("Fattura non trovata con ID: " + id);
        }
        fatturaRepository.deleteById(id);
    }

    public Map<FatturaStato, Long> getFatturePerStato() {
        return fatturaRepository.findAll().stream()
                .collect(Collectors.groupingBy(Fattura::getStato, Collectors.counting()));
    }
}