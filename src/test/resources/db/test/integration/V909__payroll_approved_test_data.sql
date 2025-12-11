-- V909: Approved Payroll Test Data for Report Testing
-- Creates a pre-approved payroll run with calculated details for report generation tests

-- Insert approved payroll run for June 2025
INSERT INTO payroll_runs (
    id, 
    payroll_period, 
    period_start, 
    period_end, 
    status, 
    total_gross, 
    total_deductions, 
    total_net_pay, 
    total_pph21,
    employee_count,
    created_at, 
    updated_at
)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    '2025-06',
    '2025-06-01',
    '2025-06-30',
    'APPROVED',
    36000000,  -- 3 employees × 12M
    5400000,   -- total deductions
    30600000,  -- total net pay
    2760000,   -- total PPh21
    3,         -- 3 employees
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Payroll detail for Employee 1: Budi Santoso (TK/0, has NPWP)
INSERT INTO payroll_details (
    id, 
    id_payroll_run, 
    id_employee, 
    base_salary,
    gross_salary, 
    pph21, 
    bpjs_jht_employee,
    bpjs_jp_employee,
    bpjs_kes_employee, 
    total_deductions,
    net_pay,
    jkk_risk_class,
    created_at, 
    updated_at
)
VALUES (
    'd0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000001',
    12000000,
    12000000,
    720000,   -- PPh 21
    240000,   -- BPJS JHT (2%)
    24000,    -- BPJS JP (0.2%)
    60000,    -- BPJS Kes (0.5%)
    1044000,  -- total deductions
    10956000, -- net pay
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Payroll detail for Employee 2: Dewi Lestari (K/2, has NPWP)
INSERT INTO payroll_details (
    id, 
    id_payroll_run, 
    id_employee, 
    base_salary,
    gross_salary, 
    pph21, 
    bpjs_jht_employee,
    bpjs_jp_employee,
    bpjs_kes_employee, 
    total_deductions,
    net_pay,
    jkk_risk_class,
    created_at, 
    updated_at
)
VALUES (
    'd0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000002',
    12000000,
    12000000,
    600000,   -- PPh 21 (lower due to K/2 PTKP)
    240000,   -- BPJS JHT
    24000,    -- BPJS JP
    60000,    -- BPJS Kes
    924000,   -- total deductions
    11076000, -- net pay
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Payroll detail for Employee 3: Agus Wijaya (TK/1, no NPWP - 20% higher PPh21)
INSERT INTO payroll_details (
    id, 
    id_payroll_run, 
    id_employee, 
    base_salary,
    gross_salary, 
    pph21, 
    bpjs_jht_employee,
    bpjs_jp_employee,
    bpjs_kes_employee, 
    total_deductions,
    net_pay,
    jkk_risk_class,
    created_at, 
    updated_at
)
VALUES (
    'd0000000-0000-0000-0000-000000000003',
    'a0000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000003',
    12000000,
    12000000,
    1440000,  -- PPh 21 (20% higher without NPWP)
    240000,   -- BPJS JHT
    24000,    -- BPJS JP
    60000,    -- BPJS Kes
    1764000,  -- total deductions
    10236000, -- net pay
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
