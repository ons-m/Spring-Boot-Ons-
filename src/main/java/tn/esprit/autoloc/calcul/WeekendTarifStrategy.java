package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component
public class WeekendTarifStrategy implements ITarifStrategy {
    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.WEEKEND;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue();
        tarifJournalier *= 0.8;
        return tarifJournalier * nbJours;
    }
}
