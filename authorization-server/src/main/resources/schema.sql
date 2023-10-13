-- Space
create table if not exists `space`
(
    id char(36) not null primary key,
    `name` varchar(255) not null
);

-- INSERT `space` VALUES("3defe7c5-dadb-4503-acbd-89fb6b70e905", "SPACE_A");
-- INSERT `space` VALUES("93fa8e5a-e02e-4dc5-9720-6e8b9a17d285", "SPACE_B");

-- Users
create table if not exists `user`
(
    id char(36) not null primary key,
    space_id char(36) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    email varchar(255) not null,
    phone varchar(255),
    password varchar(255) not null,
    enabled boolean not null,
    mfa boolean not null,
    constraint user_email UNIQUE (email),
    constraint user_space_id foreign key (space_id) references `space` (id)
);

create table if not exists user_role
(
    user_id char(36) not null,
    `role` varchar(255) not null,
    space_id char(36) not null,
    `execute` boolean not null,
    `grant` boolean not null,
    constraint user_role_user_id foreign key (user_id) references `user` (id),
    constraint user_role_space_id foreign key (space_id) references `space` (id)
);

--user
--INSERT `user` VALUES("3f003a3a-65a3-4fd5-9852-9621f432b0e0", "3defe7c5-dadb-4503-acbd-89fb6b70e905",
--    "chuck", "norris", "user", "0123456789",
--    "{bcrypt}$2a$14$tEnq90/CcR320dWQ.NdQLuj326PmgLzMGmFkUUOHQrbjPWplKK67i", true, false);

-- admin
--INSERT `user` VALUES("2fbd3869-ddb9-49da-92d9-5eb1bc6f7600", "3defe7c5-dadb-4503-acbd-89fb6b70e905",
--    "admin", "admin", "admin", "0123456789",
--    "{bcrypt}$2a$14$tEnq90/CcR320dWQ.NdQLuj326PmgLzMGmFkUUOHQrbjPWplKK67i", true, true);

-- user roles
--INSERT user_role VALUES("3f003a3a-65a3-4fd5-9852-9621f432b0e0", "FOO", "93fa8e5a-e02e-4dc5-9720-6e8b9a17d285", true, false);
--INSERT user_role VALUES("3f003a3a-65a3-4fd5-9852-9621f432b0e0", "BAR", "93fa8e5a-e02e-4dc5-9720-6e8b9a17d285", true, false);

-- admin roles
--INSERT user_role VALUES("2fbd3869-ddb9-49da-92d9-5eb1bc6f7600", "FOO", "93fa8e5a-e02e-4dc5-9720-6e8b9a17d285", true, false);
--INSERT user_role VALUES("2fbd3869-ddb9-49da-92d9-5eb1bc6f7600", "BAR", "93fa8e5a-e02e-4dc5-9720-6e8b9a17d285", true, false);
--INSERT user_role VALUES("2fbd3869-ddb9-49da-92d9-5eb1bc6f7600", "FOO", "3defe7c5-dadb-4503-acbd-89fb6b70e905", true, false);
--INSERT user_role VALUES("2fbd3869-ddb9-49da-92d9-5eb1bc6f7600", "BAR", "3defe7c5-dadb-4503-acbd-89fb6b70e905", true, false);

-- Registered Client Repository

CREATE TABLE if not exists oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE if not exists api_key
(
    id char(36) NOT NULL primary key,
    space_id char(36) not null,
    application_name varchar(255) not null,
    application_description varchar(255) not null,
    email varchar(255) not null,
    secret varchar(255) not null,
    enabled boolean not null,
    authorization_code boolean not null,
    client_credentials boolean not null,
    constraint api_key_application_name UNIQUE (application_name),
    constraint api_key_space_id foreign key (space_id) references `space` (id)
);

create table if not exists api_key_role
(
    api_key_id char(36) not null,
    `role` varchar(255) not null,
    space_id char(36) not null,
    `execute` boolean not null,
    `grant` boolean not null,
    constraint api_key_role_user_id foreign key (api_key_id) references `api_key` (id),
    constraint api_key_role_space_id foreign key (api_key_id) references `space` (id)
);

-- INSERT api_key VALUES("ee3f8327-f073-4585-8a36-ee40f7d38fac", "3defe7c5-dadb-4503-acbd-89fb6b70e905", "gateway", "Gateway API key", "gateway@vialink.fr", "{noop}gatewaysecret", true, true, false);
-- INSERT api_key VALUES("6db2682f-6c2f-40b6-8c53-80a0befde557", "3defe7c5-dadb-4503-acbd-89fb6b70e905", "bar", "Bar API key", "bar@vialink.fr", "{noop}barsecret", true, false, true);

-- sessions
CREATE TABLE if not exists SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
    ) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX if not exists SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX if not exists SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX if not exists SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

-- session attributes
CREATE TABLE if not exists SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BLOB NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
    ) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;