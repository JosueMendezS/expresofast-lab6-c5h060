Use ExpresoFastC5H060_II2026
ALTER TABLE Envio ADD fecha_despacho DATE NULL;
ALTER TABLE Envio ADD fecha_entrega_estimada DATE NULL;
GO

CREATE TABLE PAQUETES (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    envio_id INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    peso_kg DECIMAL(5,2) NOT NULL,
    CONSTRAINT FK_Paquetes_Envios FOREIGN KEY (envio_id) REFERENCES Envio(envio_id) ON DELETE CASCADE
);
GO


