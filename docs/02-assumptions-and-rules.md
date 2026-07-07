# Assunzioni e regole ufficiali

## Perimetro del generatore
- solo procedure standalone
- no package
- no cursor
- no collection
- no object type Oracle complessi

## Regole DB
- owner/schema = primi 3 caratteri del modulo
- tabella target:
```sql
select substr(text,8,length(text)-20) as tabella
from ALL_SOURCE
where owner = 'VPO_CRUD'
  and name = '<MODULO>'
  and text like 'TYPE%'
order by line
```
- nullability colonna da `ALL_TAB_COLUMNS`
- privilegi disponibili su `ALL_PROCEDURES` e `ALL_ARGUMENTS`

## Regole CRUD
- `IO-PARAMETERS` sempre standard
- function code supportati:
  - `SR`
  - `UR`
  - `IR`
  - `DR`
- normalizzazione base:
  - numerici `NOT NULL` -> `0`
  - altri -> `null`

## Return code standard
Usare la tabella completa comune a tutte le CRUD (vedi `docs/03-return-codes.md`).
