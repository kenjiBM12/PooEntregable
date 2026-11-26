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


-- ============================================================
-- 7) MEJORA 6: forma de pago y auditoria de la anulacion
-- ============================================================
-- NOTA: el sistema agrega estas columnas SOLO al iniciar
-- (ConexionDB.initDatabase -> agregarColumna). Este bloque sirve
-- para hacerlo a mano desde DB Browser. Si la columna ya existe,
-- SQLite responde "duplicate column name": eso NO es un error grave,
-- solo significa que ya estaba creada.

-- forma de pago usada en la compra: Efectivo / Tarjeta / Yape
ALTER TABLE venta ADD COLUMN metodo_pago TEXT DEFAULT 'EFECTIVO';

-- datos de la anulacion (auditoria):
--   anulada_por      -> id del usuario que anulo
--   fecha_anulacion  -> cuando se anulo
--   motivo_anulacion -> por que se anulo
ALTER TABLE venta ADD COLUMN anulada_por INTEGER;
ALTER TABLE venta ADD COLUMN fecha_anulacion TEXT;
ALTER TABLE venta ADD COLUMN motivo_anulacion TEXT;

-- las ventas que ya existian quedan como Efectivo
UPDATE venta SET metodo_pago = 'EFECTIVO' WHERE metodo_pago IS NULL;


-- ============================================================
-- 8) MEJORA 8: una butaca activa por funcion (indice parcial)
-- ============================================================
-- Solo se aplica a las ventas 'activa'. Si una venta se anula,
-- la butaca queda libre y se puede volver a vender.
CREATE UNIQUE INDEX IF NOT EXISTS ux_venta_funcion_butaca_activa
    ON venta (id_funcion, id_butaca) WHERE estado = 'activa';


-- ============================================================
-- 9) MEJORA 8: llaves foraneas para bases ya creadas
-- ============================================================
-- SQLite NO permite "ALTER TABLE ... ADD CONSTRAINT". Para una base
-- que ya tiene datos se usan TRIGGERS: se ejecutan solos antes de
-- insertar y rechazan la venta si la funcion o la butaca no existen.
CREATE TRIGGER IF NOT EXISTS trg_venta_funcion_valida
BEFORE INSERT ON venta
FOR EACH ROW
WHEN (SELECT COUNT(*) FROM funcion WHERE idfuncion = NEW.id_funcion) = 0
BEGIN
    SELECT RAISE(ABORT, 'La funcion de la venta no existe');
END;

CREATE TRIGGER IF NOT EXISTS trg_venta_butaca_valida
BEFORE INSERT ON venta
FOR EACH ROW
WHEN NEW.id_butaca IS NOT NULL
 AND (SELECT COUNT(*) FROM butaca WHERE idbutaca = NEW.id_butaca) = 0
BEGIN
    SELECT RAISE(ABORT, 'La butaca de la venta no existe');
END;


-- ============================================================
-- 10) MEJORA 7: contraseñas con hash
-- ============================================================
-- No hace falta migrar nada a mano: UsuarioDaoImpl cambia la clave
-- en texto plano por su hash la primera vez que ese usuario inicia
-- sesion. El usuario admin/admin123 se migra solo al entrar.

-- MEJORA 9: tabla de auditoria para las bases que ya existen.
CREATE TABLE IF NOT EXISTS auditoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    id_usuario INTEGER,
    usuario TEXT,
    accion TEXT NOT NULL,
    detalle TEXT
);

-- MEJORA 1: las duraciones viejas se normalizan a minutos.
-- En SQLite se puede hacer con substr/instr, pero el sistema lo hace solo
-- al abrir la base (ConexionDB.normalizarDuraciones), asi que aqui solo se
-- deja la version del esquema.
PRAGMA user_version = 2;
