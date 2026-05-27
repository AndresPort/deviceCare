-- =========================================================
-- Base de datos: Sistema de gestión de reparación de dispositivos electrónicos
-- Motor: PostgreSQL
-- Versión: 2.0
-- Cambios respecto a v1:
--   - Eliminado campo final_cost de repair_orders
--   - Eliminado módulo de notificaciones (se replanteará con soporte WhatsApp)
--   - Agregado ENUM repair_order_file_stage para categorizar archivos
--   - Agregado campo stage y description en repair_order_files
--   - Agregado campo notes en purchase_orders
--   - Agregado soft delete (deleted_at) en payments, repair_order_parts,
--     repair_order_accessories y repair_order_files
--   - Agregados índices adicionales para búsquedas frecuentes
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================================
-- MÓDULO: ENUMS / TIPOS DE DATOS
-- =========================================================

CREATE TYPE repair_status AS ENUM (
    'RECEIVED',
    'IN_DIAGNOSIS',
    'WAITING_APPROVAL',
    'WAITING_PARTS',
    'IN_REPAIR',
    'REPAIRED',
    'DELIVERED',
    'CANCELLED'
);

CREATE TYPE repair_priority AS ENUM (
    'LOW',
    'NORMAL',
    'HIGH',
    'URGENT'
);

CREATE TYPE damage_level AS ENUM (
    'MINOR',
    'MODERATE',
    'SEVERE',
    'CRITICAL'
);

CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'PARTIAL',
    'PAID'
);

CREATE TYPE payment_method_type AS ENUM (
    'CASH',
    'TRANSFER',
    'CARD',
    'CREDIT',
    'MIXED',
    'QR'
);

CREATE TYPE inventory_movement_type AS ENUM (
    'PURCHASE',
    'REPAIR_USAGE',
    'RETURN',
    'ADJUSTMENT',
    'INITIAL_LOAD',
    'DAMAGED'
);

CREATE TYPE inventory_reference_type AS ENUM (
    'PURCHASE_ORDER',
    'REPAIR_ORDER',
    'MANUAL'
);

CREATE TYPE part_source_type AS ENUM (
    'INVENTORY',
    'EXTERNAL_PURCHASE',
    'CLIENT_PROVIDED',
    'CUSTOM'
);

CREATE TYPE purchase_order_status AS ENUM (
    'PENDING',
    'APPROVED',
    'ORDERED',
    'PARTIALLY_RECEIVED',
    'RECEIVED',
    'CANCELLED'
);

-- NUEVO: Etapas para categorizar archivos adjuntos de órdenes de reparación
CREATE TYPE repair_order_file_stage AS ENUM (
    'RECEPTION',
    'DIAGNOSIS',
    'REPAIR',
    'DELIVERY'
);

-- =========================================================
-- MÓDULO: FUNCIONES AUXILIARES
-- =========================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================================
-- MÓDULO: USUARIOS Y PERMISOS
-- =========================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,

    phone VARCHAR(30),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_users_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_users_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_roles_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_roles_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);

-- =========================================================
-- MÓDULO: CLIENTES
-- =========================================================

CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID UNIQUE NULL,
    document_number VARCHAR(50) UNIQUE,
    address TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_clients_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_clients_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_clients_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_clients_user_id ON clients(user_id);
CREATE INDEX idx_clients_document_number ON clients(document_number);

-- =========================================================
-- MÓDULO: CATÁLOGO DE DISPOSITIVOS
-- =========================================================

CREATE TABLE device_brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_device_brands_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_device_brands_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE device_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_device_types_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_device_types_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    client_id UUID NOT NULL,
    brand_id UUID NULL,
    type_id UUID NULL,

    model VARCHAR(150) NOT NULL,
    serial_number VARCHAR(150) UNIQUE,
    color VARCHAR(50),
    physical_condition TEXT,
    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_devices_client
        FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT,

    CONSTRAINT fk_devices_brand
        FOREIGN KEY (brand_id) REFERENCES device_brands(id) ON DELETE RESTRICT,

    CONSTRAINT fk_devices_type
        FOREIGN KEY (type_id) REFERENCES device_types(id) ON DELETE RESTRICT,

    CONSTRAINT fk_devices_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_devices_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_devices_client_id ON devices(client_id);
