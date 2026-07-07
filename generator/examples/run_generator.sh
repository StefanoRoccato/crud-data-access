#!/usr/bin/env bash
set -euo pipefail
MODULE="VPO01100"
python generator/generate_crud_module.py   --module "$MODULE"   --db-user "$DB_USER"   --db-password "$DB_PASSWORD"   --db-tns "$DB_TNS"   --conn-name "$DB_CONN_NAME"   --table-source-owner VPO_CRUD   --mode sqlcl   --sqlcl-path sql   --out generated-modules
