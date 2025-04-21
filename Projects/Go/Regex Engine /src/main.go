package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Глобальная переменная, указывающая на то, что метасимвол был встречен
var metaCharWasMet bool

// ParseRegex анализирует регулярное выражение для определения якорей (начало и конец строки)
func ParseRegex(regex string) (string, bool, bool) {
	isStartAnchored := false
	isEndAnchored := false

	// Проверка на якорь начала строки (^)
	if len(regex) > 0 && regex[0] == '^' {
		isStartAnchored = true
		regex = regex[1:]
	}

	// Проверка на якорь конца строки ($)
	if len(regex) > 1 && regex[len(regex)-1] == '$' && regex[len(regex)-2] != '\\' {
		isEndAnchored = true
		regex = regex[:len(regex)-1]
	}

	return regex, isStartAnchored, isEndAnchored
}

// DetectRepetitions определяет, есть ли в регулярном выражении повторения (*, +, ?)
func DetectRepetitions(regex string, isEndAnchored bool) (rune, rune, rune) {
	if len(regex) <= 1 {
		return -1, -1, -1
	}

	// Определяем индекс и направление работы в зависимости от якоря конца строки
	index, shiftToBorder := 1, 1
	if isEndAnchored {
		index, shiftToBorder = len(regex)-1, -2
	}

	metacharacters := regex[index] == '?' || regex[index] == '*' || regex[index] == '+'
	switch {
	case len(regex) > 2 && metacharacters:
		// Возвращаем метасимвол, символ повторения и пограничный символ
		return rune(regex[index]), rune(regex[index-1]), rune(regex[index+shiftToBorder])
	case metacharacters:
		// Возвращаем метасимвол, символ повторения и фиктивный пограничный символ
		return rune(regex[index]), rune(regex[index-1]), -1
	default:
		return -1, -1, -1
	}
}

// ProcessRepetitions обрабатывает повторения в регулярном выражении
func ProcessRepetitions(regex, word string, isEndAnchored bool) (string, string) {
	metacharacter, repeatedRune, borderRune := DetectRepetitions(regex, isEndAnchored)
	if metacharacter == -1 || len(word) == 0 {
		return regex, word
	}

	// Если повторяется не экранированный символ
	if repeatedRune != '\\' {
		metaCharWasMet = true
	} else {
		return regex, word
	}

	shiftWord, shiftRegex, wordIndex := 0, 0, 0
	if isEndAnchored {
		shiftWord, shiftRegex, wordIndex = 1, 2, len(word)-1
	}

	switch metacharacter {
	case '?':
		if repeatedRune == rune(word[wordIndex]) || repeatedRune == '.' {
			return ProcessRepetitions(regex[2-shiftRegex:len(regex)-shiftRegex],
				word[1-shiftWord:len(word)-shiftWord], isEndAnchored)
		}
		return ProcessRepetitions(regex[2-shiftRegex:len(regex)-shiftRegex], word, isEndAnchored)
	case '*', '+':
		regex, word = MultipleRepInPlace(regex, word, string([]rune{metacharacter, repeatedRune, borderRune}),
			shiftWord, shiftRegex, isEndAnchored)
		return ProcessRepetitions(regex, word, isEndAnchored)
	default:
		return regex, word
	}
}

// MultipleRepInPlace обрабатывает множественные повторения (*, +)
func MultipleRepInPlace(regex, word, mrb string, shiftWord, shiftRegex int, isEndAnchored bool) (string, string) {
	metacharacter := rune(mrb[0])
	repeatedRune := rune(mrb[1])
	borderRune := rune(mrb[2])

	metOnce := false
	for {
		if len(word) == 0 {
			break
		}
		wordIndex := 0
		if isEndAnchored {
			wordIndex = len(word) - 1
		}

		// Проверяем соответствие символа
		if (repeatedRune == rune(word[wordIndex]) || repeatedRune == '.') && rune(word[wordIndex]) != borderRune {
			metOnce = true
			word = word[1-shiftWord : len(word)-shiftWord]
		} else {
			break
		}
	}

	// Для символа '+' проверяем, был ли символ встречен хотя бы раз
	if metacharacter == '+' && !metOnce {
		return "dummy", ""
	}

	return regex[2-shiftRegex : len(regex)-shiftRegex], word
}

// processEscapeSequences обрабатывает экранированные символы
func processEscapeSequences(regex, word string, isEndAnchored bool) (string, string) {
	escapedIndex, sliceIndex, wordIndex := 1, 0, 0
	if isEndAnchored {
		escapedIndex, wordIndex, sliceIndex = len(regex)-1, len(word)-1, 1
	}

	// Проверяем, начинается ли регулярное выражение с экранированного символа
	if len(regex) <= 1 || regex[escapedIndex-1] != '\\' {
		return regex, word
	}

	// Сравниваем экранированный символ с первым символом слова
	if rune(regex[escapedIndex]) == rune(word[wordIndex]) {
		return regex[2-2*sliceIndex : len(regex)-2*sliceIndex], word[1-sliceIndex : len(word)-sliceIndex]
	}

	return regex, word
}

// compareStrings сравнивает строку и регулярное выражение
func compareStrings(regex, word string, isStartAnchored, isEndAnchored bool) bool {
	// Если регулярное выражение пустое
	if len(regex) == 0 {
		return !(isStartAnchored && isEndAnchored && len(word) > 0)
	}

	// Если слово пустое
	if len(word) == 0 {
		return false
	}

	// Обрабатываем экранированные символы и повторения
	regex, word = processEscapeSequences(regex, word, isEndAnchored)
	regex, word = ProcessRepetitions(regex, word, isEndAnchored)

	// Если регулярное выражение закончено
	if len(regex) == 0 {
		return !(isStartAnchored && isEndAnchored && len(word) > 0)
	}

	// Если слово закончилось, но регулярное выражение осталось
	if len(word) == 0 {
		return false
	}

	// Проверяем соответствие конца строки
	if isEndAnchored && (regex[len(regex)-1] == '.' || regex[len(regex)-1] == word[len(word)-1]) {
		return compareStrings(regex[:len(regex)-1], word[:len(word)-1], isStartAnchored, isEndAnchored)
	}

	// Проверяем соответствие начала строки
	if !isEndAnchored && (regex[0] == '.' || regex[0] == word[0]) {
		return compareStrings(regex[1:], word[1:], isStartAnchored, isEndAnchored)
	}

	// Проверяем, применимо ли регулярное выражение к слову
	if isStartAnchored || isEndAnchored || metaCharWasMet {
		return false
	}

	// Пробуем начать сдвиг с другой позиции
	if len(word) > 1 {
		return compareStrings(regex, word[1:], false, false)
	}

	return false
}

func main() {
	// Чтение ввода, разделенного символом '|'
	reader := bufio.NewReader(os.Stdin)
	raw, err := reader.ReadString('\n')
	if err != nil {
		return
	}

	parts := strings.Split(strings.TrimSpace(raw), "|")
	regex, word := parts[0], parts[1]

	// Парсим регулярное выражение
	parsedRegex, isStartAnchored, isEndAnchored := ParseRegex(regex)

	// Обрабатываем случаи пустых входных данных
	switch {
	case regex == "":
		fmt.Println(true)
	case word == "":
		fmt.Println(false)
	default:
		// Сравниваем строку и регулярное выражение
		fmt.Println(compareStrings(parsedRegex, word, isStartAnchored, isEndAnchored))
	}
}
