-- Create schema
CREATE SCHEMA IF NOT EXISTS ship;

-- Fulfillment and shipment status
CREATE TABLE ship.status (
	id SMALLINT PRIMARY KEY,
	status VARCHAR(50) NOT NULL
);

-- Initialize data
INSERT INTO ship.status (id, status) VALUES
	(0, 'READY_TO_SHIP'),
	(1, 'CANCELLED'),
	(2, 'SHIPPED');

-- Fulfillments table
CREATE TABLE ship.fulfillments (
    id BIGSERIAL PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    order_external_id UUID NOT NULL,
    status_id SMALLINT NOT NULL REFERENCES ship.status(id),
    created TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes
DROP INDEX IF EXISTS idx_fulfillments_external_id;
CREATE INDEX idx_fulfillments_external_id ON ship.fulfillments(order_external_id);

DROP INDEX IF EXISTS idx_fulfillment_external_id;
CREATE INDEX idx_fulfillment_external_id ON ship.fulfillments(external_id);

COMMENT ON COLUMN ship.fulfillments.external_id
	IS 'External unique fulfillment order identifier';

COMMENT ON COLUMN ship.fulfillments.order_external_id
	IS 'External unique order identifier';

COMMENT ON COLUMN ship.fulfillments.created
	IS 'Time when the fulfillment order was submitted';

-- Shipments table
CREATE TABLE ship.shipments (
    id BIGSERIAL PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    order_external_id UUID NOT NULL,
    status_id SMALLINT NOT NULL REFERENCES ship.status(id),
    shipped TIMESTAMPTZ
);

DROP INDEX IF EXISTS idx_shipments_order_external_id;
CREATE INDEX idx_shipments_order_external_id ON ship.shipments(order_external_id);

-- Comments
COMMENT ON COLUMN ship.shipments.external_id
	IS 'External unique shipments order identifier';

COMMENT ON COLUMN ship.shipments.order_external_id
	IS 'External unique order identifier';

COMMENT ON COLUMN ship.shipments.shipped
	IS 'Time when the order was shipped';

COMMIT;
