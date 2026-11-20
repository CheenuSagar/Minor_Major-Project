# NextStore Project Viva & Technical Interview Q&A

### Q1: What is NextStore and what retail problems does it solve?
NextStore is a modern Point of Sale (POS) and inventory management system designed for high-throughput retail stores. It solves slow checkout queues, manual stock tracking, payment reconciliation delays, and paper waste by introducing camera barcode scanning, dynamic UPI QR codes, instant WhatsApp receipts, and automated AI stock forecasting.

### Q2: Why choose Spring Boot 3 with Java 17?
Java 17 provides Long Term Support (LTS) with performance enhancements like sealed classes, pattern matching, and superior garbage collection. Spring Boot 3 provides native Jakarta EE 10 integration, production-ready observability with Actuator, and declarative transaction management.

### Q3: How is data consistency maintained during simultaneous checkout?
NextStore enforces `@Transactional` isolation at the service layer (`SaleServiceImpl`). When a sale is processed, the system atomically deducts product stock, logs stock movement audit rows, and generates the invoice record within a single database transaction. If any item is out of stock, the entire transaction rolls back cleanly.

### Q4: How is security handled?
Stateless JWT (JSON Web Tokens) with HMAC-SHA256 signing, BCrypt password hashing (strength 12), token blacklisting on logout, role-based access control (RBAC), and Bucket4j IP rate limiting (100 requests per minute).
