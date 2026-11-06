package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.io.RapportFactory;

@Service
@Primary
public class AgenceServiceImpl extends GenericServiceImpl<Agence, Long>
        implements IAgenceService, ISimulationAgenceService {

    private final IVehiculeService vehiculeService;
    private final RapportFactory rapportFactory;

    public AgenceServiceImpl(IAgenceRepository repository, IVehiculeService vehiculeService,
            RapportFactory rapportFactory) {
        super(repository);
        this.vehiculeService = vehiculeService;
        this.rapportFactory = rapportFactory;
    }

    @Override
    public String simuler(Agence agence, int nbJours, VehiculeOffre offre) {
        double total = 0;

        for (Vehicule vehicule : agence.getVehicules()) {
            total += vehiculeService.calculTarif(vehicule, nbJours, offre);
        }

        // La Factory choisit l'affichage ; le calcul ne depend pas du mode de rapport.
        return rapportFactory.creer().ecrire(agence, total);
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
        super.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        return !findById(id).getVehicules().isEmpty();
    }
}
