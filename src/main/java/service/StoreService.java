package service;

import model.Stempelung;
import org.apache.commons.lang3.time.DateUtils;
import persistence.StempelungRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StoreService {

    public static void updateTimes() throws Exception {
        // get all stempelungen
        BDEService bdeService = new BDEService();
        List<List<Stempelung>> stemp = bdeService.getTimes();

        // delete all old stempelungen
        StempelungRepository stempelungRepository = new StempelungRepository();
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String from = simpleDateFormat.format(DateUtils.addDays(new Date(), -35));
        stempelungRepository.deleteStempelung(from);
        LogService.log("Alle Stempelungen von " + from + " bis heute gelöscht.");

        // insert new stempelungen
        stempelungRepository.insertStempelung(stemp);
        LogService.log("Alle neuen Stempelungen eingefügt.");
    }
}
