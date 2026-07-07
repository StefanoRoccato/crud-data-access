$module = "VPO01100"
$dbUser = $env:DB_USER
$dbPwd = $env:DB_PASSWORD
$dbTns = $env:DB_TNS
$connName = $env:DB_CONN_NAME
$sqlcl = "sql"

Write-Host ""
Write-Host ">>> Esecuzione generatore metadata Oracle su $dbUser@$dbTns ($connName)..." -ForegroundColor Cyan
python generator/generate_crud_module.py `
  --module $module `
  --db-user $dbUser `
  --db-password $dbPwd `
  --db-tns $dbTns `
  --conn-name $connName `
  --table-source-owner VPO_CRUD `
  --mode sqlcl `
  --sqlcl-path $sqlcl `
  --out generated-modules
