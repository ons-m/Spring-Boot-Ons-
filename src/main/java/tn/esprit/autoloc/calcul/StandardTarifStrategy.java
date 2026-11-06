package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component
public class StandardTarifStrategy implements ITarifStrategy {
    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.STANDARD;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue();
        return tarifJournalier * nbJours;
    }
}
