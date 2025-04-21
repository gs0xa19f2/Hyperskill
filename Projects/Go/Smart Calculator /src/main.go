package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
	"unicode"
)

// Глобальная карта для хранения переменных и их значений
var variables = make(map[string]int)

func main() {
	scanner := bufio.NewScanner(os.Stdin)

	// Основной цикл обработки пользовательского ввода
	for {
		if !scanner.Scan() {
			break
		}
		line := strings.TrimSpace(scanner.Text())

		// Пропуск пустой строки
		if line == "" {
			continue
		}

		// Обработка команд
		if strings.HasPrefix(line, "/") {
			handleCommand(line)
			continue
		}

		// Обработка присваивания переменных
		if strings.Contains(line, "=") {
			handleAssignment(line)
			continue
		}

		// Обработка математического выражения
		handleExpression(line)
	}
}

// handleCommand обрабатывает команды пользователя (например, /exit и /help)
func handleCommand(line string) {
	switch line {
	case "/exit":
		fmt.Println("Bye!")
		os.Exit(0)
	case "/help":
		fmt.Println("Этот калькулятор поддерживает переменные и следующие операторы с их приоритетами:")
		fmt.Println("  1) Скобки: ( )")
		fmt.Println("  2) Возведение в степень: ^")
		fmt.Println("  3) Умножение и Целочисленное Деление: *, /")
		fmt.Println("  4) Сложение и Вычитание: +, - (также поддерживается унарный минус)")
		fmt.Println("Переменные чувствительны к регистру. Пример: BIG != big.")
	default:
		fmt.Println("Неизвестная команда")
	}
}

// handleAssignment обрабатывает присваивание переменных
func handleAssignment(line string) {
	parts := strings.Split(line, "=")
	if len(parts) != 2 {
		fmt.Println("Неверное присваивание")
		return
	}

	left := strings.TrimSpace(parts[0])
	right := strings.TrimSpace(parts[1])

	if !isValidIdentifier(left) {
		fmt.Println("Неверное имя переменной")
		return
	}

	if !isValidAssignmentValue(right) {
		fmt.Println("Неверное значение переменной")
		return
	}

	val, err := parseAssignmentValue(right)
	if err != nil {
		fmt.Println(err.Error())
		return
	}

	variables[left] = val
}

// handleExpression обрабатывает математическое выражение
func handleExpression(line string) {
	tokens, err := tokenize(line)
	if err != nil {
		fmt.Println("Неверное выражение")
		return
	}

	postfix, convErr := infixToPostfix(tokens)
	if convErr != nil {
		fmt.Println("Неверное выражение")
		return
	}

	result, evalErr := evaluatePostfix(postfix)
	if evalErr != nil {
		if evalErr.Error() == "Неизвестная переменная" {
			fmt.Println("Неизвестная переменная")
		} else {
			fmt.Println("Неверное выражение")
		}
		return
	}

	fmt.Println(result)
}

// isValidIdentifier проверяет, является ли строка допустимым идентификатором переменной
func isValidIdentifier(s string) bool {
	if len(s) == 0 {
		return false
	}
	for _, r := range s {
		if !unicode.IsLetter(r) {
			return false
		}
	}
	return true
}

// isValidAssignmentValue проверяет, является ли строка допустимым значением для присваивания
func isValidAssignmentValue(s string) bool {
	return isValidIdentifier(s) || isInteger(s)
}

// parseAssignmentValue преобразует значение для присваивания в целое число
func parseAssignmentValue(s string) (int, error) {
	if isInteger(s) {
		val, _ := strconv.Atoi(s)
		return val, nil
	}

	if isValidIdentifier(s) {
		v, ok := variables[s]
		if !ok {
			return 0, fmt.Errorf("Неизвестная переменная")
		}
		return v, nil
	}

	return 0, fmt.Errorf("Неверное присваивание")
}

// isInteger проверяет, является ли строка целым числом
func isInteger(s string) bool {
	if s == "" {
		return false
	}

	if s[0] == '+' || s[0] == '-' {
		if len(s) == 1 {
			return false
		}
		s = s[1:]
	}

	for _, r := range s {
		if r < '0' || r > '9' {
			return false
		}
	}
	return true
}

