package com.seguridad.scheduler;

import com.seguridad.service.StockAlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockAlertaScheduler {

    @Autowired StockAlertaService stockAlertaService;

    @Scheduled(cron = "0 0 8 * * *")
    public void ejecutarAlertaDiaria() {
        stockAlertaService.enviarAlertasStockBajo();
    }
}