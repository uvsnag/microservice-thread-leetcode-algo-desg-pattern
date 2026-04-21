package com.example.learningservice.pattern.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * ===================================================================
 * BUILDER PATTERN
 * ===================================================================
 * Intent: Separate the construction of a complex object from its
 *         representation, allowing the same construction process to
 *         create different representations.
 *
 * When to use:
 *   - Objects with many optional parameters (avoids telescoping constructors)
 *   - Building HTTP requests, SQL queries, UI components
 *   - Immutable objects with many fields
 *   - Lombok @Builder does this automatically
 *
 * Key points for interview:
 *   - Solves "telescoping constructor" problem (10+ params in constructor)
 *   - Fluent API: method chaining with .field(value).field(value).build()
 *   - Can enforce required vs optional fields
 *   - Makes objects IMMUTABLE (all fields set in constructor via builder)
 *
 * This demo: Building an HTTP request object with many optional fields
 * ===================================================================
 */
public class BuilderDemo {

    // =====================================================
    // The complex object we want to build
    // =====================================================
    // This class is IMMUTABLE — all fields are final, set only via Builder
    public static class HttpRequest {
        private final String method;         // Required
        private final String url;            // Required
        private final List<String> headers;  // Optional
        private final String body;           // Optional
        private final int timeout;           // Optional (default 30s)
        private final boolean followRedirects; // Optional (default true)
        private final String contentType;    // Optional

        // PRIVATE constructor — can only be created via Builder
        private HttpRequest(Builder builder) {
            this.method = builder.method;
            this.url = builder.url;
            this.headers = List.copyOf(builder.headers);  // Immutable copy
            this.body = builder.body;
            this.timeout = builder.timeout;
            this.followRedirects = builder.followRedirects;
            this.contentType = builder.contentType;
        }

        // Only getters, no setters → IMMUTABLE
        public String getMethod() { return method; }
        public String getUrl() { return url; }
        public List<String> getHeaders() { return headers; }
        public String getBody() { return body; }
        public int getTimeout() { return timeout; }
        public boolean isFollowRedirects() { return followRedirects; }
        public String getContentType() { return contentType; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(method).append(" ").append(url).append("\n");
            if (contentType != null) sb.append("  Content-Type: ").append(contentType).append("\n");
            for (String h : headers) sb.append("  ").append(h).append("\n");
            sb.append("  Timeout: ").append(timeout).append("s\n");
            sb.append("  Follow Redirects: ").append(followRedirects).append("\n");
            if (body != null) sb.append("  Body: ").append(body).append("\n");
            return sb.toString();
        }

        // =====================================================
        // The Builder — inner static class
        // =====================================================
        public static class Builder {
            // Required fields
            private final String method;
            private final String url;

            // Optional fields with default values
            private List<String> headers = new ArrayList<>();
            private String body = null;
            private int timeout = 30;            // Default: 30 seconds
            private boolean followRedirects = true; // Default: true
            private String contentType = null;

            // Constructor takes REQUIRED fields only
            public Builder(String method, String url) {
                this.method = method;
                this.url = url;
            }

            // Each setter returns "this" for method chaining
            public Builder header(String header) {
                this.headers.add(header);
                return this;  // ← KEY: return "this" for chaining
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public Builder timeout(int seconds) {
                this.timeout = seconds;
                return this;
            }

            public Builder followRedirects(boolean follow) {
                this.followRedirects = follow;
                return this;
            }

            public Builder contentType(String contentType) {
                this.contentType = contentType;
                return this;
            }

            // build() creates the final immutable object
            public HttpRequest build() {
                // Can add validation here
                if (method == null || method.isBlank()) {
                    throw new IllegalStateException("HTTP method is required");
                }
                if (url == null || url.isBlank()) {
                    throw new IllegalStateException("URL is required");
                }
                return new HttpRequest(this);
            }
        }
    }

    /**
     * Demo showing builder in action
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Builder Pattern: HTTP Request Builder ===\n\n");

        // Simple GET request — only required fields
        HttpRequest getRequest = new HttpRequest.Builder("GET", "/api/users")
                .build();
        sb.append("--- Simple GET ---\n").append(getRequest).append("\n");

        // Complex POST request — many optional fields via fluent API
        HttpRequest postRequest = new HttpRequest.Builder("POST", "/api/users")
                .contentType("application/json")
                .header("Authorization: Bearer eyJhbGci...")
                .header("Accept: application/json")
                .header("X-Request-Id: abc-123")
                .body("{\"name\": \"John\", \"email\": \"john@example.com\"}")
                .timeout(10)
                .followRedirects(false)
                .build();
        sb.append("--- Complex POST ---\n").append(postRequest).append("\n");

        // DELETE with custom timeout
        HttpRequest deleteRequest = new HttpRequest.Builder("DELETE", "/api/users/42")
                .header("Authorization: Bearer token123")
                .timeout(5)
                .build();
        sb.append("--- DELETE with timeout ---\n").append(deleteRequest).append("\n");

        sb.append("--- Key Takeaway ---\n");
        sb.append("Without Builder: new HttpRequest(\"POST\", \"/api\", null, headers, body, 10, false, \"json\")\n");
        sb.append("With Builder: readable, self-documenting, impossible to mix up parameter order.\n");
        sb.append("The built object is IMMUTABLE — thread-safe by design.\n");

        return sb.toString();
    }
}
