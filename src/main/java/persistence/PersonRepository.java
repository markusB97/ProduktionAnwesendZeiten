package persistence;

import model.Person;
import service.ConfigService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonRepository {

    public List<Person> getAllPersons() throws Exception {
        // create connection
        ConnectionPool pool = ConnectionPool.getInstance();
        List<Person> persons = new ArrayList<>();
        Connection con = pool.getConnection();
        Statement smt = con.createStatement();

        // get needed data
        String werk = ConfigService.readFromConfig("datenbereich");
        String centers = ConfigService.readFromConfig("center");
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String today = simpleDateFormat.format(new Date());

        // build sql
        String sql = "select pcb.pcbpnr, pcb.pcbbkreis, pcb.pcbname, pcb.pcbvname, per.perkstnr \n" +
                "From " + werk + ".BPCB pcb \n" +
                "     left join " + werk + ".BPER per on pcb.pcbpstnr = per.perpstnr \n" +
                "     left join " + werk + ".BKST kst on per.perkstnr = kst.kstkstnr \n" +
                "WHERE substr(per.pergbis,1,8) > '" + today + "' \n" +
                "      and pcb.PCBPSEUDO <> 1 \n" +
                "      AND SubStr(Nvl(pcb.PCBFIRAUS,To_Char(SYSDATE, 'YYYYMMDD')), 1, 8) >=  '" + today + "' \n" +
                "      and per.perkstnr in (" + centers + ") \n" +
                "order by pcb.PCBNAME";
        ResultSet rs = smt.executeQuery(sql);

        // read result and store in model
        while(rs.next()) {
            String personalnr = rs.getString("pcbpnr");
            String kreis = rs.getString("pcbbkreis");
            String nachname = rs.getString("pcbname");
            String vorname = rs.getString("pcbvname");
            String kostenstelle = rs.getString("perkstnr");
            Person person = new Person(personalnr, kreis, nachname, vorname, kostenstelle);
            persons.add(person);
        }
        rs.close();
        smt.close();
        pool.releaseConnection();

        return persons;
    }
}
