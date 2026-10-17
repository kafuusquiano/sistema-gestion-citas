-- =====================================================================
-- V8 - Elimina las tablas de laboratorio.
--
-- El documento AVANCE 02 redujo el alcance del proyecto a admision y
-- programacion de citas: sus cinco objetivos especificos cubren pacientes,
-- citas, reprogramacion, seguimiento y base de datos en la nube. Historial
-- clinico y laboratorio quedaron fuera.
--
-- Va en una migracion nueva y no modificando V6 porque V6 ya esta aplicada y
-- Flyway guarda su checksum: editarla haria fallar el arranque con
-- "Migration checksum mismatch" (instruccion M3).
--
-- El orden de los DROP respeta las claves ajenas: primero las tablas que
-- apuntan a otras.
-- =====================================================================

DROP TABLE IF EXISTS ordenes_examenes;
DROP TABLE IF EXISTS ordenes_laboratorio;
DROP TABLE IF EXISTS rangos_referencia;
DROP TABLE IF EXISTS examenes_catalogo;
