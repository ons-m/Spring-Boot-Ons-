package tn.esprit.autoloc.io;

import tn.esprit.autoloc.domain.Agence;

public interface IRapportAgence {
    String ecrire(Agence agence, double total);
}
