[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Wnkn88Yl)
# odev-2
2.Ödev - Refresh Token Mimarisi

## 📌 Proje Amacı

Bu projede, ders kapsamında işlenen **JWT (JSON Web Token)** konusu ele alınmaktadır.  
Temel hedef, JWT kullanarak bir kimlik doğrulama (authentication) mekanizması oluşturmak ve bu yapıyı daha gelişmiş bir mimari olan **Refresh Token Mimarisi** ile genişletmektir.

Bu proje kapsamında aşağıdaki adımların gerçekleştirilmesi beklenmektedir:

- JWT tabanlı authentication yapısının kurulması  
- Access Token ve Refresh Token kavramlarının uygulanması  
- Mevcut projenin, **Refresh Token mimarisine uygun şekilde yeniden tasarlanması**  
- Token yenileme (refresh) mekanizmasının implement edilmesi  
- Güvenli ve sürdürülebilir bir authentication altyapısının oluşturulması

Bu proje, sadece temel JWT kullanımını değil; aynı zamanda gerçek dünya uygulamalarında kullanılan **Refresh Token mimarisinin mantığını ve uygulanışını kavramayı** amaçlamaktadır.

---

## ✅ Uygulanan Basit Refresh Token Akışı

Sistem aşağıdaki şekilde çalışacak şekilde güncellendi:

1. `POST /auth/login` ile giriş yapılır.
2. Başarılı girişte `accessToken` + `refreshToken` döner.
3. `accessToken` korumalı endpointlerde (`/message`) `Authorization: Bearer <accessToken>` ile kullanılır.
4. Access token süresi biterse `POST /auth/refresh` ile aynı refresh token gönderilerek yeni access token alınır.

### Örnek İstekler

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "username": "ibrahimgokyar",
  "password": "123"
}
```

#### Refresh

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "<login ile dönen refreshToken>"
}
```

#### Korumalı Mesaj Endpointi

```http
GET /message
Authorization: Bearer <accessToken>
```
