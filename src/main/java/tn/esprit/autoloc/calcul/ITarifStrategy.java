package tn.esprit.autoloc.calcul;

import tn.esprit.autoloc.domain.Vehicule;

/** Strategy : chaque offre fournit son propre calcul. */
public interface ITarifStrategy {
    VehiculeOffre getOffre();

    double calculTarif(Vehicule vehicule, int nbJours);
}
