-- Schema setup for local development (Docker compose)
CREATE SCHEMA IF NOT EXISTS us_fwd;
SET search_path TO us_fwd, public;

-- Company reference table (required by FK)
CREATE TABLE IF NOT EXISTS adm_co (
    co_cd varchar(20) NOT NULL PRIMARY KEY,
    co_nm varchar(100),
    cre_dt timestamp DEFAULT now()
);

INSERT INTO adm_co (co_cd, co_nm) VALUES
    ('DEMO', 'Demo Company'),
    ('ACME', 'Acme Corporation'),
    ('TECH', 'Tech Solutions Inc.')
ON CONFLICT DO NOTHING;

-- User table matching the production DDL
CREATE TABLE IF NOT EXISTS adm_usr (
    usr_id varchar(20) NOT NULL,
    usr_nm varchar(50) NULL,
    usr_pwd varchar(300) NULL,
    com_usr_dt varchar(20) NULL,
    com_usr_sx varchar(1) NULL,
    co_tel_no varchar(20) NULL,
    hm_tel_no varchar(20) NULL,
    mphn_no varchar(100) NULL,
    fax_no varchar(20) NULL,
    usr_eml varchar(100) NULL,
    act_flg varchar(1) NULL,
    delt_sts_flg varchar(1) NULL,
    cre_usr_id varchar(20) NOT NULL DEFAULT 'system',
    cre_dt timestamp(0) NOT NULL DEFAULT now(),
    upd_usr_id varchar(20) NOT NULL DEFAULT 'system',
    upd_dt timestamp(0) NOT NULL DEFAULT now(),
    img_url varchar(200) NULL,
    age int2 NULL,
    ntlt varchar(50) NULL,
    mrr_sts_cd varchar(20) NULL DEFAULT 'N',
    edu varchar(500) NULL,
    hby varchar(500) NULL,
    wrk_exp varchar(500) NULL,
    hm_addr varchar(1000) NULL,
    spct varchar(500) NULL,
    eng_lvl varchar(500) NULL,
    saly_lvl varchar(500) NULL,
    prj_his varchar(2000) NULL,
    cnt_cd varchar(2) NULL,
    cty_nm varchar(100) NULL,
    is_root varchar(1) NULL DEFAULT 'N',
    brdy_val varchar(10) NULL,
    co_cd varchar(20) NOT NULL,
    ctrb_pnt_no numeric(10, 2) NULL DEFAULT 0,
    perf_pnt_no numeric(10, 2) NULL DEFAULT 0,
    lst_perf_pnt_no numeric(10, 2) NULL,
    lst_ctrb_pnt_no numeric(10, 2) NULL,
    empe_no varchar(20) NULL,
    empe_st_dt timestamp(0) NULL,
    add_prbtn_vac_flg varchar(1) NULL,
    loc_cd varchar(20) NULL,
    empe_tp_cd varchar(3) NULL,
    full_nm varchar(100) NULL,
    usr_pwd_temp varchar(30) NULL,
    dvc_tkn_cd varchar(500) NULL,
    face_usr_id varchar(20) NULL,
    sky_id varchar(100) NULL,
    ofc_cd varchar(20) NULL,
    empe_end_dt date NULL,
    sns_id varchar(100) NULL,
    sns_nm varchar(50) NULL,
    cty_cd varchar(5) NULL,
    usr_eml_pwd varchar(100) NULL,
    eml_ctnt varchar(4000) NULL,
    eml_tkn_val varchar(5000) NULL,
    CONSTRAINT adm_usr_pkey PRIMARY KEY (co_cd, usr_id),
    CONSTRAINT fk_adm_usr_co FOREIGN KEY (co_cd) REFERENCES adm_co(co_cd)
);

-- Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_adm_usr_co_cd ON adm_usr(co_cd);
CREATE INDEX IF NOT EXISTS idx_adm_usr_usr_nm ON adm_usr(usr_nm);
CREATE INDEX IF NOT EXISTS idx_adm_usr_act_flg ON adm_usr(act_flg);
CREATE INDEX IF NOT EXISTS idx_adm_usr_age ON adm_usr(age);

