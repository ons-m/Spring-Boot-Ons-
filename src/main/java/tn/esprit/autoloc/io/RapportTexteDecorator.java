package tn.esprit.autoloc.io;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;

/** Decorator : ajoute l'en-tete au rapport de base sans modifier celui-ci. */
@Component
public class RapportTexteDecorator implements IRapportAgence {
    private final RapportTotal rapport;

    public RapportTexteDecorator(RapportTotal rapport) {
        this.rapport = rapport;
    }

    @Override
    public String ecrire(Agence agence, double total) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Id : " + agence.getIdAgence() + "; Nom : " + agence.getNom()
                + "; Tel : " + agence.getTelephone() + "\n");
        stringBuilder.append(rapport.ecrire(agence, total));
        return stringBuilder.toString();
    }
}
