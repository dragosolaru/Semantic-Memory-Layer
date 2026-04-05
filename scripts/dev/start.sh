#!/bin/bash
set -e

# Semantic Memory - Start Script

# Start Backend (Spring Boot)
start_backend() {
    echo "Starting Backend..."
    cd /Users/dragosolaru/Learn/AI/semantic-memory-layer/backend
    ./mvnw spring-boot:run
}

# Start Web (Next.js)
start_web() {
    echo "Starting Web App..."
    cd /Users/dragosolaru/Learn/AI/semantic-memory-layer/web
    npm run dev
}

# Start Mobile (React Native)
start_mobile() {
    echo "Starting Mobile App..."
    cd /Users/dragosolaru/Learn/AI/semantic-memory-layer/mobile
    npm run ios
}

case "${1:-all}" in
    backend) start_backend ;;
    web) start_web ;;
    mobile) start_mobile ;;
    all)
        start_backend &
        sleep 5
        start_web
        ;;
    *)
        echo "Usage: $0 {backend|web|mobile|all}"
        exit 1
        ;;
esac