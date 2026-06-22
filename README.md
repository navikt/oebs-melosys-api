# OEBS-Melosys API

REST API for integrasjon mellom OEBS og Melosys gjennom Kafka-meldingsutveksling.

## Arkitektur
Se møtedokumentasjon under [dokumentasjon](#dokumentasjon) for mer informasjon om arkitektur og design av denne tjenesten.

## Funkjsonalitet
Det flyter fakturaer fra Melosys til OEBS, og statusoppdateringer fra OEBS tilbake til Melosys. 
Dette muliggjør at fakturaer som produseres i Melosys kan sendes ut til brukere via OEBS, 
og at status på disse fakturaene kan oppdateres i Melosys basert på informasjon fra OEBS.
OeBS sender status på faktura tilbake til melosys. Dette gjøres en gang i døgnet. Feilede faktura opprettelser
sender feilmelding med en gang

OeBS sitt t1 miljø er koblet mot Melosys sitt Q1 miljø, og OeBS sitt Q1 miljø er koblet mot melosys sitt Q2 miljø
Dette kan ikke endres uten at det avtales med melosys, de bruker i hovedsak OeBS t1 til sin testing.

## Avhengigheter
- OeBS, oppretter faktura og sender ut til brukere basert på faturaer som produseres i Melosys
- Aiven Kafka, for meldingsutveksling mellom Melosys og OEBS(fakturaer og faktura status)
- Melosys-api sender fakturaer til Kafka topic som denne tjenesten konsumerer
- Quartz, for å kjøre batch job som sender status på fakturaer tilbake til Melosys en gang i døgnet
- GSA, for å kunne kjøre tjenesten lokalt og teste funksjonalitet uten å måtte deploye til et miljø

Konfig av OeBS sine kafka topics finnes [her](https://github.com/navikt/oebs-iac)
Konfig av Melosys sine kafka topics finnes [her](https://github.com/navikt/melosys-iac)
Kafka manager er satt opp og configurasjon av topics kan sees i denne [repoen](https://github.com/navikt/team-oebs-kafka-manager/tree/main)
Kafka manager trenger en oppdatering slik at den fungerer igjen etter at image ble flyttet til GHCR
Det er en fordel å ha kubernetes satt opp, men det meste kan sees og endres på gjennom nais console

## Hvordan kjøre lokalt
- Tjenesten kan per i dag ikke kjøres lokalt, men med GSA åpner det for muligheten, det som da må gjøres er å spinne opp
en lokal Kafka instans og konfigurere tjenesten til å bruke denne, og eventuelt mocke OeBS for å kunne teste funksjonalitet lokalt

## Testing
- Hvordan er funksjonalitet testet?
- Er det satt opp integrasjonstester, og hvordan kjøre disse?

## Overvåkning og alarmering
- Alarmering er satt opp i Melosys, men ikke for selve denne tjenesten. se TODO
- Overvåkning av denne tjenesten er ikke satt opp, men OeBS komponenten overvåkes av OeBS-drift se TODO

## Deploy
Per i dag er det egen branch for hvert miljø og deploy til prod skjer via main branch.
en leveranse skal gjennom alle branches før prod, men det er ikke satt opp en automatisk sjekk for dette

## Dokumentasjon
- Det finnes en del dokumentasjon i Confluence, spesielt møter og diskusjoner rundt oppsett av faktura
- [Melosys dokumentasjon](https://confluence.adeo.no/spaces/TEESSI/pages/431012462/Melosys+trygdeavgift) dette er melosys
  sin interndokumentasjon, men viser også deler av OeBS integrasjonen i denne figuren.
- [Melosys testidenter](https://confluence.adeo.no/spaces/TEESSI/pages/544324711/Testidenter+til+bruk+i+Melosys+mot+OeBS)
  ser ikke oppdatert ut, men er testidenter som Melosys har brukt.
- [Besluttningsmøte](https://confluence.adeo.no/spaces/TEESSI/pages/478487910/Beslutningsunderlag+samhandling+Melosys+og+OEBS)
  Møte hvor det ble besluttet at denne tjenesten skulle utvikles, og ikke bruke en eksisterende løsning via avgiftssystemet
- [Avklaring fakturafelter](https://confluence.adeo.no/spaces/TEESSI/pages/478274543/Samhandling+Melosys+og+OEBS+-informasjonsutveksling)
- [Designdokument](https://confluence.adeo.no/spaces/TEESSI/pages/505513949/2022-11-01+M%C3%B8tereferat+-+Melosys+og+OEBS)
  Merk at dette dokumentet er fra starten av prosjektet, og det har vært en del endringer i oppsett og funksjonalitet siden dette dokumentet ble skrevet
  I hovedsak gjelder dette navnene på topic, det er også et endepunkt i test for å produsere fakturameldinger, dette er ikke aktivt i produksjon

Det finnes en swagger, men den er ikke funksjonell per i dag, det vil kun være test endepunkt det er reelt for, kan nok fjernes
eventuelt oppdatere swagger(fjerne artifakter fra EyeShare sin swagger) og legge protection på endepunkt

## TODO
- [x] Endre props innlasting
- [ ] Oppdatere integrasjonstegning
- [ ] Legge til protection på test endepunkt, og eventuelt azure-token-generator
- [ ] Legge til tester
- [ ] Legge til overvåkning og alarmering(alarmer går hos melosys)
- [ ] Fjerne DLQ topic eller utvide denne til å håndtere feilede meldinger på en bedre måte(feilmeldinger så langt
  har ikke hatt nytte av DLQ da feilene har måtte håndteres manuelt av ØS)
- [ ] Sette opp automatisk sjekk for at leveranser går gjennom alle branches før prod
- [ ] Oppdatere Kafka manager slik at den fungerer igjen etter at image ble flyttet til GHCR

**Versjon**: 1.3.2 | **Java**: 17 | **Spring Boot**: 3.4.5
---
**Sist oppdatert**: 2. Mars 2026 | **Versjon**: 1.3.2
