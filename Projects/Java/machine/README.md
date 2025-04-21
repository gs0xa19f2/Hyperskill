# Coffee Machine

**Описание**  
Симуляция работы кофемашины с поддержкой различных операций: покупка кофе, пополнение ресурсов, получение прибыли.

---

## Возможности
- Поддержка трех типов кофе: эспрессо, латте, капучино.
- Отображение текущих ресурсов кофемашины.
- Пополнение запасов воды, молока, зерен и стаканов.

---

## Как использовать
1. Скомпилируйте код:
   ```bash
   javac machine/src/*.java -d machine/build
   ```
2. Запустите симулятор:
   ```bash
   java -cp machine/build machine.CoffeeMachine
   ```

---

**Пример игрового процесса**:
```
Write action (buy, fill, take, remaining, exit): 
> buy
What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:
> 1
I have enough resources, making you a coffee!
```

---

**Автор:** [gs0xa19f2](https://github.com/gs0xa19f2)
