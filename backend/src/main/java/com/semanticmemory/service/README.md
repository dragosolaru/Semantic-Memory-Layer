# Business Logic Services

Core business logic layer.

## Expected Services

- `AuthService.java` - Authentication, token management
- `UserService.java` - User profile, settings management
- `AssetService.java` - Asset CRUD, metadata management
- `SearchService.java` - Query parsing, hybrid retrieval, ranking
- `IndexingService.java` - Orchestrate ingestion pipeline
- `EmbeddingService.java` - Generate/manage embeddings
- `EntityExtractionService.java` - NER, entity management
- `ClassificationService.java` - Document type classification
- `OcrService.java` - OCR text extraction
- `SourceService.java` - Source lifecycle management

## Conventions

- Services contain business logic, not data access (that's repositories)
- Services call other services (e.g., IndexingService calls EmbeddingService)
- Use `@Transactional` where consistency matters
- Async methods use `@Async` with proper executor
