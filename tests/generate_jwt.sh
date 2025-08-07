#!/bin/bash

# JWT Configuration (same as in the Python version)
SECRET_KEY="acme-risk-analysis-secret-key-for-internal-communication"
ALGORITHM="HS256"

# Check if required commands are available
check_requirements() {
    if ! command -v jq &> /dev/null; then
        echo "Error: 'jq' is required but not installed." >&2
        echo "Please install it using one of the following commands:" >&2
        echo "  - Ubuntu/Debian: sudo apt-get install jq" >&2
        echo "  - macOS: brew install jq" >&2
        echo "  - Windows (with Chocolatey): choco install jq" >&2
        exit 1
    fi

    if ! command -v openssl &> /dev/null; then
        echo "Error: 'openssl' is required but not installed." >&2
        echo "Please install OpenSSL on your system." >&2
        exit 1
    fi

    if ! command -v base64 &> /dev/null; then
        echo "Error: 'base64' command not found." >&2
        echo "This is typically part of the coreutils package." >&2
        exit 1
    fi
}

# Base64 URL encode
base64_url_encode() {
    local input="$1"
    # Remove all whitespace from the input
    input=$(echo -n "$input" | tr -d '[:space:]')
    # Encode to base64, remove padding, replace URL-unsafe characters, and remove newlines
    echo -n "$input" | base64 | tr -d '=' | tr '+/' '-_' | tr -d '\n'
    # Alternative for systems where base64 might behave differently
    # echo -n "$input" | openssl base64 -e -A | tr -d '=' | tr '+/' '-_' | tr -d '\n'
    # For debugging:
    # echo "Input to base64_url_encode (${#input} chars): $input" >&2
}

# Create JWT token
create_jwt() {
    # Get current timestamp and expiration (1 hour from now)
    local current_time=$(date +%s)
    local expiry_time=$((current_time + 3600))  # 1 hour in seconds

    # Create header as a single line JSON
    local header='{"alg":"HS256","typ":"JWT"}'
    
    # Create payload as a single line JSON
    local payload=$(jq -n -c --arg sub "service-communication" \
                              --arg iss "risk-analysis-service" \
                              --argjson iat "$current_time" \
                              --argjson exp "$expiry_time" \
                              --arg service "risk-analysis-service" \
                              '{sub: $sub, iss: $iss, iat: $iat, exp: $exp, service: $service}')

    # For debugging:
    # echo "Header: $header" >&2
    # echo "Payload: $payload" >&2

    # Encode header and payload
    local encoded_header=$(base64_url_encode "$header")
    local encoded_payload=$(base64_url_encode "$payload")
    
    # Create signature
    local signature_input="${encoded_header}.${encoded_payload}"
    local signature=$(echo -n "$signature_input" | openssl dgst -sha256 -hmac "$SECRET_KEY" -binary | base64_url_encode)
    
    # Combine to create JWT token
    local token="${encoded_header}.${encoded_payload}.${signature}"
    
    # Debug output
    echo "Generated JWT token:" >&2
    echo "Header:   ${encoded_header}" >&2
    echo "Payload:  ${encoded_payload}" >&2
    echo "Signature: ${signature}" >&2
    echo "Full token: ${token}" >&2
    
    # Verify the token has all three parts
    local num_parts=$(echo "$token" | tr -cd '.' | wc -c)
    if [ "$num_parts" -ne 2 ]; then
        echo "Error: Invalid JWT format - expected 2 dots but found $num_parts" >&2
        exit 1
    fi
    
    # Output the token without any extra whitespace or newlines
    echo -n "$token" | tr -d '[:space:]' | tr -d '\n'
    
    # For debugging:
    # echo "Header (encoded): $encoded_header" >&2
    # echo "Payload (encoded): $encoded_payload" >&2
    # echo "Signature: $signature" >&2
}

# Main execution
check_requirements
create_jwt
