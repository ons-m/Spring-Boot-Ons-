package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.IAgenceRepository;

@Service
@Primary
public class AgenceServiceImpl extends GenericServiceImpl<Agence, Long> implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    public AgenceServiceImpl(IAgenceRepository agenceRepository) {
        super(agenceRepository);
        this.agenceRepository = agenceRepository;
    }

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return agence != null
                && Utils.isNotEmpty(agence.getNom())
                && Utils.isNotEmpty(agence.getVille())
                && Utils.isNotEmpty(agence.getAdresse())
                && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    protected String getEntityName() {
        return "Agence";
    }

    @Override
    protected Long getId(Agence agence) {
        return agence.getIdAgence();
    }

    @Override
    protected void setFields(Agence source, Agence target) {
        target.setNom(source.getNom());
        target.setVille(source.getVille());
        target.setAdresse(source.getAdresse());
        target.setTelephone(source.getTelephone());
    }

    @Override
    @Transactional
    public Agence update(Long id, Agence agence) {
        if (agence != null && agence.getIdAgence() != null && !id.equals(agence.getIdAgence())) {
            throw new IllegalArgumentException("L'identifiant du corps doit correspondre a celui de l'URL");
        }
        return super.update(id, agence);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Agence agence = findById(id);
        // Les cles etrangeres appartiennent aux enfants : on les detache sans les supprimer.
        agence.getVehicules().forEach(vehicule -> vehicule.setAgence(null));
        agence.getEmployes().forEach(employe -> employe.setAgence(null));
        agenceRepository.flush();
        agenceRepository.delete(agence);
    }

    @Override
    public boolean hasVehicules(Long id) {
        return !findById(id).getVehicules().isEmpty();
    }
}
