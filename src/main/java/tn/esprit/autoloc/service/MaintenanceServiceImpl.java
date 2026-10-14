package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

@Service
public class MaintenanceServiceImpl extends GenericServiceImpl<Maintenance, Long> implements IMaintenanceService {

    public MaintenanceServiceImpl(IMaintenanceRepository maintenanceRepository) {
        super(maintenanceRepository);
    }

    @Override
    public boolean check(Maintenance maintenance, boolean isNew) {
        return maintenance != null
                && maintenance.getDateDebut() != null
                && maintenance.getDateFin() != null
                && Utils.isNotEmpty(maintenance.getDescription());
    }

    @Override
    protected String getEntityName() {
        return "Maintenance";
    }

    @Override
    protected Long getId(Maintenance maintenance) {
        return maintenance.getIdMaintenance();
    }

    @Override
    protected void setFields(Maintenance source, Maintenance target) {
        target.setDateDebut(source.getDateDebut());
        target.setDateFin(source.getDateFin());
        target.setDescription(source.getDescription());
        target.setVehicule(source.getVehicule());
    }
}