CREATE INDEX idx_devices_brand_id ON devices(brand_id);
CREATE INDEX idx_devices_type_id ON devices(type_id);
CREATE INDEX idx_devices_serial_number ON devices(serial_number);

-- =========================================================
-- MÓDULO: REPARACIONES
-- =========================================================

-- CAMBIO: Se eliminó el campo final_cost.
-- El costo final se calcula como:
--   labor_cost + SUM(repair_order_parts.quantity * repair_order_parts.unit_price)
-- Se puede exponer mediante una vista o función en el backend.
CREATE TABLE repair_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_number BIGINT GENERATED BY DEFAULT AS IDENTITY UNIQUE,

    device_id UUID NOT NULL,
    received_by UUID NULL,
    assigned_technician UUID NULL,

    status repair_status NOT NULL DEFAULT 'RECEIVED',
    priority repair_priority NOT NULL DEFAULT 'NORMAL',
    damage_level damage_level NULL,

    client_problem TEXT NOT NULL,
    technical_diagnosis TEXT,

    client_approved BOOLEAN NOT NULL DEFAULT FALSE,
    client_approved_at TIMESTAMPTZ NULL,
    client_approval_notes TEXT,

    estimated_cost NUMERIC(12,2) NULL,
    labor_cost NUMERIC(12,2) NULL DEFAULT 0,
    -- final_cost eliminado: calcular como labor_cost + SUM(partes)

    estimated_delivery_date DATE NULL,
    delivered_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_repair_orders_device
        FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE RESTRICT,

    CONSTRAINT fk_repair_orders_received_by
        FOREIGN KEY (received_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_repair_orders_assigned_technician
        FOREIGN KEY (assigned_technician) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_repair_orders_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_repair_orders_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_repair_orders_estimated_cost
        CHECK (estimated_cost IS NULL OR estimated_cost >= 0),

    CONSTRAINT chk_repair_orders_labor_cost
        CHECK (labor_cost IS NULL OR labor_cost >= 0)
);

CREATE TABLE repair_order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    repair_order_id UUID NOT NULL,

    old_status repair_status NULL,
    new_status repair_status NOT NULL,

    changed_by UUID NULL,
    notes TEXT,

    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_repair_order_status_history_order
        FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_repair_order_status_history_changed_by
        FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- CAMBIO: Agregados stage, description y deleted_at para mejor trazabilidad y soft delete
CREATE TABLE repair_order_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    repair_order_id UUID NOT NULL,
    file_url TEXT NOT NULL,

    -- NUEVO: clasifica en qué etapa de la reparación se tomó la evidencia
    stage repair_order_file_stage NULL,
    description TEXT NULL,

    uploaded_by UUID NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- NUEVO: soft delete para archivos eliminados
    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_repair_order_files_order
        FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_repair_order_files_uploaded_by
        FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- CAMBIO: Agregado deleted_at para soft delete
CREATE TABLE repair_order_accessories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    repair_order_id UUID NOT NULL,

    accessory_name VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    is_received BOOLEAN NOT NULL DEFAULT TRUE,
    condition_notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID NULL,

    -- NUEVO: soft delete
    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_repair_order_accessories_order
        FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_repair_order_accessories_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_repair_order_accessories_quantity
        CHECK (quantity > 0)
);

CREATE INDEX idx_repair_orders_device_id ON repair_orders(device_id);
CREATE INDEX idx_repair_orders_received_by ON repair_orders(received_by);
CREATE INDEX idx_repair_orders_assigned_technician ON repair_orders(assigned_technician);
CREATE INDEX idx_repair_orders_status ON repair_orders(status);
CREATE INDEX idx_repair_orders_priority ON repair_orders(priority);
-- NUEVO: índice por fecha de creación para dashboards y reportes
CREATE INDEX idx_repair_orders_created_at ON repair_orders(created_at);
CREATE INDEX idx_repair_order_status_history_order_id ON repair_order_status_history(repair_order_id);
CREATE INDEX idx_repair_order_files_order_id ON repair_order_files(repair_order_id);
-- NUEVO: índice por etapa para filtrar evidencia fotográfica
CREATE INDEX idx_repair_order_files_stage ON repair_order_files(stage);
CREATE INDEX idx_repair_order_accessories_order_id ON repair_order_accessories(repair_order_id);

-- =========================================================
-- MÓDULO: INVENTARIO
-- =========================================================

CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    stock INTEGER NOT NULL DEFAULT 0,
    minimum_stock INTEGER NOT NULL DEFAULT 0,

    unit_cost NUMERIC(12,2) NOT NULL DEFAULT 0,
    sale_price NUMERIC(12,2) NOT NULL DEFAULT 0,

    brand VARCHAR(100),
    category VARCHAR(100),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_inventory_items_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_inventory_items_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_inventory_items_stock
        CHECK (stock >= 0),

    CONSTRAINT chk_inventory_items_minimum_stock
        CHECK (minimum_stock >= 0),

    CONSTRAINT chk_inventory_items_unit_cost
        CHECK (unit_cost >= 0),

    CONSTRAINT chk_inventory_items_sale_price
        CHECK (sale_price >= 0)
);

CREATE TABLE inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    inventory_item_id UUID NOT NULL,

    movement_type inventory_movement_type NOT NULL,
    quantity INTEGER NOT NULL,
    previous_stock INTEGER NOT NULL,
    new_stock INTEGER NOT NULL,

    reference_type inventory_reference_type NOT NULL,
    reference_id UUID NULL,

    notes TEXT,

    created_by UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_inventory_movements_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE RESTRICT,

    CONSTRAINT fk_inventory_movements_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_inventory_movements_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_inventory_movements_previous_stock
        CHECK (previous_stock >= 0),

    CONSTRAINT chk_inventory_movements_new_stock
        CHECK (new_stock >= 0)
);

-- CAMBIO: Agregado deleted_at para soft delete (anulación de partes)
CREATE TABLE repair_order_parts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    repair_order_id UUID NOT NULL,
    inventory_item_id UUID NULL,

    custom_part_name VARCHAR(255) NULL,
    source_type part_source_type NOT NULL,

    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,

    notes TEXT,

    created_by UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- NUEVO: soft delete para anular una parte sin perder historial
    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_repair_order_parts_order
        FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_repair_order_parts_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE RESTRICT,

    CONSTRAINT fk_repair_order_parts_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_repair_order_parts_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_repair_order_parts_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_repair_order_parts_source
        CHECK (
            (
                source_type = 'INVENTORY'
                AND inventory_item_id IS NOT NULL
                AND custom_part_name IS NULL
            )
            OR
            (
                source_type IN ('EXTERNAL_PURCHASE', 'CLIENT_PROVIDED', 'CUSTOM')
                AND inventory_item_id IS NULL
                AND custom_part_name IS NOT NULL
            )
        )
);

CREATE INDEX idx_inventory_items_sku ON inventory_items(sku);
CREATE INDEX idx_inventory_items_name ON inventory_items(name);
CREATE INDEX idx_inventory_items_is_active ON inventory_items(is_active);
CREATE INDEX idx_inventory_movements_item_id ON inventory_movements(inventory_item_id);
CREATE INDEX idx_inventory_movements_reference_type ON inventory_movements(reference_type);
CREATE INDEX idx_inventory_movements_reference_id ON inventory_movements(reference_id);
CREATE INDEX idx_repair_order_parts_order_id ON repair_order_parts(repair_order_id);
CREATE INDEX idx_repair_order_parts_item_id ON repair_order_parts(inventory_item_id);
CREATE INDEX idx_repair_order_parts_source_type ON repair_order_parts(source_type);

-- =========================================================
-- MÓDULO: COMPRAS Y PROVEEDORES
-- =========================================================

CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(150) UNIQUE,
    address TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_by UUID NULL,
    updated_by UUID NULL,

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_suppliers_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_suppliers_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- CAMBIO: Agregado campo notes para observaciones de la orden de compra
CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    supplier_id UUID NOT NULL,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    status purchase_order_status NOT NULL DEFAULT 'PENDING',

    -- NUEVO: notas para condiciones de entrega, negociaciones, etc.
    notes TEXT NULL,

    approved_at TIMESTAMPTZ NULL,
    ordered_at TIMESTAMPTZ NULL,
    received_at TIMESTAMPTZ NULL,

    created_by UUID NULL,
    updated_by UUID NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_purchase_orders_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT,

    CONSTRAINT fk_purchase_orders_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_purchase_orders_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_purchase_orders_total_amount
        CHECK (total_amount >= 0)
);

