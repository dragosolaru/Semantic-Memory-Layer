Maven Wrapper

To install Maven and run the backend:

```bash
# Install Maven (macOS)
brew install maven

# Or use SDKMAN
curl -s "https://get.sdkman.io" | bash
sdk install maven

# Then run backend
cd backend
mvn spring-boot:run
```