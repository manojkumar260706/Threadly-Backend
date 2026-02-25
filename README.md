# 🚀 Threadly Backend

Backend service for **Threadly** - a blogging platform for Devs & Tech enthusiasts.
Handles authentication, posts, follows, search, and trending feeds.

---

## 🧠 Tech Stack

* **Java + Spring Boot**
* **Spring Security + OAuth2**
* **PostgreSQL** (Neon)
* **Redis** (caching / sessions)
* **JPA / Hibernate**
* **Cloudinary** (image storage)

---

## 📁 Project Structure

```
src/
 ┣ controller/     # REST endpoints
 ┣ service/        # Business logic
 ┣ repository/     # JPA repositories
 ┣ dto/            # Request/Response DTOs
 ┣ model/          # Entity classes
 ┣ config/         # Security, OAuth, CORS configs
 ┗ util/           # Helper utilities
```

---

## ⚙️ Environment Variables

Create a `.env` (or set in deployment dashboard):

```env
# Database
SPRING_DATASOURCE_URL=your_postgres_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Redis
REDIS_URI=your_redis_uri

# OAuth
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
OAUTH_REDIRECT_URL=http://localhost:3000/oauth/callback

# JWT
JWT_SECRET=your_jwt_secret

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## 🏃‍♂️ Running Locally

```bash
# Clone repo
git clone https://github.com/manojkumar260706/Threadly-Backend.git
cd threadly-backend

# Build & run
./mvnw spring-boot:run
```

Server runs on:

```
http://localhost:8080
```

Swagger to view endpoints:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Authentication Flow

1. User clicks **Login with Google**
2. OAuth success → backend generates JWT
3. Frontend receives token & stores it
4. All protected routes require:

```
Authorization: Bearer <token>
```

---

## 📡 Key API Endpoints

### 👤 Auth

```
GET  /oauth2/authorization/google
GET  /login/oauth2/code/google
```

### 📝 Posts

```
POST   /api/posts
GET    /api/posts/{id}
DELETE /api/posts/{id}
GET    /api/posts/trending
```

### 🔍 Search

```
GET /api/search?query=java
```

### 👥 Follow System

```
POST /api/users/{id}/follow
GET  /api/users/{id}/followers
GET  /api/users/{id}/following
```

---

## 🧪 Testing

```bash
./mvnw test
```

---

## 🚀 Deployment Notes

* Set all env variables in hosting dashboard
* Update OAuth redirect URL after frontend deployment
* Ensure PostgreSQL + Redis are accessible
* Use HTTPS in production

---

## 🛠 Future Improvements

* Notifications system 🔔
* Advanced search ranking
* Post recommendations ML model 🤖
* Rate limiting & caching optimizations

---
