-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Nov 04, 2024 at 04:08 AM
-- Server version: 9.0.1
-- PHP Version: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `kulvida`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `category_id` int NOT NULL,
  `category_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`category_id`, `category_name`) VALUES
(5, 'PANTALON'),
(16, 'HABIT'),
(21, 'JUPE NOIR'),
(22, 'JUPE'),
(23, 'SHORT'),
(27, 'CHAUSSURE');

-- --------------------------------------------------------

--
-- Table structure for table `clothes`
--

CREATE TABLE `clothes` (
  `cloth_id` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sexe` int DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `available` bit(1) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `brand` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `clothes`
--

INSERT INTO `clothes` (`cloth_id`, `description`, `discount`, `name`, `sexe`, `category_id`, `available`, `creation_date`, `last_update`, `sex`, `brand`) VALUES
(15, 'PANTALON NOIR', 20, 'PANTALON', NULL, 5, b'1', '2024-10-01 21:36:10', '2024-11-02 14:14:35', 0, 'ADIDAS'),
(17, 'CHAUSSURE', 0, 'ALL STAR', NULL, 27, b'1', '2024-10-01 22:29:29', '2024-11-02 14:42:20', 1, 'ADIDAS'),
(18, 'HABIT', 0, 'JUPE PLISSE', NULL, 22, b'1', '2024-10-01 22:30:58', '2024-11-02 11:20:22', 2, 'PUMA'),
(19, 'HABIT', 0, 'JUPE ECOSSAISE', NULL, 22, b'1', '2024-10-01 22:38:46', '2024-10-27 11:43:25', 2, 'PUMA'),
(20, 'HABIT', 0, 'JUPE LONGUE', NULL, 22, b'1', '2024-10-01 23:05:03', '2024-10-27 11:43:59', 2, 'PUMA'),
(24, 'HABIT', 0, 'SHORT', NULL, 23, b'1', '2024-10-12 11:06:09', NULL, 2, 'PUMA');

-- --------------------------------------------------------

--
-- Table structure for table `cloth_pictures`
--

