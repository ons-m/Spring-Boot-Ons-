package tn.esprit.autoloc.io;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;

/** Rapport de base : il retourne uniquement le total. */
@Component
public class RapportTotal implements IRapportAgence {
    @Override
    public String ecrire(Agence agence, double total) {
        return "Total : " + total + "\n";
    }
}