// tokenize разбивает строку на токены
func tokenize(line string) ([]string, error) {
	var tokens []string
	var current strings.Builder

	for i := 0; i < len(line); i++ {
		ch := rune(line[i])
		switch {
		case unicode.IsSpace(ch):
			if current.Len() > 0 {
				tokens = append(tokens, current.String())
				current.Reset()
			}
		case ch == '(' || ch == ')':
			if current.Len() > 0 {
				tokens = append(tokens, current.String())
				current.Reset()
			}
			tokens = append(tokens, string(ch))
		case ch == '+' || ch == '-':
			if current.Len() > 0 {
				tokens = append(tokens, current.String())
				current.Reset()
			}
			var seq string
			seq += string(ch)
			for j := i + 1; j < len(line); j++ {
				if line[j] == '+' || line[j] == '-' {
					seq += string(line[j])
					i = j
				} else {
					break
				}
			}
			tokens = append(tokens, seq)
		case ch == '*' || ch == '/' || ch == '^':
			if current.Len() > 0 {
				tokens = append(tokens, current.String())
				current.Reset()
			}
			tokens = append(tokens, string(ch))
		case unicode.IsDigit(ch) || unicode.IsLetter(ch):
			current.WriteRune(ch)
		default:
			return nil, fmt.Errorf("недопустимый символ")
		}
	}

	if current.Len() > 0 {
		tokens = append(tokens, current.String())
	}

	return tokens, nil
}

// infixToPostfix преобразует инфиксное выражение в постфиксное
func infixToPostfix(tokens []string) ([]string, error) {
	var stack []string
	var output []string

	for _, token := range tokens {
		switch {
		case isValidIdentifier(token) || isInteger(token):
			output = append(output, token)
		case isPlusOrMinusSeq(token):
			op := simplifyPlusMinus(token)
			for len(stack) > 0 {
				top := stack[len(stack)-1]
				if top == "(" {
					break
				}
				if priority(top) >= priority(op) {
					output = append(output, top)
					stack = stack[:len(stack)-1]
				} else {
					break
				}
			}
			stack = append(stack, op)
		case token == "*" || token == "/" || token == "^":
			for len(stack) > 0 {
				top := stack[len(stack)-1]
				if top == "(" {
					break
				}
				if priority(top) >= priority(token) {
					output = append(output, top)
					stack = stack[:len(stack)-1]
				} else {
					break
				}
			}
			stack = append(stack, token)
		case token == "(":
			stack = append(stack, token)
		case token == ")":
			for len(stack) > 0 {
				top := stack[len(stack)-1]
				stack = stack[:len(stack)-1]
				if top == "(" {
					break
				}
				output = append(output, top)
			}
		default:
			return nil, fmt.Errorf("недопустимый токен")
		}
	}

	for len(stack) > 0 {
		output = append(output, stack[len(stack)-1])
		stack = stack[:len(stack)-1]
	}

	return output, nil
}

// evaluatePostfix вычисляет значение постфиксного выражения
func evaluatePostfix(postfix []string) (int, error) {
	var stack []int

	for _, token := range postfix {
		switch {
		case isValidIdentifier(token):
			val, ok := variables[token]
			if !ok {
				return 0, fmt.Errorf("Неизвестная переменная")
			}
			stack = append(stack, val)
		case isInteger(token):
			val, _ := strconv.Atoi(token)
			stack = append(stack, val)
		case token == "+" || token == "-" || token == "*" || token == "/" || token == "^":
			if len(stack) < 2 {
				return 0, fmt.Errorf("ошибка")
			}
			b := stack[len(stack)-1]
			a := stack[len(stack)-2]
			stack = stack[:len(stack)-2]
			switch token {
			case "+":
				stack = append(stack, a+b)
			case "-":
				stack = append(stack, a-b)
			case "*":
				stack = append(stack, a*b)
			case "/":
				if b == 0 {
					return 0, fmt.Errorf("деление на ноль")
				}
				stack = append(stack, a/b)
			case "^":
				powVal := math.Pow(float64(a), float64(b))
				stack = append(stack, int(powVal))
			}
		default:
			return 0, fmt.Errorf("ошибка")
		}
	}

	if len(stack) != 1 {
		return 0, fmt.Errorf("ошибка")
	}

	return stack[0], nil
}

// isPlusOrMinusSeq проверяет последовательность плюсов и минусов
func isPlusOrMinusSeq(token string) bool {
	for _, r := range token {
		if r != '+' && r != '-' {
			return false
		}
	}
	return len(token) > 0
}

// simplifyPlusMinus упрощает последовательность плюсов и минусов
func simplifyPlusMinus(seq string) string {
	minusCount := 0
	for _, r := range seq {
		if r == '-' {
			minusCount++
		}
	}
	if minusCount%2 == 0 {
		return "+"
	}
	return "-"
}

// priority возвращает приоритет оператора
func priority(op string) int {
	switch op {
	case "^":
		return 4
	case "*", "/":
		return 3
	case "+", "-":
		return 2
	}
	return 0
}
