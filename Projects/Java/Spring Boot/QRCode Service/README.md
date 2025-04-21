# QRCode Service

**Описание**  
REST API для генерации QR-кодов с поддержкой настройки размера, уровня коррекции ошибок и формата изображения.

---

## API Методы
- `GET /api/qrcode`: Генерация QR-кода.
- `GET /api/health`: Проверка здоровья сервиса.

---

## Как использовать
1. Перейдите в папку проекта:
   ```bash
   cd Spring\ Boot/QRCode\ Service
   ```
2. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```
3. Используйте API через Postman или другой HTTP-клиент.

---

**Пример запроса**:
```json
GET /api/qrcode?contents=Hello&size=200&correction=M&type=png
```

---

**Автор:** [gs0xa19f2](https://github.com/gs0xa19f2)
