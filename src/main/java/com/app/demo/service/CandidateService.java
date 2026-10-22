package com.app.demo.service;

import com.app.demo.entity.Candidate;
import com.app.demo.entity.InfoModel;
import com.app.demo.repo.CandidateRepository;
import com.app.demo.repo.SearchDao;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CandidateService {

    private final CandidateRepository data;

    public CandidateService(CandidateRepository data) {
        this.data = data;
    }

    @Transactional
    public Candidate addCandidate(Candidate candidate) {
        ensureInfoModel(candidate);
        updateStageDates(candidate);
        return data.save(candidate);
    }

    @Transactional
    public List<Candidate> addCandidates(List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        candidates.forEach(c -> {
            ensureInfoModel(c);
            c.getInfoModel().setRegisterDate(LocalDate.now());

            updateStageDates(c);
        });
        return data.saveAll(candidates);
    }

    @Transactional
    public Candidate updateCandidate(Long id, Candidate incoming) {
        if (id == null) {
            throw new IllegalArgumentException("Candidate ID cannot be null");
        }

        Candidate existing = data.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with ID " + id));

        applyCandidateUpdates(existing, incoming);
        return data.save(existing);
    }

    @Transactional
    public List<Candidate> updateCandidates(List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Request body cannot be empty.");
        }

        List<Long> ids = candidates.stream()
                .map(c -> Optional.ofNullable(c)
                        .map(Candidate::getId)
                        .orElseThrow(() -> new IllegalArgumentException("Candidate ID missing in bulk update list.")))
                .toList();

        // Single batch fetch to avoid N+1 queries
        Map<Long, Candidate> existingCandidatesMap = data.findAllById(ids).stream()
                .collect(Collectors.toMap(Candidate::getId, Function.identity()));

        List<Candidate> updatedList = new ArrayList<>();
        for (Candidate incoming : candidates) {
            Candidate existing = existingCandidatesMap.get(incoming.getId());
            if (existing == null) {
                throw new EntityNotFoundException("Candidate not found with ID " + incoming.getId());
            }
            applyCandidateUpdates(existing, incoming);
            updatedList.add(existing);
        }

        return data.saveAll(updatedList);
    }

    public List<Candidate> getCandidates() {
        return data.findAll();
    }

    public Candidate getCandidate(Long id) {
        return data.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with ID: " + id));
    }

    @Transactional
    public void removeCandidate(Long id) {
        if (!data.existsById(id)) {
            throw new EntityNotFoundException("Candidate not found with ID " + id);
        }
        data.deleteById(id);
    }

    @Transactional
    public void removeCandidates(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        data.deleteAllById(ids);
    }

    public List<Candidate> searchCandidates(String keyword, String dataField, String category, String stage, LocalDate start, LocalDate end) {
        return data.findAll(SearchDao.findAllBy(keyword, dataField, category, stage, start, end));
    }

    // --- Private Helper Methods ---

    private void applyCandidateUpdates(Candidate existing, Candidate incoming) {
        if (incoming.getFirstName() != null) {
            if (incoming.getFirstName().trim().isBlank()) {
                throw new IllegalArgumentException("First name cannot be blank");
            }
            existing.setFirstName(incoming.getFirstName().trim().toLowerCase());
        }

        if (incoming.getLastName() != null) {
            if (incoming.getLastName().trim().isBlank()) {
                throw new IllegalArgumentException("Last name cannot be blank");
            }
            existing.setLastName(incoming.getLastName().trim().toLowerCase());
        }

        if (incoming.getPhone() != null) {
            if (!incoming.getPhone().matches("^0[5-7][0-9]{8}$")) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
            existing.setPhone(incoming.getPhone().trim());
        }

        if (incoming.getCategory() != null) {
            existing.setCategory(incoming.getCategory());
        }

        // Only update stage dates when the stage actually changes.
        // This runs BEFORE manual date overrides below, so an explicit
        // date typed in the same request always wins over the auto-stamp.
        if (incoming.getStage() != null && incoming.getStage() != existing.getStage()) {
            ensureInfoModel(existing);
            existing.setStage(incoming.getStage());
            updateStageDates(existing);
        }

        if (incoming.getInfoModel() != null) {
            ensureInfoModel(existing);
            InfoModel incomingInfo = incoming.getInfoModel();
            InfoModel existingInfo = existing.getInfoModel();
            if (incomingInfo.getBirthDate() != null) {
                existingInfo.setBirthDate(incomingInfo.getBirthDate());
            }
            if (incomingInfo.getCodeDate() != null) {
                existingInfo.setCodeDate(incomingInfo.getCodeDate());
            }
            if (incomingInfo.getCreneauDate() != null) {
                existingInfo.setCreneauDate(incomingInfo.getCreneauDate());
            }
            if (incomingInfo.getCirculationDate() != null) {
                existingInfo.setCirculationDate(incomingInfo.getCirculationDate());
            }
        }
    }

    private void ensureInfoModel(Candidate candidate) {
        if (candidate.getInfoModel() == null) {
            candidate.setInfoModel(new InfoModel());
        }
    }

    private void updateStageDates(Candidate candidate) {
        if (candidate.getStage() == null) return;

        switch (candidate.getStage()) {
            case CODE -> candidate.getInfoModel().setCodeDate(LocalDate.now());
            case CRENEAU -> candidate.getInfoModel().setCreneauDate(LocalDate.now());
            case CIRCULATION -> candidate.getInfoModel().setCirculationDate(LocalDate.now());
        }
    }
}