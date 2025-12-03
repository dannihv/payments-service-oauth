#!/bin/bash
# Validates OAuth configuration in application files

set -e

echo "🔍 Validating OAuth configuration..."
echo ""

ERRORS=0

report_error() {
    echo "❌ ERROR: $1"
    ERRORS=$((ERRORS + 1))
}

report_success() {
    echo "✅ $1"
}

# Check application.yml exists
if [ -f "src/main/resources/application.yml" ]; then
    CONFIG_FILE="src/main/resources/application.yml"
    report_success "Config file found: $CONFIG_FILE"
elif [ -f "src/main/resources/application.properties" ]; then
    CONFIG_FILE="src/main/resources/application.properties"
    report_success "Config file found: $CONFIG_FILE"
else
    report_error "No application.yml or application.properties found"
    exit 1
fi

# Check OAuth configuration in YAML
if [ -f "src/main/resources/application.yml" ]; then
    # Check issuer-uri
    if grep -q "issuer-uri:" "$CONFIG_FILE"; then
        # Verify not hardcoded
        if grep "issuer-uri:" "$CONFIG_FILE" | grep -q "login.microsoftonline.com/[a-f0-9-]*/"; then
            report_error "Tenant ID hardcoded in issuer-uri. Use variable: \${TENANT_ID}"
        else
            report_success "issuer-uri configured with variable"
        fi
    else
        report_error "issuer-uri not found in configuration"
    fi
    
    # Check jwk-set-uri
    if grep -q "jwk-set-uri:" "$CONFIG_FILE"; then
        report_success "jwk-set-uri configured"
    else
        report_error "jwk-set-uri not found"
    fi
    
    # Check audiences
    if grep -q "audiences:" "$CONFIG_FILE"; then
        report_success "audiences configured"
    else
        report_error "audiences not found"
    fi
fi

# Check no hardcoded secrets
if grep -iE "(client[-_]?secret|password|api[-_]?key).*['\"].*[a-zA-Z0-9]{20,}" "$CONFIG_FILE"; then
    report_error "Possible hardcoded secret found in $CONFIG_FILE"
else
    report_success "No hardcoded secrets detected"
fi

# Check pom.xml (if Java)
if [ -f "pom.xml" ]; then
    if grep -q "spring-boot-starter-oauth2-resource-server" pom.xml; then
        report_success "OAuth2 Resource Server dependency present"
    else
        report_error "Missing: spring-boot-starter-oauth2-resource-server"
    fi
    
    if grep -q "spring-boot-starter-security" pom.xml; then
        report_success "Spring Security dependency present"
    else
        report_error "Missing: spring-boot-starter-security"
    fi
fi

echo ""
echo "========================================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ OAuth validation successful!"
    echo "========================================="
    exit 0
else
    echo "❌ Validation failed with $ERRORS error(s)"
    echo "========================================="
    exit 1
fi
