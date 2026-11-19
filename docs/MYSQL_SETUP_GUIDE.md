# MySQL Database Configuration Guide

## Dialect
`org.hibernate.dialect.MySQLDialect`

## HikariCP Pool Sizing
- maximumPoolSize: 10
- minimumIdle: 2
- connectionTimeout: 30000ms
- idleTimeout: 600000ms

## JDBC URL Parameters
`createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`

## Hibernate DDL Auto
Set to `update` for automated schema creation and table evolution.

## Collation
Character set: `utf8mb4`, Collation: `utf8mb4_unicode_ci` for international product names.

## Environment Variables
- `DB_URL`: JDBC URL override
- `DB_USERNAME`: Username (default root)
- `DB_PASSWORD`: Password (default root)
