CREATE SCHEMA IF NOT EXISTS person;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE person.countries (
                                  id SERIAL PRIMARY KEY,
                                  name VARCHAR(32),
                                  alpha2 VARCHAR(2),
                                  alpha3 VARCHAR(3),
                                  status VARCHAR(32),
                                  created_at TIMESTAMP DEFAULT now(),
                                  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE person.addresses (
                                  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                  country_id INT REFERENCES person.countries(id),
                                  address VARCHAR(128),
                                  zip_code VARCHAR(32),
                                  city VARCHAR(32),
                                  state VARCHAR(32),
                                  created_at TIMESTAMP DEFAULT now(),
                                  updated_at TIMESTAMP DEFAULT now(),
                                  archived_at TIMESTAMP
);

CREATE TABLE person.profiles (
                                 id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                 secret_key VARCHAR(32),
                                 first_name VARCHAR(32),
                                 last_name VARCHAR(32),
                                 status VARCHAR(64),
                                 filled BOOLEAN,
                                 address_id UUID REFERENCES person.addresses(id),
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now(),
                                 verified_at TIMESTAMP,
                                 archived_at TIMESTAMP
);

CREATE TABLE person.merchants (
                                  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                  company_name VARCHAR(32),
                                  company_id VARCHAR(32),
                                  email VARCHAR(32),
                                  phone_number VARCHAR(32),
                                  status VARCHAR(32),
                                  filled BOOLEAN,
                                  created_at TIMESTAMP DEFAULT now(),
                                  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE person.merchant_members (
                                 profile_id UUID REFERENCES person.profiles(id) PRIMARY KEY,
                                 merchant_id UUID REFERENCES person.merchants(id),
                                 member_role VARCHAR(32)
);

CREATE TABLE person.individuals (
                                    profile_id UUID REFERENCES person.profiles(id) PRIMARY KEY,
                                    passport_number VARCHAR(32),
                                    phone_number VARCHAR(32),
                                    email VARCHAR(32)
);

CREATE TABLE person.verification_statuses (
                                              id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                              profile_id UUID REFERENCES person.profiles(id),
                                              profile_type VARCHAR(32),
                                              details VARCHAR(255),
                                              verification_status VARCHAR(32),
                                              created_at TIMESTAMP DEFAULT now(),
                                              updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE person.profile_history (
                                        id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                        profile_id UUID REFERENCES person.profiles(id),
                                        profile_type VARCHAR(32),
                                        reason VARCHAR(255),
                                        comment VARCHAR(255),
                                        changed_values JSONB,
                                        created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE person.merchant_members_invitations (
                                                     id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                                     merchant_id UUID REFERENCES person.merchants(id),
                                                     first_name VARCHAR(32),
                                                     last_name VARCHAR(32),
                                                     email VARCHAR(32),
                                                     status VARCHAR(32),
                                                     created_at TIMESTAMP DEFAULT now(),
                                                     expires_at TIMESTAMP DEFAULT now() + interval '${invite.expiration.days} days'
);