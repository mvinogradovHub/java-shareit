DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name                 VARCHAR(255),
  email                VARCHAR(255),
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description   VARCHAR(1000),
  requestor_id  BIGINT NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id),
  CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name          VARCHAR(255),
  description   VARCHAR(1000),
  is_available  BOOLEAN NOT NULL DEFAULT TRUE,
  owner_id      BIGINT NOT NULL,
  request_id    BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date    TIMESTAMP WITHOUT TIME ZONE,
  end_date      TIMESTAMP WITHOUT TIME ZONE,
  item_id       BIGINT NOT NULL,
  booker_id     BIGINT NOT NULL,
  status        VARCHAR(32) NOT NULL DEFAULT 'WAITING',
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
  CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text          VARCHAR(1000),
  item_id       BIGINT NOT NULL,
  author_id     BIGINT NOT NULL,
  created       TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_comment PRIMARY KEY (id),
  CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE
);