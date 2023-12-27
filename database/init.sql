--
-- PostgreSQL database cluster dump
--

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE admin;
ALTER ROLE admin WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE "user";
ALTER ROLE "user" WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;

--
-- User Configurations
--








--
-- Databases
--

--
-- Database "template1" dump
--

\connect template1

--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1 (Debian 16.1-1.pgdg120+1)
-- Dumped by pg_dump version 16.1 (Homebrew)

-- Started on 2023-12-16 23:58:36 MSK

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Completed on 2023-12-16 23:58:36 MSK

--
-- PostgreSQL database dump complete
--

--
-- Database "fdelivery" dump
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1 (Debian 16.1-1.pgdg120+1)
-- Dumped by pg_dump version 16.1 (Homebrew)

-- Started on 2023-12-16 23:58:36 MSK

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3405 (class 1262 OID 16389)
-- Name: fdelivery; Type: DATABASE; Schema: -; Owner: postgres
--

DROP DATABASE fdelivery;
CREATE DATABASE fdelivery WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE fdelivery OWNER TO postgres;

\connect fdelivery

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16565)
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id uuid NOT NULL,
    name character varying NOT NULL,
    description character varying NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16570)
-- Name: delivery_addresses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delivery_addresses (
    id uuid NOT NULL,
    destination character varying NOT NULL,
    apartment integer NOT NULL,
    entrance integer NOT NULL,
    floor integer NOT NULL
);


ALTER TABLE public.delivery_addresses OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16575)
-- Name: delivery_men; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delivery_men (
    id uuid NOT NULL,
    status integer NOT NULL
);


ALTER TABLE public.delivery_men OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16578)
-- Name: items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.items (
    id uuid NOT NULL,
    name character varying NOT NULL,
    description character varying NOT NULL,
    category_id uuid NOT NULL,
    price numeric NOT NULL,
    image_path character varying NOT NULL
);


ALTER TABLE public.items OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16583)
-- Name: ordered_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ordered_items (
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    item_id uuid,
    ordered_amount integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.ordered_items OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16587)
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    id uuid NOT NULL,
    creation_date timestamp with time zone NOT NULL,
    status integer NOT NULL,
    assigned_to_id uuid,
    delivery_fee numeric NOT NULL,
    created_by_id uuid NOT NULL
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16592)
-- Name: settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.settings (
    key character varying NOT NULL,
    value character varying
);


ALTER TABLE public.settings OWNER TO postgres;

INSERT INTO public.settings(key, value) VALUES('deliveryFee', '100')

--
-- TOC entry 222 (class 1259 OID 16597)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    login character varying NOT NULL,
    password_hash character varying NOT NULL,
    salt character varying NOT NULL,
    role integer NOT NULL,
    name character varying NOT NULL,
    last_name character varying NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 3233 (class 2606 OID 16603)
-- Name: categories categories_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pk PRIMARY KEY (id);


--
-- TOC entry 3235 (class 2606 OID 16605)
-- Name: delivery_addresses delivery_addresses_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_addresses
    ADD CONSTRAINT delivery_addresses_pk PRIMARY KEY (id);


--
-- TOC entry 3237 (class 2606 OID 16607)
-- Name: delivery_men delivery_men_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_men
    ADD CONSTRAINT delivery_men_pk PRIMARY KEY (id);


--
-- TOC entry 3239 (class 2606 OID 16609)
-- Name: items items_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.items
    ADD CONSTRAINT items_pk PRIMARY KEY (id);


--
-- TOC entry 3241 (class 2606 OID 16611)
-- Name: ordered_items ordered_items_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ordered_items
    ADD CONSTRAINT ordered_items_pk PRIMARY KEY (id);


--
-- TOC entry 3243 (class 2606 OID 16613)
-- Name: orders orders_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pk PRIMARY KEY (id);


--
-- TOC entry 3245 (class 2606 OID 16615)
-- Name: settings settings_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pk PRIMARY KEY (key);


--
-- TOC entry 3247 (class 2606 OID 16617)
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- TOC entry 3249 (class 2606 OID 16619)
-- Name: users users_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_un UNIQUE (login);


--
-- TOC entry 3250 (class 2606 OID 16620)
-- Name: delivery_men delivery_men_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_men
    ADD CONSTRAINT delivery_men_fk FOREIGN KEY (id) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3251 (class 2606 OID 16625)
-- Name: items items_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.items
    ADD CONSTRAINT items_fk FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- TOC entry 3252 (class 2606 OID 16630)
-- Name: ordered_items ordered_items_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ordered_items
    ADD CONSTRAINT ordered_items_fk FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- TOC entry 3253 (class 2606 OID 16635)
-- Name: ordered_items ordered_items_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ordered_items
    ADD CONSTRAINT ordered_items_fk_1 FOREIGN KEY (item_id) REFERENCES public.items(id);


--
-- TOC entry 3254 (class 2606 OID 16640)
-- Name: orders orders_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_fk FOREIGN KEY (assigned_to_id) REFERENCES public.delivery_men(id);


--
-- TOC entry 3255 (class 2606 OID 16645)
-- Name: orders orders_fk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_fk_1 FOREIGN KEY (created_by_id) REFERENCES public.users(id);


--
-- TOC entry 3256 (class 2606 OID 16650)
-- Name: orders orders_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_fk_2 FOREIGN KEY (id) REFERENCES public.delivery_addresses(id);


--
-- TOC entry 3406 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO "user";
GRANT ALL ON SCHEMA public TO admin;


--
-- TOC entry 3407 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE categories; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.categories TO "user";
GRANT ALL ON TABLE public.categories TO admin;


--
-- TOC entry 3408 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE delivery_addresses; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.delivery_addresses TO "user";
GRANT ALL ON TABLE public.delivery_addresses TO admin;


--
-- TOC entry 3409 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE delivery_men; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.delivery_men TO "user";
GRANT ALL ON TABLE public.delivery_men TO admin;


--
-- TOC entry 3410 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE items; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.items TO "user";
GRANT ALL ON TABLE public.items TO admin;


--
-- TOC entry 3411 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE ordered_items; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.ordered_items TO "user";
GRANT ALL ON TABLE public.ordered_items TO admin;


--
-- TOC entry 3412 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE orders; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.orders TO "user";
GRANT ALL ON TABLE public.orders TO admin;


--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE settings; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.settings TO "user";
GRANT ALL ON TABLE public.settings TO admin;


--
-- TOC entry 3414 (class 0 OID 0)
-- Dependencies: 222
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.users TO "user";
GRANT ALL ON TABLE public.users TO admin;


--
-- TOC entry 2065 (class 826 OID 16655)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT ON TABLES TO "user";


-- Completed on 2023-12-16 23:58:36 MSK

--
-- PostgreSQL database dump complete
--

-- Completed on 2023-12-16 23:58:36 MSK

--
-- PostgreSQL database cluster dump complete
--

