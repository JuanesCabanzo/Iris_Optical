
DROP DATABASE IF EXISTS sistema_iris_v2;

CREATE DATABASE sistema_iris_v2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_iris_v2;



--  TABLAS


-- PACIENTE 
CREATE TABLE Paciente (
    ID_Paciente      INT AUTO_INCREMENT PRIMARY KEY,
    Nombre1          VARCHAR(100) NOT NULL,
    Nombre2          VARCHAR(100),
    Apellido1        VARCHAR(100) NOT NULL,
    Apellido2        VARCHAR(100),
    Cedula           VARCHAR(30)  NOT NULL UNIQUE,
    Sexo             ENUM('M','F','Otro') NOT NULL,
    Fecha_Nacimiento DATE,
    Edad             INT,
    Telefono         VARCHAR(30),
    Correo           VARCHAR(150) UNIQUE
) ENGINE=InnoDB;

-- ESPECIALISTA 
CREATE TABLE Especialista (
    ID_Especialista INT AUTO_INCREMENT PRIMARY KEY,
    Nombre1         VARCHAR(100) NOT NULL,
    Nombre2         VARCHAR(100),
    Apellido1       VARCHAR(100) NOT NULL,
    Apellido2       VARCHAR(100),
    Cedula          VARCHAR(30)  NOT NULL UNIQUE,
    Especialidad    VARCHAR(100),
    Telefono        VARCHAR(30),
    Correo          VARCHAR(150) UNIQUE,
    Estado          ENUM('Activo','Inactivo') DEFAULT 'Activo'
) ENGINE=InnoDB;

