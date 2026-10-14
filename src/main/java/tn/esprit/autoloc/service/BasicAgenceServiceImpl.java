package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicAgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return agence != null
                && (isNew || agence.getIdAgence() != null)
                && Utils.isNotEmpty(agence.getNom())
                && Utils.isNotEmpty(agence.getVille());
    }

    @Override
    public Agence create(Agence agence) {
        if (agence != null && agence.getIdAgence() != null) {
            throw new IllegalArgumentException("Id agence doit etre null!!!");
        }
        if (!check(agence, true)) {
            throw new IllegalArgumentException("Les donnees de l'agence sont invalides");
        }

        return agenceRepository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", String.valueOf(id)));
    }

    @Override
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existingAgence = findById(id);
        agence.setIdAgence(id);

        if (!check(agence, false)) {
            throw new IllegalArgumentException("Les donnees de l'agence sont invalideees");
        }

        existingAgence.setNom(agence.getNom());
        existingAgence.setVille(agence.getVille());
        existingAgence.setAdresse(agence.getAdresse());
        existingAgence.setTelephone(agence.getTelephone());
        existingAgence.setVehicules(agence.getVehicules());
        existingAgence.setEmployes(agence.getEmployes());

        return agenceRepository.save(existingAgence);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", String.valueOf(id));
        }

        agenceRepository.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        return !findById(id).getVehicules().isEmpty();
    }
}
