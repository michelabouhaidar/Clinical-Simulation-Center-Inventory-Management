-- Valentina Studio --
-- MySQL dump --
-- ---------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
-- ---------------------------------------------------------


-- CREATE DATABASE "dbproject" -----------------------------
CREATE DATABASE IF NOT EXISTS `dbproject` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `dbproject`;
-- ---------------------------------------------------------


-- CREATE TABLE "BORROWING" ------------------------------------
CREATE TABLE `BORROWING`( 
	`BORROWID` Int( 0 ) NOT NULL,
	`UPDATED_BY` Int( 0 ) NULL DEFAULT NULL,
	`BRANCHID` Int( 0 ) NOT NULL,
	`CREATED_BY` Int( 0 ) NOT NULL,
	`DEPTID` Int( 0 ) NOT NULL,
	`BORROWCODE` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`STARTDATE` Date NULL DEFAULT NULL,
	`ENDDATE` Date NULL DEFAULT NULL,
	`NOTES` VarChar( 1024 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`BORROWSTATUS` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CREATED_ON` Timestamp NULL DEFAULT NULL,
	`UPDATED_ON` Timestamp NULL DEFAULT NULL,
	PRIMARY KEY ( `BORROWID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "BORROW_CONS" ----------------------------------
CREATE TABLE `BORROW_CONS`( 
	`BORROWID` Int( 0 ) NOT NULL,
	`STOCKID` Int( 0 ) NOT NULL,
	`QUANTITY` Int( 0 ) NULL DEFAULT NULL,
	PRIMARY KEY ( `BORROWID`, `STOCKID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "BORROW_SIM" -----------------------------------
CREATE TABLE `BORROW_SIM`( 
	`SIMID` Int( 0 ) NOT NULL,
	`BORROWID` Int( 0 ) NOT NULL,
	`CONDOUT` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CONDIN` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`RETURNNOTES` VarChar( 1024 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	PRIMARY KEY ( `SIMID`, `BORROWID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "BRANCH" ---------------------------------------
CREATE TABLE `BRANCH`( 
	`BRANCHID` Int( 0 ) NOT NULL,
	`NAME` VarChar( 120 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
	`LOCATION` VarChar( 200 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	PRIMARY KEY ( `BRANCHID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "CONSUMABLE" -----------------------------------
CREATE TABLE `CONSUMABLE`( 
	`CONSID` Int( 0 ) AUTO_INCREMENT NOT NULL,
	`ITEMNAME` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`MEASURE` VarChar( 6 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	PRIMARY KEY ( `CONSID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 170;
-- -------------------------------------------------------------


-- CREATE TABLE "DEPARTMENT" -----------------------------------
CREATE TABLE `DEPARTMENT`( 
	`DEPTID` Int( 0 ) NOT NULL,
	`DEPTNAME` VarChar( 150 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CONTACTNAME` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`PHONE1` VarChar( 12 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`PHONE2` VarChar( 12 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`EMAIL` VarChar( 120 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`SCHOOL` VarChar( 150 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	PRIMARY KEY ( `DEPTID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "MAINTAINED" -----------------------------------
CREATE TABLE `MAINTAINED`( 
	`SIMID` Int( 0 ) NOT NULL,
	`EVENTID` Int( 0 ) NOT NULL,
	PRIMARY KEY ( `SIMID`, `EVENTID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "MAINTENANCE" ----------------------------------
CREATE TABLE `MAINTENANCE`( 
	`EVENTID` Int( 0 ) NOT NULL,
	`UPDATED_BY` Int( 0 ) NULL DEFAULT NULL,
	`CREATED_BY` Int( 0 ) NOT NULL,
	`TYPE` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`EVENTSTARTDATE` Date NULL DEFAULT NULL,
	`EVENTENDDATE` Date NULL DEFAULT NULL,
	`EVENTNOTES` VarChar( 1024 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`VENDOR` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CREATED_ON` Timestamp NULL DEFAULT NULL,
	`UPDATED_ON` Timestamp NULL DEFAULT NULL,
	PRIMARY KEY ( `EVENTID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- CREATE TABLE "SIMULATOR" ------------------------------------
CREATE TABLE `SIMULATOR`( 
	`SIMID` Int( 0 ) AUTO_INCREMENT NOT NULL,
	`CREATED_BY` Int( 0 ) NOT NULL,
	`UPDATED_BY` Int( 0 ) NULL DEFAULT NULL,
	`BRANCHID` Int( 0 ) NOT NULL,
	`MODELID` Int( 0 ) NOT NULL,
	`TAG` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
	`SN` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`SIMSTATUS` VarChar( 60 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CONDNOTES` VarChar( 1024 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CALDATE` Date NULL DEFAULT NULL,
	`NEXTCALDATE` Date NULL DEFAULT NULL,
	`CREATED_ON` Timestamp NULL DEFAULT NULL,
	`UPDATED_ON` Timestamp NULL DEFAULT NULL,
	PRIMARY KEY ( `SIMID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 229;
-- -------------------------------------------------------------


-- CREATE TABLE "SIMULATOR_MODEL" ------------------------------
CREATE TABLE `SIMULATOR_MODEL`( 
	`MODELID` Int( 0 ) AUTO_INCREMENT NOT NULL,
	`MODELNAME` VarChar( 50 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`SPECS` VarChar( 1024 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`CALREQ` TinyInt( 1 ) NULL DEFAULT NULL,
	`MAXDAYS` Int( 0 ) NULL DEFAULT NULL,
	PRIMARY KEY ( `MODELID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 83;
-- -------------------------------------------------------------


-- CREATE TABLE "STOCK" ----------------------------------------
CREATE TABLE `STOCK`( 
	`STOCKID` Int( 0 ) AUTO_INCREMENT NOT NULL,
	`CREATED_BY` Int( 0 ) NOT NULL,
	`BRANCHID` Int( 0 ) NOT NULL,
	`UPDATED_BY` Int( 0 ) NULL DEFAULT NULL,
	`CONSID` Int( 0 ) NOT NULL,
	`AVAILABLEQ` Int( 0 ) NULL DEFAULT NULL,
	`RESERVEDQ` Int( 0 ) NULL DEFAULT NULL,
	`LASTCOUNTDATE` Date NULL DEFAULT NULL,
	`CREATED_ON` Timestamp NULL DEFAULT NULL,
	`UPDATED_ON` Timestamp NULL DEFAULT NULL,
	PRIMARY KEY ( `STOCKID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB
AUTO_INCREMENT = 339;
-- -------------------------------------------------------------


-- CREATE TABLE "USERS" ----------------------------------------
CREATE TABLE `USERS`( 
	`USERID` Int( 0 ) NOT NULL,
	`USERNAME` VarChar( 20 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`DISPLAYNAME` VarChar( 80 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`ROLE` VarChar( 20 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`PASSHASH` VarChar( 150 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`IS_ACTIVE` TinyInt( 1 ) NULL DEFAULT NULL,
	`USERMAIL` VarChar( 120 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
	`BRANCHID` Int( 0 ) NULL DEFAULT NULL,
	`RESET` TinyInt( 1 ) NOT NULL,
	PRIMARY KEY ( `USERID` ) )
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
ENGINE = InnoDB;
-- -------------------------------------------------------------


-- Dump data of "BORROWING" --------------------------------
BEGIN;

INSERT INTO `BORROWING`(`BORROWID`,`UPDATED_BY`,`BRANCHID`,`CREATED_BY`,`DEPTID`,`BORROWCODE`,`STARTDATE`,`ENDDATE`,`NOTES`,`BORROWSTATUS`,`CREATED_ON`,`UPDATED_ON`) VALUES 
( '1', '3', '2', '3', '105', 'BRW-20251215-1', '2025-12-15', NULL, 'Test', 'CLOSED', '2025-12-15 18:48:09', '2025-12-15 19:23:19' ),
( '2', '3', '2', '3', '203', 'BRW-20251219-2', '2025-12-19', '2025-12-26', 'test', 'CLOSED', '2025-12-19 09:08:40', '2025-12-19 09:11:01' ),
( '3', '3', '2', '3', '201', 'BRW-20251219-3', '2025-12-19', '2025-12-30', 'test', 'CANCELLED', '2025-12-19 09:11:25', '2025-12-19 09:11:31' ),
( '4', '3', '2', '3', '101', 'BRW-20251219-4', '2025-12-19', '2025-12-31', '', 'CLOSED', '2025-12-19 09:13:17', '2025-12-19 09:14:16' ),
( '5', '3', '2', '3', '202', 'BRW-20251219-5', '2025-12-19', NULL, 'test', 'ACTIVE', '2025-12-19 09:14:48', '2025-12-19 09:14:48' ),
( '6', '5', '2', '5', '200', 'BRW-20251219-6', '2025-12-19', '2025-12-25', '', 'PARTIALLY_RETURNED', '2025-12-19 11:29:40', '2025-12-19 11:31:02' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "BORROW_CONS" ------------------------------
BEGIN;

INSERT INTO `BORROW_CONS`(`BORROWID`,`STOCKID`,`QUANTITY`) VALUES 
( '1', '171', '29' ),
( '1', '172', '1' ),
( '1', '185', '2' ),
( '1', '186', '6' ),
( '2', '331', '2' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "BORROW_SIM" -------------------------------
BEGIN;

INSERT INTO `BORROW_SIM`(`SIMID`,`BORROWID`,`CONDOUT`,`CONDIN`,`RETURNNOTES`) VALUES 
( '10', '1', '', 'test', 'test' ),
( '10', '2', '', 'test', 'test' ),
( '18', '2', '', 'test', 'test' ),
( '18', '4', '', '', '' ),
( '28', '1', '', '', '' ),
( '28', '2', '', 'test', 'test' ),
( '42', '4', '', 'test', 'test' ),
( '42', '6', '', '', '' ),
( '50', '6', '', NULL, NULL ),
( '55', '1', '', '', '' ),
( '55', '2', '', 'test', 'test' ),
( '61', '4', '', '', '' ),
( '138', '1', '', '', '' ),
( '167', '1', '', '', '' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "BRANCH" -----------------------------------
BEGIN;

INSERT INTO `BRANCH`(`BRANCHID`,`NAME`,`LOCATION`) VALUES 
( '1', 'LAU Beirut Campus', 'Beirut, Lebanon' ),
( '2', 'LAU Byblos Campus', 'Byblos, Lebanon' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "CONSUMABLE" -------------------------------
BEGIN;

INSERT INTO `CONSUMABLE`(`CONSID`,`ITEMNAME`,`MEASURE`) VALUES 
( '1', '2 mL seringes boxes (100 pcs)', 'BOX' ),
( '2', '5 mL seringes boxes (100 pcs)', 'BOX' ),
( '3', '10 mL seringes boxes (100 pcs)', 'BOX' ),
( '4', '10 mL seringes cartons (1200 pcs)', 'BOX' ),
( '5', 'Latex Gloves Boxes S', 'BOX' ),
( '6', 'Latex Gloves Boxes M', 'BOX' ),
( '7', 'Latex Gloves Boxes L', 'BOX' ),
( '8', 'Latex Gloves Boxes XL', 'BOX' ),
( '9', 'Nitrile Examination Gloves Cartons (10 boxes)', 'BOX' ),
( '10', 'Diapers Baby (20 pcs)', 'BOX' ),
( '11', 'Diapers Large (10 pcs)', 'BOX' ),
( '12', 'Underpad Boxes (30 pcs)', 'BOX' ),
( '13', 'Underpad Boxes (15 pcs)', 'BOX' ),
( '14', 'Large Pads (12 pcs)', 'BOX' ),
( '15', '1L Sodium Chloride 0.9% (12 units of perfusion)', 'BOX' ),
( '16', '100 mL Sodium Chloride 0.9% (96 units of perfusion)', 'BOX' ),
( '17', '1L 5% Dextrose', 'BOTTLE' ),
( '18', '1L 5% Dextrose and 0.9% Sodium Chloride', 'BOTTLE' ),
( '19', '1L 2.5% Dextrose and 0.45% Sodium Chloride', 'BOTTLE' ),
( '20', 'Bassin', 'EA' ),
( '21', 'tracheostomy tube cuffless 7.5', 'EA' ),
( '22', 'tracheostomy tube cuffed 7.6', 'EA' ),
( '23', 'tracheostomy tube cuffed 10', 'EA' ),
( '24', 'tracheostomy tube cuffed 8.9', 'EA' ),
( '25', 'tracheostomy tube cuffed 9.4', 'EA' ),
( '26', 'tracheostomy tube cuffless 3', 'EA' ),
( '27', 'airway lubricant', 'EA' ),
( '28', 'Quick trach adult', 'SET' ),
( '29', 'OR Steril equipment cover', 'EA' ),
( '30', 'Laser probe 20 curved', 'EA' ),
( '31', 'Bone Marrow Needle', 'EA' ),
( '32', 'Swab', 'EA' ),
( '33', 'ETT 3.0', 'EA' ),
( '34', 'ETT 3.5', 'EA' ),
( '35', 'ETT 4.0', 'EA' ),
( '36', 'ETT 4.5', 'EA' ),
( '37', 'ETT 5.0', 'EA' ),
( '38', 'ETT 5.5', 'EA' ),
( '39', 'ETT 6', 'EA' ),
( '40', 'ETT 6.5', 'EA' ),
( '41', 'ETT 7', 'EA' ),
( '42', 'ETT 7.5', 'EA' ),
( '43', 'ETT 8', 'EA' ),
( '44', 'ETT 8.5', 'EA' ),
( '45', 'Steril peripherial veinous kit', 'SET' ),
( '46', 'Arterial Catheterization set', 'SET' ),
( '47', 'CVC used set', 'SET' ),
( '48', 'Chemotherapy dressing set', 'SET' ),
( '49', 'Chemotherapy kit', 'SET' ),
( '50', 'Steril IV dressing kit', 'SET' ),
( '51', 'Epidural dressing (Epi-Fix)', 'EA' ),
( '52', 'Endobronchial Blocker set', 'SET' ),
( '53', 'Nerve Block kit', 'SET' ),
( '54', 'Epidural needle kit', 'SET' ),
( '55', 'Epidural Catheterization kit', 'SET' ),
( '56', 'Spinal Needle', 'EA' ),
( '57', 'PCA Kit + Ropivacaine', 'SET' ),
( '58', 'Laryngoscope Blades - Box', 'BOX' ),
( '59', 'Laryngoscope handle', 'EA' ),
( '60', 'LMA 3.0', 'EA' ),
( '61', 'LMA 5.0', 'EA' ),
( '62', 'OPA Various Sizes - box', 'BOX' ),
( '63', '5 ways stopcock', 'EA' ),
( '64', '3 ways stopcock-box', 'BOX' ),
( '65', 'Butterfly needle', 'EA' ),
( '66', 'CVC dressing set', 'SET' ),
( '67', 'Hemodialysis dressing set', 'SET' ),
( '68', 'Regular dressing set', 'SET' ),
( '69', 'IV Cannula 14G- Box', 'BOX' ),
( '70', 'IV Cannula 22G- Box', 'BOX' ),
( '71', 'IV Cannula 24G- Box', 'BOX' ),
( '72', 'Huber Needle - Box', 'BOX' ),
( '73', 'Tegaderm 10x12 - Box', 'BOX' ),
( '74', 'Tegaderm IV dressing - Box', 'BOX' ),
( '75', 'Alcohol swabs -Box', 'BOX' ),
( '76', 'Dosimeter', 'EA' ),
( '77', 'IV set without flowmeter', 'SET' ),
( '78', 'IV set with flowmeter', 'SET' ),
( '79', 'IV pump set ALARIS', 'SET' ),
( '80', 'Ampoules Various', 'EA' ),
( '81', 'Vials Various', 'EA' ),
( '82', 'Ampoules Saline 10 ML - Box', 'BOX' ),
( '83', 'Spirometer', 'EA' ),
( '84', 'Medication Cups', 'EA' ),
( '85', 'Inhalers', 'EA' ),
( '86', 'Ampoules Steril water 10 ML- Box', 'BOX' ),
( '87', 'Solvant Ampoules', 'EA' ),
( '88', 'Methotrexate injector pen', 'EA' ),
( '89', 'Urine test strip for glucose- Box', 'BOX' ),
( '90', 'Oral Tabltes Various', 'EA' ),
( '91', 'Pill Cutter', 'EA' ),
( '92', 'Medication Lable', 'EA' ),
( '93', 'Ultrasond Gel- btt', 'BOTTLE' ),
( '94', 'KY Gel - Tube', 'TUBE' ),
( '95', 'KY Gel - Sachet', 'PAC' ),
( '96', 'Synthetic Stockinet', 'EA' ),
( '97', 'Wound evacuation system', 'SET' ),
( '98', 'Tracheo Mask', 'EA' ),
( '99', 'Pediatric FaceMask', 'EA' ),
( '100', 'Adult NRB Mask', 'EA' ),
( '101', 'Adult Nebulizer Mask', 'EA' ),
( '102', 'Pediatric Nebulaizer Mask', 'EA' ),
( '103', 'Adult FaceMask', 'EA' ),
( '104', 'Adult Nasla Canula', 'EA' ),
( '105', 'Suction Catheter Various', 'EA' ),
( '106', 'Suction Control Valve', 'EA' ),
( '107', 'Suction Canister', 'EA' ),
( '108', 'O2 Humidifier', 'EA' ),
( '109', 'Ventilator Circuit single use', 'SET' ),
( '110', 'Ventilator Circuit Re-usable', 'SET' ),
( '111', 'Incentive Spirometer', 'EA' ),
( '112', 'NGT Various (6, 8, 10)', 'EA' ),
( '113', 'NGT Various (12, 14, 16)', 'EA' ),
( '114', 'Duodenal Tube', 'EA' ),
( '115', 'Spigot', 'EA' ),
( '116', 'Defibrilator Pads', 'EA' ),
( '117', 'Electrodes', 'EA' ),
( '118', 'Disposable Adulte oxymeter', 'EA' ),
( '119', 'ECG Paper', 'PAC' ),
( '120', 'Surgical Cap', 'EA' ),
( '121', 'Gown single use', 'EA' ),
( '122', 'Bath Gloves', 'EA' ),
( '123', 'Nail Brush', 'EA' ),
( '124', 'Cotton Balls - bag', 'BAG' ),
( '125', 'Hand Sanitizer 5L', 'BOTTLE' ),
( '126', 'Hand Sanitizer bottles', 'BOTTLE' ),
( '127', 'Steril Gloves 6.0 - Box', 'BOX' ),
( '128', 'Steril Gloves 6.5 - Box', 'BOX' ),
( '129', 'Steril Gloves 7.0 - Box', 'BOX' ),
( '130', 'Steril Gloves 7.5 - Box', 'BOX' ),
( '131', 'Steril Gloves 8.0 - Box', 'BOX' ),
( '132', 'Steril Gloves 8.5 - Box', 'BOX' ),
( '133', 'Shoe cover -Pac', 'PAC' ),
( '134', 'Scrub sponges- Box', 'BOX' ),
( '135', 'Absorbant Pad- Pac', 'PAC' ),
( '136', 'Band aid- Box', 'BOX' ),
( '137', 'Tongue Depressor- Box', 'BOX' ),
( '138', 'H2O2', 'BOTTLE' ),
( '139', 'Betadine - bottle', 'BOTTLE' ),
( '140', 'Artificial Blood- Bottle', 'BOTTLE' ),
( '141', 'Betadine swabs - Box', 'BOX' ),
( '142', 'Alcohol Bottle 1L', 'BOTTLE' ),
( '143', 'Alcohol 5L', 'BOTTLE' ),
( '144', 'Transpore tape', 'ROLL' ),
( '145', 'Durapore Tape', 'ROLL' ),
( '146', 'Steril Cups', 'EA' ),
( '147', 'Food Colorant', 'BOTTLE' ),
( '148', 'cosmopore 20x10 -Box', 'BOX' ),
( '149', 'Cosmopore 15x8', 'EA' ),
( '150', 'Paraffine Dressing Gauze', 'EA' ),
( '151', 'Steril Gauze- Pac', 'PAC' ),
( '152', 'Non Steril Gauze', 'PAC' ),
( '153', 'Silicone suturing pad', 'EA' ),
( '154', 'Small Suturing pad', 'EA' ),
( '155', 'Duoderm extra thin', 'EA' ),
( '156', 'Sutures Various sizes', 'EA' ),
( '157', 'Reusable Hypodermic needle - Pac', 'PAC' ),
( '158', 'Suturing skin pad trainer', 'EA' ),
( '159', 'Suturing trainer kit', 'SET' ),
( '160', 'Surgical Blade', 'EA' ),
( '161', 'Thermometer Probs- Pac', 'PAC' ),
( '162', 'Disposable Needle 18G - Box', 'BOX' ),
( '163', 'Disposable Needle 21G - Box', 'BOX' ),
( '164', 'Disposable Needle 23G - Box', 'BOX' ),
( '165', 'Disposable Needle 25G - Box', 'BOX' ),
( '166', 'Disposable Needle 27G - Box', 'BOX' ),
( '167', 'HGT Lancets', 'PAC' ),
( '168', 'HGT Strips - pac', 'PAC' ),
( '169', 'GioTest', 'EA' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "DEPARTMENT" -------------------------------
BEGIN;

INSERT INTO `DEPARTMENT`(`DEPTID`,`DEPTNAME`,`CONTACTNAME`,`PHONE1`,`PHONE2`,`EMAIL`,`SCHOOL`) VALUES 
( '100', 'School of Arts and Sciences - Dean\'s Office', 'Laila Ghorayeb', '+9611786456', '1105', 'lghrayeb@lau.edu.lb', 'School of Arts and Sciences' ),
( '101', 'Department of Biological Sciences', 'Hanan Naccache', '+9611786456', '1114', 'dbs@lau.edu.lb', 'School of Arts and Sciences' ),
( '102', 'Department of Communication, Mobility and Identity', 'Houry Gostanian', '+9611786456', '1272', 'houri.gostanian@lau.edu.lb', 'School of Arts and Sciences' ),
( '103', 'Department of Computer Science and Mathematics', 'Dania Shebaro', '+9611786456', '1813', 'dania.shebaro@lau.edu.lb', 'School of Arts and Sciences' ),
( '104', 'Department of English and Creative Arts', 'Carla Farah', '+9611786456', '1170', 'cfarah@lau.edu.lb', 'School of Arts and Sciences' ),
( '105', 'Department of Liberal Studies', 'Rola Jaber', '+9611786456', NULL, 'rola.jaber@lau.edu.lb', 'School of Arts and Sciences' ),
( '106', 'Department of Psychology and Education', 'Dina Noueiri', '+9611786456', '1648', 'dina.noueiri@lau.edu.lb', 'School of Arts and Sciences' ),
( '107', 'Department of Political and International Studies', 'Chantal Chaoul', '+9611786456', '1981', 'chantal.chaoul@lau.edu.lb', 'School of Arts and Sciences' ),
( '108', 'Department of Physical Sciences', 'Alissar Elbakht', '+9619547254', '2320', 'DPS@lau.edu.lb', 'School of Arts and Sciences' ),
( '109', 'Department of Nutrition and Food Science', 'Lorita Hanna', '+9611786456', '2481', NULL, 'School of Arts and Sciences' ),
( '200', 'School of Architecture and Design - Dean\'s Office', 'Dean\'s Office', '+9619547254', '2474', 'dean.sard@lau.edu.lb', 'School of Architecture and Design' ),
( '201', 'Department of Architecture and Interior Design', 'Department Office', '+9611786456', '2474', 'DAID@lau.edu.lb', 'School of Architecture and Design' ),
( '202', 'Department of Art and Design', 'Department Office', '+9611786456', '1748', 'DAD@lau.edu.lb', 'School of Architecture and Design' ),
( '203', 'Foundation Program (Fine Arts & Foundation Studies)', 'Department Office', '+9611786456', '2474', NULL, 'School of Architecture and Design' ),
( '300', 'Adnan Kassar School of Business - Dean\'s Office', 'Dean\'s Office', '+9611786456', '1237', 'dean.sb@lau.edu.lb', 'Adnan Kassar School of Business' ),
( '301', 'Department of Management Studies', 'Vera Jarrous', '+9611786456', '1244', 'mgtdepartment@lau.edu.lb', 'Adnan Kassar School of Business' ),
( '302', 'Department of Marketing', NULL, '+9611786456', '2355', 'mkt@lau.edu.lb', 'Adnan Kassar School of Business' ),
( '303', 'Department of Finance and Accounting', 'Nuhad Karkuti', '+9611786456', '2355', NULL, 'Adnan Kassar School of Business' ),
( '304', 'Department of Hospitality and Tourism Management', NULL, '+9611786456', '2355', NULL, 'Adnan Kassar School of Business' ),
( '305', 'Department of Information Technology and Operations Management', NULL, '+9611786456', '1356', NULL, 'Adnan Kassar School of Business' ),
( '306', 'Department of Economics', NULL, '+9611786456', '1846', NULL, 'Adnan Kassar School of Business' ),
( '400', 'School of Engineering - Dean\'s Office', 'Rola Abi Akl', '+9619547254', '2236', 'dean.soe@lau.edu.lb', 'School of Engineering' ),
( '401', 'Department of Civil Engineering', NULL, '+9619547254', '1532', NULL, 'School of Engineering' ),
( '402', 'Department of Electrical and Computer Engineering', NULL, '+9619547254', '1532', NULL, 'School of Engineering' ),
( '403', 'Department of Industrial and Mechanical Engineering', NULL, '+9619547254', '1699', NULL, 'School of Engineering' ),
( '500', 'School of Pharmacy', 'School Office', '+9619547254', NULL, 'SOPsupport@lau.edu.lb', 'School of Pharmacy' ),
( '501', 'Alice Ramez Chagoury School of Nursing', 'School Office', '+9619547254', '2494', 'nursing.school@lau.edu.lb', 'Alice Ramez Chagoury School of Nursing' ),
( '502', 'Gilbert and Rose-Marie Chagoury School of Medicine (CME Office)', 'CME Office', '+9611200800', '5830', 'cmeoffice@lau.edu.lb', 'Gilbert and Rose-Marie Chagoury School of Medicine' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "MAINTAINED" -------------------------------
BEGIN;

INSERT INTO `MAINTAINED`(`SIMID`,`EVENTID`) VALUES 
( '221', '1' ),
( '222', '1' ),
( '223', '1' ),
( '224', '1' ),
( '221', '2' ),
( '222', '2' ),
( '223', '2' ),
( '224', '2' ),
( '221', '3' ),
( '222', '3' ),
( '223', '3' ),
( '224', '3' ),
( '221', '4' ),
( '224', '4' ),
( '222', '5' ),
( '223', '5' ),
( '225', '5' ),
( '96', '6' ),
( '97', '6' ),
( '13', '7' ),
( '23', '7' ),
( '43', '7' ),
( '10', '8' ),
( '23', '8' ),
( '39', '8' ),
( '63', '8' ),
( '223', '9' ),
( '227', '10' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "MAINTENANCE" ------------------------------
BEGIN;

INSERT INTO `MAINTENANCE`(`EVENTID`,`UPDATED_BY`,`CREATED_BY`,`TYPE`,`EVENTSTARTDATE`,`EVENTENDDATE`,`EVENTNOTES`,`VENDOR`,`CREATED_ON`,`UPDATED_ON`) VALUES 
( '1', '1', '1', 'Repair', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 02:47:59', '2025-12-07 17:54:35' ),
( '2', '1', '1', 'test1', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 17:54:56', '2025-12-07 17:56:00' ),
( '3', '1', '1', 'test3', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 18:04:32', '2025-12-07 18:10:38' ),
( '4', '1', '1', 'test4', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 18:11:14', '2025-12-07 18:21:36' ),
( '5', '1', '1', 'test5', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 18:11:58', '2025-12-07 18:21:27' ),
( '6', '1', '1', 'test6', '2025-12-07', '2025-12-07', 'test', 'test', '2025-12-07 18:12:34', '2025-12-07 18:21:17' ),
( '7', '1', '1', 'testgio', '2025-12-15', '2025-12-15', 'testgio', 'testgio', '2025-12-15 17:50:21', '2025-12-15 17:51:07' ),
( '8', '3', '3', 'test', '2025-12-19', '2025-12-19', 'test', 'test', '2025-12-19 09:01:15', '2025-12-19 09:01:59' ),
( '9', '3', '3', 'test', '2025-12-19', '2025-12-19', 'test', 'test', '2025-12-19 09:02:19', '2025-12-19 09:02:40' ),
( '10', '5', '5', 'test', '2025-12-19', '2025-12-19', NULL, NULL, '2025-12-19 11:31:41', '2025-12-19 11:32:41' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "SIMULATOR" --------------------------------
BEGIN;

INSERT INTO `SIMULATOR`(`SIMID`,`CREATED_BY`,`UPDATED_BY`,`BRANCHID`,`MODELID`,`TAG`,`SN`,`SIMSTATUS`,`CONDNOTES`,`CALDATE`,`NEXTCALDATE`,`CREATED_ON`,`UPDATED_ON`) VALUES 
( '1', '1', NULL, '1', '1', '20349', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '2', '1', NULL, '1', '2', '44223', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '3', '1', NULL, '2', '2', '44224', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '4', '1', NULL, '1', '3', '44221', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '5', '1', NULL, '2', '3', '44222', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '6', '1', NULL, '1', '4', 'M4-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '7', '1', NULL, '1', '5', '25189', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '8', '1', NULL, '1', '5', '20350', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '9', '1', NULL, '2', '5', '20362', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '10', '1', '3', '2', '5', '25188', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:11:01' ),
( '11', '1', NULL, '1', '6', '25186', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '12', '1', NULL, '1', '6', '20351', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '13', '1', '1', '2', '6', '20359', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-15 17:50:40' ),
( '14', '1', NULL, '2', '6', '25187', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '15', '1', NULL, '1', '7', '20363', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '16', '1', NULL, '1', '7', '25185', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '17', '1', NULL, '2', '7', '20347', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '18', '1', '3', '2', '7', '25184', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:14:08' ),
( '19', '1', NULL, '1', '8', 'M8-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '20', '1', NULL, '1', '9', '25177', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '21', '1', NULL, '1', '9', '25176', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '22', '1', NULL, '2', '9', '20348', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '23', '1', '3', '2', '9', '20360', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:01:59' ),
( '24', '1', NULL, '1', '10', 'M10-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '25', '1', NULL, '1', '11', '25178', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '26', '1', NULL, '2', '11', '20345', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '27', '1', '2', '1', '12', '20344', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-06 16:54:55' ),
( '28', '1', '3', '2', '12', '25179', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:10:39' ),
( '29', '1', NULL, '1', '13', 'M13-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '30', '1', NULL, '1', '13', 'M13-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '31', '1', NULL, '2', '13', 'M13-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '32', '1', NULL, '1', '14', 'M14-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '33', '1', NULL, '2', '14', 'M14-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '34', '1', NULL, '1', '15', 'M15-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '35', '1', NULL, '2', '15', 'M15-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '36', '1', NULL, '1', '16', '44219', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '37', '1', NULL, '2', '16', '44220', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '38', '1', NULL, '1', '17', '39140', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '39', '1', '3', '2', '17', '39141', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:01:59' ),
( '40', '1', NULL, '1', '18', '25201', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '41', '1', NULL, '1', '19', '25180', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '42', '1', '5', '2', '19', '25182', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 11:31:02' ),
( '43', '1', '1', '2', '19', '20361', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-15 17:51:07' ),
( '44', '1', NULL, '1', '20', 'M20-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '45', '1', NULL, '1', '20', 'M20-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '46', '1', NULL, '2', '20', 'M20-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '47', '1', NULL, '2', '20', 'M20-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '48', '1', NULL, '1', '21', '25181', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '49', '1', NULL, '1', '21', '20358', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '50', '1', '5', '2', '21', '25183', NULL, 'BORROWED', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 11:30:48' ),
( '51', '1', '3', '2', '21', '20346', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 08:57:50' ),
( '52', '1', NULL, '1', '22', 'M22-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '53', '1', NULL, '1', '23', '39683', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '54', '1', NULL, '1', '24', '25035', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '55', '1', '3', '2', '24', '25036', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:10:17' ),
( '56', '1', NULL, '1', '25', '51837', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '57', '1', NULL, '1', '25', '51838', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '58', '1', NULL, '1', '25', '51839', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '59', '1', NULL, '1', '25', '38888', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '60', '1', NULL, '1', '25', '38889', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '61', '1', '3', '2', '25', '38890', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:14:16' ),
( '62', '1', NULL, '2', '25', '38891', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '63', '1', '3', '2', '25', '38892', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:01:59' ),
( '64', '1', NULL, '2', '25', 'M25-9', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '65', '1', NULL, '1', '26', 'M26-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '66', '1', NULL, '1', '27', 'M27-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '67', '1', NULL, '1', '28', '25012', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '68', '1', NULL, '1', '29', 'M29-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '69', '1', NULL, '1', '30', 'M30-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '70', '1', NULL, '1', '30', 'M30-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '71', '1', NULL, '2', '30', 'M30-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '72', '1', NULL, '2', '30', 'M30-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '73', '1', NULL, '2', '30', 'M30-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '94', '1', NULL, '1', '32', 'M32-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '95', '1', NULL, '1', '33', 'M33-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '96', '1', '1', '1', '34', '19994', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-07 18:21:17' ),
( '97', '1', '5', '2', '34', '19995', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 11:17:45' ),
( '98', '1', NULL, '1', '35', '25055', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '99', '1', NULL, '1', '36', '45832', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '100', '1', NULL, '1', '37', 'M37-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '101', '1', NULL, '1', '37', 'M37-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '102', '1', NULL, '1', '38', 'M38-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '103', '1', NULL, '1', '39', 'M39-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '104', '1', NULL, '1', '40', 'M40-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '105', '1', NULL, '1', '41', 'M41-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '106', '1', NULL, '1', '42', 'M42-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '107', '1', NULL, '1', '43', 'M43-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '108', '1', NULL, '2', '43', 'M43-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '109', '1', NULL, '1', '44', 'M44-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '110', '1', NULL, '1', '44', 'M44-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '111', '1', NULL, '1', '44', 'M44-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '112', '1', NULL, '1', '44', 'M44-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '113', '1', NULL, '1', '44', 'M44-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '114', '1', NULL, '2', '44', 'M44-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '115', '1', NULL, '2', '44', 'M44-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '116', '1', NULL, '1', '45', 'M45-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '117', '1', NULL, '1', '45', 'M45-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '118', '1', NULL, '1', '45', 'M45-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '119', '1', NULL, '2', '45', 'M45-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '120', '1', NULL, '2', '45', 'M45-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '121', '1', NULL, '2', '45', 'M45-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '122', '1', NULL, '1', '46', 'M46-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '123', '1', NULL, '1', '47', 'M47-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '124', '1', NULL, '1', '48', 'M48-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '125', '1', NULL, '1', '49', 'M49-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '126', '1', NULL, '2', '49', 'M49-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '127', '1', NULL, '1', '50', 'M50-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '128', '1', NULL, '1', '50', 'M50-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '129', '1', NULL, '1', '50', 'M50-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '130', '1', NULL, '1', '50', 'M50-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '131', '1', NULL, '2', '50', 'M50-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '132', '1', NULL, '2', '50', 'M50-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '133', '1', NULL, '2', '50', 'M50-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '134', '1', NULL, '1', '51', 'M51-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '135', '1', NULL, '1', '52', '25019(1)', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '136', '1', NULL, '2', '52', 'M52-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '137', '1', NULL, '1', '53', '25845', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '138', '1', '3', '2', '53', '25167', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-15 19:17:02' ),
( '139', '1', NULL, '1', '54', 'M54-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '140', '1', NULL, '1', '54', 'M54-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '141', '1', NULL, '1', '54', 'M54-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '142', '1', NULL, '1', '54', 'M54-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '143', '1', NULL, '1', '54', 'M54-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '144', '1', NULL, '2', '54', 'M54-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '145', '1', NULL, '2', '54', 'M54-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '146', '1', NULL, '2', '54', 'M54-8', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '147', '1', NULL, '2', '54', 'M54-9', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '148', '1', NULL, '1', '55', 'M55-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '149', '1', NULL, '2', '55', 'M55-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '150', '1', NULL, '1', '56', 'M56-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '151', '1', NULL, '1', '57', 'M57-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '152', '1', NULL, '1', '57', 'M57-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '153', '1', NULL, '2', '57', 'M57-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '154', '1', NULL, '2', '57', 'M57-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '155', '1', NULL, '1', '58', 'M58-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '156', '1', NULL, '1', '58', 'M58-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '157', '1', NULL, '2', '58', 'M58-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '158', '1', NULL, '2', '58', 'M58-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '159', '1', NULL, '1', '59', '25161', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '160', '1', NULL, '1', '60', 'M60-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '161', '1', NULL, '1', '60', 'M60-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '162', '1', NULL, '1', '60', 'M60-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '163', '1', NULL, '1', '60', 'M60-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '164', '1', NULL, '2', '60', 'M60-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '165', '1', NULL, '2', '60', 'M60-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '166', '1', NULL, '2', '60', 'M60-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '167', '1', '3', '2', '60', 'M60-8', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-15 19:23:19' ),
( '168', '1', NULL, '2', '60', 'M60-9', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '169', '1', NULL, '1', '61', 'M61-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '170', '1', NULL, '1', '62', 'M62-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '171', '1', NULL, '1', '62', 'M62-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '172', '1', NULL, '1', '62', 'M62-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '173', '1', NULL, '1', '62', 'M62-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '174', '1', NULL, '2', '62', 'M62-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '175', '1', NULL, '2', '62', 'M62-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '176', '1', NULL, '2', '62', 'M62-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '177', '1', NULL, '2', '62', 'M62-8', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '178', '1', NULL, '1', '63', 'M63-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '179', '1', NULL, '1', '63', 'M63-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '180', '1', NULL, '1', '63', 'M63-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '181', '1', NULL, '2', '63', 'M63-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '182', '1', NULL, '2', '63', 'M63-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '183', '1', NULL, '2', '63', 'M63-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '184', '1', NULL, '2', '63', 'M63-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '185', '1', NULL, '1', '64', '51384', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '186', '1', NULL, '1', '64', '51385', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '187', '1', NULL, '2', '64', '51386', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '188', '1', NULL, '2', '64', '51387', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '189', '1', NULL, '1', '65', 'M65-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '190', '1', NULL, '1', '66', 'M66-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '191', '1', NULL, '1', '66', 'M66-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '192', '1', NULL, '1', '66', 'M66-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '193', '1', NULL, '2', '66', 'M66-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '194', '1', NULL, '2', '66', 'M66-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '195', '1', NULL, '2', '66', 'M66-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '196', '1', NULL, '2', '66', 'M66-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '197', '1', NULL, '1', '67', 'M67-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '198', '1', NULL, '1', '67', 'M67-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '199', '1', NULL, '1', '67', 'M67-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '200', '1', NULL, '1', '67', 'M67-4', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '201', '1', NULL, '1', '67', 'M67-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '202', '1', NULL, '2', '67', 'M67-6', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '203', '1', NULL, '2', '67', 'M67-7', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '204', '1', NULL, '2', '67', 'M67-8', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '205', '1', NULL, '2', '67', 'M67-9', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '206', '1', NULL, '2', '67', 'M67-10', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '207', '1', NULL, '1', '68', 'M68-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '208', '1', NULL, '1', '68', 'M68-2', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '209', '1', NULL, '1', '68', 'M68-3', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '210', '1', '3', '2', '68', 'M68-4', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 00:00:00', '2025-12-19 09:24:10' ),
( '211', '1', NULL, '2', '68', 'M68-5', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '212', '1', NULL, '1', '69', 'M69-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '213', '1', NULL, '1', '70', 'M70-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '214', '1', NULL, '1', '71', 'M71-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '215', '1', NULL, '1', '72', 'M72-1', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '216', '1', NULL, '1', '73', '25031', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '217', '1', NULL, '1', '74', '25030', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '218', '1', NULL, '1', '75', '39684', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '219', '1', NULL, '1', '76', '53562', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '220', '1', NULL, '1', '77', '54619', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 00:00:00', NULL ),
( '221', '2', '1', '1', '78', '12345', NULL, 'OUT_OF_SERVICE', NULL, NULL, NULL, '2025-12-02 19:38:06', '2025-12-15 17:48:50' ),
( '222', '2', '1', '1', '78', '12346', NULL, 'AVAILABLE', NULL, NULL, NULL, '2025-12-02 20:02:19', '2025-12-07 18:21:27' ),
( '223', '3', '3', '2', '78', '12347', '14142536475869', 'AVAILABLE', 'new', NULL, '2025-12-04', '2025-12-02 20:05:42', '2025-12-19 09:02:40' ),
( '224', '2', '1', '1', '78', '123457', NULL, 'OUT_OF_SERVICE', NULL, NULL, '2025-12-07', '2025-12-06 16:36:01', '2025-12-07 18:21:31' ),
( '225', '2', '1', '1', '80', '12348', NULL, 'AVAILABLE', NULL, NULL, '2025-12-07', '2025-12-06 16:37:21', '2025-12-07 18:21:27' ),
( '226', '3', '3', '2', '81', '28374', '37734', 'AVAILABLE', 'test', NULL, '2025-12-09', '2025-12-19 09:25:07', '2025-12-19 09:25:07' ),
( '227', '5', '5', '2', '60', '29456', '23283', 'AVAILABLE', 'test', NULL, '2025-12-18', '2025-12-19 11:19:01', '2025-12-19 11:32:41' ),
( '228', '5', '5', '2', '60', '12347', '46464', 'AVAILABLE', 'test', NULL, '2025-12-04', '2025-12-19 11:19:42', '2025-12-19 11:19:42' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "SIMULATOR_MODEL" --------------------------
BEGIN;

INSERT INTO `SIMULATOR_MODEL`(`MODELID`,`MODELNAME`,`SPECS`,`CALREQ`,`MAXDAYS`) VALUES 
( '1', 'PPH trainer P97 Pro', 'Postpartum hemorrhage obstetric simulation trainer', '0', '7' ),
( '2', 'Lumbar Puncture Pediatric', 'Pediatric lumbar puncture training simulator', '0', '7' ),
( '3', 'Pedicatric IV arm task trainer', 'Pediatric intravenous arm task trainer', '0', '7' ),
( '4', 'Walking Canes', 'Walking canes for mobility training', '0', '7' ),
( '5', 'Female Catheterization Trainer', 'Female urinary catheterization training model', '0', '7' ),
( '6', 'Male Catheterization Trainer', 'Male urinary catheterization training model', '0', '7' ),
( '7', 'Rectal Examination Trainer', 'Rectal examination clinical skills trainer', '0', '7' ),
( '8', 'Central Line Trainer (Adult - Old)', 'Adult central venous line insertion trainer', '0', '7' ),
( '9', 'Lumbar Puncture Trainer ( Adult)', 'Adult lumbar puncture and spinal tap trainer', '0', '7' ),
( '10', 'LP Training Set (5 parts)', 'Lumbar puncture training set with five components', '0', '7' ),
( '11', 'Eye Examination Simulator', 'Ophthalmology and eye examination simulator', '0', '7' ),
( '12', 'Ear Examination Simulator', 'Otoscopy and ear examination simulator', '0', '7' ),
( '13', 'Intradermal injection', 'Intradermal injection practice model', '0', '7' ),
( '14', 'Injection Trainer', 'General intramuscular and subcutaneous injection trainer', '0', '7' ),
( '15', 'Airway part (for intubation)', 'Upper airway parts for intubation practice', '0', '7' ),
( '16', 'Crico Task Trainer 1/2', 'Cricothyrotomy skills task trainer', '0', '7' ),
( '17', 'Airway Management Trainer', 'Airway management and intubation trainer', '0', '7' ),
( '18', 'Tube and Trach Care Trainer', 'Tube and tracheostomy care simulation trainer', '0', '7' ),
( '19', 'Clinical Female Pelvic Trainer', 'Female pelvic examination clinical trainer', '0', '7' ),
( '20', 'Strap-On Breasts', 'Breast examination strap on trainer', '0', '7' ),
( '21', 'Clinical Male Pelvic Trainer', 'Male pelvic examination clinical trainer', '0', '7' ),
( '22', 'Laerdal manikin faces extra with skin', 'Replacement faces with skin for Laerdal manikins', '0', '7' ),
( '23', 'Anatomy Model', 'General human anatomy demonstration model', '0', '7' ),
( '24', 'Little Anne Black', 'Little Anne CPR manikin, black version', '0', '7' ),
( '25', 'Little Anne White', 'Little Anne CPR manikin, white version', '0', '7' ),
( '26', 'Little Junior', 'Little Junior pediatric CPR manikin', '0', '7' ),
( '27', 'Rescue Baby with Indicator Lights', 'Infant CPR training manikin with feedback lights', '0', '7' ),
( '28', 'Code Blue Life Monitoring', 'Code Blue resuscitation and monitoring simulator', '0', '7' ),
( '29', 'Gen II Central Line U/S model + hand pump', 'Second generation ultrasound central line model with hand pump', '0', '7' ),
( '30', 'Baby', 'Generic infant training manikin', '0', '7' ),
( '32', 'Pediatric Central Line, D-Heart', 'Pediatric central line and heart access trainer', '0', '7' ),
( '33', 'Advanced Larry Manikin', 'Advanced airway management manikin Larry', '0', '7' ),
( '34', 'Resusci Anne', 'Adult Resusci Anne CPR training manikin', '0', '7' ),
( '35', 'Noelle', 'Obstetric and birthing simulator Noelle', '0', '7' ),
( '36', 'USP', 'Simulation item labeled USP', '0', '7' ),
( '37', 'Transtracheal airway catheter', 'Transtracheal airway access catheter for training', '0', '7' ),
( '38', 'Cricothyrtiomy Device', 'Cricothyrotomy training device', '0', '7' ),
( '39', 'Cricothyrtiomy Set', 'Complete cricothyrotomy procedure training set', '0', '7' ),
( '40', 'Blood blue tubes', 'Blue top blood collection tubes for skills practice', '0', '7' ),
( '41', 'Blood red tubes', 'Red top blood collection tubes for skills practice', '0', '7' ),
( '42', 'Blood purple tubes', 'Purple top blood collection tubes for skills practice', '0', '7' ),
( '43', 'Surgical marking pen', 'Skin marking pen for surgical simulation', '0', '7' ),
( '44', 'ETT introducer', 'Endotracheal tube introducer for airway training', '0', '7' ),
( '45', 'Blood Culture bottle', 'Blood culture collection bottle for simulation', '0', '7' ),
( '46', 'Central Line catheter 4 Fr', 'Four French central venous catheter for training', '0', '7' ),
( '47', 'Central Line catheter 5 Fr', 'Five French central venous catheter for training', '0', '7' ),
( '48', 'Central Line catheter 5.5 Fr', 'Five point five French central venous catheter', '0', '7' ),
( '49', 'Central Line catheter 8.5 Fr', 'Eight point five French central venous catheter', '0', '7' ),
( '50', 'Femoral Catheter 4 Fr', 'Four French femoral access catheter for training', '0', '7' ),
( '51', 'Femoral Catheter 5 Fr', 'Five French femoral access catheter for training', '0', '7' ),
( '52', 'Welch Alyn Thenometer wall mount', 'Wall mounted Welch Alyn thermometer for skills lab', '0', '7' ),
( '53', 'Temporal Scanner Thermometer', 'Temporal artery scanning thermometer', '0', '7' ),
( '54', 'Mercury Thermometer', 'Glass mercury thermometer for demonstration', '0', '7' ),
( '55', 'Digital Thermometer', 'Electronic digital thermometer', '0', '7' ),
( '56', 'Ear Thermometer', 'Tympanic ear thermometer device', '0', '7' ),
( '57', 'Glucometer Accu-chek active', 'Accu Chek Active blood glucose meter', '0', '7' ),
( '58', 'Glucometer Accu-Chek performa', 'Accu Chek Performa blood glucose meter', '0', '7' ),
( '59', 'Portable automatic BP cuff', 'Portable automatic blood pressure cuff unit', '0', '7' ),
( '60', 'Adult Cuff Large', 'Large size adult blood pressure cuff', '0', '7' ),
( '61', 'Adult Cuff Medium', 'Medium size adult blood pressure cuff', '0', '7' ),
( '62', 'Child Cuff n.9', 'Child size blood pressure cuff number 9', '0', '7' ),
( '63', 'Infant Cuff n.7', 'Infant size blood pressure cuff number 7', '0', '7' ),
( '64', 'Sphygmonometer Automatic', 'Automatic sphygmomanometer unit', '0', '7' ),
( '65', 'Broken AED', 'Automatic external defibrillator unit for demo only', '0', '7' ),
( '66', 'AED Practi-trainer', 'AED practice trainer device', '0', '7' ),
( '67', 'AED Pads Adult', 'Adult AED training pads', '0', '7' ),
( '68', 'AED Pads Peds', 'Pediatric AED training pads', '0', '7' ),
( '69', 'Traction kit + roll', 'Orthopedic traction training kit with roll', '0', '7' ),
( '70', 'Sand Back for traction 4.5 Kg', 'Traction sand bag weight 4.5 kg', '0', '7' ),
( '71', 'Sand Back for traction 0.5 Kg', 'Traction sand bag weight 0.5 kg', '0', '7' ),
( '72', 'Portable Suction Pump - Full set', 'Portable suction pump complete set', '0', '7' ),
( '73', 'Nursing Kid Vital Sim', 'Child nursing simulator with vital signs', '0', '7' ),
( '74', 'Nursing Baby Vital Sim', 'Infant nursing simulator with vital signs', '0', '7' ),
( '75', 'Girl with hair', 'Pediatric manikin girl with hair', '0', '7' ),
( '76', 'Code Blue III Pediatrics', 'Pediatric resuscitation simulator Code Blue III', '0', '7' ),
( '77', 'Apollo', 'Apollo high fidelity patient simulator', '0', '7' ),
( '78', 'Chest Trainer', 'Chest Trainer', '0', '7' ),
( '79', 'GioTest', 'GioTest', '1', '14' ),
( '80', 'Trainer', 'Trainer', '1', '7' ),
( '81', 'test', 'test', '0', '7' ),
( '82', 'test', 'test', '0', '7' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "STOCK" ------------------------------------
BEGIN;

INSERT INTO `STOCK`(`STOCKID`,`CREATED_BY`,`BRANCHID`,`UPDATED_BY`,`CONSID`,`AVAILABLEQ`,`RESERVEDQ`,`LASTCOUNTDATE`,`CREATED_ON`,`UPDATED_ON`) VALUES 
( '1', '1', '1', NULL, '1', '56', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '2', '1', '1', NULL, '2', '132', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '3', '1', '1', '1', '3', '79', '0', '2025-12-15', '2025-12-02 00:00:00', '2025-12-15 17:49:41' ),
( '4', '1', '1', '2', '4', '15', '0', '2025-12-02', '2025-12-02 00:00:00', '2025-12-02 20:57:12' ),
( '5', '1', '1', NULL, '5', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '6', '1', '1', NULL, '6', '8', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '7', '1', '1', NULL, '7', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '8', '1', '1', NULL, '8', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '9', '1', '1', NULL, '9', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '10', '1', '1', NULL, '10', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '11', '1', '1', NULL, '11', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '12', '1', '1', NULL, '12', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '13', '1', '1', NULL, '13', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '14', '1', '1', NULL, '14', '16', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '15', '1', '1', NULL, '15', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '16', '1', '1', NULL, '16', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '17', '1', '1', NULL, '17', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '18', '1', '1', NULL, '18', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '19', '1', '1', NULL, '19', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '20', '1', '1', NULL, '20', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '21', '1', '1', NULL, '21', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '22', '1', '1', NULL, '22', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '23', '1', '1', NULL, '23', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '24', '1', '1', NULL, '24', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '25', '1', '1', NULL, '25', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '26', '1', '1', NULL, '26', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '27', '1', '1', NULL, '27', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '28', '1', '1', NULL, '28', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '29', '1', '1', NULL, '29', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '30', '1', '1', NULL, '30', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '31', '1', '1', NULL, '31', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '32', '1', '1', NULL, '32', '120', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '33', '1', '1', NULL, '33', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '34', '1', '1', NULL, '34', '27', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '35', '1', '1', NULL, '35', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '36', '1', '1', NULL, '36', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '37', '1', '1', NULL, '37', '41', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '38', '1', '1', NULL, '38', '58', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '39', '1', '1', NULL, '39', '35', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '40', '1', '1', NULL, '40', '28', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '41', '1', '1', NULL, '41', '0', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '42', '1', '1', NULL, '42', '0', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '43', '1', '1', NULL, '43', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '44', '1', '1', NULL, '44', '51', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '45', '1', '1', NULL, '45', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '46', '1', '1', NULL, '46', '24', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '47', '1', '1', NULL, '47', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '48', '1', '1', NULL, '48', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '49', '1', '1', NULL, '49', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '50', '1', '1', NULL, '50', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '51', '1', '1', NULL, '51', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '52', '1', '1', NULL, '52', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '53', '1', '1', NULL, '53', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '54', '1', '1', NULL, '54', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '55', '1', '1', NULL, '55', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '56', '1', '1', NULL, '56', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '57', '1', '1', NULL, '57', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '58', '1', '1', NULL, '58', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '59', '1', '1', NULL, '59', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '60', '1', '1', NULL, '60', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '61', '1', '1', NULL, '61', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '62', '1', '1', NULL, '62', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '63', '1', '1', NULL, '63', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '64', '1', '1', NULL, '64', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '65', '1', '1', NULL, '65', '100', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '66', '1', '1', NULL, '66', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '67', '1', '1', NULL, '67', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '68', '1', '1', NULL, '68', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '69', '1', '1', NULL, '69', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '70', '1', '1', NULL, '70', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '71', '1', '1', NULL, '71', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '72', '1', '1', NULL, '72', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '73', '1', '1', NULL, '73', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '74', '1', '1', NULL, '74', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '75', '1', '1', NULL, '75', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '76', '1', '1', NULL, '76', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '77', '1', '1', NULL, '77', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '78', '1', '1', NULL, '78', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '79', '1', '1', NULL, '79', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '80', '1', '1', NULL, '80', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '81', '1', '1', NULL, '81', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '82', '1', '1', NULL, '82', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '83', '1', '1', NULL, '83', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '84', '1', '1', NULL, '84', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '85', '1', '1', NULL, '85', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '86', '1', '1', NULL, '86', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '87', '1', '1', NULL, '87', '100', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '88', '1', '1', NULL, '88', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '89', '1', '1', NULL, '89', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '90', '1', '1', NULL, '90', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '91', '1', '1', NULL, '91', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '92', '1', '1', NULL, '92', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '93', '1', '1', NULL, '93', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '94', '1', '1', NULL, '94', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '95', '1', '1', NULL, '95', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '96', '1', '1', NULL, '96', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '97', '1', '1', NULL, '97', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '98', '1', '1', NULL, '98', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '99', '1', '1', NULL, '99', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '100', '1', '1', NULL, '100', '8', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '101', '1', '1', NULL, '101', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '102', '1', '1', NULL, '102', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '103', '1', '1', NULL, '103', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '104', '1', '1', NULL, '104', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '105', '1', '1', NULL, '105', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '106', '1', '1', NULL, '106', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '107', '1', '1', NULL, '107', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '108', '1', '1', NULL, '108', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '109', '1', '1', NULL, '109', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '110', '1', '1', NULL, '110', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '111', '1', '1', NULL, '111', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '112', '1', '1', NULL, '112', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '113', '1', '1', NULL, '113', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '114', '1', '1', NULL, '114', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '115', '1', '1', NULL, '115', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '116', '1', '1', NULL, '116', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '117', '1', '1', NULL, '117', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '118', '1', '1', NULL, '118', '24', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '119', '1', '1', NULL, '119', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '120', '1', '1', NULL, '120', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '121', '1', '1', NULL, '121', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '122', '1', '1', NULL, '122', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '123', '1', '1', NULL, '123', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '124', '1', '1', NULL, '124', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '125', '1', '1', NULL, '125', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '126', '1', '1', NULL, '126', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '127', '1', '1', NULL, '127', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '128', '1', '1', NULL, '128', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '129', '1', '1', NULL, '129', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '130', '1', '1', NULL, '130', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '131', '1', '1', NULL, '131', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '132', '1', '1', NULL, '132', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '133', '1', '1', NULL, '133', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '134', '1', '1', NULL, '134', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '135', '1', '1', NULL, '135', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '136', '1', '1', NULL, '136', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '137', '1', '1', NULL, '137', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '138', '1', '1', NULL, '138', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '139', '1', '1', NULL, '139', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '140', '1', '1', NULL, '140', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '141', '1', '1', NULL, '141', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '142', '1', '1', NULL, '142', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '143', '1', '1', NULL, '143', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '144', '1', '1', NULL, '144', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '145', '1', '1', NULL, '145', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '146', '1', '1', NULL, '146', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '147', '1', '1', NULL, '147', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '148', '1', '1', NULL, '148', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '149', '1', '1', NULL, '149', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '150', '1', '1', NULL, '150', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '151', '1', '1', NULL, '151', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '152', '1', '1', '2', '152', '100', '0', '2025-12-02', '2025-12-02 00:00:00', '2025-12-02 20:58:14' ),
( '153', '1', '1', NULL, '153', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '154', '1', '1', NULL, '154', '48', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '155', '1', '1', NULL, '155', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '156', '1', '1', NULL, '156', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '157', '1', '1', NULL, '157', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '158', '1', '1', NULL, '158', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '159', '1', '1', NULL, '159', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '160', '1', '1', NULL, '160', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '161', '1', '1', NULL, '161', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '162', '1', '1', NULL, '162', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '163', '1', '1', NULL, '163', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '164', '1', '1', NULL, '164', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '165', '1', '1', NULL, '165', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '166', '1', '1', NULL, '166', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '167', '1', '1', NULL, '167', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '168', '1', '1', NULL, '168', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '169', '1', '2', NULL, '1', '56', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '170', '1', '2', NULL, '2', '132', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '171', '1', '2', '5', '3', '81', '30', '2025-12-19', '2025-12-02 00:00:00', '2025-12-19 11:26:50' ),
( '172', '1', '2', '3', '4', '2', '1', '2025-12-02', '2025-12-02 00:00:00', '2025-12-15 18:48:40' ),
( '173', '1', '2', NULL, '5', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '174', '1', '2', NULL, '6', '8', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '175', '1', '2', NULL, '7', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '176', '1', '2', NULL, '8', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '177', '1', '2', NULL, '9', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '178', '1', '2', NULL, '10', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '179', '1', '2', NULL, '11', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '180', '1', '2', NULL, '12', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '181', '1', '2', NULL, '13', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '182', '1', '2', NULL, '14', '16', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '183', '1', '2', NULL, '15', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '184', '1', '2', NULL, '16', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '185', '1', '2', '3', '17', '4', '2', '2025-12-02', '2025-12-02 00:00:00', '2025-12-15 19:06:40' ),
( '186', '1', '2', '3', '18', '0', '6', '2025-12-02', '2025-12-02 00:00:00', '2025-12-15 19:07:24' ),
( '187', '1', '2', NULL, '19', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '188', '1', '2', NULL, '20', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '189', '1', '2', NULL, '21', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '190', '1', '2', NULL, '22', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '191', '1', '2', NULL, '23', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '192', '1', '2', NULL, '24', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '193', '1', '2', NULL, '25', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '194', '1', '2', NULL, '26', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '195', '1', '2', NULL, '27', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '196', '1', '2', NULL, '28', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '197', '1', '2', NULL, '29', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '198', '1', '2', NULL, '30', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '199', '1', '2', NULL, '31', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '200', '1', '2', NULL, '32', '120', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '201', '1', '2', NULL, '33', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '202', '1', '2', NULL, '34', '27', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '203', '1', '2', NULL, '35', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '204', '1', '2', NULL, '36', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '205', '1', '2', NULL, '37', '41', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '206', '1', '2', NULL, '38', '58', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '207', '1', '2', NULL, '39', '35', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '208', '1', '2', NULL, '40', '28', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '209', '1', '2', NULL, '41', '0', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '210', '1', '2', NULL, '42', '0', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '211', '1', '2', NULL, '43', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '212', '1', '2', NULL, '44', '51', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '213', '1', '2', NULL, '45', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '214', '1', '2', NULL, '46', '24', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '215', '1', '2', NULL, '47', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '216', '1', '2', NULL, '48', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '217', '1', '2', NULL, '49', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '218', '1', '2', NULL, '50', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '219', '1', '2', NULL, '51', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '220', '1', '2', NULL, '52', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '221', '1', '2', NULL, '53', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '222', '1', '2', NULL, '54', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '223', '1', '2', NULL, '55', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '224', '1', '2', NULL, '56', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '225', '1', '2', NULL, '57', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '226', '1', '2', NULL, '58', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '227', '1', '2', NULL, '59', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '228', '1', '2', NULL, '60', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '229', '1', '2', NULL, '61', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '230', '1', '2', NULL, '62', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '231', '1', '2', NULL, '63', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '232', '1', '2', NULL, '64', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '233', '1', '2', NULL, '65', '100', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '234', '1', '2', NULL, '66', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '235', '1', '2', NULL, '67', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '236', '1', '2', NULL, '68', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '237', '1', '2', NULL, '69', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '238', '1', '2', NULL, '70', '23', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '239', '1', '2', NULL, '71', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '240', '1', '2', NULL, '72', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '241', '1', '2', NULL, '73', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '242', '1', '2', NULL, '74', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '243', '1', '2', NULL, '75', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '244', '1', '2', NULL, '76', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '245', '1', '2', NULL, '77', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '246', '1', '2', NULL, '78', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '247', '1', '2', NULL, '79', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '248', '1', '2', NULL, '80', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '249', '1', '2', NULL, '81', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '250', '1', '2', NULL, '82', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL );

INSERT INTO `STOCK`(`STOCKID`,`CREATED_BY`,`BRANCHID`,`UPDATED_BY`,`CONSID`,`AVAILABLEQ`,`RESERVEDQ`,`LASTCOUNTDATE`,`CREATED_ON`,`UPDATED_ON`) VALUES 
( '251', '1', '2', NULL, '83', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '252', '1', '2', NULL, '84', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '253', '1', '2', NULL, '85', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '254', '1', '2', NULL, '86', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '255', '1', '2', NULL, '87', '100', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '256', '1', '2', NULL, '88', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '257', '1', '2', NULL, '89', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '258', '1', '2', NULL, '90', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '259', '1', '2', NULL, '91', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '260', '1', '2', NULL, '92', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '261', '1', '2', NULL, '93', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '262', '1', '2', NULL, '94', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '263', '1', '2', NULL, '95', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '264', '1', '2', NULL, '96', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '265', '1', '2', NULL, '97', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '266', '1', '2', NULL, '98', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '267', '1', '2', NULL, '99', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '268', '1', '2', NULL, '100', '8', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '269', '1', '2', NULL, '101', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '270', '1', '2', NULL, '102', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '271', '1', '2', NULL, '103', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '272', '1', '2', NULL, '104', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '273', '1', '2', NULL, '105', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '274', '1', '2', NULL, '106', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '275', '1', '2', NULL, '107', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '276', '1', '2', NULL, '108', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '277', '1', '2', NULL, '109', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '278', '1', '2', NULL, '110', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '279', '1', '2', NULL, '111', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '280', '1', '2', NULL, '112', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '281', '1', '2', NULL, '113', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '282', '1', '2', NULL, '114', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '283', '1', '2', NULL, '115', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '284', '1', '2', NULL, '116', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '285', '1', '2', NULL, '117', '200', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '286', '1', '2', NULL, '118', '24', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '287', '1', '2', NULL, '119', '12', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '288', '1', '2', NULL, '120', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '289', '1', '2', NULL, '121', '50', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '290', '1', '2', NULL, '122', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '291', '1', '2', NULL, '123', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '292', '1', '2', NULL, '124', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '293', '1', '2', NULL, '125', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '294', '1', '2', NULL, '126', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '295', '1', '2', NULL, '127', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '296', '1', '2', NULL, '128', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '297', '1', '2', NULL, '129', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '298', '1', '2', NULL, '130', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '299', '1', '2', NULL, '131', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '300', '1', '2', NULL, '132', '11', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '301', '1', '2', NULL, '133', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '302', '1', '2', NULL, '134', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '303', '1', '2', NULL, '135', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '304', '1', '2', NULL, '136', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '305', '1', '2', NULL, '137', '9', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '306', '1', '2', NULL, '138', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '307', '1', '2', NULL, '139', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '308', '1', '2', NULL, '140', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '309', '1', '2', NULL, '141', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '310', '1', '2', NULL, '142', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '311', '1', '2', NULL, '143', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '312', '1', '2', NULL, '144', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '313', '1', '2', NULL, '145', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '314', '1', '2', NULL, '146', '20', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '315', '1', '2', NULL, '147', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '316', '1', '2', NULL, '148', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '317', '1', '2', NULL, '149', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '318', '1', '2', NULL, '150', '15', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '319', '1', '2', NULL, '151', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '320', '1', '2', NULL, '152', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '321', '1', '2', NULL, '153', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '322', '1', '2', NULL, '154', '48', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '323', '1', '2', NULL, '155', '10', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '324', '1', '2', NULL, '156', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '325', '1', '2', NULL, '157', '7', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '326', '1', '2', NULL, '158', '3', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '327', '1', '2', NULL, '159', '1', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '328', '1', '2', NULL, '160', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '329', '1', '2', NULL, '161', '60', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '330', '1', '2', NULL, '162', '4', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '331', '1', '2', '3', '163', '3', '0', '2025-12-02', '2025-12-02 00:00:00', '2025-12-19 09:09:19' ),
( '332', '1', '2', NULL, '164', '2', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '333', '1', '2', NULL, '165', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '334', '1', '2', NULL, '166', '5', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '335', '1', '2', NULL, '167', NULL, '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '336', '1', '2', NULL, '168', '6', '0', '2025-12-02', '2025-12-02 00:00:00', NULL ),
( '337', '2', '1', '2', '3', '12', '0', '2025-12-02', '2025-12-02 20:45:37', '2025-12-02 20:45:37' ),
( '338', '2', '1', '2', '169', '24938', '1256', '2025-12-02', '2025-12-02 20:57:48', '2025-12-02 20:58:01' );
COMMIT;
-- ---------------------------------------------------------


-- Dump data of "USERS" ------------------------------------
BEGIN;

INSERT INTO `USERS`(`USERID`,`USERNAME`,`DISPLAYNAME`,`ROLE`,`PASSHASH`,`IS_ACTIVE`,`USERMAIL`,`BRANCHID`,`RESET`) VALUES 
( '1', 'admin', 'Administrator', 'ADMIN', '$2a$12$QvC5UlHl.acXgEptf9rYiu8OiPRMl6YCoRnqgoBLokmppIjljMbJS', '1', 'simlab.admin@lau.edu.lb', NULL, '0' ),
( '2', 'beirut_user', 'Simulation Beirut Staff', 'STAFF', '$2a$12$as22/u9SRhHnO5iuIXJXae4cfLxpCiNC5MFYUVAAd/y4W6k3wyOWi', '0', 'simlab.beirut@lau.edu.lb', '1', '0' ),
( '3', 'byblos_user', 'Simulation Byblos Staff', 'STAFF', '$2a$12$Yfahit1x/79z2an5QprifO4LYfL.B5ZfwT9IEWdVNBl.EWzn6b.tS', '1', 'simlab.byblos@lau.edu.lb', '2', '0' ),
( '4', 'byblos_user_2', 'Byblos_test_2', 'STAFF', '$2a$12$k4WoGwnpiw.W2UNjXi1FoeoTit8mA/vU6REFPbLMEV77FAtiKzjrW', '1', 'simlab2.byblos@lau.edu.lb', '2', '0' ),
( '5', 'giouser', 'Giovan Edde', 'STAFF', '$2a$12$BZMPoMQgCZPnojXfGSRDCO7cAlSjBMZqhhbX8/MUFx10ENH0qm3dq', '1', 'gio@lau.edu.lb', '2', '0' );
COMMIT;
-- ---------------------------------------------------------


-- CREATE INDEX "FK_CREATED_BY2" -------------------------------
CREATE INDEX `FK_CREATED_BY2` USING BTREE ON `BORROWING`( `CREATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_ISSUED_BY" ---------------------------------
CREATE INDEX `FK_ISSUED_BY` USING BTREE ON `BORROWING`( `BRANCHID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_LEASED_TO" ---------------------------------
CREATE INDEX `FK_LEASED_TO` USING BTREE ON `BORROWING`( `DEPTID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_UPDATED_BY2" -------------------------------
CREATE INDEX `FK_UPDATED_BY2` USING BTREE ON `BORROWING`( `UPDATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_BORROW_CONS2" ------------------------------
CREATE INDEX `FK_BORROW_CONS2` USING BTREE ON `BORROW_CONS`( `STOCKID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_BORROW_SIM2" -------------------------------
CREATE INDEX `FK_BORROW_SIM2` USING BTREE ON `BORROW_SIM`( `BORROWID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_MAINTAINED2" -------------------------------
CREATE INDEX `FK_MAINTAINED2` USING BTREE ON `MAINTAINED`( `EVENTID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_CREATED_BY4" -------------------------------
CREATE INDEX `FK_CREATED_BY4` USING BTREE ON `MAINTENANCE`( `CREATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_UPDATED_BY4" -------------------------------
CREATE INDEX `FK_UPDATED_BY4` USING BTREE ON `MAINTENANCE`( `UPDATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_CREATED_BY3" -------------------------------
CREATE INDEX `FK_CREATED_BY3` USING BTREE ON `SIMULATOR`( `CREATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_OWNS_SIM" ----------------------------------
CREATE INDEX `FK_OWNS_SIM` USING BTREE ON `SIMULATOR`( `BRANCHID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_SIM_IN" ------------------------------------
CREATE INDEX `FK_SIM_IN` USING BTREE ON `SIMULATOR`( `MODELID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_UPDATED_BY3" -------------------------------
CREATE INDEX `FK_UPDATED_BY3` USING BTREE ON `SIMULATOR`( `UPDATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_BRANCH_CONS" -------------------------------
CREATE INDEX `FK_BRANCH_CONS` USING BTREE ON `STOCK`( `CONSID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_BRANCH_CONS2" ------------------------------
CREATE INDEX `FK_BRANCH_CONS2` USING BTREE ON `STOCK`( `BRANCHID` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_STOCK_CREATED_BY" --------------------------
CREATE INDEX `FK_STOCK_CREATED_BY` USING BTREE ON `STOCK`( `CREATED_BY` );
-- -------------------------------------------------------------


-- CREATE INDEX "FK_STOCK_UPDATED_BY" --------------------------
CREATE INDEX `FK_STOCK_UPDATED_BY` USING BTREE ON `STOCK`( `UPDATED_BY` );
-- -------------------------------------------------------------


-- CREATE LINK "FK_BORROW_CONS" --------------------------------
ALTER TABLE `BORROW_CONS`
	ADD CONSTRAINT `FK_BORROW_CONS` FOREIGN KEY ( `BORROWID` )
	REFERENCES `BORROWING`( `BORROWID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_BORROW_CONS2" -------------------------------
ALTER TABLE `BORROW_CONS`
	ADD CONSTRAINT `FK_BORROW_CONS2` FOREIGN KEY ( `STOCKID` )
	REFERENCES `STOCK`( `STOCKID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_BORROW_SIM" ---------------------------------
ALTER TABLE `BORROW_SIM`
	ADD CONSTRAINT `FK_BORROW_SIM` FOREIGN KEY ( `SIMID` )
	REFERENCES `SIMULATOR`( `SIMID` )
	ON DELETE No Action
	ON UPDATE No Action;
-- -------------------------------------------------------------


-- CREATE LINK "FK_BORROW_SIM2" --------------------------------
ALTER TABLE `BORROW_SIM`
	ADD CONSTRAINT `FK_BORROW_SIM2` FOREIGN KEY ( `BORROWID` )
	REFERENCES `BORROWING`( `BORROWID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_BRANCH_CONS" --------------------------------
ALTER TABLE `STOCK`
	ADD CONSTRAINT `FK_BRANCH_CONS` FOREIGN KEY ( `CONSID` )
	REFERENCES `CONSUMABLE`( `CONSID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_BRANCH_CONS2" -------------------------------
ALTER TABLE `STOCK`
	ADD CONSTRAINT `FK_BRANCH_CONS2` FOREIGN KEY ( `BRANCHID` )
	REFERENCES `BRANCH`( `BRANCHID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_CREATED_BY2" --------------------------------
ALTER TABLE `BORROWING`
	ADD CONSTRAINT `FK_CREATED_BY2` FOREIGN KEY ( `CREATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_CREATED_BY3" --------------------------------
ALTER TABLE `SIMULATOR`
	ADD CONSTRAINT `FK_CREATED_BY3` FOREIGN KEY ( `CREATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_CREATED_BY4" --------------------------------
ALTER TABLE `MAINTENANCE`
	ADD CONSTRAINT `FK_CREATED_BY4` FOREIGN KEY ( `CREATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_ISSUED_BY" ----------------------------------
ALTER TABLE `BORROWING`
	ADD CONSTRAINT `FK_ISSUED_BY` FOREIGN KEY ( `BRANCHID` )
	REFERENCES `BRANCH`( `BRANCHID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_LEASED_TO" ----------------------------------
ALTER TABLE `BORROWING`
	ADD CONSTRAINT `FK_LEASED_TO` FOREIGN KEY ( `DEPTID` )
	REFERENCES `DEPARTMENT`( `DEPTID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_MAINTAINED2" --------------------------------
ALTER TABLE `MAINTAINED`
	ADD CONSTRAINT `FK_MAINTAINED2` FOREIGN KEY ( `EVENTID` )
	REFERENCES `MAINTENANCE`( `EVENTID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_MAINTAINED_SIM" -----------------------------
ALTER TABLE `MAINTAINED`
	ADD CONSTRAINT `FK_MAINTAINED_SIM` FOREIGN KEY ( `SIMID` )
	REFERENCES `SIMULATOR`( `SIMID` )
	ON DELETE No Action
	ON UPDATE No Action;
-- -------------------------------------------------------------


-- CREATE LINK "FK_OWNS_SIM" -----------------------------------
ALTER TABLE `SIMULATOR`
	ADD CONSTRAINT `FK_OWNS_SIM` FOREIGN KEY ( `BRANCHID` )
	REFERENCES `BRANCH`( `BRANCHID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_SIM_IN" -------------------------------------
ALTER TABLE `SIMULATOR`
	ADD CONSTRAINT `FK_SIM_IN` FOREIGN KEY ( `MODELID` )
	REFERENCES `SIMULATOR_MODEL`( `MODELID` )
	ON DELETE No Action
	ON UPDATE No Action;
-- -------------------------------------------------------------


-- CREATE LINK "FK_STOCK_CREATED_BY" ---------------------------
ALTER TABLE `STOCK`
	ADD CONSTRAINT `FK_STOCK_CREATED_BY` FOREIGN KEY ( `CREATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_STOCK_UPDATED_BY" ---------------------------
ALTER TABLE `STOCK`
	ADD CONSTRAINT `FK_STOCK_UPDATED_BY` FOREIGN KEY ( `UPDATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_UPDATED_BY2" --------------------------------
ALTER TABLE `BORROWING`
	ADD CONSTRAINT `FK_UPDATED_BY2` FOREIGN KEY ( `UPDATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_UPDATED_BY3" --------------------------------
ALTER TABLE `SIMULATOR`
	ADD CONSTRAINT `FK_UPDATED_BY3` FOREIGN KEY ( `UPDATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


-- CREATE LINK "FK_UPDATED_BY4" --------------------------------
ALTER TABLE `MAINTENANCE`
	ADD CONSTRAINT `FK_UPDATED_BY4` FOREIGN KEY ( `UPDATED_BY` )
	REFERENCES `USERS`( `USERID` )
	ON DELETE Restrict
	ON UPDATE Restrict;
-- -------------------------------------------------------------


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
-- ---------------------------------------------------------


