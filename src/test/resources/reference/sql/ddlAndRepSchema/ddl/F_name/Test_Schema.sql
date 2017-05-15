CREATE TABLE T1_CLASS2 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P SDO_GEOMETRY NOT NULL,
   PMIXIN VARCHAR2(4000) NOT NULL
);

CREATE TABLE T1_CLASS2_P1 (

   T1_CLASS2TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   T1_CLASS4TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   PRIMARY KEY (T1_CLASS2TESTOBJECTIDENTIFIER, T1_CLASS4TESTOBJECTIDENTIFIER)
);

CREATE TABLE T1_CLASS3 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   PMIXIN VARCHAR2(4000) NOT NULL,
   P SDO_GEOMETRY NOT NULL
);

CREATE TABLE T1_CLASS3_P1 (

   T1_CLASS3TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   T1_CLASS4TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   PRIMARY KEY (T1_CLASS3TESTOBJECTIDENTIFIER, T1_CLASS4TESTOBJECTIDENTIFIER)
);

CREATE TABLE T1_CLASS4 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY
);

CREATE TABLE T2_CLASS1 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1 VARCHAR2(10) NOT NULL,
   P2 VARCHAR2(20) NOT NULL,
   P3_NOSIZETV VARCHAR2(4000) NOT NULL,
   P4_SIZE0 CLOB NOT NULL
);

CREATE TABLE T3_CLASS1 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1 SDO_GEOMETRY NOT NULL,
   P2 VARCHAR2(4000) NOT NULL,
   P3_FK CLOB NOT NULL,
   P4 VARCHAR2(4000) NOT NULL
);

CREATE TABLE T3_CODELIST (

   NAME CLOB NOT NULL PRIMARY KEY,
   DOCUMENTATION CLOB
);

CREATE TABLE T4_CLASS1 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1 INTEGER NOT NULL,
   P2 TIMESTAMP
);

CREATE TABLE T5_CLASS1 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1 NUMBER NOT NULL
);

CREATE TABLE T6_CLASS1 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1_FK INTEGER NOT NULL
);

CREATE TABLE T6_CLASS1_P2 (

   T6_CLASS1TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   P2 VARCHAR2(4000) NOT NULL,
   PRIMARY KEY (T6_CLASS1TESTOBJECTIDENTIFIER, P2)
);

CREATE TABLE T6_CLASS1_P3 (

   T6_CLASS1TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   T6_CLASS2TESTOBJECTIDENTIFIER INTEGER NOT NULL,
   PRIMARY KEY (T6_CLASS1TESTOBJECTIDENTIFIER, T6_CLASS2TESTOBJECTIDENTIFIER)
);

CREATE TABLE T6_CLASS2 (

   TESTOBJECTIDENTIFIER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 ORDER NOCACHE) PRIMARY KEY,
   P1_C1_P1 NUMBER,
   P1_C2_P1 INTEGER
);


ALTER TABLE T1_CLASS2_P1 ADD CONSTRAINT FK_T1_CLASS_T1_CLASS_T1_CLASS FOREIGN KEY (T1_CLASS2TESTOBJECTIDENTIFIER) REFERENCES T1_CLASS2;
ALTER TABLE T1_CLASS2_P1 ADD CONSTRAINT FK_T1_CLASS_T1_CLASS_T1_CLASS0 FOREIGN KEY (T1_CLASS4TESTOBJECTIDENTIFIER) REFERENCES T1_CLASS4;
ALTER TABLE T1_CLASS3_P1 ADD CONSTRAINT FK_T1_CLASS_T1_CLASS_T1_CLASS1 FOREIGN KEY (T1_CLASS3TESTOBJECTIDENTIFIER) REFERENCES T1_CLASS3;
ALTER TABLE T1_CLASS3_P1 ADD CONSTRAINT FK_T1_CLASS_T1_CLASS_T1_CLASS2 FOREIGN KEY (T1_CLASS4TESTOBJECTIDENTIFIER) REFERENCES T1_CLASS4;
ALTER TABLE T3_CLASS1 ADD CONSTRAINT FK_T3_CLASS_T3_CODEL_P3 FOREIGN KEY (P3_FK) REFERENCES T3_CODELIST;
ALTER TABLE T3_CLASS1 ADD CONSTRAINT T3_CLASS1_P2_CK CHECK (P2 IN ('e1', 'e2', 'e3'));
ALTER TABLE T3_CLASS1 ADD CONSTRAINT T3_CLASS1_P4_CK CHECK (P4 IN ('A', 'B'));
ALTER TABLE T6_CLASS1 ADD CONSTRAINT FK_T6_CLASS_T6_CLASS_P1 FOREIGN KEY (P1_FK) REFERENCES T6_CLASS2;
ALTER TABLE T6_CLASS1_P2 ADD CONSTRAINT FK_T6_CLASS_T6_CLASS_T6_CLASS FOREIGN KEY (T6_CLASS1TESTOBJECTIDENTIFIER) REFERENCES T6_CLASS1;
ALTER TABLE T6_CLASS1_P3 ADD CONSTRAINT FK_T6_CLASS_T6_CLASS_T6_CLASS0 FOREIGN KEY (T6_CLASS1TESTOBJECTIDENTIFIER) REFERENCES T6_CLASS1;
ALTER TABLE T6_CLASS1_P3 ADD CONSTRAINT FK_T6_CLASS_T6_CLASS_T6_CLASS1 FOREIGN KEY (T6_CLASS2TESTOBJECTIDENTIFIER) REFERENCES T6_CLASS2;

CREATE INDEX IDX_T1_CLASS2_P ON T1_CLASS2 (P) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS('layer_gtype=collection');
CREATE INDEX IDX_T1_CLASS3_P ON T1_CLASS3 (P) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS('layer_gtype=point');
CREATE INDEX IDX_T3_CLASS1_P1 ON T3_CLASS1 (P1) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS('layer_gtype=point');

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID) VALUES ('T1_CLASS2', 'P', MDSYS.SDO_DIM_ARRAY(FIXME), 31467);
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID) VALUES ('T1_CLASS3', 'P', MDSYS.SDO_DIM_ARRAY(FIXME), 31467);
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID) VALUES ('T3_CLASS1', 'P1', MDSYS.SDO_DIM_ARRAY(FIXME), 31467);
