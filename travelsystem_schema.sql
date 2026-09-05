-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: travelsystem
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ts_attraction`
--

DROP TABLE IF EXISTS `ts_attraction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_attraction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `capacity` int DEFAULT NULL,
  `cover_image` varchar(200) DEFAULT NULL,
  `description` text,
  `location` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `open_time` varchar(100) DEFAULT NULL,
  `status` enum('CLOSED','MAINTENANCE','OPEN') NOT NULL,
  `ticket_price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ts_attraction_status` (`status`),
  KEY `idx_ts_attraction_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_hotel`
--

DROP TABLE IF EXISTS `ts_hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `address` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `description` text,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `star_level` int DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ts_hotel_city` (`city`),
  KEY `idx_ts_hotel_status` (`status`),
  KEY `idx_ts_hotel_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_notice`
--

DROP TABLE IF EXISTS `ts_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `content` text NOT NULL,
  `published` bit(1) NOT NULL,
  `title` varchar(200) NOT NULL,
  `type` enum('ACTIVITY','NEWS','NOTICE') NOT NULL,
  `author_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmxloyw1vj0p17g13tvta23fsp` (`author_id`),
  CONSTRAINT `FKmxloyw1vj0p17g13tvta23fsp` FOREIGN KEY (`author_id`) REFERENCES `ts_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_order`
--

DROP TABLE IF EXISTS `ts_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `contact_name` varchar(200) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `order_no` varchar(30) NOT NULL,
  `persons` int NOT NULL,
  `remark` text,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','PAID','PENDING','REFUNDED','REFUNDING') NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `travel_date` date DEFAULT NULL,
  `route_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKccpprvlpykh6f5nymy5fvk3jq` (`order_no`),
  KEY `idx_ts_order_status` (`status`),
  KEY `idx_ts_order_created_at` (`created_at`),
  KEY `idx_ts_order_user_id` (`user_id`),
  KEY `idx_ts_order_route_id` (`route_id`),
  CONSTRAINT `FKbaf4i4dfgrjore1vujnjhfm50` FOREIGN KEY (`route_id`) REFERENCES `ts_tour_route` (`id`),
  CONSTRAINT `FKghk26q0d5ugv4aqcn6sycfmfc` FOREIGN KEY (`user_id`) REFERENCES `ts_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_review`
--

DROP TABLE IF EXISTS `ts_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `content` text,
  `rating` int NOT NULL,
  `visible` bit(1) NOT NULL,
  `route_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj8hsyav07hkyb384pqnm2p258` (`route_id`),
  KEY `FKc7g2v06ril0j3kf33ffxjd635` (`user_id`),
  CONSTRAINT `FKc7g2v06ril0j3kf33ffxjd635` FOREIGN KEY (`user_id`) REFERENCES `ts_user` (`id`),
  CONSTRAINT `FKj8hsyav07hkyb384pqnm2p258` FOREIGN KEY (`route_id`) REFERENCES `ts_tour_route` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_tour_route`
--

DROP TABLE IF EXISTS `ts_tour_route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_tour_route` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `cover_image` varchar(200) DEFAULT NULL,
  `days` int NOT NULL,
  `departure` varchar(100) DEFAULT NULL,
  `description` text,
  `destination` varchar(100) DEFAULT NULL,
  `max_group_size` int DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` enum('DRAFT','OFFLINE','PUBLISHED') NOT NULL,
  `title` varchar(100) NOT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ts_route_status` (`status`),
  KEY `idx_ts_route_destination` (`destination`),
  KEY `idx_ts_route_hotel_id` (`hotel_id`),
  CONSTRAINT `FKj6fmghtwb2upjfxwhy9sq1l5` FOREIGN KEY (`hotel_id`) REFERENCES `ts_hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ts_user`
--

DROP TABLE IF EXISTS `ts_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ts_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `avatar` varchar(200) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKh2heum2ao735j82wjfost34l` (`username`),
  KEY `idx_ts_user_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-05 14:53:47
