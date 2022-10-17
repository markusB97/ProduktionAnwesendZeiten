package main;

import service.LogService;
import service.StoreService;

public class Start {
    public static void main(String[] args) {
        try {
            LogService.log("---------------Beginn---------------");
            StoreService.updateTimes();
        } catch (Exception e) {
            LogService.log(e);
            LogService.sendErrorMail(e);
        } finally {
            LogService.log("---------------Ende---------------");
        }
    }
}
