# MyPocket

![logo](logo.png)

## Tato aplikace slouží ke správě osobních financí a umožňuje uživatelům:
- Sledovat své příjmy a výdaje.
- Vytvářet účty s možností přidávat další uživatele.
- Spravovat rozpočet každého účtu.
- Používat podporované měny: CZK, EUR, a USD.
- Uživatelé mohou přidávat transakce a sledovat finanční výsledky.
## Funkce pro administrátora:
- Přístup ke všem CRUD operacím (vytvoření, čtení, úprava a mazání).
## Prémiový účet:
- Majitelé prémiových účtů si mohou generovat reporty za zvolené časové období.

## Databáze
Aplikace používá databázi H2, která je dostupná na adrese:
http://localhost:8080/h2-console po spuštění aplikace.


## Instalace a spuštění
### Postup:
- Otevřete projekt v nástroji IntelliJ IDEA.
- Spusťte aplikaci – server se automaticky spustí.

## Testovací data
### Přidání testovacích dat:
- Testovací data lze nahrát prostřednictvím konzole H2 databáze.
- Všechny potřebné SQL příkazy jsou k dispozici v souboru:
resources/db.txt

## Testování aplikace
### Scénáře v Postmanu:
Pro testování lze využít nástroj Postman.
Importujte soubor SCENARIES.json, který najdete v projektu.
Po importu spusťte připravené scénáře ve složce "Scenarios", abyste ověřili funkčnost aplikace
