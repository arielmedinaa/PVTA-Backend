-- Tabla de Cuenta Corriente para gestionar cobros y pagos
CREATE TABLE cuenta_corriente (
    id BIGSERIAL PRIMARY KEY,
    entidad_id INTEGER NOT NULL,
    tipo_entidad VARCHAR(20) NOT NULL, -- CLIENTE o PROVEEDOR
    tipo_movimiento VARCHAR(10) NOT NULL, -- DEBE o HABER
    monto NUMERIC(18,2) NOT NULL,
    concepto TEXT,
    factura_id BIGINT,
    comprobante_id BIGINT,
    fecha TIMESTAMP NOT NULL DEFAULT NOW(),
    usuario_id INTEGER,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_cuenta_corriente_entidad ON cuenta_corriente(entidad_id, tipo_entidad);
CREATE INDEX idx_cuenta_corriente_fecha ON cuenta_corriente(fecha);
CREATE INDEX idx_cuenta_corriente_factura ON cuenta_corriente(factura_id);

-- Tabla de Configuración de Facturación para manejar numeración 001-001-secuencial
CREATE TABLE configuracion_facturacion (
    id BIGSERIAL PRIMARY KEY,
    establecimiento INTEGER NOT NULL, -- 001
    punto_expedicion INTEGER NOT NULL, -- 001
    ultimo_secuencial BIGINT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT unique_configuracion_activa UNIQUE (establecimiento, punto_expedicion)
);

-- Tabla de Proveedores (usando la tabla clienteproveedor existente con tipo = 'PROVEEDOR')
-- La tabla clienteproveedor ya existe, se usa con tipo = 'PROVEEDOR'

-- Tabla de Clientes (usando la tabla clienteproveedor existente con tipo = 'CLIENTE')
-- La tabla clienteproveedor ya existe, se usa con tipo = 'CLIENTE'

-- Agregar columna de stock mínimo a productos
ALTER TABLE productos ADD COLUMN stock_minimo INTEGER DEFAULT 0;

-- Agregar columna de stock máximo a productos
ALTER TABLE productos ADD COLUMN stock_maximo INTEGER DEFAULT 0;

-- Agregar columnas para gestionar múltiples puntos de expedición por caja
ALTER TABLE caja ADD COLUMN punto_expedicion INTEGER;
ALTER TABLE caja ADD COLUMN ultimo_secuencial BIGINT DEFAULT 0;

-- Agregar tabla de secuencia para control de numeración por caja
CREATE TABLE secuencia_factura_caja (
    id BIGSERIAL PRIMARY KEY,
    caja_id INTEGER NOT NULL REFERENCES caja(id),
    ultimo_secuencial BIGINT NOT NULL DEFAULT 0,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_caja_secuencia UNIQUE (caja_id)
);

-- Insertar configuración de facturación por defecto
INSERT INTO configuracion_facturacion (establecimiento, punto_expedicion, ultimo_secuencial, activo)
VALUES (1, 1, 0, true);