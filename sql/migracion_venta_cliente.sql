-- Migracion de la base existente C:/sqlite/db/gestionCine.db
-- Ejecutar UNA sola vez, antes de abrir el sistema con el codigo nuevo.
-- SQLite no permite "IF NOT EXISTS" en ALTER TABLE, por eso si la columna
-- ya existe el motor avisara "duplicate column name"; en ese caso no pasa nada.

-- 1) la venta guarda el id del cliente (referencia estable), no solo el nombre
ALTER TABLE venta ADD COLUMN id_cliente INTEGER;
-- (se deja sin REFERENCES porque SQLite no permite agregar una llave foranea
--  con ALTER TABLE; el dato se sigue controlando desde la aplicacion)

-- 2) se completan las ventas antiguas buscando el cliente por su nombre
UPDATE venta
SET id_cliente = (SELECT c.idcliente FROM cliente c WHERE c.nombre = venta.cliente LIMIT 1)
WHERE id_cliente IS NULL;

-- 3) una butaca no se puede repetir dentro de la misma sala
--    (asi una generacion incompleta se puede completar sin duplicar)
CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero
    ON butaca (id_sala, fila, numero);

-- 4) indice para buscar rapido las butacas ocupadas de una funcion
CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca
    ON venta (id_funcion, id_butaca);
