drop table if exists BORROWING;

drop table if exists BORROW_CONS;

drop table if exists BORROW_SIM;

drop table if exists BRANCH;

drop table if exists CONSUMABLE;

drop table if exists DEPARTMENT;

drop table if exists MAINTAINED;

drop table if exists MAINTENANCE;

drop table if exists SIMULATOR;

drop table if exists SIMULATOR_MODEL;

drop table if exists STOCK;

drop table if exists USERS;

create table BORROWING
(
   BORROWID             Int not null,
   UPDATED_BY           int,
   BRANCHID             int not null,
   CREATED_BY           int not null,
   DEPTID               int not null,
   BORROWCODE           varchar(60),
   STARTDATE            date,
   ENDDATE              date,
   NOTES                varchar(1024),
   BORROWSTATUS         varchar(60),
   CREATED_ON           timestamp,
   UPDATED_ON           timestamp,
   primary key (BORROWID)
);

create table BORROW_CONS
(
   BORROWID             int not null,
   STOCKID              int not null,
   QUANTITY             int,
   primary key (BORROWID, STOCKID)
);

create table BORROW_SIM
(
   SIMID                int not null,
   BORROWID             int not null,
   CONDOUT              varchar(60),
   CONDIN               varchar(60),
   RETURNNOTES          varchar(1024),
   primary key (SIMID, BORROWID)
);

create table BRANCH
(
   BRANCHID             int not null,
   NAME                 varchar(120) not null,
   LOCATION             varchar(200),
   primary key (BRANCHID)
);

create table CONSUMABLE
(
   CONSID               int not null,
   ITEMNAME             varchar(60),
   MEASURE              varchar(6),
   primary key (CONSID)
);

create table DEPARTMENT
(
   DEPTID               int not null,
   DEPTNAME             varchar(60),
   CONTACTNAME          varchar(60),
   PHONE1               varchar(12),
   PHONE2               varchar(12),
   EMAIL                varchar(120),
   primary key (DEPTID)
);

create table MAINTAINED
(
   SIMID                int not null,
   EVENTID              int not null,
   primary key (SIMID, EVENTID)
);

create table MAINTENANCE
(
   EVENTID              int not null,
   UPDATED_BY           int,
   CREATED_BY           int not null,
   TYPE                 varchar(60),
   EVENTSTARTDATE       date,
   EVENTENDDATE         date,
   EVENTNOTES           varchar(1024),
   VENDOR               varchar(60),
   CREATED_ON           timestamp,
   UPDATED_ON           timestamp,
   primary key (EVENTID)
);

create table SIMULATOR
(
   SIMID                int not null,
   CREATED_BY           int not null,
   UPDATED_BY           int,
   BRANCHID             int not null,
   MODELID              int not null,
   TAG                  varchar(60) not null,
   SN                   varchar(60),
   SIMSTATUS            varchar(60),
   CONDNOTES            varchar(1024),
   CALDATE              date,
   NEXTCALDATE          date,
   CREATED_ON           timestamp,
   UPDATED_ON           timestamp,
   primary key (SIMID)
);

create table SIMULATOR_MODEL
(
   MODELID              int not null,
   MODELNAME            varchar(50),
   SPECS                varchar(1024),
   CALREQ               bool,
   MAXDAYS              int,
   primary key (MODELID)
);

create table STOCK
(
   STOCKID              int not null,
   CREATED_BY           int not null,
   BRANCHID             int not null,
   UPDATED_BY           int,
   CONSID               int not null,
   AVAILABLEQ           int,
   RESERVEDQ            int,
   LASTCOUNTDATE        date,
   CREATED_ON           timestamp,
   UPDATED_ON           timestamp,
   primary key (STOCKID)
);

CREATE TABLE USERS (
    USERID int NOT NULL,
    USERNAME varchar(20) DEFAULT NULL,
    DISPLAYNAME varchar(80) DEFAULT NULL,
    ROLE varchar(20) DEFAULT NULL,
    PASSHASH varchar(150) DEFAULT NULL,
    IS_ACTIVE tinyint(1) DEFAULT NULL,
    USERMAIL varchar(120) DEFAULT NULL,
    BRANCHID int DEFAULT NULL,
    RESET tinyint(1) NOT NULL,
    PRIMARY KEY (`USERID`)
);

alter table BORROWING add constraint FK_CREATED_BY2 foreign key (CREATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table BORROWING add constraint FK_ISSUED_BY foreign key (BRANCHID)
      references BRANCH (BRANCHID) on delete restrict on update restrict;

alter table BORROWING add constraint FK_LEASED_TO foreign key (DEPTID)
      references DEPARTMENT (DEPTID) on delete restrict on update restrict;

alter table BORROWING add constraint FK_UPDATED_BY2 foreign key (UPDATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table BORROW_CONS add constraint FK_BORROW_CONS foreign key (BORROWID)
      references BORROWING (BORROWID) on delete restrict on update restrict;

alter table BORROW_CONS add constraint FK_BORROW_CONS2 foreign key (STOCKID)
      references STOCK (STOCKID) on delete restrict on update restrict;

alter table BORROW_SIM add constraint FK_BORROW_SIM foreign key (SIMID)
      references SIMULATOR (SIMID) on delete restrict on update restrict;

alter table BORROW_SIM add constraint FK_BORROW_SIM2 foreign key (BORROWID)
      references BORROWING (BORROWID) on delete restrict on update restrict;

alter table MAINTAINED add constraint FK_MAINTAINED foreign key (SIMID)
      references SIMULATOR (SIMID) on delete restrict on update restrict;

alter table MAINTAINED add constraint FK_MAINTAINED2 foreign key (EVENTID)
      references MAINTENANCE (EVENTID) on delete restrict on update restrict;

alter table MAINTENANCE add constraint FK_CREATED_BY4 foreign key (CREATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table MAINTENANCE add constraint FK_UPDATED_BY4 foreign key (UPDATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table SIMULATOR add constraint FK_CREATED_BY3 foreign key (CREATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table SIMULATOR add constraint FK_OWNS_SIM foreign key (BRANCHID)
      references BRANCH (BRANCHID) on delete restrict on update restrict;

alter table SIMULATOR add constraint FK_SIM_IN foreign key (MODELID)
      references SIMULATOR_MODEL (MODELID) on delete restrict on update restrict;

alter table SIMULATOR add constraint FK_UPDATED_BY3 foreign key (UPDATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table STOCK add constraint FK_BRANCH_CONS foreign key (CONSID)
      references CONSUMABLE (CONSID) on delete restrict on update restrict;

alter table STOCK add constraint FK_BRANCH_CONS2 foreign key (BRANCHID)
      references BRANCH (BRANCHID) on delete restrict on update restrict;

alter table STOCK add constraint FK_STOCK_CREATED_BY foreign key (CREATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table STOCK add constraint FK_STOCK_UPDATED_BY foreign key (UPDATED_BY)
      references USERS (USERID) on delete restrict on update restrict;

alter table STOCK add constraint BRANCH_USER foreign key (BRANCHID)
      references BRANCH (BRANCHID) on delete restrict on update restrict;



COMMIT;
