-- Invoice Payments
CREATE TABLE invoice_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_invoice UUID NOT NULL REFERENCES invoices(id),
    payment_date DATE NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoice_payments_invoice ON invoice_payments(id_invoice);
CREATE INDEX idx_invoice_payments_date ON invoice_payments(payment_date);

-- Bill Payments
CREATE TABLE bill_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_bill UUID NOT NULL REFERENCES bills(id),
    payment_date DATE NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bill_payments_bill ON bill_payments(id_bill);
CREATE INDEX idx_bill_payments_date ON bill_payments(payment_date);
