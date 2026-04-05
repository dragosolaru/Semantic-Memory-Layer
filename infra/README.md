# Infrastructure

Deployment and infrastructure configuration.

## Structure

```
infra/
├── docker/     # Docker Compose, Dockerfiles
├── k8s/        # Kubernetes manifests
└── terraform/  # Terraform IaC
```

## Docker

- `docker-compose.yml` - Local dev environment (PostgreSQL, Redis, backend)
- `Dockerfile.backend` - Backend container
- `Dockerfile.web` - Web app container
- `Dockerfile.mobile` - Not needed for mobile (built via CI)

## Kubernetes (Production)

- Deployments for backend and web
- Services for internal communication
- Ingress for external access
- ConfigMaps and Secrets for configuration
- HorizontalPodAutoscaler for scaling

## Terraform

- Cloud infrastructure provisioning (AWS/GCP)
- PostgreSQL managed database
- Redis managed cache
- Container registry
- DNS and SSL certificates

## Conventions

- Docker Compose for local dev only
- Kubernetes for staging and production
- Terraform for infrastructure, not application config
- Never commit secrets — use sealed secrets or vault
