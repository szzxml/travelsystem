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
-- Current Database: `travelsystem`
--

/*!40000 DROP DATABASE IF EXISTS `travelsystem`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `travelsystem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `travelsystem`;

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
-- Dumping data for table `ts_attraction`
--

LOCK TABLES `ts_attraction` WRITE;
/*!40000 ALTER TABLE `ts_attraction` DISABLE KEYS */;
INSERT INTO `ts_attraction` VALUES (2,'2026-06-24 23:29:07.737158','2026-06-24 23:34:54.077575',5000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=800&q=80','集客家文化、民俗风情、生态山水旅游于一体的文旅胜地，拥有古色古香的客家土楼群与百花盛放的自然生态园。','梅州','客天下客家小镇生态度假区','08:30 - 18:00','OPEN',68),(3,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000',10000,'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80','北半球最南端的雄伟雪山，终年积雪覆盖，蓝月谷如翡翠般纯净，是国家5A级著名旅游景区。','云南丽江','玉龙雪山国家风景名胜区','07:30 - 16:30','OPEN',100),(4,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000',8000,'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=80','桂林山水的经典地标精华段，奇峰夹岸，碧水回环，泛舟江上宛如置身水墨丹青画卷中。','广西桂林','漓江风景名胜区·九马画山','08:00 - 17:30','OPEN',80),(5,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000',12000,'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80','背负青山，面朝碧海，巨石耸立于蔚蓝海岸边，承载千年浪漫传奇与南国海滨绝美景致。','海南三亚','天涯海角游览区','07:30 - 18:20','OPEN',68);
/*!40000 ALTER TABLE `ts_attraction` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ts_hotel`
--

LOCK TABLES `ts_hotel` WRITE;
/*!40000 ALTER TABLE `ts_hotel` DISABLE KEYS */;
INSERT INTO `ts_hotel` VALUES (4,'2026-06-24 22:42:48.186110','2026-06-24 23:38:10.972785','广东省梅州市梅江区客天下旅游产业园东路1号','梅州','坐落于客天下景区核心腹地，依山傍水，环境清幽，提供五星级奢华度假客房与地道客家养生美食服务。','梅州客天下国际大酒店','0753-8168888',5,'ACTIVE');
/*!40000 ALTER TABLE `ts_hotel` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ts_notice`
--

LOCK TABLES `ts_notice` WRITE;
/*!40000 ALTER TABLE `ts_notice` DISABLE KEYS */;
INSERT INTO `ts_notice` VALUES (2,'2026-09-05 13:21:31.000000','2026-09-05 13:21:31.000000','即日起，全平台上线云南、桂林、川西等当季热门精品路线，包含高品质合作酒店与全程安心出行服务保障。',_binary '','2026春季精选旅游路线特惠上线公告','NOTICE',NULL),(3,'2026-09-05 13:21:31.000000','2026-09-05 13:21:31.000000','为保障各位游客舒适安全的出游体验，建议出行前提前查阅景区开放时间并备齐有效身份证件，合理安排游览行程。',_binary '','关于热门景区实名制预约与客流高峰提示','NOTICE',NULL),(4,'2026-09-05 13:21:31.000000','2026-09-05 13:21:31.000000','精选多条长线深度游，预订即享合作星级酒店优先房型升级，支持在线流转极速确认出团。',_binary '','春日出游季：合作酒店联合立减与多日游线路礼遇活动','ACTIVITY',NULL);
/*!40000 ALTER TABLE `ts_notice` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ts_order`
--

LOCK TABLES `ts_order` WRITE;
/*!40000 ALTER TABLE `ts_order` DISABLE KEYS */;
INSERT INTO `ts_order` VALUES (1,'2026-04-28 16:31:59.604469','2026-04-28 16:33:08.507760','1','1','TS202604281631591304',1,'','PAID',10.00,'2026-04-29',1,1,NULL),(8,'2026-06-24 22:58:26.697513','2026-06-24 23:12:35.956750','123','123','TS202606242258264655',1,'','PAID',200.00,'2026-06-25',6,1,NULL),(10,'2026-06-24 23:37:38.045014','2026-06-24 23:37:57.778258','0','000','TS202606242337389638',1,'','PAID',10.00,'2026-06-26',1,1,NULL),(11,'2026-06-24 23:42:22.867659','2026-06-24 23:42:36.610022','999','999','TS202606242342227107',1,'','COMPLETED',200.00,'2026-06-25',6,1,NULL);
/*!40000 ALTER TABLE `ts_order` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ts_review`
--

LOCK TABLES `ts_review` WRITE;
/*!40000 ALTER TABLE `ts_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `ts_review` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ts_tour_route`
--

LOCK TABLES `ts_tour_route` WRITE;
/*!40000 ALTER TABLE `ts_tour_route` DISABLE KEYS */;
INSERT INTO `ts_tour_route` VALUES (1,'2026-03-28 18:35:09.667388','2026-06-24 23:28:53.291885','https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=800&q=80',6,'上海','漫步大理古城，泛舟洱海生态廊道，登临玉龙雪山冰川公园，品味纯正云南特色慢生活与自然风光。','云南',30,2980.00,'PUBLISHED','七彩云南·昆明大理丽江6日风情深度游',NULL),(5,'2026-06-24 22:42:09.338647','2026-06-24 23:42:45.774802','https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=800&q=80',3,'梅州','登广州塔俯瞰珠江新城CBD全景，搭乘珠江游船欣赏两岸流光溢彩，品味西关老字号正宗粤式早茶点心。','广州',25,880.00,'PUBLISHED','羊城经典·广州塔地标与珠江夜游3日游',NULL),(6,'2026-06-24 22:57:39.743739','2026-06-24 22:57:39.743739','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',4,'梅州','漫步盐田海滨栈道与大梅沙沙滩，感受南国滨海椰风；游览深圳湾科技生态园区，感受创新之都的独特魅力。','深圳',20,1280.00,'PUBLISHED','滨海鹏城·深圳大梅沙海滨与现代科技都市4日游',4),(7,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=800&q=80',4,'广州','泛舟漓江赏喀斯特奇峰倒影，遇龙河人工竹筏漂流，夜游阳朔西街品尝特色啤酒鱼。','桂林',20,1580.00,'PUBLISHED','山水甲天下·桂林漓江竹筏与阳朔西街4日休闲游',4),(8,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000','https://images.unsplash.com/photo-1508804185872-d7badad00f7d?auto=format&fit=crop&w=800&q=80',5,'上海','探访紫禁城六百年皇城底蕴，登临八达岭长城感受壮丽河山，游览皇家园林颐和园与天坛祈年殿。','北京',35,2680.00,'PUBLISHED','古都风韵·北京故宫长城与颐和园5日历史研学游',4),(9,'2026-09-05 13:19:41.000000','2026-09-05 13:19:41.000000','https://images.unsplash.com/photo-1516483638261-f4dbaf036963?auto=format&fit=crop&w=800&q=80',6,'深圳','漫步锦里宽窄巷子品尝川味美食，深度探寻九寨沟五彩瑶池与诺日朗瀑布的童话仙境。','成都',24,3280.00,'PUBLISHED','蜀道天府·成都锦里与九寨沟仙境6日摄影品质游',4);
/*!40000 ALTER TABLE `ts_tour_route` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `ts_user`
--

LOCK TABLES `ts_user` WRITE;
/*!40000 ALTER TABLE `ts_user` DISABLE KEYS */;
INSERT INTO `ts_user` VALUES (1,'2026-03-20 14:02:48.370452','2026-03-20 14:02:48.370452',NULL,NULL,_binary '','$2a$10$/SB2yZKUKVFw7nKVwvYvRueDHPAGerC.5EPjfxA46byXGz3PoCScK',NULL,'超级管理员','ADMIN','admin'),(2,'2026-03-20 15:31:00.079484','2026-03-28 18:33:29.671921',NULL,NULL,_binary '','$2a$10$X/SbXSNN.1OSsNNbv8uN6O7aZA.L5vSmRi18SHpGLRT9UOqLWOy2O','','','USER','jyu'),(3,'2026-06-12 11:19:42.354214','2026-06-24 23:43:59.334861',NULL,'tester70720@test.com',_binary '','$2a$10$jTDOMfJk7DX9nevw6.zlQOPnR6Iy6HonzCeLwoZPqy2gDln.rXhOC','13800001111',NULL,'USER','tester70720'),(4,'2026-06-12 11:19:43.100840','2026-06-12 11:19:43.100840',NULL,NULL,_binary '','$2a$10$nfkE1vNnitvftk7WJoVDUezWcwtYNEVoIkv7NfXbB2WdK7F4mkqFG',NULL,NULL,'USER','tester15208'),(5,'2026-06-12 11:45:25.476896','2026-06-12 11:45:25.476896',NULL,NULL,_binary '','$2a$10$0BgUcABdd.AnXwBlDacQA.HtdIltaVmeqC0KvHsS0qLyF29wbAZlK',NULL,NULL,'USER','tester10951'),(6,'2026-06-12 11:45:49.632566','2026-06-12 11:45:49.632566',NULL,NULL,_binary '','$2a$10$ZROsmsYZalrH9ylE1x4YTOLb/g6ijtgqUras.J9anJk595MgFmHWq',NULL,NULL,'USER','dbg2890');
/*!40000 ALTER TABLE `ts_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'travelsystem'
--

--
-- Dumping routines for database 'travelsystem'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-05 14:53:17
