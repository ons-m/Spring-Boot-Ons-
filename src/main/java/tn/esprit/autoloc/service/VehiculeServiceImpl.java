package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.calcul.ITarifStrategy;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class VehiculeServiceImpl extends GenericServiceImpl<Vehicule, Long> implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final Map<VehiculeOffre, ITarifStrategy> strategies = new EnumMap<>(VehiculeOffre.class);

    public VehiculeServiceImpl(IVehiculeRepository vehiculeRepository, IAgenceRepository agenceRepository,
                               IEquipementRepository equipementRepository, List<ITarifStrategy> strategies) {
        super(vehiculeRepository);
        this.vehiculeRepository = vehiculeRepository;
        this.agenceRepository = agenceRepository;
        this.equipementRepository = equipementRepository;
        // Spring injecte automatiquement toutes les strategies annotees @Component.
        for (ITarifStrategy strategie : strategies) {
            this.strategies.put(strategie.getOffre(), strategie);
        }
    }

    @Override
    public Vehicule desaffecterAgence(Long idVehicule) {
        Vehicule vehicule = findById(idVehicule);
        vehicule.setAgence(null);
        return vehicule;
    }

    @Override
    public Vehicule affecterAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = findById(idVehicule);
        vehicule.setAgence(agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", String.valueOf(idAgence))));
        return vehicule;
    }

    @Override
    public Vehicule affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);
        var equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", String.valueOf(idEquipement)));
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
        return vehicule;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours, VehiculeOffre offre) {
        ITarifStrategy strategie = strategies.get(offre);
        if (strategie == null) {
            throw new IllegalArgumentException("Offre obligatoire ou non prise en charge");
        }
        return strategie.calculTarif(vehicule, nbJours);
    }

    @Override
    public boolean check(Vehicule vehicule, boolean isNew) {
        return vehicule != null
                && Utils.isNotEmpty(vehicule.getImmatriculation())
                && Utils.isNotEmpty(vehicule.getMarque())
                && Utils.isNotEmpty(vehicule.getModele())
                && vehicule.getCategorie() != null
                && vehicule.getTarifJournalier() != null
                && vehicule.getStatut() != null;
    }

    @Override
    protected String getEntityName() {
        return "Vehicule";
    }

    @Override
    protected Long getId(Vehicule vehicule) {
        return vehicule.getIdVehicule();
    }

    @Override
    protected void setFields(Vehicule source, Vehicule target) {
        target.setImmatriculation(source.getImmatriculation());
        target.setMarque(source.getMarque());
        target.setModele(source.getModele());
        target.setCategorie(source.getCategorie());
        target.setTarifJournalier(source.getTarifJournalier());
        target.setStatut(source.getStatut());
        target.setAgence(source.getAgence());
        target.setEquipements(source.getEquipements());
    }

    @Override
    public void deleteById(Long id) {
        Vehicule vehicule = findById(id);
        if (!vehicule.getEquipements().isEmpty()) {
            throw new DataIntegrityViolationException("Supprimez les equipements du vehicule avant de le supprimer");
        }
        vehicule.getMaintenances().forEach(maintenance -> maintenance.setVehicule(null));
        vehiculeRepository.flush();
        vehiculeRepository.delete(vehicule);
    }
}
