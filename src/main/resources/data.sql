-- Customers
INSERT INTO customer (id, username, password_hash, first_name, last_name, email)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'alice',
        '$2a$10$Dow1a6Lk6B9VQZo.5X8YfuZsD6c5DcM45QK6Z04.L3XxN7Sfhpj9K', -- password: alice123
        'Alice',
        'Doe',
        'alice@example.com'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'bob',
        '$2a$10$7QhZGBEqM3s8ThFSz0ziAuj5j9SuDrBqUcdh7QU2n9nmJY4dqppLS', -- password: bob123
        'Bob',
        'Smith',
        'bob@example.com'
    );

-- Accounts
INSERT INTO account (id, customer_id, kind, currency)
VALUES
    ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', 'checking', 'USD'),
    ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '11111111-1111-1111-1111-111111111111', 'savings', 'USD'),
    ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '22222222-2222-2222-2222-222222222222', 'checking', 'EUR');

-- Journals
INSERT INTO journal (id, status, description)
VALUES
    ('33333333-3333-3333-3333-333333333333', 'posted', 'Initial deposit for Alice'),
    ('44444444-4444-4444-4444-444444444444', 'pending', 'Bob’s first deposit');

-- Ledger Entries (double-entry bookkeeping)
INSERT INTO ledger_entry (id, journal_id, account_id, side, amount_cents, currency)
VALUES
    ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'credit', 100000, 'USD'),
    ('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'debit', 100000, 'USD');

-- Balances
INSERT INTO account_balance (account_id, balance_cents, currency)
VALUES
    ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 100000, 'USD'),
    ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 0, 'USD'),
    ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 0, 'EUR');
