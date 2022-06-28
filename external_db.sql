-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: external_db
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `is_logged_in` tinyint(4) NOT NULL DEFAULT '0',
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `telephone` varchar(45) DEFAULT NULL,
  `role` enum('CEO','STORE_MANAGER','CLIENT','CUSTOMER_SERVICE_WORKER','STORE_WORKER','COMPANY_MARKETING_WORKER','SERVICE_EXPERT','DELIVERY_MAN') NOT NULL,
  `account_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'client','client',0,'Omri','Gawi','zerli.g16@gmail.com','0542233111','CLIENT',1),(2,'sm','sm',0,'Ofek','Cohen','sm@gmail.com','0542233112','STORE_MANAGER',NULL),(3,'ceo','ceo',0,'Yuval','Markin','ceo@gmail.com','0542233113','CEO',NULL),(4,'csw','csw',0,'Tal','Cohen','zerli.g16@gmail.com','0542233114','CUSTOMER_SERVICE_WORKER',NULL),(5,'cmw','cmw',0,'Omri','Gawi','cmw@gmail.com','0542233115','COMPANY_MARKETING_WORKER',NULL),(6,'sw','sw',0,'Avi','Avi','sw@gmail.com','0542233116','STORE_WORKER',NULL),(7,'se','se',0,'Koral','Ben David','se@gmail.com','0542233117','SERVICE_EXPERT',NULL),(8,'dm','dm',0,'Avi','Shalom','dm@gmail.com','0542233118','DELIVERY_MAN',NULL),(9,'smhaifa','smhaifa',0,'Daniel','Yakir','smhaifa@gmail.com','0542233119','STORE_MANAGER',NULL),(10,'smtlv','smtlv',0,'Liran','Mizrahi','smtlv@gmail.com','0542233120','STORE_MANAGER',NULL),(11,'client1','client1',0,'Omer','Zulutnizky','client1@gmail.com','0542233121','CLIENT',2),(12,'client2','client2',0,'Daniel','Elmaliach','client2@gmail.com','0542233122','CLIENT',3),(13,'client3','client3',0,'Iftach','Shoham','client3@gmail.com','0542233123','CLIENT',4),(14,'client4','client4',0,'Ron','Berez','client4@gmail.com','0542233124','CLIENT',5),(15,'client5','client5',0,'Neomi','Yasmin','client5@gmail.com','0542233125','CLIENT',6),(16,'client6','client6',0,'Vered','Gold','client6@gmail.com','0542233126','CLIENT',7),(17,'client7','client7',0,'Dana','Avshalom','client7@gmail.com','0542233127','CLIENT',8),(18,'client8','client8',0,'Nurit','Danieli','client8@gmail.com','0542233128','CLIENT',9),(24,'client9','client9',0,'Amir','Levy','zerli.g16@gmail.com','0542233129','CLIENT',NULL),(25,'client10','client10',0,'May','Lotem','client10@gmail.com','0542233130','CLIENT',NULL),(26,'client11','client11',0,'Bar','Refaeli','client11@gmail.com','0542233131','CLIENT',NULL),(27,'client12','client12',0,'Eden','Ben Zaken','client12@gmail.com','0542233132','CLIENT',NULL),(28,'client13','client13',0,'Omer','Adam','client13@gmail.com','0542233133','CLIENT',NULL),(29,'client14','client14',0,'Anna','Zak','client14@gmail.com','0542233134','CLIENT',NULL),(30,'sw1','sw1',0,'Aviel','Yona','sw1@gmail.com','0542233135','STORE_WORKER',NULL),(31,'sw2','sw2',0,'Alex','Kovnetzki','sw2@gmail.com','0542233136','STORE_WORKER',NULL),(32,'csw1','csw1',0,'Naor','Barda','csw1@gmail.com','0542233137','CUSTOMER_SERVICE_WORKER',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'external_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-07 10:47:40
