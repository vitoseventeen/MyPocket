# MyPocket

![logo](logo.png)

## <i> Aplikace pro správu osobních financí </i>

###  Stepaniuk Vitalii, Havrikov Bohdan

#### Checkpoint 1 (Předmět EAR)


## UML CLASS DIAGRAM




## Software Requirements Specification

#### 2. CELKOVÝ POPIS APLIKACE

#### 2.1 PERSPEKTIVA PRODUKTU

Aplikace pro správu osobních financí je navržena tak, aby poskytovala uživatelům přehled o jejich
finanční situaci a usnadnila sledování příjmů a výdajů. Díky tomu mohou uživatelé lépe kontrolovat
své finance, analyzovat své výdajové návyky a plánovat své rozpočty. Cílem aplikace je pomoci
uživatelům lépe hospodařit s penězi, identifikovat zbytečné výdaje a zlepšit jejich finanční
rozhodování.

Aplikace je určena pro jednotlivce, kteří chtějí mít snadný a přístupný přehled o svých financích.
Uživatelé budou moci vytvářet a spravovat různé typy transakcí (příjmy i výdaje), přiřazovat je ke
konkrétním kategoriím (např. potraviny, doprava, zábava), a sledovat své výdaje v rámci
stanoveného rozpočtu. Aplikace bude k dispozici prostřednictvím uživatelsky přívětivého rozhraní,
které usnadní správu osobních financí.


2.2 FUNKCE PRODUKTU

_Hlavní funkce aplikace zahrnují:_


- Registrace a autentizace uživatelů: Uživatelé se mohou registrovat, přihlásit a spravovat své
  účty. Prémioví uživatelé mohou vygenerovat reporty (popsané níž).
- Správa transakcí: Uživatelé mohou zadávat a spravovat své příjmy a výdaje. Každá transakce
  může být přiřazena konkrétní kategorii, což usnadňuje třídění a analýzu výdajů.
- Rozpočty pro kategorie: Uživatelé mohou vytvářet rozpočty pro jednotlivé kategorie výdajů.
  Aplikace umožňuje sledovat, jak se uživatelé přibližují ke svým limitům, což podporuje
  uvědomělé utrácení.
- Generování finančních reportů: Prémioví uživatelé mohou generovat reporty podle kategorií,
  období nebo konkrétních typů transakcí, což jim umožňuje podrobně analyzovat jejich
  finanční situaci.
- Podpora různých měn: Aplikace umožňuje spravovat finance v různých měnách, což je
  vhodné pro uživatele s mezinárodními financemi.
- Export dat: Možnost exportu finančních dat (např. ve formátu CSV) umožňuje uživatelům
  uchovávat a dále analyzovat své finanční informace mimo aplikaci.

2.3 CHARAKTERISTIKY UŽIVATELŮ A TŘÍDY

_Aplikace bude podporovat následující typy uživatelů:_


1. Běžní uživatelé
Běžní uživatelé mají základní účet bez předplatného.

Možnosti:

- Přidávání a správa vlastních transakcí.
- Prohlížení seznamu vlastních kategorií příjmů a výdajů.
- Sledování celkových příjmů a výdajů.

Omezení:

- Nemají přístup k funkcím pro generování reportů a analytických výstupů.

2. Prémioví uživatelé
Prémioví uživatelé mají předplatné, které jim umožňuje přístup k rozšířeným funkcím.

Možnosti:

- Veškeré funkce běžného uživatele.
- Generování detailních reportů o příjmech a výdajích za zvolené časové období.

2.4 PROVOZNÍ PROSTŘEDÍ

Aplikace bude fungovat na lokálním počítači nebo serveru s podporou Javy. Pro provoz bude využívat:

- Serverové prostředí: Aplikace může být nasazena na serverový kontejner, jako je Apache Tomcat, nebo v prostředí Docker.
- Databázový systém: Data budou ukládána v relační databázi, například PostgreSQL nebo MySQL.
- Prostředí pro vývoj: Aplikace je vytvořena v Javě a vyžaduje Java SE Runtime Environment (JRE) a Java Development Kit (JDK).

2.5 OMEZENÍ

- Aplikace nebude poskytovat přímé propojení s bankovními účty pro automatické načítání
  transakcí.
- Žádná podpora a analýza investic, akciových portfolií, kryptoměn nebo jiných finančních
  aktiv.
- Aplikace nebude obsahovat funkce pro správu daní a daňových odpočtů.
- Neumožňuje automatické aktualizace směnných kurzů v reálném čase.

2.6 PŘEDPOKLADY A ZÁVISLOSTI

Předpokládáme, že aplikace bude používána jednotlivci, kteří chtějí mít kontrolu nad svými osobními
financemi. Základní funkce jsou navrženy tak, aby byly přístupné širokému okruhu uživatelů.
Prémiové funkce jsou určené pro uživatele, kteří chtějí podrobnější analýzu svých financí.