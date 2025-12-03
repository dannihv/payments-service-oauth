#!/bin/bash
# Validates JWT implementation in code

set -e

echo "🔍 Validating JWT implementation..."
echo ""

ERRORS=0

report_error() {
    echo "❌ ERROR: $1"
    ERRORS=$((ERRORS + 1))
}

report_success() {
    echo "✅ $1"
}

# Find security config files
SECURITY_FILES=$(find src/main/java -name "*SecurityConfig*.java" 2>/dev/null || true)

if [ -z "$SECURITY_FILES" ]; then
    report_error "No SecurityConfig.java found"
    exit 1
fi

for FILE in $SECURITY_FILES; do
    echo "Analyzing: $FILE"
    
    # Check JWT decoder
    if grep -q "JwtDecoder" "$FILE"; then
        report_success "JwtDecoder configured in $FILE"
    else
        report_error "JwtDecoder not found in $FILE"
    fi
    
    # Check audience validation
    if grep -q "audience" "$FILE" || grep -q "JwtClaimValidator" "$FILE"; then
        report_success "Audience validation implemented"
    else
        report_error "Missing audience validation in $FILE"
    fi
    
    # Check issuer validation
    if grep -q "issuer" "$FILE" || grep -q "JwtValidators" "$FILE"; then
        report_success "Issuer validation implemented"
    else
        report_error "Missing issuer validation in $FILE"
    fi
    
    # Check OAuth2 Resource Server
    if grep -q "oauth2ResourceServer" "$FILE"; then
        report_success "OAuth2 Resource Server configured"
    else
        report_error "Missing OAuth2 Resource Server configuration"
    fi
done

echo ""
echo "========================================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ JWT validation successful!"
    echo "========================================="
    exit 0
else
    echo "❌ Validation failed with $ERRORS error(s)"
    echo "========================================="
    exit 1
fi
