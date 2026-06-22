package com.seguridad.service;

import com.seguridad.model.Sede;
import com.seguridad.model.StockSede;
import com.seguridad.model.Usuario;
import com.seguridad.repository.SedeRepository;
import com.seguridad.repository.StockSedeRepository;
import com.seguridad.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockAlertaService {

    @Autowired StockSedeRepository stockSedeRepo;
    @Autowired SedeRepository sedeRepo;
    @Autowired UsuarioRepository usuarioRepo;
    @Autowired EmailService emailService;
    @Autowired NotificacionService notificacionService;

    @Value("${app.stock.umbral:10}")
    private int umbral;

    public void enviarAlertasStockBajo() {
        List<Sede> sedesActivas = sedeRepo.findAll().stream()
                .filter(s -> s.getEstado() != null && s.getEstado() == 1)
                .toList();

        for (Sede sede : sedesActivas) {
            List<StockSede> stockBajo = stockSedeRepo.findStockBajoPorSede(sede.getIdSede(), umbral);
            
            System.out.println("=== SEDE: " + sede.getNombre() + " ===");
            System.out.println("Stock bajo encontrado: " + stockBajo.size() + " productos");
            
            if (stockBajo.isEmpty()) continue;

            List<Usuario> usuariosAlmacen = usuarioRepo
                    .findByRolAndSede_IdSedeAndEstado("ALMACEN", sede.getIdSede(), 1);
            
            System.out.println("Usuarios ALMACEN activos en sede: " + usuariosAlmacen.size());
            
            for (Usuario u : usuariosAlmacen) {
                System.out.println("  -> Usuario: " + u.getNombre() + " | Email: " + u.getEmail());
                
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    System.out.println("  -> Enviando correo a: " + u.getEmail());
                    emailService.enviarAlertaStockBajo(u.getEmail(), u.getNombre(), stockBajo);
                } else {
                    System.out.println("  -> SIN EMAIL, no se envía correo");
                }
                
                notificacionService.crearNotificacion(
                    u,
                    "⚠ Stock Bajo - " + sede.getNombre(),
                    stockBajo.size() + " producto(s) con stock bajo en " + sede.getNombre(),
                    "STOCK_BAJO"
                );
            
            }
        }

        List<StockSede> stockBajoGlobal = stockSedeRepo.findStockBajoGlobal(umbral);
        System.out.println("=== GLOBAL: " + stockBajoGlobal.size() + " productos ===");
        
        List<Usuario> admins = usuarioRepo.findByRolAndEstado("ADMIN", 1);
        System.out.println("Admins activos: " + admins.size());
        
        for (Usuario admin : admins) {
            System.out.println("  -> Admin: " + admin.getNombre() + " | Email: " + admin.getEmail());
            
            if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                System.out.println("  -> Enviando correo a: " + admin.getEmail());
                emailService.enviarAlertaStockBajo(admin.getEmail(), admin.getNombre(), stockBajoGlobal);
            } else {
                System.out.println("  -> ADMIN SIN EMAIL");
            }
            
            notificacionService.crearNotificacion(
                admin,
                "⚠ Stock Bajo Global",
                stockBajoGlobal.size() + " producto(s) con stock bajo en todas las sedes",
                "STOCK_BAJO"
            );
        }
    }
    
}