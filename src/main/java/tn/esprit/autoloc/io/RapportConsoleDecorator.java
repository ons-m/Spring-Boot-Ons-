package tn.esprit.autoloc.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;

/** Decorator gere par Spring : affiche l'en-tete, puis delegue le retour du total. */
@Component
public class RapportConsoleDecorator implements IRapportAgence {
    private static final Logger LOGGER = LoggerFactory.getLogger(RapportConsoleDecorator.class);
    private final RapportTotal rapport;

    public RapportConsoleDecorator(RapportTotal rapport) {
        this.rapport = rapport;
    }

    @Override
    public String ecrire(Agence agence, double total) {
        LOGGER.info("Id : {}; Nom : {}; Tel : {}", agence.getIdAgence(), agence.getNom(), agence.getTelephone());
        return rapport.ecrire(agence, total);
    }
}
