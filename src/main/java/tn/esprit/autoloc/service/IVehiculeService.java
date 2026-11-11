package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.calcul.VehiculeOffre;

public interface IVehiculeService extends IGenericService<Vehicule, Long> {
    Vehicule desaffecterAgence(Long idVehicule);
    Vehicule affecterAgence(Long idVehicule, Long idAgence);
    Vehicule affecterEquipement(Long idVehicule, Long idEquipement);

    double calculTarif(Vehicule vehicule, int jours, VehiculeOffre offre);
}
