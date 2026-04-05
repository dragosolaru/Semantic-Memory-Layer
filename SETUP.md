# Semantic Memory - Aplicații Funcionale

## Structura Proiectului

```
semantic-memory-layer/
├── mobile/           # React Native (iOS + Android)
├── web/              # Next.js (web app)
├── backend/          # Spring Boot (API)
├── shared/           # Tipuri comune
├── infra/           # Docker configs
└── scripts/         # Dev scripts
```

## Cum să Pornești

### 1. Backend (necesită Java 17+)

```bash
# Instalează Maven
brew install maven

# Pornește backend
cd backend
mvn spring-boot:run
```

Backend va porni pe `http://localhost:8080`

### 2. Web App (necesită Node.js 20+)

```bash
# Upgrade Node.js
nvm install 20
nvm use 20

# Pornește web
cd web
npm run dev
```

Web va porni pe `http://localhost:3000`

### 3. Mobile App

```bash
cd mobile
npm install
npm run ios        # iOS
npm run android    # Android
```

### Endpoint-uri API

| Method | Path | Description |
|-------|------|-------------|
| POST | /api/auth/register | Înregistrare |
| POST | /api/auth/login | Autentificare |
| POST | /api/search | Căutare |
| GET | /api/health | Health check |

### Baza de Date

Pentru backend, rulează PostgreSQL:
```bash
docker run -d -p 5432:5432 -e POSTGRES_DB=semanticmemory -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15
```

## Probleme Cunoscute

1. **Node.js 18** - Web necesită Node.js 20+. Folosește `nvm` pentru upgrade.
2. **Maven** - Instalează cu `brew install maven` pentru backend.
3. **CocoaPods** - Rulează `pod install` în folderul `mobile/ios`.

## Configurare Rapidă macOS

```bash
# Instalează dependențele
brew install maven
nvm install 20
nvm use 20

# Pornește totul
cd backend && mvn spring-boot:run &
cd web && npm run dev
```