CREATE TABLE `cloth_pictures` (
  `id` int NOT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `cloth_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cloth_pictures`
--

INSERT INTO `cloth_pictures` (`id`, `tag`, `url`, `cloth_id`) VALUES
(1, 'MAIN', 'https://ik.imagekit.io/Heisen/pant_sdWqP72hV.jpg', 15),
(4, 'MAIN', 'https://ik.imagekit.io/Heisen/allstar_KTKsKtPna.jpg', 17),
(5, 'MAIN', 'https://ik.imagekit.io/Heisen/jupe_mH8Q_1-cb.jpg', 18),
(6, 'MAIN', 'https://ik.imagekit.io/Heisen/ecossaise_oxwziBGi4.jpg', 19),
(7, 'MAIN', 'https://ik.imagekit.io/Heisen/short_a2Rr50tVs.jpg', 24),
(8, 'MAIN', 'https://ik.imagekit.io/Heisen/jupelongue_KXMflo2Ea.jpg', 20);

-- --------------------------------------------------------

--
-- Table structure for table `cloth_sizes`
--

CREATE TABLE `cloth_sizes` (
  `cloth_id` int NOT NULL,
  `size_id` int NOT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cloth_sizes`
--

INSERT INTO `cloth_sizes` (`cloth_id`, `size_id`, `price`, `quantity`) VALUES
(24, 7, 100, 10),
(19, 13, 10, 3),
(20, 7, 600, 0),
(18, 26, 400, 3),
(15, 7, 12, 14),
(15, 12, 10, 0),
(15, 13, 20, 30),
(15, 25, 15, 30),
(17, 70, 200, 3),
(17, 71, 300, 20);

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(92);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `id` int NOT NULL,
  `available` bit(1) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount` int DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `quantity` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`id`, `available`, `category`, `creation_date`, `description`, `discount`, `last_update`, `name`, `price`, `quantity`) VALUES
(1, b'1', 'habit', '2024-09-18 21:07:07', 'black pant', NULL, NULL, 'pant', 1000, 1),
(2, b'1', 'HABIT', '2024-09-21 03:49:25', 'SHORT', NULL, NULL, 'SHORT', 50, 5);

-- --------------------------------------------------------

--
-- Table structure for table `item_pictures`
--

CREATE TABLE `item_pictures` (
  `id` int NOT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `item_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `item_pictures`
--

INSERT INTO `item_pictures` (`id`, `tag`, `url`, `item_id`) VALUES
(1, 'MAIN', 'https://ik.imagekit.io/Heisen/pant_seFDrTTab.jpg', 1),
(2, NULL, 'https://ik.imagekit.io/Heisen/pant_AYcZ-TWis.jpg', 2),
(3, 'MAIN', 'https://ik.imagekit.io/Heisen/logo_mYn-91k__.PNG', 2);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `item_id` int NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `order_date` datetime DEFAULT NULL,
  `quantity` int NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  `user_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int NOT NULL,
  `discount` int DEFAULT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` double DEFAULT NULL,
  `cloth_id` int DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  `size_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `discount`, `price`, `quantity`, `subtotal`, `cloth_id`, `order_id`, `size_id`) VALUES
(32, 0, 400, 2, 800, 18, 'S5IOSOMDPXJIFA', 26),
(33, 20, 12, 1, 9.600000000000001, 15, 'KHCYNVJJLVA7PW', 7),
(34, 20, 12, 1, 9.600000000000001, 15, '2NAFMB0S5USDHA', 7),
(35, 20, 12, 1, 9.600000000000001, 15, 'ZLOGEWWWBJPWW', 7),
(36, 0, 200, 2, 400, 17, 'LUMA8FBFD8VKW', 7),
(37, 0, 600, 1, 600, 20, 'RTLISZETYMVIQ', 7),
(38, 0, 200, 1, 200, 17, 'IF77UPWZ9GHAG', 7),
(39, 0, 600, 1, 600, 20, 'XIHIYDQ6YUUNG', 7),
(40, 0, 600, 1, 600, 20, '5NVPAWXXVYTQYA', 7),
(47, 0, 400, 1, 400, 18, 'OFJQ2VUUKSMP1W', 26),
(48, 0, 400, 1, 400, 18, 'TITWPEJFRGOIEG', 26),
(49, 20, 10, 1, 8, 15, 'M5PT1IC30RHHAA', 12),
(50, 0, 200, 2, 400, 17, 'M5PT1IC30RHHAA', 7),
(51, 0, 400, 1, 400, 18, 'M5PT1IC30RHHAA', 26),
(52, 0, 200, 1, 200, 17, 'TKG5OARVTLEQA', 7),
(53, 0, 200, 1, 200, 17, 'TZLE8RE4V9TFIQ', 7),
(54, 20, 20, 1, 16, 15, 'TZLE8RE4V9TFIQ', 13),
(55, 0, 200, 1, 200, 17, 'WTAWJMTV8YTEBG', 7),
(56, 0, 400, 1, 400, 18, 'WTAWJMTV8YTEBG', 26),
(57, 20, 12, 1, 9.600000000000001, 15, 'VKNJC82WEC72UA', 7),
(58, 20, 12, 1, 9.600000000000001, 15, '4WGPSGBBTROG2W', 7),
(59, 20, 12, 1, 9.600000000000001, 15, 'R3BGJV1UBLQAKA', 7),
(60, 20, 12, 1, 9.600000000000001, 15, 'ASSH3UOV7YHMW', 7),
(61, 20, 12, 1, 9.600000000000001, 15, '7NSFV5NTQUUEEA', 7),
(62, 20, 12, 1, 9.600000000000001, 15, 'VJUSRIBSYR4CA', 7),
(63, 20, 12, 1, 9.600000000000001, 15, 'LVYBFDONHQKUOG', 7),
(64, 20, 12, 1, 9.600000000000001, 15, 'LQRGF3HG5IYKW', 7),
(65, 20, 12, 1, 9.600000000000001, 15, 'MMFU7JDN3SQQBG', 7),
(66, 0, 200, 1, 200, 17, 'TZIZWXSTSLWG', 7),
(67, 20, 12, 1, 9.600000000000001, 15, 'QIFKSERUES2X7A', 7),
(68, 0, 200, 1, 200, 17, 'QIFKSERUES2X7A', 7),
(69, 0, 10, 1, 10, 19, 'QIFKSERUES2X7A', 13),
(72, 0, 200, 1, 200, 17, 'KOJGJ8FBK7VDKQ', 70),
(73, 20, 12, 1, 9.600000000000001, 15, 'IU6BEZDFL0WK1Q', 7),
(74, 0, 10, 1, 10, 19, 'W9Z37CVR8ILUXQ', 13),
(75, 20, 12, 1, 9.600000000000001, 15, 'RHEZI6MXH1I5MQ', 7),
(76, 0, 200, 1, 200, 17, '22N8K73LX1HIA', 70),
(77, 0, 200, 1, 200, 17, 'F25UZNW98NIUMG', 70),
(78, 20, 12, 2, 19.200000000000003, 15, 'ZIVTTYNX9F5UJW', 7),
(79, 20, 12, 1, 9.600000000000001, 15, 'NLB8KTNONO22YG', 7),
(80, 20, 12, 1, 9.600000000000001, 15, 'EH3ISZYTTAKLWG', 7),
(82, 20, 12, 1, 9.600000000000001, 15, 'T2FPMD6XFVJRW', 7),
(83, 0, 200, 1, 200, 17, 'T2FPMD6XFVJRW', 70),
(84, 20, 12, 2, 19.200000000000003, 15, 'CBOMEE7UHDSCQ', 7),
(85, 0, 200, 1, 200, 17, 'G9GC12HSJUMGBQ', 70),
(86, 20, 12, 1, 9.600000000000001, 15, 'OVCKGFJZALOM0Q', 7),
(87, 0, 200, 1, 200, 17, 'QLYDZRPMVRXRQG', 70),
(88, 0, 400, 1, 400, 18, 'X0KP2XCO4A7FSW', 26),
(89, 0, 200, 1, 200, 17, 'UX5JZYF6Q2JWQW', 70),
(90, 0, 200, 3, 600, 17, 'HMC2PVGTI4G', 70),
(91, 0, 200, 2, 400, 17, 'HMC2PVGTI4G', 70);

-- --------------------------------------------------------

--
-- Table structure for table `sizes`
--

CREATE TABLE `sizes` (
  `size_id` int NOT NULL,
  `size_name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `sizes`
--

INSERT INTO `sizes` (`size_id`, `size_name`, `price`) VALUES
(7, 'S', NULL),
(12, 'XS', NULL),
(13, 'M', NULL),
(25, 'XL', NULL),
(26, 'XXL', NULL),
(70, '10', NULL),
(71, '11', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `password`, `phone`, `role`, `username`) VALUES
(1, 'Laurence', 'Ngankem', '$2a$10$3MVy9xPrhVQzDeFL6rVd0.RAQdCqWsX/CdT6KnvkSfuc.3iKTG6VC', '+393803407233', 'ADMIN', 'laurencengankem@yahoo.fr'),
(4, 'Laurence', 'Ngankem', '$2a$10$YDsltfpizmL93nKDToGtmeNHmJMH.bVNrooAx6FkKjwGPBXBHzmre', '+23723994048', 'USER', 'bbastien@yahoo.com'),
(81, 'Patrick', 'Ngankem', '$2a$10$6er5g7PDev8kkjnHyDHNxe/GkgTnmi9xgNvQ/TLHlxB6grf28mBXq', '+2133452834', 'USER', 'patrickngankem@yahoo.fr');

-- --------------------------------------------------------

--
-- Table structure for table `user_address`
--

CREATE TABLE `user_address` (
  `id` int NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` int DEFAULT NULL,
  `user_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_items`
--

CREATE TABLE `user_items` (
  `user_id` int NOT NULL,
  `cloth_id` int NOT NULL,
  `quantity` int NOT NULL,
  `size` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_order`
--

CREATE TABLE `user_order` (
  `order_id` varchar(255) NOT NULL,
  `order_date` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user_order`
--

INSERT INTO `user_order` (`order_id`, `order_date`, `status`, `user_id`) VALUES
('22N8K73LX1HIA', '2024-11-02 15:28:48', 'INITIALIZED', 1),
('2NAFMB0S5USDHA', '2024-11-02 11:25:08', 'INITIALIZED', 1),
('4WGPSGBBTROG2W', '2024-11-02 14:10:27', 'INITIALIZED', 1),
('5NVPAWXXVYTQYA', '2024-11-02 12:12:49', 'INITIALIZED', 1),
('7NSFV5NTQUUEEA', '2024-11-02 14:15:15', 'INITIALIZED', 1),
('ASSH3UOV7YHMW', '2024-11-02 14:13:20', 'INITIALIZED', 1),
('CBOMEE7UHDSCQ', '2024-11-02 16:14:03', 'INITIALIZED', 1),
('EH3ISZYTTAKLWG', '2024-11-02 16:02:41', 'INITIALIZED', 1),
('F25UZNW98NIUMG', '2024-11-02 15:33:40', 'INITIALIZED', 1),
('G9GC12HSJUMGBQ', '2024-11-02 16:35:52', 'INITIALIZED', 1),
('HMC2PVGTI4G', '2024-11-03 04:26:56', 'INITIALIZED', 1),
('IF77UPWZ9GHAG', '2024-11-02 11:36:18', 'INITIALIZED', 1),
('IU6BEZDFL0WK1Q', '2024-11-02 14:44:32', 'INITIALIZED', 1),
('KHCYNVJJLVA7PW', '2024-11-02 11:21:19', 'INITIALIZED', 1),
('KOJGJ8FBK7VDKQ', '2024-11-02 14:42:36', 'INITIALIZED', 1),
('LQRGF3HG5IYKW', '2024-11-02 14:27:42', 'INITIALIZED', 1),
('LUMA8FBFD8VKW', '2024-11-02 11:30:12', 'INITIALIZED', 1),
('LVYBFDONHQKUOG', '2024-11-02 14:26:53', 'INITIALIZED', 1),
('M5PT1IC30RHHAA', '2024-11-02 13:32:56', 'INITIALIZED', 1),
('MMFU7JDN3SQQBG', '2024-11-02 14:31:07', 'INITIALIZED', 1),
('NLB8KTNONO22YG', '2024-11-02 15:50:52', 'INITIALIZED', 1),
('OFJQ2VUUKSMP1W', '2024-11-02 12:20:34', 'INITIALIZED', 1),
('OVCKGFJZALOM0Q', '2024-11-02 16:53:26', 'INITIALIZED', 1),
('QIFKSERUES2X7A', '2024-11-02 14:38:36', 'INITIALIZED', 1),
('QLYDZRPMVRXRQG', '2024-11-02 16:55:08', 'INITIALIZED', 1),
('R3BGJV1UBLQAKA', '2024-11-02 14:12:10', 'INITIALIZED', 1),
('RHEZI6MXH1I5MQ', '2024-11-02 15:26:52', 'INITIALIZED', 1),
('RTLISZETYMVIQ', '2024-11-02 11:34:42', 'INITIALIZED', 1),
('S5IOSOMDPXJIFA', '2024-11-02 11:20:26', 'INITIALIZED', 1),
('T2FPMD6XFVJRW', '2024-11-02 16:12:55', 'INITIALIZED', 1),
('TITWPEJFRGOIEG', '2024-11-02 12:22:47', 'INITIALIZED', 1),
('TKG5OARVTLEQA', '2024-11-02 13:36:21', 'INITIALIZED', 1),
('TZIZWXSTSLWG', '2024-11-02 14:35:23', 'INITIALIZED', 1),
('TZLE8RE4V9TFIQ', '2024-11-02 13:45:05', 'INITIALIZED', 1),
('UX5JZYF6Q2JWQW', '2024-11-02 18:51:20', 'INITIALIZED', 1),
('VJUSRIBSYR4CA', '2024-11-02 14:17:59', 'INITIALIZED', 1),
('VKNJC82WEC72UA', '2024-11-02 14:09:00', 'INITIALIZED', 1),
('W9Z37CVR8ILUXQ', '2024-11-02 14:56:23', 'INITIALIZED', 1),
('WTAWJMTV8YTEBG', '2024-11-02 13:50:43', 'INITIALIZED', 1),
('X0KP2XCO4A7FSW', '2024-11-02 16:58:12', 'INITIALIZED', 1),
('XIHIYDQ6YUUNG', '2024-11-02 12:12:46', 'INITIALIZED', 1),
('ZIVTTYNX9F5UJW', '2024-11-02 15:46:47', 'INITIALIZED', 1),
('ZLOGEWWWBJPWW', '2024-11-02 11:27:49', 'INITIALIZED', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `clothes`
--
ALTER TABLE `clothes`
  ADD PRIMARY KEY (`cloth_id`),
  ADD KEY `FKhuemmmxpg0baj2dfbhslojm85` (`category_id`);

--
-- Indexes for table `cloth_pictures`
--
ALTER TABLE `cloth_pictures`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKbgwn82ylcrxhw58obpe7m3t1t` (`cloth_id`);

--
-- Indexes for table `cloth_sizes`
--
ALTER TABLE `cloth_sizes`
  ADD KEY `FKay1gcillu1s82e0e42lq4u2a5` (`size_id`),
  ADD KEY `FKndc708chguvq9xvq8jtiu8h01` (`cloth_id`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `item_pictures`
--
ALTER TABLE `item_pictures`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKhychkwavgy4lpnpnbgo7n73l7` (`item_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`item_id`,`order_id`),
  ADD KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKgbnrii6gxtg5tvfg1drvokfij` (`cloth_id`),
  ADD KEY `FKiu1e4lmt328kgxyf767jwqm7c` (`order_id`),
  ADD KEY `FK9t2qyxv7hnjv24ox49t7oyga2` (`size_id`);

--
-- Indexes for table `sizes`
--
ALTER TABLE `sizes`
  ADD PRIMARY KEY (`size_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_address`
--
ALTER TABLE `user_address`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrmincuqpi8m660j1c57xj7twr` (`user_id`);

--
-- Indexes for table `user_items`
--
ALTER TABLE `user_items`
  ADD PRIMARY KEY (`user_id`,`cloth_id`),
  ADD KEY `FK89nfyu4xtq229cby3eshq5cmr` (`cloth_id`);

--
-- Indexes for table `user_order`
--
ALTER TABLE `user_order`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `FKbbwlke5ei3gh1ki65yiiojmck` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cloth_pictures`
--
ALTER TABLE `cloth_pictures`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `item_pictures`
--
ALTER TABLE `item_pictures`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `clothes`
--
ALTER TABLE `clothes`
  ADD CONSTRAINT `FKhuemmmxpg0baj2dfbhslojm85` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`);

--
-- Constraints for table `cloth_pictures`
--
ALTER TABLE `cloth_pictures`
  ADD CONSTRAINT `FKbgwn82ylcrxhw58obpe7m3t1t` FOREIGN KEY (`cloth_id`) REFERENCES `clothes` (`cloth_id`);

--
-- Constraints for table `cloth_sizes`
--
ALTER TABLE `cloth_sizes`
  ADD CONSTRAINT `FKay1gcillu1s82e0e42lq4u2a5` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`size_id`),
  ADD CONSTRAINT `FKndc708chguvq9xvq8jtiu8h01` FOREIGN KEY (`cloth_id`) REFERENCES `clothes` (`cloth_id`);

--
-- Constraints for table `item_pictures`
--
ALTER TABLE `item_pictures`
  ADD CONSTRAINT `FKhychkwavgy4lpnpnbgo7n73l7` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK247nnxschdfm8lre0ssvy3k1r` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`),
  ADD CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `FK9t2qyxv7hnjv24ox49t7oyga2` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`size_id`),
  ADD CONSTRAINT `FKgbnrii6gxtg5tvfg1drvokfij` FOREIGN KEY (`cloth_id`) REFERENCES `clothes` (`cloth_id`),
  ADD CONSTRAINT `FKiu1e4lmt328kgxyf767jwqm7c` FOREIGN KEY (`order_id`) REFERENCES `user_order` (`order_id`);

--
-- Constraints for table `user_address`
--
ALTER TABLE `user_address`
  ADD CONSTRAINT `FKrmincuqpi8m660j1c57xj7twr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `user_items`
--
ALTER TABLE `user_items`
  ADD CONSTRAINT `FK55mpmb46vtbmw6xljldr2wvvf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK89nfyu4xtq229cby3eshq5cmr` FOREIGN KEY (`cloth_id`) REFERENCES `clothes` (`cloth_id`);

--
-- Constraints for table `user_order`
--
ALTER TABLE `user_order`
  ADD CONSTRAINT `FKbbwlke5ei3gh1ki65yiiojmck` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
