# Connessione DB

La connessione è strutturata per lavorare con:
- `DB_USER`
- `DB_PASSWORD`
- `DB_TNS`
- `DB_CONN_NAME`
- opzionale `TNS_ADMIN`

## Python direct
```python
oracledb.connect(user=db_user, password=db_password, dsn=db_tns)
```

## SQLcl-compatible
Pattern compatibile con script PowerShell:
```powershell
& $sqlcl "$dbUser/$dbPwd@$dbTns" "@$sqlTmp"
```

## Java JDBC
```java
jdbc:oracle:thin:@<DB_TNS>
```
