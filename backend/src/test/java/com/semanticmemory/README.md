# Backend Tests

Unit and integration tests.

## Structure

Mirror the main source structure:
- `service/SearchServiceTest.java`
- `controller/AuthControllerTest.java`
- `repository/AssetRepositoryTest.java`
- `pipeline/IngestionPipelineTest.java`

## Conventions

- Unit tests: `*Test.java`
- Integration tests: `*IT.java`
- Use `@DataJpaTest` for repository tests
- Use `@WebMvcTest` for controller tests
- Use `@SpringBootTest` for full integration tests
- Testcontainers for database integration tests

## Commands

```bash
./mvnw test                    # Run all tests
./mvnw test -Dtest=SearchServiceTest  # Run specific test
./mvnw verify                  # Run tests + integration tests
```
