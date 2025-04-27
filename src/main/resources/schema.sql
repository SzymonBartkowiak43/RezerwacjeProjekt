--SELECT * FROM "users"
CREATE TABLE IF NOT EXISTS "users" (
                                       id SERIAL PRIMARY KEY,
                                       email VARCHAR(100) UNIQUE,
    name VARCHAR(100),
    password VARCHAR(200)
    );

CREATE TABLE IF NOT EXISTS "user_role" (
                                           id SERIAL PRIMARY KEY,
                                           name VARCHAR(100) UNIQUE,
    description VARCHAR(250)
    );

CREATE TABLE IF NOT EXISTS "user_roles" (
                                            user_id BIGINT,
                                            role_id BIGINT,
                                            PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "users"(id),
    FOREIGN KEY (role_id) REFERENCES "user_role"(id)
    );

CREATE TABLE IF NOT EXISTS "salon" (
                                       id SERIAL PRIMARY KEY,
                                       salon_name VARCHAR(250),
    category VARCHAR(250),
    city VARCHAR(250),
    zip_code VARCHAR(10),
    street VARCHAR(250),
    number VARCHAR(220),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES "users"(id)
    );

CREATE TABLE IF NOT EXISTS "image" (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_id VARCHAR(255) NOT NULL,
    salon_id BIGINT,
    FOREIGN KEY (salon_id) REFERENCES "salon"(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS "code" (
                                      id SERIAL PRIMARY KEY,
                                      code VARCHAR(255) NOT NULL,
    is_consumed BOOLEAN DEFAULT FALSE,
    data_generated TIMESTAMP NOT NULL,
    data_consumption TIMESTAMP NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES "users"(id)
    );

CREATE TABLE IF NOT EXISTS "opening_hours" (
                                               id SERIAL PRIMARY KEY,
                                               day_of_week VARCHAR(20) NOT NULL,
    opening_time TIME,
    closing_time TIME,
    salon_id BIGINT,
    FOREIGN KEY (salon_id) REFERENCES "salon"(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS "employee" (
                                          id SERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          salon_id BIGINT,
                                          FOREIGN KEY (salon_id) REFERENCES "salon"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id)
    );

CREATE TABLE IF NOT EXISTS "employee_availability" (
                                                       id SERIAL PRIMARY KEY,
                                                       day_of_week VARCHAR(10) NOT NULL,
    start_time TIME,
    end_time TIME,
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES "employee"(id)
    );

CREATE TABLE IF NOT EXISTS "offer" (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2),
    duration TIME,
    salon_id BIGINT,
    FOREIGN KEY (salon_id) REFERENCES "salon"(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS "employee_offers" (
                                                 employee_id BIGINT,
                                                 offer_id BIGINT,
                                                 PRIMARY KEY (employee_id, offer_id),
    FOREIGN KEY (employee_id) REFERENCES "employee"(id) ON DELETE CASCADE,
    FOREIGN KEY (offer_id) REFERENCES "offer"(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS "reservation" (
                                             id SERIAL PRIMARY KEY,
                                             salon_id BIGINT NOT NULL,
                                             employee_id BIGINT NOT NULL,
                                             user_id BIGINT NOT NULL,
                                             offer_id BIGINT NOT NULL,
                                             reservation_date_time TIMESTAMP,
                                             FOREIGN KEY (salon_id) REFERENCES "salon"(id),
    FOREIGN KEY (employee_id) REFERENCES "employee"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id),
    FOREIGN KEY (offer_id) REFERENCES "offer"(id)
    );
