-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: almacen_seguridad
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_ingreso`
--

DROP TABLE IF EXISTS `tb_ingreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_ingreso` (
  `id_ingreso` int NOT NULL AUTO_INCREMENT,
  `id_producto` varchar(10) NOT NULL,
  `id_proveedor` int DEFAULT NULL,
  `id_sede` int NOT NULL,
  `cantidad` int NOT NULL,
  `costo_unitario` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) DEFAULT '0.00',
  `fecha` date NOT NULL,
  `nro_factura` varchar(50) DEFAULT NULL COMMENT 'N° Factura E001-186',
  `observacion` varchar(300) DEFAULT NULL,
  `estado` tinyint DEFAULT '1' COMMENT '101=normal, 102=stock antiguo',
  PRIMARY KEY (`id_ingreso`),
  KEY `id_producto` (`id_producto`),
  KEY `id_proveedor` (`id_proveedor`),
  KEY `id_sede` (`id_sede`),
  CONSTRAINT `tb_ingreso_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id_producto`),
  CONSTRAINT `tb_ingreso_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `tb_proveedor` (`id_proveedor`),
  CONSTRAINT `tb_ingreso_ibfk_3` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_ingreso`
--

LOCK TABLES `tb_ingreso` WRITE;
/*!40000 ALTER TABLE `tb_ingreso` DISABLE KEYS */;
INSERT INTO `tb_ingreso` VALUES (1,'UC0074',2,1,20,44.90,898.00,'2026-06-14','20607088021','',2),(2,'UC0004',2,1,30,29.90,897.00,'2026-06-03','12345678901','Nuevos ingresos',1),(3,'UC0055',3,1,20,35.00,700.00,'2026-06-04','20607088021','Nuevos ingresos',2),(4,'UC0083',2,1,85,30.00,2550.00,'2026-06-04','20607088021','Nuevas gorras\r\n',1),(5,'UC0015',2,1,50,30.00,1500.00,'2026-06-04','98765432110','Nuevos imgresos',2),(6,'UC0055',1,1,68,25.00,1700.00,'2026-06-05','45673948572','Nuevo',2),(7,'UC0007',3,1,100,49.00,4900.00,'2026-06-05','98765432112','Nuevos\r\n',1),(8,'UC0015',3,1,15,40.00,600.00,'2026-06-05','87654567819','Nuevos',2),(9,'UC0006',1,1,70,20.00,1400.00,'2026-06-05','76543267891','Nuevos',1),(10,'UC0055',3,1,100,25.00,2500.00,'2026-06-05','09890768923','Nuevos',2),(11,'UC0010',1,1,260,20.00,5200.00,'2026-06-05','26475909921','',1),(12,'UC0005',3,1,100,50.00,5000.00,'2026-06-05','20202024829','Nuevo',1),(13,'UC0074',2,1,270,99.00,26730.00,'2026-06-05','02747573831','Nuevos',2),(14,'UC0055',1,1,600,100.00,60000.00,'2026-06-05','98765432112','Nuevo',1),(15,'UC0004',2,1,80,40.00,3200.00,'2026-06-06','29292929299','Nuevo',1),(16,'UC0004',2,1,600,27.00,16200.00,'2026-06-06','28288228288','Nuevo',2),(17,'UC0008',2,1,200,75.00,15000.00,'2026-06-06','29292929299','Nuevo',1),(18,'UC0009',2,1,300,80.00,24000.00,'2026-06-06','12345678901','nuevo',1),(19,'UC0009',1,1,17,60.00,1020.00,'2026-06-06','8282828282','Se ingreso nuevo stock',1),(20,'UC0004',1,1,50,35.00,1750.00,'2026-01-10','FAC-1001','Ingreso Enero',1),(21,'UC0005',2,1,40,38.00,1520.00,'2026-01-18','FAC-1002','Ingreso Enero',1),(22,'UC0007',3,1,25,40.00,1000.00,'2026-01-25','FAC-1003','Ingreso Enero',1),(23,'UC0008',1,1,60,65.00,3900.00,'2026-02-05','FAC-1004','Ingreso Febrero',1),(24,'UC0009',2,1,45,60.00,2700.00,'2026-02-14','FAC-1005','Ingreso Febrero',1),(25,'UC0010',3,1,35,65.00,2275.00,'2026-02-22','FAC-1006','Ingreso Febrero',1),(26,'UC0015',1,1,70,40.00,2800.00,'2026-03-08','FAC-1007','Ingreso Marzo',1),(27,'UC0055',2,1,80,70.00,5600.00,'2026-03-15','FAC-1008','Ingreso Marzo',1),(28,'UC0074',3,1,90,30.00,2700.00,'2026-03-28','FAC-1009','Ingreso Marzo',1),(29,'UC0083',1,1,55,30.00,1650.00,'2026-04-04','FAC-1010','Ingreso Abril',1),(30,'UC0004',2,1,65,35.00,2275.00,'2026-04-12','FAC-1011','Ingreso Abril',1),(31,'UC0005',3,1,75,38.00,2850.00,'2026-04-25','FAC-1012','Ingreso Abril',1),(32,'UC0007',1,1,85,40.00,3400.00,'2026-05-03','FAC-1013','Ingreso Mayo',1),(33,'UC0008',2,1,95,65.00,6175.00,'2026-05-16','FAC-1014','Ingreso Mayo',1),(34,'UC0009',3,1,100,60.00,6000.00,'2026-05-28','FAC-1015','Ingreso Mayo',1),(35,'UC0008',1,1,30,65.00,1950.00,'2026-01-29','FAC-1016','Ingreso Enero',1),(36,'UC0004',1,1,45,35.00,1575.00,'2026-02-07','FAC-1017','Ingreso Febrero',1),(37,'UC0005',2,1,55,38.00,2090.00,'2026-02-18','FAC-1018','Ingreso Febrero',1),(38,'UC0007',3,1,35,40.00,1400.00,'2026-02-26','FAC-1019','Ingreso Febrero',1),(39,'UC0008',1,1,50,65.00,3250.00,'2026-03-03','FAC-1020','Ingreso Marzo',1),(40,'UC0009',2,1,60,60.00,3600.00,'2026-03-10','FAC-1021','Ingreso Marzo',1),(41,'UC0010',3,1,40,65.00,2600.00,'2026-03-17','FAC-1022','Ingreso Marzo',1),(42,'UC0015',1,1,70,40.00,2800.00,'2026-03-21','FAC-1023','Ingreso Marzo',1),(43,'UC0055',2,1,80,70.00,5600.00,'2026-03-29','FAC-1024','Ingreso Marzo',1),(44,'UC0004',1,1,40,35.00,1400.00,'2026-04-02','FAC-1025','Ingreso Abril',1),(45,'UC0005',2,1,45,38.00,1710.00,'2026-04-07','FAC-1026','Ingreso Abril',1),(46,'UC0007',3,1,55,40.00,2200.00,'2026-04-11','FAC-1027','Ingreso Abril',1),(47,'UC0008',1,1,60,65.00,3900.00,'2026-04-15','FAC-1028','Ingreso Abril',1),(48,'UC0009',2,1,65,60.00,3900.00,'2026-04-19','FAC-1029','Ingreso Abril',1),(49,'UC0010',3,1,70,65.00,4550.00,'2026-04-23','FAC-1030','Ingreso Abril',1),(50,'UC0015',1,1,75,40.00,3000.00,'2026-04-28','FAC-1031','Ingreso Abril',1),(51,'UC0004',1,1,55,35.00,1925.00,'2026-05-02','FAC-1032','Ingreso Mayo',1),(52,'UC0005',2,1,60,38.00,2280.00,'2026-05-05','FAC-1033','Ingreso Mayo',1),(53,'UC0007',3,1,65,40.00,2600.00,'2026-05-08','FAC-1034','Ingreso Mayo',1),(54,'UC0008',1,1,70,65.00,4550.00,'2026-05-11','FAC-1035','Ingreso Mayo',1),(55,'UC0009',2,1,75,60.00,4500.00,'2026-05-14','FAC-1036','Ingreso Mayo',1),(56,'UC0010',3,1,80,65.00,5200.00,'2026-05-18','FAC-1037','Ingreso Mayo',1),(57,'UC0015',1,1,85,40.00,3400.00,'2026-05-21','FAC-1038','Ingreso Mayo',1),(58,'UC0055',2,1,90,70.00,6300.00,'2026-05-24','FAC-1039','Ingreso Mayo',1),(59,'UC0074',3,1,95,30.00,2850.00,'2026-05-29','FAC-1040','Ingreso Mayo',1),(61,'UC00011',1,1,25,10.00,250.00,'2026-06-13','20607088021','Nuevos ingresos',1),(62,'UC0083',1,1,80,34.00,2720.00,'2026-06-17','20601234567','Nuevos',1),(63,'UC00011',2,1,100,200.00,20000.00,'2026-06-17','20607654321','Nuevos',1),(64,'UC0001',1,1,65,20.00,1300.00,'2026-06-17','20601234567','Nuevos',2),(65,'UC00011',2,2,100,50.00,5000.00,'2026-06-19','20607654321','Nuevos ica',1),(66,'UC00011',2,2,50,100.00,5000.00,'2026-06-19','20607654321','Nuevos ica',1),(67,'UC00011',2,1,20,50.00,1000.00,'2026-06-19','20607654321','Nuevos',1),(68,'UC00011',2,2,50,47.00,2350.00,'2026-06-19','20607654321','Nuevos',1),(69,'UC0004',3,2,100,20.00,2000.00,'2026-06-19','10456789012','',1),(70,'UC0001',2,2,80,30.00,2400.00,'2026-06-20','20607654321','',1),(71,'BC00015',3,2,26,40.00,1040.00,'2026-06-20','10456789012','',1),(72,'BC00015',2,2,30,32.00,960.00,'2026-06-20','20607654321','',1),(73,'UC00030',5,1,50,25.00,1250.00,'2026-06-24','20607088021','',1);
/*!40000 ALTER TABLE `tb_ingreso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_kardex`
--

DROP TABLE IF EXISTS `tb_kardex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_kardex` (
  `id_kardex` int NOT NULL AUTO_INCREMENT,
  `id_producto` varchar(10) NOT NULL,
  `id_sede` int NOT NULL,
  `tipo_mov` varchar(1) NOT NULL COMMENT 'E=Entrada, S=Salida',
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `cantidad` int NOT NULL,
  `costo_unit` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) DEFAULT '0.00',
  `saldo_cant` int NOT NULL,
  `saldo_valor` decimal(10,2) DEFAULT '0.00',
  `referencia` varchar(50) DEFAULT NULL,
  `observacion` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id_kardex`),
  KEY `id_producto` (`id_producto`),
  KEY `id_sede` (`id_sede`),
  CONSTRAINT `tb_kardex_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id_producto`),
  CONSTRAINT `tb_kardex_ibfk_2` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_kardex`
--

LOCK TABLES `tb_kardex` WRITE;
/*!40000 ALTER TABLE `tb_kardex` DISABLE KEYS */;
INSERT INTO `tb_kardex` VALUES (1,'UC0074',1,'E','2026-06-03 16:23:11',15,44.90,673.50,15,673.50,'ING-1','20607088021'),(2,'UC0004',1,'E','2026-06-03 16:43:01',30,29.90,897.00,30,897.00,'ING-2','12345678901'),(3,'UC0055',1,'E','2026-06-04 15:09:31',20,35.00,700.00,20,700.00,'ING-3','20607088021'),(4,'UC0083',1,'E','2026-06-04 15:13:23',85,30.00,2550.00,85,2550.00,'ING-4','20607088021'),(5,'UC0015',1,'E','2026-06-04 23:07:10',15,20.00,300.00,15,300.00,'ING-5','98765432110'),(6,'UC0015',1,'E','2026-06-04 23:10:18',30,20.00,600.00,45,900.00,'ING-5','98765432110'),(7,'UC0015',1,'E','2026-06-04 23:17:25',50,30.00,1500.00,95,2850.00,'ING-5','98765432110'),(8,'UC0055',1,'E','2026-06-05 16:36:43',68,25.00,1700.00,88,2200.00,'ING-6','45673948572'),(9,'UC0007',1,'E','2026-06-05 16:37:20',100,49.00,4900.00,100,4900.00,'ING-7','98765432112'),(10,'UC0015',1,'E','2026-06-05 16:38:10',15,40.00,600.00,110,4400.00,'ING-8','87654567819'),(11,'UC0006',1,'E','2026-06-05 16:38:39',70,20.00,1400.00,70,1400.00,'ING-9','76543267891'),(12,'UC0055',1,'E','2026-06-05 16:39:12',100,25.00,2500.00,188,4700.00,'ING-10','09890768923'),(13,'UC0015',1,'E','2026-06-05 16:39:40',200,16.00,3200.00,310,4960.00,'ING-11','26475909921'),(14,'UC0010',1,'E','2026-06-05 16:40:50',250,20.00,5000.00,250,5000.00,'ING-11','26475909921'),(15,'UC0010',1,'E','2026-06-05 16:55:18',260,20.00,5200.00,510,10200.00,'ING-11','26475909921'),(16,'UC0005',1,'E','2026-06-05 16:56:18',100,50.00,5000.00,100,5000.00,'ING-12','20202024829'),(17,'UC0074',1,'E','2026-06-05 19:11:39',270,99.00,26730.00,285,28215.00,'ING-13','02747573831'),(18,'UC0055',1,'E','2026-06-05 19:24:29',500,90.00,45000.00,688,61920.00,'ING-14','98765432112'),(19,'UC0055',1,'E','2026-06-05 19:24:46',600,100.00,60000.00,1288,128800.00,'ING-14','98765432112'),(20,'UC0004',1,'E','2026-06-06 12:22:37',80,40.00,3200.00,110,4400.00,'ING-15','29292929299'),(21,'UC0004',1,'E','2026-06-06 12:23:26',600,27.00,16200.00,710,19170.00,'ING-16','28288228288'),(22,'UC0008',1,'E','2026-06-06 16:03:44',200,75.00,15000.00,200,15000.00,'ING-17','29292929299'),(23,'UC0009',1,'E','2026-06-06 16:05:20',300,80.00,24000.00,300,24000.00,'ING-18','12345678901'),(24,'UC0009',1,'E','2026-06-06 16:47:06',17,60.00,1020.00,317,19020.00,'ING-19','8282828282'),(25,'UC00011',1,'E','2026-06-13 00:12:09',25,10.00,250.00,25,250.00,'ING-61','20607088021'),(26,'UC0074',1,'E','2026-06-14 13:44:16',20,44.90,898.00,305,13694.50,'ING-1','20607088021'),(27,'UC0083',1,'E','2026-06-17 13:29:52',80,34.00,2720.00,165,5610.00,'ING-62','20601234567'),(28,'UC00011',1,'E','2026-06-17 16:11:08',100,200.00,20000.00,125,25000.00,'ING-63','20607654321'),(29,'UC0001',1,'E','2026-06-17 20:37:24',65,20.00,1300.00,65,1300.00,'ING-64','20601234567'),(30,'UC00011',2,'E','2026-06-19 16:35:34',100,50.00,5000.00,100,5000.00,'ING-65','20607654321'),(31,'UC00011',2,'E','2026-06-19 16:36:35',50,100.00,5000.00,150,15000.00,'ING-66','20607654321'),(32,'UC00011',1,'E','2026-06-19 22:59:25',20,50.00,1000.00,145,7250.00,'ING-67','20607654321'),(33,'UC00011',2,'E','2026-06-19 23:02:14',50,47.00,2350.00,200,9400.00,'ING-68','20607654321'),(34,'UC0004',2,'E','2026-06-19 23:34:55',100,20.00,2000.00,100,2000.00,'ING-69','10456789012'),(35,'UC0001',2,'E','2026-06-19 23:50:13',40,30.00,1200.00,40,1200.00,'ING-70','20607654321'),(36,'UC0001',2,'E','2026-06-20 00:10:58',80,30.00,2400.00,120,3600.00,'ING-70','20607654321'),(37,'BC00015',2,'E','2026-06-20 00:58:04',26,40.00,1040.00,26,1040.00,'ING-71','10456789012'),(38,'BC00015',2,'E','2026-06-20 00:59:36',30,32.00,960.00,56,1792.00,'ING-72','20607654321'),(39,'UC00011',1,'S','2026-06-24 16:27:24',2,47.00,94.00,143,6721.00,'SAL-12','Uniforme nuevo'),(40,'UC00011',2,'S','2026-06-24 16:28:27',3,47.00,141.00,197,9259.00,'SAL-13','Productos en buen estado'),(41,'UC00030',1,'E','2026-06-24 20:42:01',50,25.00,1250.00,50,1250.00,'ING-73','20607088021'),(42,'UC00030',1,'S','2026-06-24 20:43:12',5,25.00,125.00,45,1125.00,'SAL-14','');
/*!40000 ALTER TABLE `tb_kardex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_mensaje`
--

DROP TABLE IF EXISTS `tb_mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_mensaje` (
  `id_mensaje` bigint NOT NULL AUTO_INCREMENT,
  `id_emisor` int NOT NULL,
  `id_destinatario` int DEFAULT NULL,
  `contenido` varchar(1000) NOT NULL,
  `fecha` datetime NOT NULL,
  `leido` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_mensaje`),
  KEY `id_emisor` (`id_emisor`),
  KEY `id_destinatario` (`id_destinatario`),
  CONSTRAINT `tb_mensaje_ibfk_1` FOREIGN KEY (`id_emisor`) REFERENCES `tb_usuario` (`id_usuario`),
  CONSTRAINT `tb_mensaje_ibfk_2` FOREIGN KEY (`id_destinatario`) REFERENCES `tb_usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_mensaje`
