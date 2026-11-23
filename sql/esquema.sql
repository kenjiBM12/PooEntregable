-- ============================================================
-- esquema.sql  -  Sistema de gestion de cine (SQLite)
-- Sirve para crear la base desde cero en una instalacion nueva.
-- Para la base que YA existe use sql/migracion.sql (no este archivo).
-- ============================================================

CREATE TABLE IF NOT EXISTS pelicula (
    idpelicula INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER,
    nombre TEXT,
    duracion TEXT,
    categoria TEXT,
    clasificacion TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_pelicula_numero ON pelicula(numero);

CREATE TABLE IF NOT EXISTS sala (
    idsala INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER NOT NULL UNIQUE,
    -- nombre se conserva porque es NOT NULL; el sistema guarda "Sala " + numero
    -- y en pantalla muestra "Sala 2 — 3D" usando numero + tipo
    nombre TEXT NOT NULL,
    tipo TEXT NOT NULL,
    capacidad INTEGER NOT NULL,
    estado TEXT NOT NULL DEFAULT 'ACTIVA'
);

CREATE TABLE IF NOT EXISTS cliente (
    idcliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    dni TEXT NOT NULL UNIQUE,
    email TEXT,
    genero TEXT,
    telefono TEXT
);

CREATE TABLE IF NOT EXISTS funcion (
    idfuncion INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER,
    id_pelicula INTEGER NOT NULL,
    id_sala INTEGER NOT NULL,
    fecha TEXT NOT NULL,
    horario TEXT NOT NULL,
    precio REAL NOT NULL,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(idpelicula),
    FOREIGN KEY (id_sala) REFERENCES sala(idsala)
);

CREATE TABLE IF NOT EXISTS butaca (
    idbutaca INTEGER PRIMARY KEY AUTOINCREMENT,
    id_sala INTEGER NOT NULL,
    fila INTEGER NOT NULL,
    numero INTEGER NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero
    ON butaca (id_sala, fila, numero);

CREATE TABLE IF NOT EXISTS venta (
    idventa INTEGER PRIMARY KEY AUTOINCREMENT,
    id_funcion INTEGER NOT NULL,
    id_cliente INTEGER,
    -- nombre completo del cliente: la columna es NOT NULL por compatibilidad
    cliente TEXT NOT NULL,
    -- cada fila de venta es UN boleto, por eso cantidad_asientos siempre vale 1
    cantidad_asientos INTEGER NOT NULL DEFAULT 1,
    total_pagar REAL NOT NULL DEFAULT 0,
    fecha_venta TEXT,
    id_butaca INTEGER,
    estado TEXT DEFAULT 'activa',
    id_usuario INTEGER,
    -- mismo codigo para todos los boletos de una misma compra (C-0001).
    -- Las ventas viejas quedan NULL y se muestran como "V-" + idventa.
    codigo_compra TEXT
);
CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca ON venta (id_funcion, id_butaca);
CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra ON venta (codigo_compra);

CREATE TABLE IF NOT EXISTS usuario (
    idusuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    usuario TEXT NOT NULL UNIQUE,
    contrasena TEXT NOT NULL,
    rol TEXT NOT NULL DEFAULT 'empleado',
    activo INTEGER NOT NULL DEFAULT 1
);
