# Bulls and Cows

**Описание**  
Игра "Быки и Коровы", в которой игрок должен угадать секретный код, используя подсказки.

---

## Возможности
- Поддержка произвольной длины кода и набора символов (0-9, a-z).
- Подсказки в формате "быков" и "коров".
- Обработка ошибок ввода.

---

## Как использовать
1. Скомпилируйте код:
   ```bash
   javac bullscows/src/*.java -d bullscows/build
   ```
2. Запустите игру:
   ```bash
   java -cp bullscows/build bullscows.Main
   ```

---

**Пример игрового процесса**:
```
Input the length of the secret code:
4
Input the number of possible symbols in the code:
6
The secret is prepared: **** (0-5).
Okay, let's start a game!
Turn 1:
1234
Grade: 1 bull and 1 cow
```

---

**Автор:** [gs0xa19f2](https://github.com/gs0xa19f2)
