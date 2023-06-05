
--CRIAÇÃO DE SEQUENCES
CREATE SEQUENCE IF NOT EXISTS public.sq_siusuari
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
    
--CRIAÇÃO DE SEQUENCES
CREATE SEQUENCE IF NOT EXISTS public.sq_siclient
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
    
CREATE SEQUENCE IF NOT EXISTS public.sq_sicliusu
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;    
    
CREATE SEQUENCE IF NOT EXISTS public.sq_esprodut
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;        

--CRIAÇÃO DE TABELAS    
CREATE TABLE IF NOT EXISTS public.siusuari
(
    id bigint NOT NULL,
    logusu character varying(30) COLLATE pg_catalog."default" NOT NULL,
    senusu character varying(100) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_siusuari PRIMARY KEY (id)
)TABLESPACE pg_default;

COMMENT ON TABLE public.siusuari IS 'TABELA DE USUÁRIOS';
COMMENT ON COLUMN public.siusuari.id IS 'CÓDIGO';
COMMENT ON COLUMN public.siusuari.logusu IS 'LOGIN DO USUÁRIO';
COMMENT ON COLUMN public.siusuari.senusu IS 'SENHA DO USUÁRIO';

--TABELA DE CLIENTE
CREATE TABLE public.siclient
(
    id bigint NOT NULL,
    descri character varying(100) NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE public.siclient IS 'TABELA DE CLIENTE';
COMMENT ON COLUMN public.siclient.id IS 'CÓDIGO DO CLIENTE';

--TABELA DE LIGAÇÃO ENTRE USUÁRIO E CLIENTE
CREATE TABLE public.sicliusu
(
    id bigint NOT NULL,
    usuari bigint NOT NULL,
    client bigint NOT NULL,
    CONSTRAINT pk_siusuari PRIMARY KEY (id),
    CONSTRAINT uk_sicliusu UNIQUE (usuari, client)
);

COMMENT ON TABLE public.sicliusu IS 'LIGAÇÃO ENTRE CLIENTE E USUÁRIO';
COMMENT ON COLUMN public.sicliusu.id IS 'CÓDIGO';
COMMENT ON COLUMN public.sicliusu.usuari IS 'CÓDIGO DO USUÁRIO';
COMMENT ON COLUMN public.sicliusu.client IS 'CÓDIGO DO CLIENTE';

--TABELA DE PRODUTOS
CREATE TABLE public.esprodut
(
    id bigint NOT NULL,
    descri character varying(100) NOT NULL,
    codbar character varying(20),
    status character varying(1) NOT NULL,
    qtdest numeric(11, 4) NOT NULL,
    vlrcom numeric(9, 2) NOT NULL,
    vlrven numeric(9, 2) NOT NULL,
    client bigint NOT NULL,
    CONSTRAINT pk_esprodut PRIMARY KEY (id),
    CONSTRAINT fk_esprodut_siclient FOREIGN KEY (client)
        REFERENCES public.siclient (id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

COMMENT ON TABLE public.esprodut IS 'TABELA DE ARMAZENAGEM DE PRODUTOS';
COMMENT ON COLUMN public.esprodut.id IS 'CÓDIGO';
COMMENT ON COLUMN public.esprodut.descri IS 'NOME DO PRODUTO';
COMMENT ON COLUMN public.esprodut.codbar IS 'CÓDIGO DE BARRAS';
COMMENT ON COLUMN public.esprodut.status IS 'STATUS (A-ATIVO / I-INATIVO)';
COMMENT ON COLUMN public.esprodut.qtdest IS 'QUANTIDADE EM ESTOQUE';
COMMENT ON COLUMN public.esprodut.vlrcom IS 'VALOR DE COMPRA';
COMMENT ON COLUMN public.esprodut.vlrven IS 'VALOR DE VENDA';
COMMENT ON COLUMN public.esprodut.client IS 'CÓDIGO DO CLIENTE';

--INSERÇÃO DE VALORES
INSERT INTO public.siclient(id, descri) VALUES (nextval('sq_siclient'), 'Cliente A');
INSERT INTO public.siclient(id, descri) VALUES (nextval('sq_siclient'), 'Cliente B');


INSERT INTO public.siusuari(id, logusu, senusu)	VALUES (nextval('sq_siusuari'), 'pedro', '123456');	
INSERT INTO public.siusuari(id, logusu, senusu)	VALUES (nextval('sq_siusuari'), 'joao', '123456');


INSERT INTO public.sicliusu(id, usuari, client) VALUES (nextval('sq_sicliusu'), 1, 1);
INSERT INTO public.sicliusu(id, usuari, client) VALUES (nextval('sq_sicliusu'), 1, 2);
INSERT INTO public.sicliusu(id, usuari, client) VALUES (nextval('sq_sicliusu'), 2, 2);


INSERT INTO public.esprodut(id, descri, codbar, status, qtdest, vlrcom, vlrven, client) values (nextval('sq_esprodut'), 'MANTEIGA', '7.891.000.087.213', 'I', 25, 2.85, 3.40, 1);
INSERT INTO public.esprodut(id, descri, codbar, status, qtdest, vlrcom, vlrven, client) values (nextval('sq_esprodut'), 'ESCOVA DE DENTES', '8.121.454.487.541', 'I', 10, 8.85, 13.40, 1);





