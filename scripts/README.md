# OAuth Validation Scripts

Scripts to validate OAuth 2.0 configuration in applications.

## 🚀 Quick Start
```bash
# Install hooks
./scripts/install-hooks.sh

# Run validations manually
./scripts/validation/check-oauth-config.sh
./scripts/validation/check-jwt-validation.sh
./scripts/validation/check-secrets.sh
./scripts/validation/check-dependencies.sh
```

## 📋 Available Scripts

### check-oauth-config.sh

Validates OAuth configuration in application.yml/properties:
- ✅ Presence of issuer-uri, jwk-set-uri, audiences
- ✅ Use of environment variables (not hardcoded)
- ✅ Correct Maven dependencies

### check-jwt-validation.sh

Validates JWT implementation in code:
- ✅ JwtDecoder configured
- ✅ Audience validation
- ✅ Issuer validation
- ✅ OAuth2 Resource Server enabled

### check-secrets.sh

Detects hardcoded secrets:
- ❌ Client secrets
- ❌ API keys
- ❌ Passwords
- ❌ JWT tokens
- ❌ AWS keys

### check-dependencies.sh

Checks dependency versions:
- 📦 Spring Boot version
- 📦 Outdated OAuth libraries

## 🔄 CI/CD Integration

Scripts run automatically in GitHub Actions on every push/PR.

See: `.github/workflows/oauth-validation.yml`

## 🛠️ Local Usage

### Pre-commit (automatic)
```bash
# Runs on every commit
git commit -m "feat: add feature"
# [Running validations...]
```

### Manual
```bash
# Run all validations
for script in scripts/validation/*.sh; do
    bash "$script"
done
```

### Skip validations (emergencies)
```bash
git commit --no-verify -m "hotfix: urgent fix"
```

## 📊 Exit Codes

- `0` - All validations passed
- `1` - At least one validation failed

## 🆘 Troubleshooting

### Error: "Permission denied"
```bash
chmod +x scripts/validation/*.sh
```

### Error: "Command not found"
```bash
# Verify scripts are in correct path
ls -la scripts/validation/
```

## 📚 References

- [GitHub Actions Docs](https://docs.github.com/actions)
- [Git Hooks](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks)
- [OAuth Guide](../docs/oauth2/README.md)
