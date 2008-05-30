create table trv_doc_2 (
        FDOC_NBR                       VARCHAR2(14) CONSTRAINT FP_INT_BILL_DOC_TN1 NOT NULL,
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT FP_INT_BILL_DOC_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT FP_INT_BILL_DOC_TN3 NOT NULL,
        FDOC_EXPLAIN_TXT               VARCHAR2(400),
        request_trav varchar2(30) not null,
        traveler          varchar2(200),
        org          varchar2(60),
        dest         varchar2(60),
        CONSTRAINT trv_doc_2P1 PRIMARY KEY (FDOC_NBR)
)
/

create table trv_acct (
    acct_num  varchar2(10) not null,
    acct_name varchar2(50),
    acct_type varchar2(100),
    acct_fo_id number(14),
    constraint trv_acct_pk primary key(acct_num)
)
/

create table trv_doc_acct (
    doc_hdr_id  number(14) not null,
    acct_num    varchar2(10) not null,
    constraint trv_doc_acct_pk primary key(doc_hdr_id, acct_num)
)
/

create table trv_acct_fo (
    acct_fo_id  number(14) not null,
    acct_fo_user_name varchar2(50) not null,
    constraint trv_acct_fo_id_pk primary key(acct_fo_id)
)
/

create table TRAV_DOC_2_ACCOUNTS (
    FDOC_NBR VARCHAR2(14),
    ACCT_NUM varchar2(10),
    CONSTRAINT TRAV_DOC_2_ACCOUNTS_P1 PRIMARY KEY (FDOC_NBR, ACCT_NUM)
)
/

create table TRV_ACCT_TYPE (
    ACCT_TYPE VARCHAR2(10),
    ACCT_TYPE_NAME varchar2(50),
    CONSTRAINT TRV_ACCT_TYPE_PK PRIMARY KEY (ACCT_TYPE)
)
/

create table TRV_ACCT_EXT (
    ACCT_NUM VARCHAR2(10),
    ACCT_TYPE varchar2(100),
    CONSTRAINT TRV_ACCT_TYPE_P1 PRIMARY KEY (ACCT_NUM, ACCT_TYPE)
)
/

CREATE SEQUENCE SEQ_TRAVEL_DOC_ID INCREMENT BY 1 START WITH 1000
/
CREATE SEQUENCE SEQ_TRAVEL_FO_ID INCREMENT BY 1 START WITH 1000
/

alter table trv_acct add constraint trv_acct_fk1 foreign key(acct_fo_id) references trv_acct_fo(acct_fo_id)
/

insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (1, 'fred', 0)
/
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (2, 'fran', 0)
/
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (3, 'frank', 0)
/

insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a1', 'a1', 1, 0)
/
insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a2', 'a2', 2, 0)
/
insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a3', 'a3', 3, 0)
/

insert into en_usr_t (PRSN_EN_ID, PRSN_UNIV_ID, PRSN_NTWRK_ID, PRSN_UNVL_USR_ID, PRSN_EMAIL_ADDR, PRSN_NM, PRSN_GVN_NM, PRSN_LST_NM, USR_CRTE_DT, USR_LST_UPDT_DT, DB_LOCK_VER_NBR) values ('quickstart','quickstart','quickstart','quickstart','quickstart@localhost','quickstart','quickstart','quickstart',to_date('01/01/2000', 'dd/mm/yyyy'),to_date('01/01/2100', 'dd/mm/yyyy'),0)
/
insert into EN_WRKGRP_MBR_T (WRKGRP_MBR_PRSN_EN_ID, WRKGRP_ID, WRKGRP_MBR_TYP, WRKGRP_VER_NBR, DB_LOCK_VER_NBR) values ('quickstart', 1, 'U', 1, 0)
/

INSERT INTO FP_DOC_GROUP_T (FDOC_GRP_CD, OBJ_ID, VER_NBR, FDOC_GRP_NM, FDOC_CLASS_CD) VALUES ('TR', '054EDFB3B260C8D2E043814FD881C8D2', 1,  'Travel Documents', null)
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('TRAV', '1A6FEB2501C7607EE043814FD881607E', 1, 'TR', 'TRAV ACCNT', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('TRFO', '1A6FEB250342607EE043814FD881607E', 1, 'TR', 'TRAV FO', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('TRD2', '1A6FEB250342607EE043814FD889607E', 1, 'TR', 'TRAV D2', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('RUSR', '1A6FEB253342607EE043814FD889607E', 1, 'TR', 'RICE USR', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('PARM', '1A6FRB253342607EE043814FD889607E', 1, 'TR', 'System Parms', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('BR', '1A6FRB253343337EE043814FD889607E', 1, 'TR', 'Biz Rules', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('TRVA', '1A5FEB250342607EE043814FD889607E', 1, 'TR',  'TRAV MAINT', 'N', 'Y', 'N', 0, 'N', 'N')
/
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a1', 'IAT')
/
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a2', 'EAT')
/
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a3', 'IAT')
/
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('CAT', 'Clearing Account Type')
/
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('EAT', 'Expense Account Type')
/
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('IAT', ' Income Account Type')
/
-- KEN sample data --

-- NOTIFICATION_PRODUCERS --
INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(2, 'University Library System', 'This producer represents messages sent from the University Library system.', 'kuali-ken-testing@cornell.edu')
/

INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(3, 'University Events Office', 'This producer represents messages sent from the University Events system.', 'kuali-ken-testing@cornell.edu')
/

-- NOTIFICATION_CHANNELS --
DELETE FROM NOTIFICATION_CHANNELS WHERE NAME != 'KEW'
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(1, 'Kuali Rice Channel', 'This channel is used for sending out information about the Kuali Rice effort.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(2, 'Library Events Channel', 'This channel is used for sending out information about Library Events.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(3, 'Overdue Library Books', 'This channel is used for sending out information about your overdue books.', 'N')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(4, 'Concerts Coming to Campus', 'This channel broadcasts any concerts coming to campus.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(5, 'University Alerts', 'This channel broadcasts general announcements for the university.', 'N')
/

-- NOTIFICATION_CHANNEL_SUBSCRIPTIONS

INSERT INTO USER_CHANNEL_SUBSCRIPTIONS
(ID, CHANNEL_ID, USER_ID)
VALUES
(1, 1, 'TestUser4')
/

-- NOTIFICATION_RECIPIENTS_LISTS

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(1, 4, 'USER', 'TestUser1')
/

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(2, 4, 'USER', 'TestUser3')
/

-- NOTIFICATION_CHANNEL_REVIEWERS

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(1, 1, 'GROUP', 'RiceTeam')
/

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(2, 5, 'USER', 'TestUser3')
/

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(3, 5, 'GROUP', 'TestGroup1')
/

-- NOTIFICATION_CHANNEL_PRODUCERS --
DELETE FROM NOTIFICATION_CHANNEL_PRODUCERS
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(1, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(2, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(3, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(4, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(5, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(2, 2)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(3, 2)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(4, 3)
/

-- Sample data that KCB contributes to the sample app --
-- just add some deliverer configurations

insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (1, 'TestUser6', 'KEW', 'mock', 0)
/
insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (2, 'TestUser1', 'KEW', 'mock', 0)
/
insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (3, 'TestUser2', 'KEW', 'mock', 0)
/
insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (4, 'quickstart', 'KEW', 'mock', 0)
/
insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (5, 'TestUser5', 'KEW', 'mock', 0)
/
insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (6, 'TestUser4', 'KEW', 'mock', 0)
/

commit
/
