package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

@Service
public class ContratServiceImpl extends GenericServiceImpl<Contrat, Long> implements IContratService {

    private final IContratRepository contratRepository;

    public ContratServiceImpl(IContratRepository contratRepository) {
        super(contratRepository);
        this.contratRepository = contratRepository;
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
    }

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat != null && contrat.getPaiements() != null) {
            contrat.getPaiements().forEach(paiement -> {
                if (paiement == null || paiement.getIdPaiement() != null) {
                    throw new IllegalArgumentException("Les paiements du nouveau contrat doivent etre nouveaux");
                }
                paiement.setContrat(contrat);
            });
        }
        return super.create(contrat);
    }

    @Override
    public void deleteById(Long id) {
        Contrat contrat = findById(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().setContrat(null);
            contratRepository.flush();
        }
        contratRepository.delete(contrat);
    }
}
