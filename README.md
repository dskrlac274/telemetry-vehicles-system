# 🚗 Telemetry System for Vehicles  
### Advanced Vehicle Tracking and Speed Analysis  

## 📌 Overview  

This project is a scalable **telemetry system for vehicles**, designed to **track, analyze, and manage vehicle movements** while integrating various levels of complexity across three implementations. Each phase introduces new features, transitioning from a **basic multi-server system** to an **enterprise-level microservices architecture**.

## 📂 Project Structure  

The repository consists of three implementations, each representing an evolution in system design:  

1. **Multi-Server System** – Network-based architecture with simulated vehicle tracking.  
2. **RESTful Web Services** – Enhanced with RESTful APIs, database integration, and Docker support.  
3. **Microservices System** – Migrates services to a distributed **MicroProfile**-based architecture using **Helidon**.

---

## 📍 Implementation 1: Multi-Server Telemetry System  

**Description:**  
The first implementation establishes a **server-client architecture** that simulates the movement of vehicles in a city. The system reads data from CSV files, simulating telemetry data such as speed, battery status, and location.

### 🔧 Features:
- **Vehicle Simulation:** Reads telemetry data and sends it sequentially to the central system.
- **Speed Monitoring:** Detects vehicles exceeding speed limits near radar locations.
- **Multi-threaded Servers:** Handles multiple vehicle connections simultaneously.
- **Real-time Processing:** Simulates real-world driving conditions by adjusting delay intervals.

### 🔄 How It Works:
1. A **Central System** manages the communication between components.
2. A **Radar Server** registers and monitors vehicles.
3. A **Penalty Server** processes violations and logs them accordingly.

🚀 *This implementation forms the foundation for the next stages by ensuring smooth data transmission and vehicle tracking.*

---

## 📍 Implementation 2: RESTful Web Services & Docker Integration  

**Description:**  
The second implementation enhances the system by introducing **RESTful APIs, database storage, and containerization** using **Docker**.

### 🔧 New Features:
- **REST API Integration:** RESTful endpoints for managing vehicle data, radar registration, and penalties.
- **Database Support:** Vehicles and penalties are stored in a **relational database** (PostgreSQL, MSSQL).
- **Docker Support:** The system is now **containerized** and runs in a Docker-based environment.
- **Radar & Vehicle Management:** REST endpoints allow vehicles and radars to be dynamically registered and monitored.

### 🔄 Key Differences from the First Implementation:
✅ **State Persistence:** Unlike the previous version, all data is now stored in a **database** instead of being processed in memory.  
✅ **Improved Access:** REST APIs allow seamless integration with other systems and user interfaces.  
✅ **More Control:** Vehicles can now be started, stopped, and queried via API requests.  
✅ **Docker Deployment:** Services are now packaged and deployed using **Docker & Docker Compose**.

🔗 *This version bridges the gap between a simple multi-server system and a scalable, modern web service architecture.*

---

## 📍 Implementation 3: Microservices-Based Telemetry System  

**Description:**  
The third implementation transforms the system into a **fully distributed microservices-based architecture**, ensuring **scalability, flexibility, and modular deployment**. It replaces monolithic services with containerized **MicroProfile-based** services using **Helidon**.

### 🔧 New Features:
- **Microservices Architecture:** Services are now modular and independently deployable.
- **Helidon MicroProfile Integration:** RESTful services are migrated to a **lightweight, cloud-native** MicroProfile framework.
- **JPA + Criteria API:** The database access layer now **uses JPA with Criteria API**, replacing DAO/JDBC.
- **Event-Driven Messaging:** **JMS & WebSockets** enable real-time notifications for speed violations.
- **Monitoring Dashboard:** A **live WebSocket-based frontend** displays real-time penalty data.

### 🔄 Key Improvements Over the Previous Version:
✅ **Microservices Architecture:** Each service runs independently, reducing dependencies.  
✅ **Helidon + MicroProfile:** Uses **Helidon** for lightweight and fast microservices.  
✅ **JPA with Criteria API:** Eliminates raw SQL queries for a **type-safe**, dynamic query approach.  
✅ **Better Performance:** Asynchronous messaging and event-driven processing improve system responsiveness.  
✅ **Cloud-Ready Design:** Docker and MicroProfile Config enable **containerized deployment with flexible configuration**.

🌎 *This final implementation transforms the system into a scalable, future-proof platform for real-time vehicle telemetry management.*

---

## 📜 Conclusion  

This project demonstrates the **evolution of a telemetry system** from a **basic networked simulation** to an **enterprise-level microservices-based platform**. Each iteration introduces new architectural improvements, making the system more **scalable, flexible, and powerful**.

🔹 **Implementation 1:** Core system with network-based telemetry tracking.  
🔹 **Implementation 2:** RESTful services with persistent data storage and Docker integration.  
🔹 **Implementation 3:** **Microservices** with **Helidon, MicroProfile, and JPA Criteria API** for enhanced scalability.  

💡 *This project showcases how to architect and refine a real-world IoT telemetry system using industry best practices.*

---

## 🚀 Technologies Used  

🔹 **Languages:** Java, Jakarta EE  
🔹 **Frameworks:** **MicroProfile, Helidon, JPA, RESTful APIs**  
🔹 **Databases:** PostgreSQL, MSSQL, H2  
🔹 **Messaging & Communication:** JMS, WebSockets  
🔹 **Containerization:** Docker & Docker Compose  
🔹 **Web Services:** **Jakarta EE, MicroProfile, RESTful endpoints**  
