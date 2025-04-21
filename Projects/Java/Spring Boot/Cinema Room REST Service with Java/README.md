# Cinema Room REST Service

**Описание**  
REST API для управления кинозалом. Поддерживает бронирование и возврат билетов, просмотр статистики.

---

## API Методы
- `GET /seats`: Получение доступных мест.
- `POST /purchase`: Покупка билета.
- `POST /return`: Возврат билета.
- `GET /stats`: Получение статистики (требуется пароль).

---

## Как использовать
1. Перейдите в папку проекта:
   ```bash
   cd Spring\ Boot/Cinema\ Room\ REST\ Service\ with\ Java
   ```
2. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```
3. Используйте API через Postman или другой HTTP-клиент.

---

**Пример запроса**:
```json
POST /purchase
{
  "row": 1,
  "column": 1
}
```

---

**Автор:** [gs0xa19f2](https://github.com/gs0xa19f2)
