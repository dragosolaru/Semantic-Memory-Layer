# Ingestion & AI Enrichment Pipeline

Async processing pipeline for indexing assets.

## Pipeline Stages

1. **MetadataReceiver** - Accept metadata from client
2. **TextExtractor** - Extract text from documents (fallback OCR)
3. **EmbeddingGenerator** - Generate vector embeddings
4. **EntityExtractor** - NER entity extraction
5. **DocumentClassifier** - Classify asset type
6. **IndexWriter** - Store everything in database

## Expected Classes

- `IngestionPipeline.java` - Orchestrator
- `MetadataReceiver.java` - Stage 1
- `TextExtractor.java` - Stage 2
- `EmbeddingGenerator.java` - Stage 3
- `EntityExtractor.java` - Stage 4
- `DocumentClassifier.java` - Stage 5
- `IndexWriter.java` - Stage 6
- `PipelineJob.java` - Job representation for queue

## Conventions

- Each stage is a separate class implementing a common interface
- Pipeline is async, uses Spring `@Async` or message queue
- Failed stages retry with backoff
- Job status tracked in Redis
