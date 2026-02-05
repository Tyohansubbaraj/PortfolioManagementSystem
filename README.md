# Portfolio Management System

A comprehensive full-stack web application for managing investment portfolios. Track assets, holdings, transactions, and build watchlists with real-time price updates.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Configuration](#configuration)
- [Development](#development)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Functionality
- **Asset Management**: Create, read, update, and delete investment assets (stocks, bonds, etc.)
- **Portfolio Holdings**: Track your current investment holdings with quantities and valuations
- **Transaction Tracking**: Record and manage buy/sell transactions for your assets
- **Watchlist**: Monitor assets you're interested in but haven't invested in yet
- **Price Updates**: Scheduled automatic price updates for assets
- **Charts & Analytics**: Visualize portfolio performance and trends with interactive charts

### Dashboard Features
- Overview of total portfolio value
- Asset allocation by type and sector
- Performance metrics and analytics
- Real-time price tracking

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.10
- **Language**: Java 17
- **Database**: MySQL 8
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Async Support**: Spring WebFlux

### Frontend
- **Framework**: React 18.2.0
- **HTTP Client**: Axios 1.6.0
- **Charting**: Recharts 2.10.0
- **Module Bundler**: Webpack 5
- **Transpiler**: Babel 7

### Development Tools
- **Testing**: Jest, Testing Library React
- **Styling**: CSS3
- **Hot Reload**: Webpack Dev Server

## 📁 Project Structure

```
PortfolioManagementSystem/
├── backend/                          # Frontend React application
│   └── [React components and assets]
│
├── src/
│   ├── main/
│   │   ├── java/com/marketminds/portfoliomanagementsystem/
│   │   │   ├── config/               # Spring configuration classes
│   │   │   ├── controller/           # REST API controllers
│   │   │   │   ├── AssetController.java
│   │   │   │   ├── HoldingController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── WatchlistController.java
│   │   │   │   └── ChartController.java
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── model/                # Entity models
│   │   │   │   ├── Asset.java
│   │   │   │   ├── Holding.java
│   │   │   │   ├── Transaction.java
│   │   │   │   └── Watchlist.java
│   │   │   ├── repository/           # JPA Repositories
│   │   │   ├── service/              # Business logic
│   │   │   └── scheduler/            # Scheduled tasks
│   │   │
│   │   ├── python/
│   │   │   ├── main.py               # Python utility scripts
│   │   │   └── requirements.txt
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-test.properties
│   │       └── dbscripts/
│   │           ├── schema.sql        # Database schema
│   │           └── portfolio_dump.sql
│   │
│   └── test/                         # Unit and integration tests
│       └── java/com/marketminds/portfoliomanagementsystem/
│
├── frontend/
│   ├── App.js                        # Main React component
│   ├── index.js                      # Entry point
│   ├── components/                   # React components
│   │   ├── Dashboard.js
│   │   ├── Assets.js
│   │   ├── Holdings.js
│   │   ├── Transactions.js
│   │   ├── Watchlist.js
│   │   └── ChartModal.js
│   ├── services/
│   │   └── api.js                    # Axios API client
│   ├── styles/
│   │   └── App.css
│   └── public/
│       └── index.html
│
├── pom.xml                           # Maven configuration
├── package.json                      # NPM dependencies
├── webpack.config.js                 # Webpack configuration
├── mvnw                              # Maven wrapper (Unix)
├── mvnw.cmd                          # Maven wrapper (Windows)
└── README.md                         # This file
```

## 📋 Prerequisites

- **Java**: JDK 17 or higher
- **Node.js**: v18.0.0 or higher
- **npm**: v9.0.0 or higher
- **MySQL**: Version 8.0 or higher
- **Maven**: 3.6.0 or higher (optional - use included mvnw)

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/marketminds/PortfolioManagementSystem.git
cd PortfolioManagementSystem
```

### 2. Database Setup

#### Create MySQL Database
```bash
mysql -u root -p
```

```sql
CREATE DATABASE portfoliodb;
CREATE DATABASE portfoliotest;
```

#### Run Database Scripts
```bash
# For production database
mysql -u root -p portfoliodb < src/main/resources/dbscripts/schema.sql
mysql -u root -p portfoliodb < src/main/resources/dbscripts/portfolio_dump.sql

# For test database
mysql -u root -p portfoliotest < src/main/resources/dbscripts/schema.sql
```

### 3. Configure Application Properties

Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfoliodb
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Install Backend Dependencies
```bash
# Using Maven wrapper (Windows)
mvnw clean install

# Or using Maven (if installed globally)
maven clean install
```

### 5. Install Frontend Dependencies
```bash
npm install
```

## ▶️ Running the Application

### Option 1: Run Backend and Frontend Separately

#### Start Backend (Spring Boot)
```bash
# Windows
mvnw spring-boot:run

# Unix/Linux/Mac
./mvnw spring-boot:run
```
Backend will start at `http://localhost:8080`

#### Start Frontend (React)
In a new terminal:
```bash
npm start
```
Frontend will start at `http://localhost:3000`

### Option 2: Build and Run JAR

#### Build the Project
```bash
mvnw clean package
```

#### Run the JAR
```bash
java -jar target/portfoliomanagementsystem-0.0.1-SNAPSHOT.jar
```

#### Build Frontend
```bash
npm run build
```

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api
```

### Asset Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/assets` | Create a new asset |
| `GET` | `/assets` | Get all assets |
| `GET` | `/assets/{id}` | Get asset by ID |
| `GET` | `/assets/symbol/{symbol}` | Get asset by stock symbol |
| `GET` | `/assets/type/{type}` | Get assets by type |
| `GET` | `/assets/sector/{sector}` | Get assets by sector |
| `PUT` | `/assets/{id}` | Update asset |
| `DELETE` | `/assets/{id}` | Delete asset |

### Holdings Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/holdings` | Create a new holding |
| `GET` | `/holdings` | Get all holdings |
| `GET` | `/holdings/{id}` | Get holding by ID |
| `GET` | `/holdings/summary` | Get holdings summary |
| `PUT` | `/holdings/{id}` | Update holding |
| `DELETE` | `/holdings/{id}` | Delete holding |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/transactions` | Create a new transaction |
| `GET` | `/transactions` | Get all transactions |
| `GET` | `/transactions/{id}` | Get transaction by ID |
| `GET` | `/transactions/asset/{assetId}` | Get transactions for asset |
| `DELETE` | `/transactions/{id}` | Delete transaction |

### Watchlist
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/watchlist` | Add to watchlist |
| `GET` | `/watchlist` | Get watchlist items |
| `DELETE` | `/watchlist/{id}` | Remove from watchlist |

### Charts & Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/charts/data` | Get chart data |
| `GET` | `/charts/types` | Get available chart types |

For detailed API documentation, visit the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## 🗄️ Database

### Database Schema
The application uses the following main tables:

#### Assets
Stores information about available assets (stocks, bonds, etc.)

#### Holdings
Tracks current investments - how many units of each asset you own

#### Transactions
Records all buy/sell transactions for portfolio management

#### Watchlist
Contains assets the user is monitoring but hasn't invested in

Database initialization scripts are provided in:
- `src/main/resources/dbscripts/schema.sql` - Table definitions
- `src/main/resources/dbscripts/portfolio_dump.sql` - Sample data

## ⚙️ Configuration

### Application Properties

#### Production (`application.properties`)
```properties
spring.application.name=portfoliomanagementsystem
spring.datasource.url=jdbc:mysql://localhost:3306/portfoliodb
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

#### Testing (`application-test.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfoliotest
spring.jpa.hibernate.ddl-auto=create-drop
```

### Profiles
- **default**: Production profile with update DDL
- **test**: Test profile with create-drop DDL for fresh database each run

## 🔧 Development

### Frontend Development

```bash
# Start development server with hot reload
npm start

# Build for production
npm run build

# Run in watch mode
npm run dev

# Run tests
npm test
```

### Backend Development

The backend uses Spring Boot with hot reload capabilities. Changes to Java files will be automatically recompiled when using:
```bash
mvnw spring-boot:run
```

### Scheduled Tasks

The application includes scheduled tasks for:
- **Price Updates**: Automatic price updates for assets at configured intervals

Check `scheduler/PriceScheduler.java` for scheduling configuration.

## 🧪 Testing

### Run All Tests
```bash
mvnw test
```

### Run Specific Test Class
```bash
mvnw test -Dtest=ControllerTest
```

### Run Frontend Tests
```bash
npm test
```

## 📚 Additional Documentation

- [Architecture Documentation](./ARCHITECTURE.md) - System design and components
- [API Documentation](./API_DOCUMENTATION.md) - Detailed API specifications
- [API Architecture](./API_ARCHITECTURE.md) - API design patterns

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/YourFeature`
2. Commit your changes: `git commit -m 'Add YourFeature'`
3. Push to the branch: `git push origin feature/YourFeature`
4. Open a Pull Request

## 📝 License

This project is licensed under the ISC License - see the package.json file for details.

## 📞 Support

For support, issues, or questions:
- Open an issue on GitHub
- Contact: MarketMinds Team

## 🎯 Roadmap

- [ ] User authentication and authorization
- [ ] Multiple portfolio support
- [ ] Advanced analytics and reporting
- [ ] Mobile app (React Native)
- [ ] Real-time stock price integration (API)
- [ ] Export reports (PDF, Excel)
- [ ] Performance benchmarking
- [ ] Risk analysis tools

---

**Version**: 1.0.0  
**Last Updated**: February 5, 2026  
**Maintained by**: MarketMinds
