#!/bin/bash

# Semantic Memory Layer - Run All Services
# Starts Backend, Web, and Mobile dev servers

set -e

echo "=========================================="
echo "Starting Semantic Memory Layer"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check for required tools
check_tool() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${YELLOW}Warning: $1 not found${NC}"
        return 1
    fi
    return 0
}

# Function to start backend
start_backend() {
    echo -e "${GREEN}[1/3] Starting Backend (Spring Boot)...${NC}"
    cd backend
    if [ ! -f "mvnw" ]; then
        mvn spring-boot:run &
    else
        ./mvnw spring-boot:run &
    fi
    cd ..
    echo "Backend starting on http://localhost:8080"
}

# Function to start web
start_web() {
    echo -e "${GREEN}[2/3] Starting Web (Next.js)...${NC}"
    cd web
    npm run dev &
    cd ..
    echo "Web starting on http://localhost:3000"
}

# Function to start mobile
start_mobile() {
    echo -e "${GREEN}[3/3] Starting Mobile (React Native)...${NC}"
    cd mobile
    npm run start &
    cd ..
    echo "Metro bundler starting on http://localhost:8081"
}

# Parse arguments
case "${1:-all}" in
    all)
        check_tool mvn || echo "Install Maven: brew install maven"
        check_tool npm || echo "Install Node.js"
        
        start_backend &
        BACKEND_PID=$!
        
        sleep 5
        
        start_web &
        WEB_PID=$!
        
        start_mobile &
        MOBILE_PID=$!
        
        echo ""
        echo "=========================================="
        echo -e "${GREEN}All services started!${NC}"
        echo "=========================================="
        echo "Backend:  http://localhost:8080"
        echo "Web:      http://localhost:3000"
        echo "Mobile:   http://localhost:8081"
        echo ""
        echo "Press Ctrl+C to stop all services"
        
        # Wait for any process to exit
        wait
        ;;
    backend)
        start_backend
        ;;
    web)
        start_web
        ;;
    mobile)
        start_mobile
        ;;
    stop)
        pkill -f "spring-boot:run" || true
        pkill -f "next dev" || true
        pkill -f "react-native start" || true
        pkill -f "metro" || true
        echo "All services stopped"
        ;;
    *)
        echo "Usage: $0 [all|backend|web|mobile|stop]"
        echo ""
        echo "  all      - Start all services (default)"
        echo "  backend  - Start only backend"
        echo "  web      - Start only web"
        echo "  mobile   - Start only mobile"
        echo "  stop     - Stop all services"
        exit 1
        ;;
esac