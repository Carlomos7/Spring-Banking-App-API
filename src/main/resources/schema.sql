-- Customer table
CREATE TABLE customer (
    id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(320) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Account table
CREATE TABLE account (
    id UUID PRIMARY KEY,
    customer_id UUID,
    kind VARCHAR(32) NOT NULL CHECK (kind IN ('checking','savings','internal')),
    currency CHAR(3) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Journal table
CREATE TABLE journal (
    id UUID PRIMARY KEY,
    status VARCHAR(16) NOT NULL CHECK (status IN ('pending','posted')),
    description VARCHAR(500),
    external_ref VARCHAR(120) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    posted_at TIMESTAMP
);

-- Ledger entries
CREATE TABLE ledger_entry (
    id UUID PRIMARY KEY,
    journal_id UUID NOT NULL,
    account_id UUID NOT NULL,
    side VARCHAR(6) NOT NULL CHECK (side IN ('debit','credit')),
    amount_cents BIGINT NOT NULL CHECK (amount_cents > 0),
    currency CHAR(3) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_le_journal FOREIGN KEY (journal_id) REFERENCES journal(id) ON DELETE CASCADE,
    CONSTRAINT fk_le_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE INDEX le_by_acct ON ledger_entry(account_id, created_at);
CREATE INDEX le_by_journal ON ledger_entry(journal_id);

-- Cached balances
CREATE TABLE account_balance (
    account_id UUID PRIMARY KEY,
    balance_cents BIGINT DEFAULT 0,
    currency CHAR(3) NOT NULL,
    CONSTRAINT fk_ab_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);
