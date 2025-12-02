# Payments Service with OAuth 2.0

Secure payments microservice with OAuth 2.0 JWT authentication.

## 🔐 Features

- ✅ OAuth 2.0 authentication with JWT validation
- ✅ Automatic JWT signature, issuer, audience, and expiration validation
- ✅ Scope-based authorization
- ✅ Stateless (no HTTP sessions)
- ✅ Spring Security 6.x
- ✅ Unit and integration tests

## 📋 Prerequisites

- Java 21+
- Maven 3.8+
- OAuth 2.0 provider (Azure Entra ID / Okta / Auth0 / Keycloak)

## 🚀 Configuration

### 1. Environment Variables
```bash
export TENANT_ID="your-tenant-id"
export OAUTH_CLIENT_ID="api://payments-service"
export OAUTH_ISSUER_URI="https://your-oauth-provider.com/oauth2/v2.0"
export OAUTH_JWKS_URI="https://your-oauth-provider.com/oauth2/v2.0/keys"
```

### 2. Run Locally
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. Testing
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# With coverage
mvn test jacoco:report
```

## 📡 Endpoints

### GET /api/payments/balance

Get account balance.

**Required scope:** `api://payments-service/read`

**Request:**
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/payments/balance
```

**Response:**
```json
{
  "userId": "user123",
  "userName": "John Doe",
  "accountNumber": "4532-1234-5678-9012",
  "balance": 15000.50,
  "currency": "USD",
  "lastUpdate": "2025-01-15T10:30:00Z"
}
```

### POST /api/payments/transfer

Execute money transfer.

**Required scope:** `api://payments-service/write`

**Request:**
```bash
curl -X POST \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccount": "4532-1234-5678-9012",
    "toAccount": "4532-9876-5432-1098",
    "amount": 100.00,
    "currency": "USD",
    "description": "Payment"
  }' \
  http://localhost:8080/api/payments/transfer
```

**Response:**
```json
{
  "transactionId": "TXN-1705318200000",
  "status": "COMPLETED",
  "fromAccount": "4532-1234-5678-9012",
  "toAccount": "4532-9876-5432-1098",
  "amount": 100.00,
  "currency": "USD",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

## 🔒 Security

### JWT Validations

The service automatically validates:
- ✅ **Signature**: Using JWKS from OAuth provider
- ✅ **Issuer**: Configured issuer URI
- ✅ **Audience**: `api://payments-service`
- ✅ **Expiration**: Token not expired
- ✅ **Scopes**: Based on endpoint

### Get Test Token

Using OAuth provider CLI or Postman:
```bash
# Example with Azure CLI
az login
az account get-access-token \
  --resource api://payments-service \
  --query accessToken -o tsv

# Example with Okta
curl -X POST https://your-domain.okta.com/oauth2/v1/token \
  -d "grant_type=client_credentials" \
  -d "client_id=YOUR_CLIENT_ID" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "scope=api://payments-service/.default"
```

## 🏗️ Architecture
```
Client (Web/Mobile/API)
    ↓
OAuth Provider (Authentication)
    ↓
JWT Token
    ↓
Payments Service (Automatic validation)
    ↓
Business Logic
```

## 📚 Tech Stack

- **Framework**: Spring Boot 3.2.x
- **Security**: Spring Security 6.x
- **OAuth**: Spring OAuth2 Resource Server
- **Java**: 21
- **Build**: Maven

## 🧪 Testing
```bash
# Run all tests
mvn clean test

# Run specific test
mvn test -Dtest=PaymentsControllerTest

# Generate coverage report
mvn clean test jacoco:report
# Report in: target/site/jacoco/index.html
```

## 📖 Documentation

- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [JWT RFC 7519](https://tools.ietf.org/html/rfc7519)
- [Spring Security OAuth](https://spring.io/projects/spring-security-oauth)

## 👥 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

MIT License - see LICENSE file for details