-- CITA 
CREATE TABLE Cita (
    ID_Cita         INT AUTO_INCREMENT PRIMARY KEY,
    ID_Paciente     INT NOT NULL,
    ID_Especialista INT NOT NULL,
    Fecha           DATE NOT NULL,
    Hora            TIME NOT NULL,
    Motivo          VARCHAR(255),
    Estado          ENUM('Programada','Confirmada','Cancelada','Finalizada')
                    DEFAULT 'Programada',

    CONSTRAINT FK_Cita_Paciente
        FOREIGN KEY (ID_Paciente) REFERENCES Paciente(ID_Paciente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT FK_Cita_Especialista
        FOREIGN KEY (ID_Especialista) REFERENCES Especialista(ID_Especialista)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- EXAMEN VISUAL 
CREATE TABLE Examen_Visual (
    ID_Examen       INT AUTO_INCREMENT PRIMARY KEY,
    ID_Paciente     INT NOT NULL,
    ID_Especialista INT NOT NULL,
    Fecha_Examen    DATETIME DEFAULT CURRENT_TIMESTAMP,
    Tipo_Examen     VARCHAR(100),
    Observaciones   TEXT,
    Estado          ENUM('Pendiente','Realizado','Cancelado') DEFAULT 'Pendiente',

    CONSTRAINT FK_Examen_Paciente
        FOREIGN KEY (ID_Paciente) REFERENCES Paciente(ID_Paciente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT FK_Examen_Especialista
        FOREIGN KEY (ID_Especialista) REFERENCES Especialista(ID_Especialista)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- FORMULA OPTICA 
-- OD = ojo derecho, OI = ojo izquierdo
CREATE TABLE Formula_Optica (
    ID_Formula        INT AUTO_INCREMENT PRIMARY KEY,
    ID_Examen         INT NOT NULL,
    Fecha_Emision     DATETIME DEFAULT CURRENT_TIMESTAMP,
    OD_Esfera         DECIMAL(5,2),
    OD_Cilindro       DECIMAL(5,2),
    OD_Eje            SMALLINT,
    OI_Esfera         DECIMAL(5,2),
    OI_Cilindro       DECIMAL(5,2),
    OI_Eje            SMALLINT,
    Adicion           DECIMAL(4,2),
    Distancia_Pupilar DECIMAL(4,1),
    Observaciones     TEXT,

    CONSTRAINT CK_Formula_OD_Eje CHECK (OD_Eje BETWEEN 0 AND 180),
    CONSTRAINT CK_Formula_OI_Eje CHECK (OI_Eje BETWEEN 0 AND 180),
    CONSTRAINT FK_Formula_Examen
        FOREIGN KEY (ID_Examen) REFERENCES Examen_Visual(ID_Examen)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- DIAGNOSTICO 
CREATE TABLE Diagnostico (
    ID_Diagnostico    INT AUTO_INCREMENT PRIMARY KEY,
    ID_Examen         INT NOT NULL,
    Fecha_Diagnostico DATETIME DEFAULT CURRENT_TIMESTAMP,
    Resultado         TEXT,
    Recomendaciones   TEXT,
    Observaciones     TEXT,

    CONSTRAINT FK_Diagnostico_Examen
        FOREIGN KEY (ID_Examen) REFERENCES Examen_Visual(ID_Examen)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- HISTORIA CLINICA 
CREATE TABLE Historia_Clinica (
    ID_Historia    INT AUTO_INCREMENT PRIMARY KEY,
    ID_Paciente    INT NOT NULL UNIQUE,
    Fecha_Creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    Antecedentes   TEXT,
    Observaciones  TEXT,

    CONSTRAINT FK_Historia_Paciente
        FOREIGN KEY (ID_Paciente) REFERENCES Paciente(ID_Paciente)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- AUDITORIA 
CREATE TABLE Auditoria (
    ID_Auditoria    INT AUTO_INCREMENT PRIMARY KEY,
    Tabla_Afectada  VARCHAR(100) NOT NULL,
    ID_Registro     INT,
    Operacion       ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    Fecha_Operacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    Usuario         VARCHAR(100),
    Detalle         TEXT
) ENGINE=InnoDB;




DELIMITER $$

-- Paciente
CREATE TRIGGER trg_paciente_insert
AFTER INSERT ON Paciente
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Paciente', NEW.ID_Paciente, 'INSERT', CURRENT_USER(), 'Paciente creado');
END$$

CREATE TRIGGER trg_paciente_update
AFTER UPDATE ON Paciente
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Paciente', NEW.ID_Paciente, 'UPDATE', CURRENT_USER(), 'Paciente actualizado');
END$$

CREATE TRIGGER trg_paciente_delete
AFTER DELETE ON Paciente
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Paciente', OLD.ID_Paciente, 'DELETE', CURRENT_USER(), 'Paciente eliminado');
END$$

-- Especialista
CREATE TRIGGER trg_especialista_insert
AFTER INSERT ON Especialista
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Especialista', NEW.ID_Especialista, 'INSERT', CURRENT_USER(), 'Especialista creado');
END$$

CREATE TRIGGER trg_especialista_update
AFTER UPDATE ON Especialista
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Especialista', NEW.ID_Especialista, 'UPDATE', CURRENT_USER(), 'Especialista actualizado');
END$$

CREATE TRIGGER trg_especialista_delete
AFTER DELETE ON Especialista
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Especialista', OLD.ID_Especialista, 'DELETE', CURRENT_USER(), 'Especialista eliminado');
END$$

-- Cita
CREATE TRIGGER trg_cita_insert
AFTER INSERT ON Cita
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Cita', NEW.ID_Cita, 'INSERT', CURRENT_USER(), 'Cita creado');
END$$

CREATE TRIGGER trg_cita_update
AFTER UPDATE ON Cita
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Cita', NEW.ID_Cita, 'UPDATE', CURRENT_USER(), 'Cita actualizado');
END$$

CREATE TRIGGER trg_cita_delete
AFTER DELETE ON Cita
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Cita', OLD.ID_Cita, 'DELETE', CURRENT_USER(), 'Cita eliminado');
END$$

-- Examen_Visual
CREATE TRIGGER trg_examen_visual_insert
AFTER INSERT ON Examen_Visual
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Examen_Visual', NEW.ID_Examen, 'INSERT', CURRENT_USER(), 'Examen visual creado');
END$$

CREATE TRIGGER trg_examen_visual_update
AFTER UPDATE ON Examen_Visual
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Examen_Visual', NEW.ID_Examen, 'UPDATE', CURRENT_USER(), 'Examen visual actualizado');
END$$

CREATE TRIGGER trg_examen_visual_delete
AFTER DELETE ON Examen_Visual
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Examen_Visual', OLD.ID_Examen, 'DELETE', CURRENT_USER(), 'Examen visual eliminado');
END$$

-- Formula_Optica
CREATE TRIGGER trg_formula_optica_insert
AFTER INSERT ON Formula_Optica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Formula_Optica', NEW.ID_Formula, 'INSERT', CURRENT_USER(), 'Formula optica creado');
END$$

CREATE TRIGGER trg_formula_optica_update
AFTER UPDATE ON Formula_Optica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Formula_Optica', NEW.ID_Formula, 'UPDATE', CURRENT_USER(), 'Formula optica actualizado');
END$$

CREATE TRIGGER trg_formula_optica_delete
AFTER DELETE ON Formula_Optica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Formula_Optica', OLD.ID_Formula, 'DELETE', CURRENT_USER(), 'Formula optica eliminado');
END$$

-- Diagnostico
CREATE TRIGGER trg_diagnostico_insert
AFTER INSERT ON Diagnostico
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Diagnostico', NEW.ID_Diagnostico, 'INSERT', CURRENT_USER(), 'Diagnostico creado');
END$$

CREATE TRIGGER trg_diagnostico_update
AFTER UPDATE ON Diagnostico
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Diagnostico', NEW.ID_Diagnostico, 'UPDATE', CURRENT_USER(), 'Diagnostico actualizado');
END$$

CREATE TRIGGER trg_diagnostico_delete
AFTER DELETE ON Diagnostico
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Diagnostico', OLD.ID_Diagnostico, 'DELETE', CURRENT_USER(), 'Diagnostico eliminado');
END$$

-- Historia_Clinica
CREATE TRIGGER trg_historia_clinica_insert
AFTER INSERT ON Historia_Clinica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Historia_Clinica', NEW.ID_Historia, 'INSERT', CURRENT_USER(), 'Historia clinica creado');
END$$

CREATE TRIGGER trg_historia_clinica_update
AFTER UPDATE ON Historia_Clinica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Historia_Clinica', NEW.ID_Historia, 'UPDATE', CURRENT_USER(), 'Historia clinica actualizado');
END$$

CREATE TRIGGER trg_historia_clinica_delete
AFTER DELETE ON Historia_Clinica
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria
        (Tabla_Afectada, ID_Registro, Operacion, Usuario, Detalle)
    VALUES
        ('Historia_Clinica', OLD.ID_Historia, 'DELETE', CURRENT_USER(), 'Historia clinica eliminado');
END$$



--  PROCEDIMIENTO ALMACENADO


CREATE PROCEDURE sp_buscar_paciente_por_cedula(IN p_cedula VARCHAR(30))
BEGIN
    SELECT ID_Paciente,
           CONCAT_WS(' ', Nombre1, Nombre2, Apellido1, Apellido2) AS Paciente,
           Cedula, Sexo, Fecha_Nacimiento, Edad, Telefono, Correo
    FROM Paciente
    WHERE Cedula = TRIM(p_cedula);
END$$

DELIMITER ;



--  VERIFICACION 

SHOW FULL TABLES;
SHOW TRIGGERS;
SHOW PROCEDURE STATUS WHERE Db = 'sistema_iris_v2';
