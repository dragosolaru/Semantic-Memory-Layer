# Enumerations

Shared enum types.

## Expected Enums

- `AssetType.java` - image, pdf, document, spreadsheet, other
- `AssetStatus.java` - indexed, pending, error, deleted
- `SourceType.java` - local_folder, icloud, google_drive, dropbox, email
- `SourceStatus.java` - active, paused, error
- `EntityType.java` - organization, person, location, product, event, date
- `CollectionType.java` - auto, manual
- `RelationshipType.java` - same_event, same_entity, same_location, temporal_proximity, semantic_similarity
- `SubscriptionTier.java` - free, personal, family, pro

## Conventions

- All enums are uppercase with underscores
- Include a description field if needed for UI display
