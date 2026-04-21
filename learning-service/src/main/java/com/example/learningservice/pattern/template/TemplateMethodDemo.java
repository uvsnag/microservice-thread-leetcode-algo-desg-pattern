package com.example.learningservice.pattern.template;

import java.util.List;

/**
 * ===================================================================
 * TEMPLATE METHOD PATTERN
 * ===================================================================
 * Intent: Define the skeleton of an algorithm in a method, deferring
 *         some steps to subclasses. Subclasses can redefine certain
 *         steps without changing the algorithm's structure.
 *
 * When to use:
 *   - Common algorithm structure, but steps differ
 *   - Frameworks: Spring's JdbcTemplate, RestTemplate, AbstractController
 *   - Data processing pipelines (read → transform → validate → write)
 *   - Report generation (header → body → footer)
 *
 * Key points for interview:
 *   - Uses INHERITANCE — the "template" method is in the parent class
 *   - Concrete steps in parent, abstract steps deferred to children
 *   - Hollywood Principle: "Don't call us, we'll call you"
 *   - Java 8+ lambdas can replace some uses of Template Method
 *   - Spring uses this HEAVILY (JdbcTemplate, HibernateTemplate, etc.)
 *
 * This demo: Data export pipeline — same flow, different formats (CSV, JSON, XML)
 * ===================================================================
 */
public class TemplateMethodDemo {

    // =====================================================
    // STEP 1: Abstract class with the TEMPLATE METHOD
    // =====================================================
    public static abstract class DataExporter {
        private final StringBuilder log = new StringBuilder();

        /**
         * THE TEMPLATE METHOD — defines the algorithm skeleton
         * This method is FINAL — subclasses CANNOT change the steps or their order
         */
        public final String export(List<String[]> data) {
            log.setLength(0);

            log.append("1. Connecting to data source...\n");
            connect();                              // Common step (concrete)

            log.append("2. Validating data...\n");
            validate(data);                         // Common step (concrete)

            log.append("3. Writing header...\n");
            String header = writeHeader();          // ← ABSTRACT — differs by format
            log.append("   ").append(header).append("\n");

            log.append("4. Writing body rows...\n");
            String body = writeBody(data);          // ← ABSTRACT — differs by format
            log.append("   ").append(body.replace("\n", "\n   ")).append("\n");

            log.append("5. Writing footer...\n");
            String footer = writeFooter(data.size());  // ← ABSTRACT — differs by format
            log.append("   ").append(footer).append("\n");

            log.append("6. Disconnecting...\n");
            disconnect();                           // Common step (concrete)

            return log.toString();
        }

        // Concrete methods — same for all subclasses
        private void connect() { /* simulated connection */ }
        private void disconnect() { /* simulated disconnection */ }
        private void validate(List<String[]> data) {
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Data cannot be empty");
            }
        }

        // Abstract methods — MUST be implemented by each subclass
        protected abstract String writeHeader();
        protected abstract String writeBody(List<String[]> data);
        protected abstract String writeFooter(int rowCount);

        // Hook method — CAN be overridden (optional)
        protected String getFormatName() { return "Unknown"; }
    }

    // =====================================================
    // STEP 2: Concrete implementations — different formats
    // =====================================================

    /** CSV Exporter — writes comma-separated values */
    public static class CsvExporter extends DataExporter {
        @Override
        protected String writeHeader() {
            return "id,name,email";  // CSV header row
        }

        @Override
        protected String writeBody(List<String[]> data) {
            StringBuilder sb = new StringBuilder();
            for (String[] row : data) {
                sb.append(String.join(",", row)).append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        protected String writeFooter(int rowCount) {
            return "# Total rows: " + rowCount;
        }

        @Override
        protected String getFormatName() { return "CSV"; }
    }

    /** JSON Exporter — writes JSON array */
    public static class JsonExporter extends DataExporter {
        @Override
        protected String writeHeader() {
            return "{ \"data\": [";
        }

        @Override
        protected String writeBody(List<String[]> data) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                String[] row = data.get(i);
                sb.append(String.format("  {\"id\":\"%s\", \"name\":\"%s\", \"email\":\"%s\"}", row[0], row[1], row[2]));
                if (i < data.size() - 1) sb.append(",");
                sb.append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        protected String writeFooter(int rowCount) {
            return "], \"total\": " + rowCount + " }";
        }

        @Override
        protected String getFormatName() { return "JSON"; }
    }

    /** XML Exporter — writes XML document */
    public static class XmlExporter extends DataExporter {
        @Override
        protected String writeHeader() {
            return "<?xml version=\"1.0\"?>\n<users>";
        }

        @Override
        protected String writeBody(List<String[]> data) {
            StringBuilder sb = new StringBuilder();
            for (String[] row : data) {
                sb.append(String.format("  <user id=\"%s\" name=\"%s\" email=\"%s\"/>", row[0], row[1], row[2]));
                sb.append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        protected String writeFooter(int rowCount) {
            return "</users>\n<!-- Total: " + rowCount + " users -->";
        }

        @Override
        protected String getFormatName() { return "XML"; }
    }

    /**
     * Demo — same algorithm, different format outputs
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Template Method Pattern: Data Export Pipeline ===\n\n");

        // Sample data — same data for all exporters
        List<String[]> data = List.of(
                new String[]{"1", "Alice", "alice@example.com"},
                new String[]{"2", "Bob", "bob@example.com"},
                new String[]{"3", "Charlie", "charlie@example.com"}
        );

        // Same algorithm, different outputs
        DataExporter[] exporters = {
                new CsvExporter(),
                new JsonExporter(),
                new XmlExporter()
        };

        for (DataExporter exporter : exporters) {
            sb.append("--- ").append(exporter.getFormatName()).append(" Export ---\n");
            sb.append(exporter.export(data));
            sb.append("\n");
        }

        sb.append("--- Key Takeaway ---\n");
        sb.append("The algorithm steps (connect → validate → header → body → footer → disconnect) NEVER change.\n");
        sb.append("Only the format-specific parts differ — subclasses implement abstract methods.\n");
        sb.append("Spring's JdbcTemplate uses this: you provide SQL + RowMapper, Spring handles connection/cleanup.\n");

        return sb.toString();
    }
}
