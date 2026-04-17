-- =====================================================
-- Library Management System - Database Setup (Stage 3)
-- =====================================================

DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db;
USE library_db;

-- Table structure for table `member` (with BLOB for F17)
CREATE TABLE `member` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `name` varchar(100) NOT NULL,
                          `address` varchar(200) NOT NULL,
                          `phone` varchar(20) NOT NULL,
                          `file_name` varchar(255) NOT NULL DEFAULT '',
                          `content_type` varchar(100) NOT NULL DEFAULT '',
                          `file_size` int(11) NOT NULL DEFAULT 0,
                          `profile_image` LONGBLOB,
                          PRIMARY KEY (`id`)
);

-- Insert sample members
INSERT INTO `member` (`id`, `name`, `address`, `phone`, `file_name`, `content_type`, `file_size`, `profile_image`) VALUES
                                                                                                                       (1, 'Ali Abdi', '123 Main St, Dublin', '087-123-4567', '', '', 0, NULL),
                                                                                                                       (2, 'Mary Johnson', '45 Oak Avenue, Dundalk', '086-234-5678', '', '', 0, NULL),
                                                                                                                       (3, 'Mohammed Ali', '78 High Street, Galway', '085-345-6789', '', '', 0, NULL);

-- Verification
SELECT * FROM member;