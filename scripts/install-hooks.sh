#!/bin/bash
# Installs Git hooks

echo "📦 Installing Git hooks..."

# Configure Git to use .githooks directory
git config core.hooksPath .githooks

echo "✅ Git hooks installed successfully!"
echo ""
echo "Installed hooks:"
echo "  - pre-commit: Validates OAuth config and detects secrets"
echo ""
echo "To bypass hooks temporarily:"
echo "  git commit --no-verify"
