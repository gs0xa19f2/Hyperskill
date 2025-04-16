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

var variables = make(map[string]int)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	for {
		if !scanner.Scan() {
			break
		}
		line := strings.TrimSpace(scanner.Text())
		if line == "" {
			continue
		}
		if strings.HasPrefix(line, "/") {
			switch line {
			case "/exit":
				fmt.Println("Bye!")
				return
			case "/help":
				fmt.Println("This calculator supports variables and the following operators with their priorities:")
				fmt.Println("  1) Parentheses: ( )")
				fmt.Println("  2) Power: ^")
				fmt.Println("  3) Multiplication and Integer Division: *, /")
				fmt.Println("  4) Addition and Subtraction: +, - (with unary minus as well)")
				fmt.Println("The program is case sensitive for variable names. E.g., BIG != big.")
				continue
			default:
				fmt.Println("Unknown command")
				continue
			}
		}

		if strings.Contains(line, "=") {
			handleAssignment(line)
			continue
		}
		handleExpression(line)
	}
}

func handleAssignment(line string) {
	parts := strings.Split(line, "=")
	if len(parts) != 2 {
		fmt.Println("Invalid assignment")
		return
	}
	left := strings.TrimSpace(parts[0])
	right := strings.TrimSpace(parts[1])
	if !isValidIdentifier(left) {
		fmt.Println("Invalid identifier")
		return
	}
	if !isValidAssignmentValue(right) {
		fmt.Println("Invalid assignment")
		return
	}
	val, err := parseAssignmentValue(right)
	if err != nil {
		fmt.Println(err.Error())
		return
	}
	variables[left] = val
}

func handleExpression(line string) {
	tokens, err := tokenize(line)
	if err != nil {
		fmt.Println("Invalid expression")
		return
	}
	postfix, convErr := infixToPostfix(tokens)
	if convErr != nil {
		fmt.Println("Invalid expression")
		return
	}
	result, evalErr := evaluatePostfix(postfix)
	if evalErr != nil {
		if evalErr.Error() == "Unknown variable" {
			fmt.Println("Unknown variable")
		} else {
			fmt.Println("Invalid expression")
		}
		return
	}
	fmt.Println(result)
}

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

func isValidAssignmentValue(s string) bool {
	if len(s) == 0 {
		return false
	}
	if isValidIdentifier(s) {
		return true
	}
	if isInteger(s) {
		return true
	}
	return false
}

func parseAssignmentValue(s string) (int, error) {
	if isInteger(s) {
		val, _ := strconv.Atoi(s)
		return val, nil
	}
	if isValidIdentifier(s) {
		v, ok := variables[s]
		if !ok {
			return 0, fmt.Errorf("Unknown variable")
		}
		return v, nil
	}
	return 0, fmt.Errorf("Invalid assignment")
}

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
			count := 1
			for j := i + 1; j < len(line); j++ {
				if rune(line[j]) == ch {
					count++
					i = j
				} else {
					break
				}
			}
			if count > 1 {
				return nil, fmt.Errorf("invalid")
			}
			tokens = append(tokens, string(ch))
		case unicode.IsDigit(ch) || unicode.IsLetter(ch):
			current.WriteRune(ch)
		default:
			return nil, fmt.Errorf("invalid")
		}
	}
	if current.Len() > 0 {
		tokens = append(tokens, current.String())
	}
	return tokens, nil
}

func infixToPostfix(tokens []string) ([]string, error) {
	var stack []string
	var output []string
	for i := 0; i < len(tokens); i++ {
		token := tokens[i]
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
			foundLeftParen := false
			for len(stack) > 0 {
				top := stack[len(stack)-1]
				stack = stack[:len(stack)-1]
				if top == "(" {
					foundLeftParen = true
					break
				}
				output = append(output, top)
			}
			if !foundLeftParen {
				return nil, fmt.Errorf("mismatched parentheses")
			}
		default:
			return nil, fmt.Errorf("invalid token")
		}
	}
	for len(stack) > 0 {
		top := stack[len(stack)-1]
		stack = stack[:len(stack)-1]
		if top == "(" || top == ")" {
			return nil, fmt.Errorf("mismatched parentheses")
		}
		output = append(output, top)
	}
	return output, nil
}

func evaluatePostfix(postfix []string) (int, error) {
	var stack []int
	for _, token := range postfix {
		switch {
		case isValidIdentifier(token):
			val, ok := variables[token]
			if !ok {
				return 0, fmt.Errorf("Unknown variable")
			}
			stack = append(stack, val)
		case isInteger(token):
			val, _ := strconv.Atoi(token)
			stack = append(stack, val)
		case token == "+" || token == "-" || token == "*" || token == "/" || token == "^":
			if len(stack) < 2 {
				return 0, fmt.Errorf("error")
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
					return 0, fmt.Errorf("division by zero")
				}
				stack = append(stack, a/b)
			case "^":
				powVal := math.Pow(float64(a), float64(b))
				stack = append(stack, int(powVal))
			}
		default:
			return 0, fmt.Errorf("error")
		}
	}
	if len(stack) != 1 {
		return 0, fmt.Errorf("error")
	}
	return stack[0], nil
}

func isPlusOrMinusSeq(token string) bool {
	for _, r := range token {
		if r != '+' && r != '-' {
			return false
		}
	}
	return len(token) > 0
}

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
