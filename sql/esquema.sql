-- ============================================================
-- esquema.sql  -  Sistema de gestion de cine (SQLite)
-- Sirve para crear la base desde cero en una instalacion nueva.
-- Para la base que YA existe use sql/migracion.sql (no este archivo).
--
-- MEJORA 1: este archivo ahora define LO MISMO que ConexionDB.initDatabase().
-- Antes usaba pelicula.nombre y sala.tipo (sin filas/columnas) y el codigo
-- usaba pelicula.titulo y sala.tipo_sala, asi que una instalacion nueva
-- rompia la aplicacion. Los nombres correctos son los del codigo.
-- ============================================================

CREATE TABLE IF NOT EXISTS pelicula (
    idpelicula INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER,
    titulo TEXT,
    duracion TEXT,
    categoria TEXT,
    clasificacion TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_pelicula_numero ON pelicula(numero);

CREATE TABLE IF NOT EXISTS sala (
    idsala INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER NOT NULL UNIQUE,
    -- tipo_sala es el nombre que usa el codigo: 2D, 3D, XD, VIP
    tipo_sala TEXT NOT NULL,
    capacidad INTEGER NOT NULL,
    estado TEXT NOT NULL DEFAULT 'ACTIVA',
    -- cuadricula de la sala: de aqui salen sus butacas
    filas INTEGER NOT NULL DEFAULT 0,
    columnas INTEGER NOT NULL DEFAULT 0
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
    numero INTEGER NOT NULL,
    FOREIGN KEY (id_sala) REFERENCES sala(idsala)
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
    codigo_compra TEXT,
    -- MEJORA 6: forma de pago y datos de quien anulo el boleto
    metodo_pago TEXT DEFAULT 'EFECTIVO',
    anulada_por INTEGER,
    fecha_anulacion TEXT,
    motivo_anulacion TEXT,
    -- MEJORA 8: llaves foraneas declaradas
    FOREIGN KEY (id_funcion) REFERENCES funcion(idfuncion),
    FOREIGN KEY (id_cliente) REFERENCES cliente(idcliente),
    FOREIGN KEY (id_butaca) REFERENCES butaca(idbutaca),
    FOREIGN KEY (id_usuario) REFERENCES usuario(idusuario)
);
CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca ON venta (id_funcion, id_butaca);
CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra ON venta (codigo_compra);

-- MEJORA 8: una butaca solo puede estar vendida UNA vez por funcion,
-- pero solo entre las ventas 'activa'. Si se anula, se puede vender otra vez.
CREATE UNIQUE INDEX IF NOT EXISTS ux_venta_funcion_butaca_activa
    ON venta (id_funcion, id_butaca) WHERE estado = 'activa';

CREATE TABLE IF NOT EXISTS usuario (
    idusuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    usuario TEXT NOT NULL UNIQUE,
    -- MEJORA 7: aqui se guarda el hash SHA-256, nunca la contraseña escrita
    contrasena TEXT NOT NULL,
    rol TEXT NOT NULL DEFAULT 'empleado',
    activo INTEGER NOT NULL DEFAULT 1
);
