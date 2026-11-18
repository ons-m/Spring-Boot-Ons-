package tn.esprit.autoloc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import tn.esprit.autoloc.calcul.ITarifStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.IRapportAgence;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.AgenceServiceImpl;
import tn.esprit.autoloc.service.BasicAgenceServiceImpl;
import tn.esprit.autoloc.service.ClientServiceImpl;
import tn.esprit.autoloc.service.ContratServiceImpl;
import tn.esprit.autoloc.service.EmployeServiceImpl;
import tn.esprit.autoloc.service.EquipementServiceImpl;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.service.MaintenanceServiceImpl;
import tn.esprit.autoloc.service.PaiementServiceImpl;
import tn.esprit.autoloc.service.ReservationServiceImpl;
import tn.esprit.autoloc.service.VehiculeServiceImpl;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceUnitTests {

    @Mock private IAgenceRepository agenceRepository;
    @Mock private IClientRepository clientRepository;
    @Mock private IContratRepository contratRepository;
    @Mock private IEmployeRepository employeRepository;
    @Mock private IEquipementRepository equipementRepository;
    @Mock private IMaintenanceRepository maintenanceRepository;
    @Mock private IPaiementRepository paiementRepository;
    @Mock private IReservationRepository reservationRepository;
    @Mock private IVehiculeRepository vehiculeRepository;
    @Mock private IVehiculeService vehiculeService;
    @Mock private IEmployeService employeService;
    @Mock private IAgenceService agenceServiceProxy;
    @Mock private RapportFactory rapportFactory;
    @Mock private IRapportAgence rapport;
    @Mock private ITarifStrategy standardStrategy;
    @Mock private ITarifStrategy weekendStrategy;
    @Mock private ITarifStrategy longueDureeStrategy;

    private AgenceServiceImpl agenceService;
    private BasicAgenceServiceImpl basicAgenceService;
    private ClientServiceImpl clientService;
    private ContratServiceImpl contratService;
    private EmployeServiceImpl employeServiceImpl;
    private EquipementServiceImpl equipementServiceImpl;
    private MaintenanceServiceImpl maintenanceServiceImpl;
    private PaiementServiceImpl paiementServiceImpl;
    private ReservationServiceImpl reservationServiceImpl;
    private VehiculeServiceImpl vehiculeServiceImpl;

    @BeforeEach
    void initialiserServices() {
        agenceService = new AgenceServiceImpl(agenceRepository, vehiculeService,
                rapportFactory, employeService, agenceServiceProxy);
        basicAgenceService = new BasicAgenceServiceImpl(agenceRepository, employeService);
        clientService = new ClientServiceImpl(clientRepository);
        contratService = new ContratServiceImpl(contratRepository);
        employeServiceImpl = new EmployeServiceImpl(employeRepository);
        equipementServiceImpl = new EquipementServiceImpl(equipementRepository);
        maintenanceServiceImpl = new MaintenanceServiceImpl(maintenanceRepository);
        paiementServiceImpl = new PaiementServiceImpl(paiementRepository);
        reservationServiceImpl = new ReservationServiceImpl(reservationRepository);
        when(standardStrategy.getOffre()).thenReturn(VehiculeOffre.STANDARD);
        when(weekendStrategy.getOffre()).thenReturn(VehiculeOffre.WEEKEND);
        when(longueDureeStrategy.getOffre()).thenReturn(VehiculeOffre.LONGUE_DUREE);
        vehiculeServiceImpl = new VehiculeServiceImpl(vehiculeRepository, agenceRepository,
                equipementRepository, List.of(standardStrategy, weekendStrategy, longueDureeStrategy));
    }

    @Test
    void agenceServiceAjouteLesEmployesEtMetAJourLEffectif() {
        Agence agence = agence(1L, "Avant");
        Employe employe = employe("Ali");
        when(agenceServiceProxy.update(1L, agence)).thenReturn(agence);
        when(employeService.create(employe)).thenReturn(employe);

        Agence resultat = agenceService.updateAvecEmployes(1L, agence, List.of(employe));

        assertSame(agence, resultat);
        assertEquals(1, resultat.getEffectif());
        assertSame(agence, employe.getAgence());
        verify(employeService).create(employe);
    }

    @Test
    void agenceServiceCalculeEtDelegueLeRapport() {
        Agence agence = agence(1L, "Tunis");
        Vehicule vehicule = vehicule(1L);
        agence.getVehicules().add(vehicule);
        when(vehiculeService.calculTarif(vehicule, 3, VehiculeOffre.STANDARD)).thenReturn(300.0);
        when(rapportFactory.creer()).thenReturn(rapport);
        when(rapport.ecrire(agence, 300.0)).thenReturn("Total : 300.0");

        assertEquals("Total : 300.0", agenceService.simuler(agence, 3, VehiculeOffre.STANDARD));
        verify(vehiculeService).calculTarif(vehicule, 3, VehiculeOffre.STANDARD);
    }

    @Test
    void basicAgenceServiceRefuseUneAgenceInvalide() {
        assertFalse(basicAgenceService.check(new Agence(), true));
    }

    @Test
    void clientServiceCreeUnClientValide() {
        Client client = client();
        when(clientRepository.save(client)).thenReturn(client);

        assertSame(client, clientService.create(client));
        assertTrue(clientService.check(client, true));
        verify(clientRepository).save(client);
    }

    @Test
    void contratServiceAssocieLesPaiementsLorsDeLaCreation() {
        Contrat contrat = contrat();
        Paiement paiement = paiement();
        contrat.setPaiements(List.of(paiement));
        when(contratRepository.save(contrat)).thenReturn(contrat);

        contratService.create(contrat);

        assertSame(contrat, paiement.getContrat());
        verify(contratRepository).save(contrat);
    }

    @Test
    void contratServiceDetacheLaReservationAvantSuppression() {
        Contrat contrat = contrat();
        Reservation reservation = new Reservation();
        reservation.setContrat(contrat);
        contrat.setReservation(reservation);
        when(contratRepository.findById(1L)).thenReturn(Optional.of(contrat));

        contratService.deleteById(1L);

        assertNull(reservation.getContrat());
        verify(contratRepository).flush();
        verify(contratRepository).delete(contrat);
    }

    @Test
    void employeServiceRefuseUnEmployeSansRole() {
        Employe employe = employe("Ali");
        employe.setRole(null);

        assertFalse(employeServiceImpl.check(employe, true));
    }

    @Test
    void equipementServiceDetacheLesVehiculesAvantSuppression() {
        Equipement equipement = new Equipement();
        Vehicule vehicule = vehicule(1L);
        equipement.getVehicules().add(vehicule);
        vehicule.getEquipements().add(equipement);
        when(equipementRepository.findById(1L)).thenReturn(Optional.of(equipement));

        equipementServiceImpl.deleteById(1L);

        assertFalse(vehicule.getEquipements().contains(equipement));
        verify(equipementRepository).flush();
        verify(equipementRepository).delete(equipement);
    }

    @Test
    void maintenanceServiceAccepteUneMaintenanceComplete() {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDateFin(LocalDate.now().plusDays(1));
        maintenance.setDescription("Revision");

        assertTrue(maintenanceServiceImpl.check(maintenance, true));
    }

    @Test
    void paiementServiceLeveUneErreurSiLePaiementEstAbsent() {
        when(paiementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paiementServiceImpl.findById(1L));
    }

    @Test
    void reservationServiceValideLesChampsObligatoires() {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(2));
        reservation.setStatut("CONFIRMEE");

        assertTrue(reservationServiceImpl.check(reservation, true));
    }

    @Test
    void vehiculeServiceAffecteAgenceEtEquipement() {
        Vehicule vehicule = vehicule(1L);
        Agence agence = agence(2L, "Tunis");
        Equipement equipement = new Equipement();
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(agence));
        when(equipementRepository.findById(3L)).thenReturn(Optional.of(equipement));

        vehiculeServiceImpl.affecterAgence(1L, 2L);
        vehiculeServiceImpl.affecterEquipement(1L, 3L);

        assertSame(agence, vehicule.getAgence());
        assertTrue(vehicule.getEquipements().contains(equipement));
    }

    @Test
    void vehiculeServiceDesaffecteUneAgence() {
        Vehicule vehicule = vehicule(1L);
        vehicule.setAgence(agence(2L, "Tunis"));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        vehiculeServiceImpl.desaffecterAgence(1L);

        assertNull(vehicule.getAgence());
    }

    @Test
    void vehiculeServiceCalculeAvecLaStrategieDemandee() {
        Vehicule vehicule = vehicule(1L);
        when(standardStrategy.calculTarif(vehicule, 2)).thenReturn(150.0);

        assertEquals(150.0, vehiculeServiceImpl.calculTarif(vehicule, 2, VehiculeOffre.STANDARD));
    }

    @Test
    void vehiculeServiceRefuseLaSuppressionAvecDesEquipements() {
        Vehicule vehicule = vehicule(1L);
        vehicule.getEquipements().add(new Equipement());
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        assertThrows(DataIntegrityViolationException.class, () -> vehiculeServiceImpl.deleteById(1L));
    }

    @Test
    void agenceServiceRefuseUneAgenceNulle() {
        assertFalse(agenceService.check(null, true));
    }

    @Test
    void agenceServiceRefuseLesChampsVides() {
        Agence agence = agence(1L, "");

        assertFalse(agenceService.check(agence, true));
    }

    @Test
    void agenceServiceAccepteUneAgenceComplete() {
        assertTrue(agenceService.check(agence(1L, "Tunis"), false));
    }

    @Test
    void agenceServiceRefuseUnIdentifiantDifferent() {
        Agence agence = agence(2L, "Sfax");

        assertThrows(IllegalArgumentException.class, () -> agenceService.update(1L, agence));
    }

    @Test
    void agenceServiceRefuseUneListeEmployesAbsente() {
        Agence agence = agence(1L, "Tunis");
        when(agenceServiceProxy.update(1L, agence)).thenReturn(agence);

        assertThrows(IllegalArgumentException.class,
                () -> agenceService.updateAvecEmployes(1L, agence, null));
    }

    @Test
    void agenceServiceDetecteLaPresenceDeVehicules() {
        Agence agence = agence(1L, "Tunis");
        agence.getVehicules().add(vehicule(1L));
        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence));

        assertTrue(agenceService.hasVehicules(1L));
    }

    @Test
    void agenceServiceDetecteLAbsenceDeVehicules() {
        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence(1L, "Tunis")));

        assertFalse(agenceService.hasVehicules(1L));
    }

    @Test
    void agenceServiceDetacheLesEnfantsAvantSuppression() {
        Agence agence = agence(1L, "Tunis");
        Vehicule vehicule = vehicule(1L);
        Employe employe = employe("Ali");
        agence.getVehicules().add(vehicule);
        vehicule.setAgence(agence);
        employe.setAgence(agence);
        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence));
        when(agenceRepository.existsById(1L)).thenReturn(true);

        agenceService.deleteById(1L);

        assertNull(vehicule.getAgence());
        assertNull(employe.getAgence());
        verify(agenceRepository).deleteById(1L);
    }

    @Test
    void basicAgenceServiceCreeUneAgenceValide() {
        Agence agence = agence(null, "Tunis");
        agence.setIdAgence(null);
        when(agenceRepository.save(agence)).thenReturn(agence);

        assertSame(agence, basicAgenceService.create(agence));
    }

    @Test
    void basicAgenceServiceSignaleUneAgenceIntrouvable() {
        when(agenceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> basicAgenceService.findById(1L));
    }

    @Test
    void basicAgenceServiceSignaleUneSuppressionIntrouvable() {
        when(agenceRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> basicAgenceService.deleteById(1L));
    }

    @Test
    void clientServiceRefuseUnClientNul() {
        assertFalse(clientService.check(null, true));
    }

    @Test
    void clientServiceRefuseUneDateInscriptionAbsente() {
        Client client = client();
        client.setDateInscription(null);

        assertFalse(clientService.check(client, true));
    }

    @Test
    void clientServiceRecopieLesChampsLorsDeLaModification() {
        Client source = client();
        source.setNom("Nouveau nom");
        Client cible = client();
        cible.setIdClient(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(cible));
        when(clientRepository.save(cible)).thenReturn(cible);

        Client resultat = clientService.update(1L, source);

        assertEquals("Nouveau nom", resultat.getNom());
        assertEquals(source.getEmail(), resultat.getEmail());
    }

    @Test
    void clientServiceRefuseUnClientAvecUnIdentifiant() {
        Client client = client();
        client.setIdClient(1L);

        assertThrows(IllegalArgumentException.class, () -> clientService.create(client));
    }

    @Test
    void contratServiceRefuseUnContratIncomplet() {
        assertFalse(contratService.check(new Contrat(), true));
    }

    @Test
    void contratServiceRefuseUnPaiementDejaPersistant() {
        Contrat contrat = contrat();
        Paiement paiement = paiement();
        paiement.setIdPaiement(5L);
        contrat.setPaiements(List.of(paiement));

        assertThrows(IllegalArgumentException.class, () -> contratService.create(contrat));
    }

    @Test
    void contratServiceSupprimeUnContratSansReservation() {
        Contrat contrat = contrat();
        when(contratRepository.findById(1L)).thenReturn(Optional.of(contrat));

        contratService.deleteById(1L);

        verify(contratRepository).delete(contrat);
    }

    @Test
    void employeServiceAccepteUnEmployeValide() {
        assertTrue(employeServiceImpl.check(employe("Ali"), true));
    }

    @Test
    void employeServiceRefuseUnEmployeNul() {
        assertFalse(employeServiceImpl.check(null, true));
    }

    @Test
    void equipementServiceAccepteUnEquipementValide() {
        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS");

        assertTrue(equipementServiceImpl.check(equipement, true));
    }

    @Test
    void equipementServiceRefuseUnLibelleVide() {
        Equipement equipement = new Equipement();
        equipement.setLibelle(" ");

        assertFalse(equipementServiceImpl.check(equipement, true));
    }

    @Test
    void equipementServiceSupprimeUnEquipementSansVehicule() {
        Equipement equipement = new Equipement();
        when(equipementRepository.findById(1L)).thenReturn(Optional.of(equipement));

        equipementServiceImpl.deleteById(1L);

        verify(equipementRepository).delete(equipement);
    }

    @Test
    void maintenanceServiceRefuseUneMaintenanceNulle() {
        assertFalse(maintenanceServiceImpl.check(null, true));
    }

    @Test
    void maintenanceServiceRefuseUneDescriptionVide() {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDateFin(LocalDate.now().plusDays(1));
        maintenance.setDescription(" ");

        assertFalse(maintenanceServiceImpl.check(maintenance, true));
    }

    @Test
    void paiementServiceRetourneLaListeDesPaiements() {
        List<Paiement> paiements = List.of(paiement());
        when(paiementRepository.findAll()).thenReturn(paiements);

        assertSame(paiements, paiementServiceImpl.findAll());
    }

    @Test
    void paiementServiceRetourneUnPaiementExistant() {
        Paiement paiement = paiement();
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));

        assertSame(paiement, paiementServiceImpl.findById(1L));
    }

    @Test
    void reservationServiceRefuseUneReservationNulle() {
        assertFalse(reservationServiceImpl.check(null, true));
    }

    @Test
    void reservationServiceRefuseUneDateManquante() {
        Reservation reservation = new Reservation();
        reservation.setDateFin(LocalDate.now().plusDays(1));
        reservation.setStatut("CONFIRMEE");

        assertFalse(reservationServiceImpl.check(reservation, true));
    }

    @Test
    void vehiculeServiceAccepteUnVehiculeValide() {
        assertTrue(vehiculeServiceImpl.check(vehicule(1L), true));
    }

    @Test
    void vehiculeServiceRefuseUneCategorieAbsente() {
        Vehicule vehicule = vehicule(1L);
        vehicule.setCategorie(null);

        assertFalse(vehiculeServiceImpl.check(vehicule, true));
    }

    @Test
    void vehiculeServiceRefuseUneOffreInconnue() {
        Vehicule vehicule = vehicule(1L);

        assertThrows(IllegalArgumentException.class,
                () -> vehiculeServiceImpl.calculTarif(vehicule, 2, null));
    }

    @Test
    void vehiculeServiceSignaleUneAgenceIntrouvable() {
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule(1L)));
        when(agenceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehiculeServiceImpl.affecterAgence(1L, 2L));
    }

    @Test
    void vehiculeServiceSignaleUnEquipementIntrouvable() {
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule(1L)));
        when(equipementRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehiculeServiceImpl.affecterEquipement(1L, 2L));
    }

    @Test
    void vehiculeServiceNInserePasDeuxFoisLeMemeEquipement() {
        Vehicule vehicule = vehicule(1L);
        Equipement equipement = new Equipement();
        vehicule.getEquipements().add(equipement);
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(2L)).thenReturn(Optional.of(equipement));

        vehiculeServiceImpl.affecterEquipement(1L, 2L);

        assertEquals(1, vehicule.getEquipements().size());
    }

    @Test
    void vehiculeServiceSupprimeUnVehiculeSansEquipement() {
        Vehicule vehicule = vehicule(1L);
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicule(vehicule);
        vehicule.getMaintenances().add(maintenance);
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        vehiculeServiceImpl.deleteById(1L);

        assertNull(maintenance.getVehicule());
        verify(vehiculeRepository).flush();
        verify(vehiculeRepository).delete(vehicule);
    }

    private Agence agence(Long id, String nom) {
        Agence agence = new Agence();
        agence.setIdAgence(id);
        agence.setNom(nom);
        agence.setVille("Tunis");
        agence.setAdresse("Centre");
        agence.setTelephone("70123456");
        return agence;
    }

    private Employe employe(String nom) {
        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom("Ali");
        employe.setRole(RoleEmploye.AGENT);
        return employe;
    }

    private Client client() {
        Client client = new Client();
        client.setNom("Client");
        client.setPrenom("Ali");
        client.setEmail("client@test.tn");
        client.setTelephone("70123456");
        client.setNumPermis("PERMIS-1");
        client.setDateInscription(LocalDate.now());
        return client;
    }

    private Contrat contrat() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("300"));
        contrat.setValide(true);
        return contrat;
    }

    private Paiement paiement() {
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("100"));
        paiement.setDatePaiement(LocalDate.now());
        paiement.setModePaiement("CARTE");
        return paiement;
    }

    private Vehicule vehicule(Long id) {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(id);
        vehicule.setImmatriculation("TEST-" + id);
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("100"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        return vehicule;
    }

}
