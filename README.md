# mag-userhub

## Tech Stack

- **Backend:** Java, Spring Boot, Maven
- **Frontend:** Vite
- **Database:** PostgreSQL

## Project Structure

```
mag-userhub/
├── backend/     # Spring Boot API
└── frontend/    # Vite-based client
```

## Getting Started

### Database

Create a `.env` file in the project root with the following variables:

```
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
```

Then start the database:

```bash
docker compose up --build
```

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:[8080]` by default.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:[5173]` by default.

## Environment Variables

Add environment variables to `application.yml` (backend):

```
# Backend
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
```

## Testing

### Backend

```bash
cd backend
./mvnw test
```