-- Sample data for demo/testing — DEMO company
INSERT INTO adm_usr (co_cd, usr_id, usr_nm, usr_pwd, full_nm, usr_eml, mphn_no, age, act_flg, cnt_cd, cty_nm, loc_cd, ofc_cd, empe_no, empe_tp_cd, is_root, edu, wrk_exp, spct, eng_lvl, brdy_val, empe_st_dt, com_usr_sx, cre_usr_id, upd_usr_id)
VALUES
    ('DEMO', 'admin',    'Admin',     'admin123',   'Administrator',      'admin@demo.com',    '010-1111-1111', 35, 'Y', 'KR', 'Seoul',    'HQ',   'MAIN', 'EMP001', 'FT', 'Y', 'Master Computer Science', '10 years Java/Spring', 'System Architecture', 'Advanced', '1991-03-15', '2015-01-01', 'M', 'system', 'system'),
    ('DEMO', 'john',     'John',      'john123',    'John Doe',           'john@demo.com',     '010-2222-2222', 28, 'Y', 'KR', 'Seoul',    'HQ',   'MAIN', 'EMP002', 'FT', 'N', 'BS Software Engineering', '5 years Backend', 'Spring Boot, Kafka', 'Intermediate', '1998-07-22', '2020-03-01', 'M', 'system', 'system'),
    ('DEMO', 'jane',     'Jane',      'jane123',    'Jane Smith',         'jane@demo.com',     '010-3333-3333', 32, 'Y', 'US', 'New York', 'NY',   'SUB',  'EMP003', 'FT', 'N', 'MS Data Science', '8 years Full-Stack', 'React, Node.js, Python', 'Native', '1994-11-08', '2018-06-15', 'F', 'system', 'system'),
    ('DEMO', 'bob',      'Bob',       'bob123',     'Bob Wilson',         'bob@demo.com',      '010-4444-4444', 45, 'Y', 'JP', 'Tokyo',    'TK',   'SUB',  'EMP004', 'CT', 'N', 'PhD Distributed Systems', '20 years Architecture', 'Microservices, AWS', 'Advanced', '1981-01-30', '2010-09-01', 'M', 'system', 'system'),
    ('DEMO', 'alice',    'Alice',     'alice123',   'Alice Johnson',      'alice@demo.com',    '010-5555-5555', 26, 'Y', 'KR', 'Busan',    'BS',   'MAIN', 'EMP005', 'FT', 'N', 'BS Computer Engineering', '3 years DevOps', 'Docker, K8s, CI/CD', 'Intermediate', '2000-05-12', '2023-01-10', 'F', 'system', 'system'),
    ('DEMO', 'charlie',  'Charlie',   'charlie123', 'Charlie Brown',      'charlie@demo.com',  '010-6666-6666', 38, 'Y', 'KR', 'Seoul',    'HQ',   'MAIN', 'EMP006', 'FT', 'N', 'MS Information Security', '12 years Security', 'OAuth2, JWT, OWASP', 'Advanced', '1988-09-25', '2014-04-01', 'M', 'system', 'system'),
    ('DEMO', 'diana',    'Diana',     'diana123',   'Diana Prince',       'diana@demo.com',    '010-7777-7777', 30, 'Y', 'US', 'San Francisco','SF','SUB',  'EMP007', 'FT', 'N', 'BS Mathematics', '6 years Data Engineer', 'Spark, Kafka, Redis', 'Advanced', '1996-12-01', '2019-08-20', 'F', 'system', 'system'),
    ('DEMO', 'inactive', 'Inactive',  'inactive123','Inactive User',      'inactive@demo.com', '010-9999-9999', 50, 'N', 'KR', 'Seoul',    'HQ',   'MAIN', 'EMP008', 'FT', 'N', 'BS Business', '25 years Management', 'Project Management', 'Basic', '1976-06-10', '2005-01-01', 'M', 'system', 'system')
ON CONFLICT DO NOTHING;

-- Sample data for demo/testing — ACME company
INSERT INTO adm_usr (co_cd, usr_id, usr_nm, usr_pwd, full_nm, usr_eml, mphn_no, age, act_flg, cnt_cd, cty_nm, loc_cd, ofc_cd, empe_no, empe_tp_cd, edu, wrk_exp, com_usr_sx, cre_usr_id, upd_usr_id)
VALUES
    ('ACME', 'manager',  'Manager',   'manager123', 'Mike Manager',       'mike@acme.com',     '010-8001-0001', 40, 'Y', 'US', 'Chicago',  'CH',   'MAIN', 'AE001',  'FT', 'MBA', '15 years Management', 'M', 'system', 'system'),
    ('ACME', 'dev01',    'Developer', 'dev123',     'Dave Developer',     'dave@acme.com',     '010-8001-0002', 29, 'Y', 'US', 'Chicago',  'CH',   'MAIN', 'AE002',  'FT', 'BS CS', '5 years Java', 'M', 'system', 'system'),
    ('ACME', 'qa01',     'QA',        'qa123',      'Quinn QA',           'quinn@acme.com',    '010-8001-0003', 33, 'Y', 'US', 'Chicago',  'CH',   'MAIN', 'AE003',  'CT', 'BS SE', '8 years Testing', 'F', 'system', 'system')
ON CONFLICT DO NOTHING;

-- Sample data for demo/testing — TECH company
INSERT INTO adm_usr (co_cd, usr_id, usr_nm, usr_pwd, full_nm, usr_eml, mphn_no, age, act_flg, cnt_cd, cty_nm, loc_cd, ofc_cd, empe_no, empe_tp_cd, edu, wrk_exp, com_usr_sx, cre_usr_id, upd_usr_id)
VALUES
    ('TECH', 'cto',      'CTO',       'cto123',     'Chris Technology',   'chris@techsol.com', '010-9001-0001', 42, 'Y', 'KR', 'Seoul',    'SE',   'MAIN', 'TE001',  'FT', 'PhD CS', '18 years Architecture', 'M', 'system', 'system'),
    ('TECH', 'sre01',    'SRE',       'sre123',     'Sam Reliable',       'sam@techsol.com',   '010-9001-0002', 31, 'Y', 'KR', 'Seoul',    'SE',   'MAIN', 'TE002',  'FT', 'MS DevOps', '7 years SRE', 'M', 'system', 'system')
ON CONFLICT DO NOTHING;
