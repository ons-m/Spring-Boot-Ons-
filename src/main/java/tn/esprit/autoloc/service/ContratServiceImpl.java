package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

@Service
public class ContratServiceImpl extends GenericServiceImpl<Contrat, Long> implements IContratService {

    public ContratServiceImpl(IContratRepository contratRepository) {
        super(contratRepository);
    }

    @Override
    public boolean check(Contrat contrat, boolean isNew) {
        return contrat != null
                && contrat.getDateSignature() != null
                && contrat.getMontantTotal() != null
                && contrat.getValide() != null;
    }

    @Override
    protected String getEntityName() {
        return "Contrat";
    }

    @Override
    protected Long getId(Contrat contrat) {
        return contrat.getIdContrat();
    }

    @Override
    protected void setFields(Contrat source, Contrat target) {
        target.setDateSignature(source.getDateSignature());
        target.setMontantTotal(source.getMontantTotal());
        target.setValide(source.getValide());
        target.setReservation(source.getReservation());
        target.setPaiements(source.getPaiements());
    }
}
