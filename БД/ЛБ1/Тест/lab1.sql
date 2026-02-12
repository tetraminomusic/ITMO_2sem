

CREATE TYPE PRIORITY AS ENUM('Low', 'Medium', 'High');
CREATE TYPE WEATHER AS ENUM('Rainy', 'Windy', 'Foggy', 'Sunny', 'Snowy', 'Cloudy', 'Stormy');


CREATE TABLE IF NOT EXISTS AIRPLANE (
-- PK
                        ID_Airplane SMALLSERIAL,
                        Serial_Number CHAR(5),
                        Max_Volume INTEGER,
                        Inventor_Name VARCHAR(20),
                        Citizen_Number SMALLINT
);

CREATE TABLE IF NOT EXISTS MEETING (
-- PK
                        ID_Meeting SMALLSERIAL,
                        Time_Meeting TIMESTAMP,
                        Discussion_Subject VARCHAR(15),
                        Participants_Count SMALLINT,
-- FK
                        Taking_Place VARCHAR(15),

                        FOREIGN KEY (Taking_Place) REFERENCES BASE (Name_Base)
);

CREATE TABLE IF NOT EXISTS BASE (
-- PK
                        ID_Base SMALLSERIAL,
                        Name_Base VARCHAR(15),
                        Has_Runaway BOOLEAN,
                        Importance_Level PRIORITY
);

CREATE TABLE IF NOT EXISTS HUMAN (
-- PK
                        ID_Person SMALLSERIAL,
                        Human_Name VARCHAR(30),
                        Post VARCHAR(20),
                        ID_Licence CHAR(5),
                        Licence_Period DATE,
                        Is_License_Has_Period BOOLEAN
);

CREATE TABLE IF NOT EXISTS FLIGHT (
-- PK
                        TYPE_OF_RISK PRIORITY,
                        ID_Flight SMALLINT,
                        Arrival_Time TIMESTAMP,
                        Departure_Time TIMESTAMP,
-- FK
                        Arrival_Place VARCHAR(15),
                        Departure_Place VARCHAR(15),
                        ID_Pilot SMALLSERIAL,
                        ID_Airplane SMALLSERIAL,

                        FOREIGN KEY (Arrival_Time) REFERENCES BASE(Name_Base),
                        FOREIGN KEY (Departure_Time) REFERENCES BASE(Name_Base),
                        FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Airplane) REFERENCES AIRPLANE(ID_Airplane)
);

CREATE TABLE IF NOT EXISTS FLIGHT_HISTORY (
-- FK
                        ID_Meteorologist SMALLSERIAL,
                        ID_Pilot SMALLSERIAL,
                        ID_Flight SMALLSERIAL,

                        FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Pilot) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
);

CREATE TABLE IF NOT EXISTS METEOROLOGIST_DATA (
-- PK
                        Air_Pressure SMALLINT,
                        Weather WEATHER,
                        Speed_Of_Wind SMALLINT,
-- FK
                        ID_Meteorologist SMALLSERIAL,
                        ID_Flight SMALLSERIAL,

                        FOREIGN KEY (ID_Meteorologist) REFERENCES HUMAN(ID_Person),
                        FOREIGN KEY (ID_Flight) REFERENCES FLIGHT(ID_Flight)
);