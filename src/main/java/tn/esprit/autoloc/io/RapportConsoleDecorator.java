package tn.esprit.autoloc.io;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;

/** Decorator gere par Spring : affiche l'en-tete, puis delegue le retour du total. */
@Component
public class RapportConsoleDecorator implements IRapportAgence {
    private final RapportTotal rapport;

    public RapportConsoleDecorator(RapportTotal rapport) {
        this.rapport = rapport;
    }

    @Override
    public String ecrire(Agence agence, double total) {
        System.out.println("Id : " + agence.getIdAgence() + "; Nom : " + agence.getNom()
                + "; Tel : " + agence.getTelephone());
        return rapport.ecrire(agence, total);
    }
}