--

LOCK TABLES `tb_mensaje` WRITE;
/*!40000 ALTER TABLE `tb_mensaje` DISABLE KEYS */;
INSERT INTO `tb_mensaje` VALUES (1,1,3,'Poner mas stock a los productos','2026-06-23 01:40:31',1),(2,1,NULL,'Reunion a las 8 de la mañana','2026-06-23 01:40:54',0),(3,3,NULL,'Oki!!!','2026-06-23 01:44:59',0),(4,3,1,'Oki!!!','2026-06-23 01:45:07',1),(5,3,NULL,'Hola como estan?','2026-06-24 18:39:14',0),(6,3,2,'Agregar nuevos productos','2026-06-24 18:39:28',1),(7,3,NULL,'Holaaa','2026-06-24 19:03:22',0),(8,3,1,'Holaa','2026-06-24 19:03:31',1),(9,1,NULL,'Hola ann pagame','2026-06-24 19:26:45',0),(10,1,14,'ann pagameee','2026-06-24 19:26:58',1),(11,14,1,'Hechale tierra','2026-06-24 19:27:51',1),(12,1,14,'Yapeeaaaaaa','2026-06-24 19:33:25',0),(13,1,NULL,'ann pagame','2026-06-24 20:09:56',0),(14,1,3,'Ingresos','2026-06-24 20:49:12',0),(15,1,3,'Ingresos','2026-06-24 20:49:18',0),(16,1,3,'Paga','2026-06-24 20:49:34',0);
/*!40000 ALTER TABLE `tb_mensaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_notificacion`
--

DROP TABLE IF EXISTS `tb_notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_notificacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `mensaje` varchar(1000) DEFAULT NULL,
  `leida` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `tipo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notif_usuario` (`id_usuario`),
  CONSTRAINT `fk_notif_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `tb_usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_notificacion`
--

LOCK TABLES `tb_notificacion` WRITE;
/*!40000 ALTER TABLE `tb_notificacion` DISABLE KEYS */;
INSERT INTO `tb_notificacion` VALUES (1,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',1,'2026-06-22 15:33:52','STOCK_BAJO'),(2,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-22 15:33:52','STOCK_BAJO'),(3,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-22 15:33:52','STOCK_BAJO'),(4,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-22 15:33:52','STOCK_BAJO'),(5,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-22 15:33:52','STOCK_BAJO'),(6,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',1,'2026-06-22 16:35:26','STOCK_BAJO'),(7,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-22 16:35:26','STOCK_BAJO'),(8,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-22 16:35:26','STOCK_BAJO'),(9,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-22 16:35:26','STOCK_BAJO'),(10,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-22 16:35:26','STOCK_BAJO'),(11,3,'Nuevo mensaje de Administrador','Poner mas stock a los productos',1,'2026-06-23 01:40:31','MENSAJE'),(12,1,'Nuevo mensaje de Almacén Ica','Oki!!!',1,'2026-06-23 01:45:07','MENSAJE'),(13,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',1,'2026-06-23 09:26:42','STOCK_BAJO'),(14,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-23 09:26:42','STOCK_BAJO'),(15,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-23 09:26:42','STOCK_BAJO'),(16,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-23 09:26:42','STOCK_BAJO'),(17,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-23 09:26:53','STOCK_BAJO'),(18,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',1,'2026-06-24 11:49:52','STOCK_BAJO'),(19,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 11:49:52','STOCK_BAJO'),(20,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-24 11:49:52','STOCK_BAJO'),(21,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-24 11:49:52','STOCK_BAJO'),(22,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-24 11:50:03','STOCK_BAJO'),(23,2,'Nuevo mensaje de Almacén Ica','Agregar nuevos productos',1,'2026-06-24 18:39:28','MENSAJE'),(24,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 18:52:14','STOCK_BAJO'),(25,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 18:52:14','STOCK_BAJO'),(26,12,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 18:52:14','STOCK_BAJO'),(27,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-24 18:52:15','STOCK_BAJO'),(28,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-24 18:52:15','STOCK_BAJO'),(29,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-24 18:52:15','STOCK_BAJO'),(30,1,'Nuevo mensaje de Almacén Ica','Holaa',1,'2026-06-24 19:03:31','MENSAJE'),(31,14,'Nuevo mensaje de Administrador','ann pagameee',1,'2026-06-24 19:26:58','MENSAJE'),(32,1,'Nuevo mensaje de Almacen Cuzco','Hechale tierra',1,'2026-06-24 19:27:51','MENSAJE'),(33,14,'Nuevo mensaje de Administrador','Yapeeaaaaaa',0,'2026-06-24 19:33:25','MENSAJE'),(34,2,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 19:33:41','STOCK_BAJO'),(35,5,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 19:33:41','STOCK_BAJO'),(36,12,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 19:33:41','STOCK_BAJO'),(37,13,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 19:33:41','STOCK_BAJO'),(38,15,'⚠ Stock Bajo - Almacén Central Lima','13 producto(s) con stock bajo en Almacén Central Lima',0,'2026-06-24 19:33:41','STOCK_BAJO'),(39,3,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',1,'2026-06-24 19:33:41','STOCK_BAJO'),(40,10,'⚠ Stock Bajo - Almacén Ica','5 producto(s) con stock bajo en Almacén Ica',0,'2026-06-24 19:33:41','STOCK_BAJO'),(41,1,'⚠ Stock Bajo Global','18 producto(s) con stock bajo en todas las sedes',1,'2026-06-24 19:33:41','STOCK_BAJO'),(42,3,'Nuevo mensaje de Administrador','Ingresos',1,'2026-06-24 20:49:12','MENSAJE'),(43,3,'Nuevo mensaje de Administrador','Ingresos',1,'2026-06-24 20:49:18','MENSAJE'),(44,3,'Nuevo mensaje de Administrador','Paga',1,'2026-06-24 20:49:34','MENSAJE');
/*!40000 ALTER TABLE `tb_notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_producto`
--

DROP TABLE IF EXISTS `tb_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_producto` (
  `id_producto` varchar(10) NOT NULL COMMENT 'UC0001, UC0002...',
  `ean_int` varchar(20) DEFAULT NULL COMMENT 'BMLA-B-L, BPAT-N-39...',
  `descripcion` varchar(200) NOT NULL,
  `id_tipo` int NOT NULL,
  `costo_unitario` decimal(10,2) DEFAULT '0.00',
  `precio_venta` decimal(10,2) DEFAULT '0.00',
  `stock_total` int DEFAULT '0',
  `estado` tinyint DEFAULT '1',
  PRIMARY KEY (`id_producto`),
  KEY `id_tipo` (`id_tipo`),
  CONSTRAINT `tb_producto_ibfk_1` FOREIGN KEY (`id_tipo`) REFERENCES `tb_tipo_equipo` (`id_tipo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_producto`
--

LOCK TABLES `tb_producto` WRITE;
/*!40000 ALTER TABLE `tb_producto` DISABLE KEYS */;
INSERT INTO `tb_producto` VALUES ('BC00015','HPK-S','PANTALONES ADMI',1,32.00,40.00,56,1),('BD00020','LTPE-L','BOTAS TODO TERRENO',1,40.00,60.00,50,1),('BD00021','LPQE-M','CORREAS Y BOTONES',3,10.00,20.00,30,1),('BE00025','LTPQ-L','POLERAS NEGRAS M',1,25.00,30.00,35,1),('UC0001','BMLA-B-L','BLUSA M.LARGA ADMI BLANCA L',1,50.00,50.00,150,1),('UC00011','BMLA-M','Camisa de cuadros',1,47.00,65.00,245,1),('UC0002','BMLA-B-M','BLUSA M.LARGA ADMI BLANCA M',1,35.00,42.00,8,1),('UC0003','BMLA-B-S','BLUSA M.LARGA ADMI BLANCA S',1,35.00,42.00,3,1),('UC00030','BMLA-S','Chalecos refractarios',1,25.00,50.00,45,1),('UC0004','BMLA-B-XL','BLUSA M.LARGA ADMI BLANCA XL',1,20.00,42.00,150,1),('UC0005','BMLA-B-M','CAMISA ROJO Y AZUL',1,50.00,100.00,40,1),('UC0006','BMLA-B-XXXL','BLUSA M.LARGA ADMI BLANCA XXXL',1,38.00,45.00,70,1),('UC0007','BPAT-N-39','BOTAS PTA.ACERO TACTICA NEGRO 39',2,40.00,48.00,100,1),('UC0008','BPAT-N-40','BOTAS PTA.ACERO TACTICA NEGRO 40',2,65.00,77.00,200,1),('UC00085','LP2E_E','PANTALONES',1,10.00,20.00,30,1),('UC0009','BPAT-N-41','BOTAS PTA.ACERO TACTICA NEGRO 41',2,60.00,77.00,320,1),('UC0010','BPAT-N-42','BOTAS PTA.ACERO TACTICA NEGRO 42',2,65.00,77.00,510,1),('UC0015','BTC-39','BOTAS TACTICA COYOTE 39',2,40.00,48.00,313,1),('UC0055','CAN-N','CAPOTIN AGENTE NEGRO',3,70.00,84.00,1348,1),('UC0074','CA-G','CORBATA AGENTE GUINDA',3,44.90,0.00,321,1),('UC0083','GA-N','GORRA AGENTE NEGRO',3,34.00,0.00,171,1);
/*!40000 ALTER TABLE `tb_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_proveedor`
--

DROP TABLE IF EXISTS `tb_proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) NOT NULL,
  `ruc` varchar(20) DEFAULT NULL,
  `direccion` varchar(300) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL COMMENT 'NATURAL o JURIDICA',
  `estado_sunat` varchar(20) DEFAULT NULL COMMENT 'ACTIVO, INHABILITADO, etc',
  `estado` tinyint DEFAULT '1',
  PRIMARY KEY (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_proveedor`
--

LOCK TABLES `tb_proveedor` WRITE;
/*!40000 ALTER TABLE `tb_proveedor` DISABLE KEYS */;
INSERT INTO `tb_proveedor` VALUES (1,'RAGVILH EMPRENDEDORES S.R.L.','20601234567','Javier Prado','123456789','JURIDICA','ACTIVO',1),(2,'CORPORACION PYSA E.I.R.L.','20607654321','San Borja Sur','645667838','JURIDICA','ACTIVO',1),(3,'SANCHO VALERO ELIZABETH ROSARIO','10456789012','San Borja Norte','112356745','NATURAL','ACTIVO',1),(4,'EBONY SUPERMARKET ','20901636765','Guardia Civil','998678378','JURIDICA','ACTIVO',1),(5,'VIRREY Y CAPITANA S.A.C.','20607088021','JR. GARCILASO DE LA VEGA NRO. 1584 DPTO. 306 URB. LINCE LIMA LIMA LINCE','936163227','JURIDICA','ACTIVO',1),(6,'SUPERMERCADOS PERUANOS SOCIEDAD ANONIMA \'O \' S.P.S.A.','20100070970','CAL. MORELLI NRO. 181 INT. P-2 LIMA LIMA SAN BORJA','','JURIDICA','ACTIVO',1),(7,'CENCOSUD RETAIL PERU S.A.','20109072177','CAL. AUGUSTO ANGULO NRO. 130 URB. SAN ANTONIO LIMA LIMA MIRAFLORES','','JURIDICA','ACTIVO',1),(8,'BANCO BBVA PERU','20100130204','AV. REPUBLICA DE PANAMA NRO. 3055 URB. EL PALOMAR LIMA LIMA SAN ISIDRO','','JURIDICA','ACTIVO',1),(9,'BANCO DE CREDITO DEL PERU','20100047218','JR. CENTENARIO NRO. 156 URB. LADERAS DE MELGAREJO LIMA LIMA LA MOLINA','','JURIDICA','ACTIVO',1),(10,'BOTICAS IP S.A.C.','20608430301','CAL. VICTOR ALZAMORA NRO. 147 INT. C URB. SANTA CATALINA LIMA LIMA LA VICTORIA','','JURIDICA','ACTIVO',1);
/*!40000 ALTER TABLE `tb_proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_salida`
--

DROP TABLE IF EXISTS `tb_salida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_salida` (
  `id_salida` int NOT NULL AUTO_INCREMENT,
  `id_producto` varchar(10) NOT NULL,
  `id_trabajador` int NOT NULL,
  `id_sede` int NOT NULL,
  `cantidad` int NOT NULL,
  `fecha` date NOT NULL,
  `observacion` varchar(300) DEFAULT NULL COMMENT 'ENVIO ICA RENOVACION DE UNIFORMES',
  `estado` tinyint DEFAULT '1',
  PRIMARY KEY (`id_salida`),
  KEY `id_producto` (`id_producto`),
  KEY `id_trabajador` (`id_trabajador`),
  KEY `id_sede` (`id_sede`),
  CONSTRAINT `tb_salida_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id_producto`),
  CONSTRAINT `tb_salida_ibfk_2` FOREIGN KEY (`id_trabajador`) REFERENCES `tb_trabajador` (`id_trabajador`),
  CONSTRAINT `tb_salida_ibfk_3` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_salida`
--

LOCK TABLES `tb_salida` WRITE;
/*!40000 ALTER TABLE `tb_salida` DISABLE KEYS */;
INSERT INTO `tb_salida` VALUES (1,'UC0004',1,1,10,'2026-01-15','Entrega uniforme',1),(2,'UC0005',1,1,15,'2026-02-10','Entrega uniforme',1),(3,'UC0007',1,1,20,'2026-03-18','Entrega uniforme',1),(4,'UC0008',1,1,18,'2026-04-12','Entrega uniforme',1),(5,'UC0009',1,1,25,'2026-05-20','Entrega uniforme',1),(6,'UC0010',1,1,30,'2026-06-06','Entrega uniforme',1),(7,'UC0015',2,1,12,'2026-01-22','Reposición',1),(8,'UC0055',2,1,17,'2026-02-19','Reposición',1),(9,'UC0074',2,1,22,'2026-03-21','Reposición',1),(10,'UC0083',2,1,15,'2026-04-24','Reposición',1),(11,'UC0004',2,1,28,'2026-05-27','Reposición',1),(12,'UC00011',7,1,2,'2026-06-24','Uniforme nuevo',1),(13,'UC00011',10,2,5,'2026-06-24','Productos en buen estado',1),(14,'UC00030',8,1,5,'2026-06-24','',1);
/*!40000 ALTER TABLE `tb_salida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_sede`
--

DROP TABLE IF EXISTS `tb_sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sede` (
  `id_sede` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL COMMENT '101=Lima, 102=Ica, 103=Arequipa',
  `nombre` varchar(100) NOT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `estado` tinyint DEFAULT '1' COMMENT '1=activo, 2=inactivo',
  PRIMARY KEY (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sede`
--

LOCK TABLES `tb_sede` WRITE;
/*!40000 ALTER TABLE `tb_sede` DISABLE KEYS */;
INSERT INTO `tb_sede` VALUES (1,'101','Almacén Central Lima','Av. Principal 123, Lima',1),(2,'102','Almacén Ica','Av. Grau 456, Ica',1),(3,'103','Almacén Arequipa','Av. Lima 789, Arequipa',1),(4,'104','Almacén Cuzco','Av. Benavides 267, Cuzco',1),(5,'105','Almacén Trujillo','Av. Trujillo 55, Trujillo',1),(6,'106','Almacen Piura','Av. Ucayali 166, Piura',1);
/*!40000 ALTER TABLE `tb_sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_stock_sede`
--

DROP TABLE IF EXISTS `tb_stock_sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_stock_sede` (
  `id_stock_sede` int NOT NULL AUTO_INCREMENT,
  `id_producto` varchar(20) NOT NULL,
  `id_sede` int NOT NULL,
  `cantidad` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_stock_sede`),
  UNIQUE KEY `uk_stock_sede_producto_sede` (`id_producto`,`id_sede`),
  KEY `fk_stock_sede_sede` (`id_sede`),
  CONSTRAINT `fk_stock_sede_producto` FOREIGN KEY (`id_producto`) REFERENCES `tb_producto` (`id_producto`),
  CONSTRAINT `fk_stock_sede_sede` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_stock_sede`
--

LOCK TABLES `tb_stock_sede` WRITE;
/*!40000 ALTER TABLE `tb_stock_sede` DISABLE KEYS */;
INSERT INTO `tb_stock_sede` VALUES (1,'UC0074',1,305),(2,'UC0004',1,710),(3,'UC0055',1,1288),(4,'UC0083',1,165),(5,'UC0015',1,310),(6,'UC0007',1,100),(7,'UC0006',1,70),(8,'UC0010',1,510),(9,'UC0005',1,100),(10,'UC0008',1,200),(11,'UC0009',1,317),(12,'UC00011',1,143),(13,'UC0001',1,65),(14,'UC00011',2,197),(16,'UC0004',2,100),(17,'UC0001',2,120),(18,'BD00021',2,30),(19,'BC00015',2,56),(20,'UC00030',1,45);
/*!40000 ALTER TABLE `tb_stock_sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_tipo_equipo`
--

DROP TABLE IF EXISTS `tb_tipo_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_tipo_equipo` (
  `id_tipo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL COMMENT 'Uniforme, EPP, Accesorio',
  `estado` tinyint DEFAULT '1',
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_tipo_equipo`
--

LOCK TABLES `tb_tipo_equipo` WRITE;
/*!40000 ALTER TABLE `tb_tipo_equipo` DISABLE KEYS */;
INSERT INTO `tb_tipo_equipo` VALUES (1,'Uniforme',1),(2,'EPP',1),(3,'Accesorio',1);
/*!40000 ALTER TABLE `tb_tipo_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_trabajador`
--

DROP TABLE IF EXISTS `tb_trabajador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_trabajador` (
  `id_trabajador` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(200) NOT NULL,
  `documento_identidad` varchar(20) NOT NULL,
  `puesto` varchar(100) DEFAULT NULL,
  `cliente` varchar(200) DEFAULT NULL COMMENT 'Empresa donde trabaja',
  `id_sede` int DEFAULT NULL,
  `activo_cesado` varchar(10) DEFAULT 'ACTIVO' COMMENT 'ACTIVO/CESADO',
  `fecha_ingreso` date DEFAULT NULL,
  `estado` tinyint DEFAULT '1',
  PRIMARY KEY (`id_trabajador`),
  KEY `id_sede` (`id_sede`),
  CONSTRAINT `tb_trabajador_ibfk_1` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_trabajador`
--

LOCK TABLES `tb_trabajador` WRITE;
/*!40000 ALTER TABLE `tb_trabajador` DISABLE KEYS */;
INSERT INTO `tb_trabajador` VALUES (1,'Juan Perez Torres','71234567','Supervisor de Seguridad ','Banco Continental',1,'ACTIVO','2025-01-15',NULL),(2,'Maria Lopez Garcia','72345678','Agente de Seguridad','Interbank',1,'ACTIVO','2025-02-10',1),(3,'Carlos Ramirez Soto','73456789','Agente de Seguridad','BBVA',1,'ACTIVO','2025-03-05',1),(4,'Luis Mendoza Cruz','74567890','Operador CCTV','Scotiabank',1,'CESADO','2025-01-20',1),(5,'Ana Flores Diaz','75678901','Agente de Seguridad','Saga Falabella',1,'ACTIVO','2025-04-01',1),(6,'Moises Quispe Mamani','12345678','Supervisor de Seguridad','Banco Banbif',1,'ACTIVO','2025-01-17',1),(7,'Jose Lopez Chumpe','98765432','Agente Operador','Ebony',1,'ACTIVO','2025-02-12',1),(8,'Angel Lescano Zea','67459876','Supervisor','PlazaVea',1,'ACTIVO','2025-03-26',1),(9,'Cristian Ramirez Cuzcano','89234097','Agente Operador','Bancho pichincha',1,'ACTIVO','2025-01-08',1),(10,'Juan Balvin Sulca','67755892','Operario de Vigilancia','Ripley',1,'ACTIVO','2025-04-10',1),(11,'ISRAEL ROMEL ZEA VALENZUELA','40776294','Supervisor de seguridad','Restaurante Rosita',1,'CESADO','2026-07-14',1),(12,'ARIANA FERNANDA MELGAREJO FERRO','75198925','Agente Operador','Movistar ICA',2,'ACTIVO','2026-06-19',1),(13,'LEYDE VANESSA MALLQUI CALDERON','46215041','Supervisor ','Entel ica',2,'CESADO','2026-06-19',1),(14,'DIEGO RODRIGUEZ ALBERTO','76492665','Supervisor','Promart',2,'ACTIVO','2026-06-19',1),(15,'GUIDO ALONSO LIONEL LARA CANDELA','76776412','Supervisor de seguridad','Restaurante Carmen',1,'CESADO','2026-06-24',1);
/*!40000 ALTER TABLE `tb_trabajador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_usuario`
--

DROP TABLE IF EXISTS `tb_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(200) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `rol` varchar(20) NOT NULL COMMENT 'ADMIN/ALMACEN',
  `id_sede` int NOT NULL,
  `estado` tinyint DEFAULT '1',
  `email` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`),
  KEY `id_sede` (`id_sede`),
  CONSTRAINT `tb_usuario_ibfk_1` FOREIGN KEY (`id_sede`) REFERENCES `tb_sede` (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_usuario`
--

LOCK TABLES `tb_usuario` WRITE;
/*!40000 ALTER TABLE `tb_usuario` DISABLE KEYS */;
INSERT INTO `tb_usuario` VALUES (1,'admin','Lp2proyecto','Administrador','ADMIN',1,1,'juliano290519@gmail.com'),(2,'lima','1234','Almacén Lima','ALMACEN',1,1,'maximilianojulianolopez@gmail.com'),(3,'ica','Ica1234','Almacén Ica','ALMACEN',2,1,'maximiliano290519@gmail.com'),(4,'arequipa','1234','Almacén Arequipa','ALMACEN',3,1,'julios290519@gmail.com'),(5,'admin2','1234','Administrador 2','ALMACEN',1,1,'silenneth@gmail.com'),(6,'cuzco','Cuzco1234','Almacen Cuzco','ALMACEN',4,1,'maxlopezavalos2905@gmail.com'),(7,'trujillo','Trujillo123','Almacen Trujillo','ALMACEN',5,1,'maxitolopez2905@gmail.com'),(8,'cuzco 2','cuzco123','Almacen Trujillo','ALMACEN',4,1,'maxitolopez2905@gmail.com'),(9,'trujillo2','Trujillo12','Almacen Trujillo','ALMACEN',5,1,'maxitolopez2905@gmail.com'),(10,'ica2','Ica12','Almacen Ica','ALMACEN',2,1,'maxitolopez2905@gmail.com'),(11,'Lionel Piura','Lionel123','Lionel Lara','ALMACEN',6,1,'lionel.2laracandela@gmail.com'),(12,'Maxi','Maxi123','Almacen de lima 2','ALMACEN',1,1,'i202319562@cibertec.edu.pe'),(13,'lima 3','123456','Almacén Lima','ALMACEN',1,1,'lopezvaleramaximojulian@gmail.com'),(14,'Angeles','AngelesPagame','Almacen Cuzco','ALMACEN',4,2,'papasalhilo.050420@gmail.com'),(15,'Jefferson','catalino1234','Almacén Lima','ALMACEN',1,1,'catalino.drowsy@gmail.com'),(16,'ann','Pagame123','Almacén Cuzco','ALMACEN',4,1,'u60545800@konecta.com'),(17,'Lionel2','123456789','Almacen Lima','ALMACEN',1,1,'guidoalonsolaracandela@gmail.com');
/*!40000 ALTER TABLE `tb_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_kardex`
--

DROP TABLE IF EXISTS `vw_kardex`;
/*!50001 DROP VIEW IF EXISTS `vw_kardex`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_kardex` AS SELECT 
 1 AS `id_producto`,
 1 AS `ean_int`,
 1 AS `descripcion`,
 1 AS `tipo`,
 1 AS `total_ingresos`,
 1 AS `total_salidas`,
 1 AS `stock_actual`,
 1 AS `costo_unitario`,
 1 AS `valorizado`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_kardex`
--

/*!50001 DROP VIEW IF EXISTS `vw_kardex`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_kardex` AS select `p`.`id_producto` AS `id_producto`,`p`.`ean_int` AS `ean_int`,`p`.`descripcion` AS `descripcion`,`t`.`nombre` AS `tipo`,coalesce(sum(`i`.`cantidad`),0) AS `total_ingresos`,coalesce(sum(`s`.`cantidad`),0) AS `total_salidas`,(coalesce(sum(`i`.`cantidad`),0) - coalesce(sum(`s`.`cantidad`),0)) AS `stock_actual`,`p`.`costo_unitario` AS `costo_unitario`,((coalesce(sum(`i`.`cantidad`),0) - coalesce(sum(`s`.`cantidad`),0)) * `p`.`costo_unitario`) AS `valorizado` from (((`tb_producto` `p` left join `tb_tipo_equipo` `t` on((`p`.`id_tipo` = `t`.`id_tipo`))) left join `tb_ingreso` `i` on((`p`.`id_producto` = `i`.`id_producto`))) left join `tb_salida` `s` on((`p`.`id_producto` = `s`.`id_producto`))) group by `p`.`id_producto`,`p`.`ean_int`,`p`.`descripcion`,`t`.`nombre`,`p`.`costo_unitario` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-24 21:05:25
