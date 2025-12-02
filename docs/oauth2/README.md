# OAuth 2.0 Implementation Guide

## 📋 Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Implementation by Stack](#implementation-by-stack)
5. [Token Validation](#token-validation)
6. [Troubleshooting](#troubleshooting)
7. [Best Practices](#best-practices)

## 🎯 Introduction

This guide helps you integrate OAuth 2.0 JWT authentication in your application.

**Estimated time:** 30-45 minutes

## ✅ Prerequisites

- [ ] Access to OAuth provider (Azure / Okta / Auth0 / Keycloak)
- [ ] Java 21+ / Node.js 18+ / Python 3.10+ (depending on your stack)
- [ ] Basic OAuth 2.0 knowledge
- [ ] API client (Postman, curl, etc.)

## 🚀 Quick Start

### Step 1: Register Application

**Azure Entra ID:**
```bash
1. Azure Portal → Azure Active Directory
2. App registrations → New registration
3. Name: "Your App Name"
4. Redirect URI: https://your-app.com/callback
5. Click "Register"
6. Note: Client ID and Tenant ID
```

**Okta:**
```bash
1. Okta Admin Console → Applications
2. Create App Integration → API Services
3. Name: "Your App Name"
4. Note: Client ID and Client Secret
```

**Auth0:**
```bash
1. Auth0 Dashboard → Applications
2. Create Application → Machine to Machine
3. Name: "Your App Name"
4. Select API
5. Note: Client ID, Client Secret, Domain
```

### Step 2: Configure Scopes

Define required scopes:
```
Scopes needed:
- api://payments-service/read  (Read access)
- api://payments-service/write (Write access)

Justification: [Your use case]
```

### Step 3: Implement in Your App

Choose your technology:
- [Java Spring Boot](../examples/java-spring-boot/)
- [Node.js / Express](../examples/nodejs-express/)
- [React SPA](../examples/react-spa/)
- [Python Flask](../examples/python-flask/)

## 📚 Implementation by Stack

### Java Spring Boot

See complete guide: [Java Spring Boot Guide](../examples/java-spring-boot/README.md)

**Quick Start:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```
```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-oauth-provider.com/oauth2/v2.0
```
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .oauth2ResourceServer().jwt();
        return http.build();
    }
}
```

### Node.js / Express

**Quick Start:**
```bash
npm install passport passport-azure-ad
```
```javascript
const BearerStrategy = require('passport-azure-ad').BearerStrategy;

const options = {
    identityMetadata: 'https://login.microsoftonline.com/{tenant}/v2.0/.well-known/openid-configuration',
    clientID: process.env.CLIENT_ID,
    audience: 'api://payments-service'
};

passport.use(new BearerStrategy(options, (token, done) => {
    return done(null, token);
}));

app.use('/api', passport.authenticate('oauth-bearer', { session: false }));
```

### React SPA

**Quick Start:**
```bash
npm install @azure/msal-react @azure/msal-browser
```
```javascript
import { PublicClientApplication } from "@azure/msal-browser";

const msalConfig = {
    auth: {
        clientId: "your-client-id",
        authority: "https://login.microsoftonline.com/{tenant}",
        redirectUri: "https://your-app.com"
    }
};

const msalInstance = new PublicClientApplication(msalConfig);

// In your component
const { instance } = useMsal();
await instance.loginPopup({
    scopes: ["api://payments-service/read"]
});
```

### Python Flask

**Quick Start:**
```bash
pip install flask-oidc
```
```python
from flask import Flask
from flask_oidc import OpenIDConnect

app = Flask(__name__)
app.config['OIDC_CLIENT_SECRETS'] = 'client_secrets.json'
oidc = OpenIDConnect(app)

@app.route('/api/protected')
@oidc.require_login
def protected():
    return jsonify({'message': 'Authenticated'})
```

## 🔍 Token Validation

### Required Validations

Your application MUST validate:

1. **Signature** - Token signed by OAuth provider
2. **Issuer** - Correct issuer URI
3. **Audience** - Your Client ID
4. **Expiration** - `exp` claim not passed
5. **Not Before** - `nbf` claim reached

### Manual Validation (Debugging)
```bash
# Decode JWT (no validation)
echo "eyJ0eXAiOiJKV1Q..." | cut -d'.' -f2 | base64 -d | jq .

# Verify signature with JWKS
curl https://your-oauth-provider.com/.well-known/jwks.json
```

## 🐛 Troubleshooting

See complete guide: [Troubleshooting Guide](troubleshooting/README.md)

### Common Errors

#### Error: 401 Unauthorized

**Possible causes:**
- Token expired
- Invalid token
- Missing Authorization header

**Solution:**
```bash
# Verify token
jwt decode <token>

# Get new token
curl -X POST https://oauth-provider.com/oauth2/token ...
```

#### Error: 403 Forbidden

**Cause:** Valid token but missing required scope

**Solution:**
```bash
# Verify scopes in token
jwt decode <token> | jq .scp

# Request correct scope
scope=api://payments-service/read
```

## ✨ Best Practices

### DO ✅

- Use HTTPS for all endpoints
- Validate signature, issuer, and audience
- Implement refresh token rotation
- Store secrets in environment variables or secret managers
- Log authentication events
- Implement rate limiting per client_id

### DON'T ❌

- Hardcode client secrets in code
- Use Basic Auth in production
- Trust tokens without validation
- Expose JWKS keys without cache
- Log complete tokens

## 📖 References

- [Architecture Details](architecture/README.md)
- [OAuth Flows](flows/README.md)
- [RFC 6749 - OAuth 2.0](https://tools.ietf.org/html/rfc6749)
- [RFC 7519 - JWT](https://tools.ietf.org/html/rfc7519)

## 🆘 Support

- **Issues:** [GitHub Issues](https://github.com/your-org/payments-service-oauth/issues)
- **Discussions:** [GitHub Discussions](https://github.com/your-org/payments-service-oauth/discussions)
- **Documentation:** [Wiki](https://github.com/your-org/payments-service-oauth/wiki)

---

**Last update:** January 2025  
**Version:** 1.0.0
