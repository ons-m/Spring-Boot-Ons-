package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component
public class LongueDureeTarifStrategy implements ITarifStrategy {
    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.LONGUE_DUREE;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue();
        tarifJournalier *= 0.75;
        double total = tarifJournalier * nbJours;
        if (total > 2000) {
            total *= 0.95;
        }
        return total;
    }
}
