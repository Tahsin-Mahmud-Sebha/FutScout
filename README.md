# ⚽ FutScout — Football Player Scout Database

CRUD app for scouting football players — Spring Boot 4.1.1 / Java 25, MongoDB,
and a self-contained Thymeleaf `index.html` (inline CSS/JS, no build step).

Structure mirrors your `Tahsin` project: capitalized `Model` / `DTO` / `Repository`
/ `Service` / `Controller` packages, a `Service` interface + `...Impl`, Lombok
everywhere, `GlobalExceptionHandler` inside `Service`, Micrometer counters wired
into the controller/service, Maven wrapper, and env-var-driven config.

## Project structure

```
FutScout/
├── mvnw, mvnw.cmd, .mvn/            <- Maven wrapper (no local Maven install needed)
├── pom.xml
├── src/main/java/com/goat/futscout/
│   ├── FutScoutApplication.java
│   ├── Model/Player.java             <- MongoDB document (Lombok)
│   ├── DTO/PlayerDTO.java            <- validated request/response shape
│   ├── Repository/PlayerRepository.java
│   ├── Service/PlayerService.java        <- interface
│   ├── Service/PlayerServiceImpl.java    <- implementation
│   ├── Service/GlobalExceptionHandler.java
│   └── Controller/PlayerController.java
├── src/main/resources/
│   ├── application.properties
│   ├── static/                       <- empty (kept for parity)
│   └── templates/index.html          <- the whole UI, one file
└── src/test/java/com/goat/futscout/FutScoutApplicationTests.java
```

## 1. Prerequisites

- **JDK 25** (matches `java.version` in `pom.xml` — the reference project pins this
  too; drop it to 17 or 21 in `pom.xml` if you'd rather use an LTS JDK you already have)
- **MongoDB** — local install, Docker (`docker run -d -p 27017:27017 mongo`), or
  a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) cluster
- No local Maven needed — use the bundled `./mvnw` wrapper

## 2. Open in IntelliJ IDEA

1. Unzip the project.
2. `File → Open...` → select the `FutScout` folder. IntelliJ detects `pom.xml`
   and imports dependencies automatically.
3. Set Project SDK to Java 25 (or lower `java.version` in `pom.xml` to match
   whatever JDK you have — Java 17+ is all Spring Boot 4 actually requires).

## 3. Configure MongoDB

`application.properties` reads the connection string from an environment variable:

```properties
spring.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/futscout}
```

- **Local MongoDB:** nothing to do — it falls back to `localhost:27017/futscout`
  automatically if `MONGODB_URI` isn't set.
- **MongoDB Atlas / a remote instance:** set the `MONGODB_URI` environment
  variable (in IntelliJ: Run Configuration → Environment variables), e.g.
  ```
  MONGODB_URI=mongodb+srv://<user>:<password>@<cluster-url>/futscout?retryWrites=true&w=majority
  ```

Note: Spring Boot 4 renamed `spring.data.mongodb.uri` to `spring.mongodb.uri` —
that's already reflected here.

## 4. Run it

**From IntelliJ:** open `FutScoutApplication.java` → click ▶.

**From the terminal:**
```bash
./mvnw spring-boot:run
```

Then open **http://localhost:8080** — `templates/index.html` is auto-served as
the welcome page (Spring Boot does this automatically for a template engine
project once Thymeleaf is on the classpath), no extra controller needed.

The database starts empty — add a few players from the form on the left to get going.

## 5. REST API reference

| Method | Endpoint                       | Description                                |
|--------|----------------------------------|---------------------------------------------|
| GET    | `/api/players`                  | List players (search/filter/sort params below) |
| GET    | `/api/players/{id}`             | Get one player                              |
| POST   | `/api/players`                  | Create a player                             |
| PUT    | `/api/players/{id}`             | Update a player                             |
| DELETE | `/api/players/{id}`             | Delete a player                             |
| GET    | `/api/players/meta/positions`   | Distinct positions (for the filter dropdown) |
| GET    | `/api/players/meta/clubs`       | Distinct clubs (for the filter dropdown)     |

**Query params for `GET /api/players`:**
- `search` — matches name (case-insensitive, partial)
- `position` — exact position filter (e.g. `ST`)
- `club` — exact club filter
- `sortBy` — `rating` (default), `goals`, `assists`, `age`, `marketValue`, `name`
- `order` — `asc` or `desc` (default `desc`)

**Sample POST body:**
```json
{
  "name": "Kylian Mbappe",
  "club": "Real Madrid",
  "position": "ST",
  "nationality": "France",
  "age": 27,
  "goals": 31,
  "assists": 8,
  "rating": 9.4,
  "marketValue": 180
}
```

## 6. Actuator

`management.endpoints.web.exposure.include=health,info,metrics` is set, so
`/actuator/health` and `/actuator/metrics` are available — including the custom
counters/timers wired in (`api.players.created.count`,
`service.players.updated.count`, `repository.player.search.time`).

## 7. Running tests

```bash
./mvnw test
```
`FutScoutApplicationTests` just checks the Spring context loads — it needs a
reachable MongoDB (local or via `MONGODB_URI`), same as the reference project.

## 8. Deployment

```bash
./mvnw clean package
java -jar target/futscout-0.0.1-SNAPSHOT.jar
```

Set `MONGODB_URI` (and optionally `PORT`, which Render/Railway/Heroku inject
automatically) as environment variables on whatever host you deploy to — nothing
in `application.properties` needs editing for production.

**Docker:**
```dockerfile
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/futscout-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```
```bash
./mvnw clean package
docker build -t futscout .
docker run -p 8080:8080 -e MONGODB_URI="mongodb+srv://..." futscout
```

Since the UI is served by this same Spring app (not a separately hosted static
site), there's no split-origin API URL to configure — unlike a setup where the
frontend is deployed to Netlify and the backend elsewhere.
