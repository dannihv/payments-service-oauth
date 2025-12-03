# OAuth 2.0 Troubleshooting Guide

Common issues and solutions when implementing OAuth 2.0.

## 🔴 Authentication Errors

### AADSTS50011: redirect_uri mismatch

**Full error:**
```
AADSTS50011: The redirect URI 'https://your-app.com/callback' 
specified in the request does not match the redirect URIs 
configured for the application.
```

**Cause:**
- Redirect URI in code doesn't match configuration in OAuth provider

**Solution:**
```bash
# Azure Portal
1. Go to Azure Portal → App registrations → Your app
2. Authentication → Redirect URIs
3. Add: https://your-app.com/callback
4. Save
5. Wait 5 minutes for propagation

# Okta
1. Okta Admin → Applications → Your app
2. General Settings → Edit
3. Sign-in redirect URIs → Add URI
4. Save

# Auth0
1. Auth0 Dashboard → Applications → Your app
2. Settings → Allowed Callback URLs
3. Add: https://your-app.com/callback
4. Save
```

---

### AADSTS65001: user consent required

**Full error:**
```
AADSTS65001: The user or administrator has not consented 
to use the application with ID 'xxx'.
```

**Cause:**
- User hasn't consented to requested scopes
- Admin consent required for sensitive scopes

**Solution:**
```bash
# Option A: Admin consent (recommended)
# Azure Portal → App registrations → Your app
# API permissions → Grant admin consent

# Option B: User consent flow
# Add to authorization URL: &prompt=consent
```

---

### AADSTS700016: invalid client secret

**Cause:**
- Client secret expired or incorrect

**Solution:**
```bash
# Generate new secret
1. OAuth Provider admin console
2. Navigate to your application
3. Credentials / Certificates & secrets
4. Create new client secret
5. Copy value IMMEDIATELY
6. Update in environment variables
```

---

## 🟡 Authorization Errors

### 403 Forbidden - Insufficient Scope

**Symptom:**
```http
HTTP/1.1 403 Forbidden
{
  "error": "insufficient_scope",
  "error_description": "The token does not have required scope"
}
```

**Cause:**
- Token missing required scope for endpoint

**Diagnosis:**
```bash
# Decode token and check scopes
jwt decode <token> | jq .scp

# Should include: ["api://payments-service/read"]
```

**Solution:**
```bash
# Request with correct scope
scope=api://payments-service/read api://payments-service/write
```

---

### 401 Unauthorized - Token Expired

**Symptom:**
```http
HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer error="invalid_token", 
                  error_description="The token expired"
```

**Cause:**
- Access token expired (typical duration: 60 min)

**Solution:**
```javascript
// Use refresh token to get new access token
const response = await fetch(tokenEndpoint, {
  method: 'POST',
  body: new URLSearchParams({
    grant_type: 'refresh_token',
    refresh_token: refreshToken,
    client_id: clientId,
    client_secret: clientSecret
  })
});

const { access_token } = await response.json();
```

---

## 🔵 JWT Validation Errors

### Invalid Signature

**Symptom:**
```
JWT signature validation failed
```

**Cause:**
- JWKS endpoint unreachable
- Key cache expired
- Token tampered

**Diagnosis:**
```bash
# Verify JWKS endpoint
curl https://your-oauth-provider.com/.well-known/jwks.json

# Should return list of public keys
```

**Solution:**
```java
// Configure retry policy for JWKS
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuerUri);
    // Add cache with TTL
    decoder.setJwtValidator(validator);
    return decoder;
}
```

---

### Invalid Audience

**Symptom:**
```
JWT audience validation failed. Expected: api://payments-service, 
Got: api://other-service
```

**Cause:**
- Token obtained for different API
- Wrong Client ID in configuration

**Solution:**
```yaml
# Verify application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          audiences: api://payments-service  # ← Check this
```

---

## 🟢 Network Errors

### Connection Timeout to OAuth Provider

**Symptom:**
```
java.net.ConnectException: Connection timed out
at https://login.microsoftonline.com/...
```

**Cause:**
- Firewall blocking outbound to OAuth provider
- Proxy not configured

**Solution:**
```bash
# Verify connectivity
curl -v https://login.microsoftonline.com

# If behind corporate proxy:
export HTTP_PROXY=http://proxy.company.com:8080
export HTTPS_PROXY=http://proxy.company.com:8080
```

---

## 🛠️ Debugging Tools

### 1. JWT.io

Decode and validate JWTs visually:
```
https://jwt.io/
```

Paste token and verify:
- Header (alg, kid)
- Payload (claims)
- Signature (online validation)

### 2. Postman OAuth 2.0
```
1. Authorization → Type: OAuth 2.0
2. Grant Type: Authorization Code
3. Configure OAuth provider URLs
4. Get New Access Token
```

### 3. CLI Tools
```bash
# Get token (Azure)
az account get-access-token \
  --resource api://payments-service

# Get token (Okta)
curl -X POST https://your-domain.okta.com/oauth2/v1/token \
  -d "grant_type=client_credentials" \
  -d "client_id=YOUR_CLIENT_ID" \
  -d "client_secret=YOUR_CLIENT_SECRET"
```

### 4. OpenSSL (verify certificates)
```bash
# Verify TLS certificate
openssl s_client -connect login.microsoftonline.com:443 -showcerts

# Check expiration
echo | openssl s_client -connect login.microsoftonline.com:443 2>/dev/null \
  | openssl x509 -noout -dates
```

---

## 📊 Debugging Checklist

When something doesn't work, check in order:

- [ ] Is token present in Authorization header?
- [ ] Does token have correct format? (Bearer [token])
- [ ] Is token not expired? (check `exp` claim)
- [ ] Does token have correct audience? (check `aud` claim)
- [ ] Does token have required scope? (check `scp` claim)
- [ ] Is signature valid? (verify with JWKS)
- [ ] Is issuer correct? (check `iss` claim)
- [ ] Is application validating correctly? (check logs)
- [ ] Does network allow connection? (firewall/proxy)

---

## 🆘 Getting Help

If you can't solve the problem after reviewing this guide:

**GitHub Issues:**
```
https://github.com/your-org/payments-service-oauth/issues
Response: Community-driven
```

**Stack Overflow:**
```
Tag: [oauth-2.0] [jwt] [your-framework]
```

**Issue Template:**
```markdown
**Problem:** [Brief description]

**Exact error:**
[Copy complete error message]

**What I tried:**
1. [Action 1]
2. [Action 2]

**Environment:**
- Environment: [DEV/STAGING/PROD]
- OAuth Provider: [Azure/Okta/Auth0]
- Framework: [Spring Boot/Express/Flask]
- Client ID: [xxx...xxx]
- Error timestamp: [date time]

**Relevant logs:**
[Paste logs]
```

---

**Next update:** Based on feedback  
**Contributions:** Pull requests welcome
