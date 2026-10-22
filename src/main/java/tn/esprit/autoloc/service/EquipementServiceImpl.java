package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.IEquipementRepository;

@Service
public class EquipementServiceImpl extends GenericServiceImpl<Equipement, Long> implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    public EquipementServiceImpl(IEquipementRepository equipementRepository) {
        super(equipementRepository);
        this.equipementRepository = equipementRepository;
    }

    @Override
    public boolean check(Equipement equipement, boolean isNew) {
        return equipement != null && Utils.isNotEmpty(equipement.getLibelle());
    }

    @Override
    protected String getEntityName() {
        return "Equipement";
    }

    @Override
    protected Long getId(Equipement equipement) {
        return equipement.getIdEquipement();
    }

    @Override
    protected void setFields(Equipement source, Equipement target) {
        target.setLibelle(source.getLibelle());
    }

    @Override
    public void deleteById(Long id) {
        Equipement equipement = findById(id);
        equipement.getVehicules().forEach(vehicule -> vehicule.getEquipements().remove(equipement));
        equipementRepository.flush();
        equipementRepository.delete(equipement);
    }
}
