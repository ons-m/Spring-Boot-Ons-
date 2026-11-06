package tn.esprit.autoloc.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Factory : le service demande un rapport sans connaitre sa classe concrete. */
@Component
public class RapportFactory {
    private final ModeRapport mode;
    private final RapportTexteDecorator rapportTexte;
    private final RapportConsoleDecorator rapportConsole;

    public RapportFactory(@Value("${autoloc.rapport.mode:TEXTE}") ModeRapport mode,
            RapportTexteDecorator rapportTexte, RapportConsoleDecorator rapportConsole) {
        this.mode = mode;
        this.rapportTexte = rapportTexte;
        this.rapportConsole = rapportConsole;
    }

    public IRapportAgence creer() {
        if (mode == ModeRapport.CONSOLE) {
            return rapportConsole;
        }
        return rapportTexte;
    }
}
