#!/bin/bash
# Checks dependency versions

set -e

echo "📦 Checking dependencies..."
echo ""

if [ -f "pom.xml" ]; then
    echo "Checking Maven dependencies..."
    
    # Check Spring Boot version
    SPRING_VERSION=$(grep -oP '<parent>.*?<version>\K[^<]+' pom.xml | head -1)
    echo "Spring Boot version: $SPRING_VERSION"
    
    # Check if supported version (3.x)
    if [[ $SPRING_VERSION == 3.* ]]; then
        echo "✅ Spring Boot 3.x detected (supported)"
    else
        echo "⚠️  Spring Boot $SPRING_VERSION - consider upgrade to 3.x"
    fi
fi

if [ -f "package.json" ]; then
    echo "Checking npm dependencies..."
    
    if command -v npm &> /dev/null; then
        npm outdated || true
        echo "✅ npm check completed"
    fi
fi

echo ""
echo "✅ Dependency check completed"
