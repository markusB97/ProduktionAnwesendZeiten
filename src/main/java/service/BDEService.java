package service;

import de.psi.pen.base.jbob.*;
import model.Person;
import model.Stempelung;
import org.apache.commons.lang3.time.DateUtils;
import persistence.PersonRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BDEService {

    private PsipentaApplicationImpl remoteBOB;

    public List<List<Stempelung>> getTimes() throws Exception {
        List<List<Stempelung>> allStempelungen = new ArrayList<>();
        login();

        // get dates
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String from = simpleDateFormat.format(DateUtils.addDays(new Date(), -35));
        String until = simpleDateFormat.format(new Date());

        // get all persons to calc
        PersonRepository personRepository = new PersonRepository();
        List<Person> persons = personRepository.getAllPersons();
        LogService.log("Alle Personen geladen");
        // get all stempelungen
        for (Person p : persons) {
            List<Stempelung> stempelungs = getAllFromPersonalNumber(p, from, until);
            allStempelungen.add(stempelungs);
        }
        LogService.log("Alle Stempelungen geladen");
        releaseConnection();

        return allStempelungen;
    }

    private void login() throws IOException, JBOBException, JBOBCoreException {
        String server = ConfigService.readFromConfig("server.name");
        int port = Integer.parseInt(ConfigService.readFromConfig("port"));
        String data = ConfigService.readFromConfig("datenbereich");
        int serverTimeout = Integer.parseInt(ConfigService.readFromConfig("server.timeout"));
        String username = ConfigService.readFromConfig("username");
        String password = ConfigService.readFromConfig("passwort");
        String language = ConfigService.readFromConfig("language");

        // Create Connection to Remote BOB Server
        InetAddress bobIp = InetAddress.getByName(server);
        remoteBOB = new PsipentaApplicationImpl(bobIp, port, serverTimeout, data);

        // login to Penta
        remoteBOB.login(username, password, language);
    }

    /**
     * Logs the current user out and closes the connection to the sever.
     */
    private void releaseConnection() throws JBOBException, JBOBCoreException {
        remoteBOB.logout();
        remoteBOB.shutdown();

    }

    private List<Stempelung> getAllFromPersonalNumber(Person person, String dateFrom, String dateUntil) throws JBOBException, JBOBCoreException {
        PsipentaBO bpms = remoteBOB.createBO("BPMS");
        PsipentaFilter bpmsFilter = bpms.getFilter();
        PsipentaOverview bpmsOverview = bpms.getOverview();

        List<Stempelung> stempelungen = new ArrayList<>();

        // fill filter and go to overview
        bpmsFilter.setFieldContent("PCBPNR", person.getPersonalNr());
        bpmsFilter.setFieldContent("PCBPNRBIS", person.getPersonalNr());
        bpmsFilter.setFieldContent("BEGINN_DATE", dateFrom);
        bpmsFilter.setFieldContent("ENDE_DATE", dateUntil);

        // if there is no stempelung, an exception occurs
        try {
            bpmsFilter.gotoOverview();
        } catch (JBOBException e) {
            LogService.log("Fehler bei Personalnummer: " + person.getPersonalNr());
            LogService.log(e);
            LogService.sendErrorMail(e);
        }

        // iterate over all found
        for (int i = 0; i < bpmsOverview.getArraySize(1) - 2; i++) {
            String date = bpmsOverview.getFieldContent("PMPDATUM_DATE", null, i);
            String soll = bpmsOverview.getFieldContent("PMPSOLL", null, i);
            String anwesend = bpmsOverview.getFieldContent("PMPSP1", null, i);
            String saldo = bpmsOverview.getFieldContent("PMPSP2", null, i);
            String diere = bpmsOverview.getFieldContent("PMPSP7", null, i);
            String plan = bpmsOverview.getFieldContent("PMPTAZNR", null, i);

            // convert date to YYYYMMDD from DD.MM.YY
            String newDate = "20" + date.substring(6) + date.substring(3, 5) + date.substring(0, 2);

            Stempelung stm = new Stempelung(newDate, soll, anwesend, saldo, diere, plan, person);
            stm = convertTimes(stm);
            stempelungen.add(stm);
        }
        remoteBOB.destroyBO(bpms, true);
        return stempelungen;
    }

    private Stempelung convertTimes(Stempelung stempelung) {
        DecimalFormat df = new DecimalFormat("#.##");
        // check diere and convert anwesend time into hours with comma
        double anwesend = 0;
        if(stempelung.getDiere().equals("") && !stempelung.getAnwesend().equals("")) {
            anwesend = Double.parseDouble(stempelung.getAnwesend().substring(0,stempelung.getAnwesend().indexOf(':')));
            if(anwesend < 0) {
                anwesend -= Double.parseDouble(stempelung.getAnwesend().substring(stempelung.getAnwesend().indexOf(':') + 1)) / 60;
            } else {
                anwesend += Double.parseDouble(stempelung.getAnwesend().substring(stempelung.getAnwesend().indexOf(':') + 1)) / 60;
            }
        }
        String newAnwesend = df.format(anwesend).replace(',', '.');
        stempelung.setAnwesend(newAnwesend);

        // convert sollzeit
        double sollzeit = 0;
        if(!stempelung.getSollzeit().equals("")) {
            sollzeit = Double.parseDouble(stempelung.getSollzeit().substring(0,stempelung.getSollzeit().indexOf(':')));
            if(sollzeit < 0) {
                sollzeit -= Double.parseDouble(stempelung.getSollzeit().substring(stempelung.getSollzeit().indexOf(':') + 1)) / 60;
            } else {
                sollzeit += Double.parseDouble(stempelung.getSollzeit().substring(stempelung.getSollzeit().indexOf(':') + 1)) / 60;
            }
        }
        String newSollzeit = df.format(sollzeit).replace(',', '.');
        stempelung.setSollzeit(newSollzeit);

        // convert saldo
        double saldo = 0;
        if(!stempelung.getSaldo().equals("")) {
            saldo = Double.parseDouble(stempelung.getSaldo().substring(0,stempelung.getSaldo().indexOf(':')));
            if(saldo < 0) {
                saldo -= Double.parseDouble(stempelung.getSaldo().substring(stempelung.getSaldo().indexOf(':') + 1)) / 60;
            } else {
                saldo += Double.parseDouble(stempelung.getSaldo().substring(stempelung.getSaldo().indexOf(':') + 1)) / 60;
            }
        }
        String newSaldo = df.format(saldo).replace(',', '.');
        stempelung.setSaldo(newSaldo);

        return stempelung;
    }
}
