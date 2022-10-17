package persistence;

import model.Stempelung;
import service.ConfigService;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class StempelungRepository {

    public void deleteStempelung(String fromDate) throws Exception {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        Statement smt = con.createStatement();
        String werk = ConfigService.readFromConfig("datenbereich");
        String sql = "delete " + werk + ".bpcb_parat where sdatum >=" + fromDate;
        smt.executeUpdate(sql);
        smt.close();
        pool.releaseConnection();
    }

    public void insertStempelung(List<List<Stempelung>> list) throws Exception {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = pool.getConnection();
        Statement smt = con.createStatement();
        String werk = ConfigService.readFromConfig("datenbereich");
        for (List<Stempelung> l : list) {
            for(Stempelung s : l) {
                String sql = "insert into " + werk + ".bpcb_parat values (" +
                        "'" + s.getPerson().getPersonalNr() + "'," +
                        "'" + s.getPerson().getKreis() + "'," +
                        "'" + s.getPerson().getKostenstelle() + "'," +
                        "'" + s.getPerson().getNachName() + "'," +
                        "'" + s.getPerson().getVorName() + "'," +
                        "'" + s.getDatum() + "'," +
                        s.getSollzeit() + "," +
                        s.getAnwesend() + "," +
                        s.getSaldo() + "," +
                        "'" + s.getPlan() + "')";
                smt.executeUpdate(sql);
            }
        }
        smt.close();
        pool.releaseConnection();
    }
}
