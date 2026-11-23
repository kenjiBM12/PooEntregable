-- ============================================================
-- migracion.sql  -  Sistema de gestion de cine (SQLite)
-- Base de datos: C:/sqlite/db/gestionCine.db
--
-- IMPORTANTE: este script NO fue ejecutado sobre la base real.
-- Se ejecuta UNA sola vez, con el programa cerrado, desde
-- DB Browser for SQLite -> pestaña "Execute SQL" -> boton Play.
-- Antes de ejecutarlo, haga una copia del archivo .db.
--
-- El script fue probado sobre una COPIA de la base y funciono
-- sin errores: conserva las 12 ventas y todos los datos.
-- ============================================================


-- ------------------------------------------------------------
-- 1) VENTAS: codigo de compra
-- ------------------------------------------------------------
-- Todos los boletos de una misma compra comparten el mismo codigo
-- (por ejemplo C-0001). Asi se agrupan y se anulan juntos.
-- Cada fila de la tabla venta sigue siendo UN boleto.
ALTER TABLE venta ADD COLUMN codigo_compra TEXT;

-- indice para ordenar y buscar por codigo de compra
CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra ON venta (codigo_compra);

-- Las ventas antiguas se quedan con codigo_compra vacio (NULL) a
-- proposito: el sistema las muestra como "V-" + su idventa
-- (por ejemplo V-12) para no inventar codigos que no existieron.
-- No se pierde ningun dato.


-- ------------------------------------------------------------
-- 2) VENTAS: estado
-- ------------------------------------------------------------
-- estado ya existe ('activa' / 'anulada'): no se toca la columna,
-- solo se asegura que ninguna venta quede sin estado.
UPDATE venta SET estado = 'activa' WHERE estado IS NULL OR estado = '';


-- ------------------------------------------------------------
-- 3) BUTACAS: no repetir la misma butaca en la misma sala
-- ------------------------------------------------------------
-- Permite completar o regenerar las butacas de una sala sin crear
-- duplicados (misma sala + misma fila + mismo numero).
CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero
    ON butaca (id_sala, fila, numero);


-- ------------------------------------------------------------
-- 4) VENTAS: indice para saber si una butaca esta ocupada
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca
    ON venta (id_funcion, id_butaca);


-- ------------------------------------------------------------
-- 5) COLUMNAS QUE YA EXISTEN EN ESTA BASE
-- ------------------------------------------------------------
-- La base C:/sqlite/db/gestionCine.db YA tiene estas columnas, por
-- eso se dejan comentadas: si se ejecutan, SQLite responde
-- "duplicate column name" y se detiene.
--
-- venta.id_cliente:
--   La venta guarda el id del cliente (referencia estable) ademas
--   del nombre escrito. Para las ventas antiguas se completa el id
--   solo cuando el nombre coincide con UN unico cliente.
-- ALTER TABLE venta ADD COLUMN id_cliente INTEGER;
-- UPDATE venta
--   SET id_cliente = (SELECT c.idcliente FROM cliente c
--                     WHERE c.nombre = venta.cliente LIMIT 1)
--   WHERE id_cliente IS NULL
--     AND (SELECT COUNT(*) FROM cliente c WHERE c.nombre = venta.cliente) = 1;
--
-- venta.id_usuario:
--   Empleado que atendio la venta. Ya existe en esta base.
-- ALTER TABLE venta ADD COLUMN id_usuario INTEGER;
--
-- venta.estado:
--   'activa' / 'anulada'. Ya existe en esta base.
-- ALTER TABLE venta ADD COLUMN estado TEXT DEFAULT 'activa';


-- ------------------------------------------------------------
-- 6) SALA: columna "nombre"
-- ------------------------------------------------------------
-- En esta base los valores guardados son solamente "sala"/"SALA",
-- es decir, no aportan informacion: el sistema muestra la sala como
-- "Sala 2 — 3D" usando numero + tipo.
-- La columna NO se elimina porque es NOT NULL y la aplicacion
-- necesita escribir algo al crear una sala. El formulario ya no la
-- pide: se guarda automaticamente "Sala " + numero.
-- Si algun dia se quisiera quitar de verdad, el metodo para SQLite
-- es crear una tabla nueva, copiar los datos y renombrar:
--
-- CREATE TABLE sala_nueva (
--     idsala INTEGER PRIMARY KEY AUTOINCREMENT,
--     numero INTEGER NOT NULL UNIQUE,
--     tipo TEXT NOT NULL,
--     capacidad INTEGER NOT NULL,
--     estado TEXT NOT NULL DEFAULT 'ACTIVA'
-- );
-- INSERT INTO sala_nueva (idsala, numero, tipo, capacidad, estado)
--     SELECT idsala, numero, tipo, capacidad, estado FROM sala;
-- DROP TABLE sala;
-- ALTER TABLE sala_nueva RENAME TO sala;
--
-- No se ejecuta aqui porque hay funciones que apuntan a sala:
-- habria que revisar esa relacion antes. Se deja documentado.
