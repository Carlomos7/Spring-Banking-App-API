-- =========================
-- Customers
-- =========================
INSERT INTO customer (id, username, password_hash, first_name, last_name, email)
VALUES
  (
    '11111111-1111-1111-1111-111111111111',
    'alice',
    '$2a$10$Dow1a6Lk6B9VQZo.5X8YfuZsD6c5DcM45QK6Z04.L3XxN7Sfhpj9K', -- "alice123"
    'Alice',
    'Doe',
    'alice@example.com'
  ),
  (
    '22222222-2222-2222-2222-222222222222',
    'bob',
    '$2a$10$7QhZGBEqM3s8ThFSz0ziAuj5j9SuDrBqUcdh7QU2n9nmJY4dqppLS', -- "bob123"
    'Bob',
    'Smith',
    'bob@example.com'
  );

-- =========================
-- Accounts
-- =========================
INSERT INTO account (id, customer_id, kind, currency, is_active)
VALUES
  -- Alice
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', 'checking', 'USD', TRUE),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '11111111-1111-1111-1111-111111111111', 'savings',  'USD', TRUE),

  -- Bob
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '22222222-2222-2222-2222-222222222222', 'checking', 'EUR', TRUE),

  -- Internal (bank) accounts for balancing entries
  ('ccccccc1-cccc-cccc-cccc-ccccccccccc1', NULL, 'internal', 'USD', TRUE), -- bank_cash_usd
  ('ccccccc2-cccc-cccc-cccc-ccccccccccc2', NULL, 'internal', 'EUR', TRUE); -- bank_cash_eur

-- =========================
-- Journals
-- =========================
INSERT INTO journal (id, status, description, external_ref)
VALUES
  ('33333333-3333-3333-3333-333333333333', 'posted',  'Initial deposit for Alice',                    'init-alice-1'),
  ('33333333-3333-3333-3333-333333333334', 'posted',  'Transfer from Alice checking to savings',       'alice-xfer-1'),
  ('44444444-4444-4444-4444-444444444444', 'pending', 'Bob initial deposit (pending, not posted yet)', 'init-bob-1');

-- =========================
-- Ledger Entries
-- =========================

-- J1: Alice initial deposit: $1,000.00 USD
INSERT INTO ledger_entry (id, journal_id, account_id, side, amount_cents, currency)
VALUES
  -- Debit internal USD cash
  ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333',
   'ccccccc1-cccc-cccc-cccc-ccccccccccc1', 'debit',  100000, 'USD'),
  -- Credit Alice checking
  ('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333',
   'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'credit', 100000, 'USD');

-- J2: Alice transfer from checking -> savings: $250.00 USD
INSERT INTO ledger_entry (id, journal_id, account_id, side, amount_cents, currency)
VALUES
  -- Debit Alice savings (increase)
  ('77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333334',
   'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'debit',  25000, 'USD'),
  -- Credit Alice checking (decrease)
  ('88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333334',
   'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'credit', 25000, 'USD');

-- =========================
-- Cached Balances
-- - Alice checking:  +100000 (J1 credit) - 25000 (J2 credit against checking) = 75000
-- - Alice savings:   +25000  (J2 debit)  = 25000
-- - Internal USD:    +100000 (J1 debit)  = 100000
-- - Bob checking EUR: 0 (no posted journals)
-- - Internal EUR:      0
-- =========================
INSERT INTO account_balance (account_id, balance_cents, currency)
VALUES
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1',  75000,  'USD'),  -- Alice checking
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2',  25000,  'USD'),  -- Alice savings
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1',      0,  'EUR'),  -- Bob checking
  ('ccccccc1-cccc-cccc-cccc-ccccccccccc1', 100000,  'USD'),  -- internal USD cash
  ('ccccccc2-cccc-cccc-cccc-ccccccccccc2',      0,  'EUR');  -- internal EUR cash