CREATE TABLE purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_order_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,

    quantity INTEGER NOT NULL,
    received_quantity INTEGER NOT NULL DEFAULT 0,
    unit_cost NUMERIC(12,2) NOT NULL,

    CONSTRAINT fk_purchase_order_items_order
        FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_purchase_order_items_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE RESTRICT,

    CONSTRAINT chk_purchase_order_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_purchase_order_items_received_quantity
        CHECK (received_quantity >= 0 AND received_quantity <= quantity),

    CONSTRAINT chk_purchase_order_items_unit_cost
        CHECK (unit_cost >= 0)
);

CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_purchase_order_items_order_id ON purchase_order_items(purchase_order_id);
CREATE INDEX idx_purchase_order_items_item_id ON purchase_order_items(inventory_item_id);

-- =========================================================
-- MÓDULO: PAGOS
-- =========================================================

-- CAMBIO: Agregado deleted_at para soft delete (anulación de pagos)
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    repair_order_id UUID NOT NULL,
    amount NUMERIC(12,2) NOT NULL,

    payment_method payment_method_type NOT NULL,
    payment_status payment_status NOT NULL DEFAULT 'PENDING',

    transaction_reference VARCHAR(150),
    paid_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- NUEVO: soft delete para anular un pago sin perder el registro contable
    deleted_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_payments_repair_order
        FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id) ON DELETE CASCADE,

    CONSTRAINT chk_payments_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_payments_repair_order_id ON payments(repair_order_id);
CREATE INDEX idx_payments_payment_status ON payments(payment_status);
CREATE INDEX idx_payments_payment_method ON payments(payment_method);
-- NUEVO: índice por fecha de pago para reportes financieros
CREATE INDEX idx_payments_paid_at ON payments(paid_at);

-- =========================================================
-- MÓDULO: LOGS Y AUDITORÍA
-- =========================================================

-- NOTA: El módulo de notificaciones fue eliminado en esta versión.
-- Se replanteará en una iteración futura con soporte para WhatsApp
-- y otros canales de comunicación.

CREATE TABLE system_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NULL,
    action VARCHAR(255) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID NULL,

    old_data JSONB NULL,
    new_data JSONB NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_system_logs_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_entity_name ON system_logs(entity_name);
CREATE INDEX idx_system_logs_entity_id ON system_logs(entity_id);
CREATE INDEX idx_system_logs_created_at ON system_logs(created_at);

-- =========================================================
-- TRIGGERS PARA updated_at
-- =========================================================

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_roles_updated_at
BEFORE UPDATE ON roles
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_clients_updated_at
BEFORE UPDATE ON clients
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_device_brands_updated_at
BEFORE UPDATE ON device_brands
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_device_types_updated_at
BEFORE UPDATE ON device_types
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_devices_updated_at
BEFORE UPDATE ON devices
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_repair_orders_updated_at
BEFORE UPDATE ON repair_orders
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_inventory_items_updated_at
BEFORE UPDATE ON inventory_items
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_suppliers_updated_at
BEFORE UPDATE ON suppliers
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_purchase_orders_updated_at
BEFORE UPDATE ON purchase_orders
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- =========================================================
-- VISTA: Costo total calculado por orden de reparación
-- Reemplaza el campo final_cost eliminado de repair_orders.
-- Uso: SELECT * FROM v_repair_order_costs WHERE repair_order_id = '...';
-- =========================================================

CREATE VIEW v_repair_order_costs AS
SELECT
    ro.id                                           AS repair_order_id,
    ro.order_number,
    ro.labor_cost,
    COALESCE(SUM(rop.quantity * rop.unit_price), 0) AS parts_cost,
    ro.labor_cost + COALESCE(SUM(rop.quantity * rop.unit_price), 0) AS total_cost
FROM repair_orders ro
LEFT JOIN repair_order_parts rop
       ON rop.repair_order_id = ro.id
      AND rop.deleted_at IS NULL
GROUP BY ro.id, ro.order_number, ro.labor_cost;

-- =========================================================
-- SEED INICIAL
-- =========================================================

INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Administrador del sistema'),
    ('TECHNICIAN', 'Técnico reparador'),
    ('RECEPTION', 'Personal de recepción'),
    ('CLIENT', 'Cliente del sistema')
ON CONFLICT (name) DO NOTHING;

-- =========================================================
-- FIN DEL SCRIPT
-- =========================================================