package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

var metaCharWasMet bool

func ParseRegex(regex string) (string, bool, bool) {
	isStartAnchored := false
	isEndAnchored := false

	if len(regex) > 0 && regex[0] == '^' {
		isStartAnchored = true
		regex = regex[1:]
	}
	if len(regex) > 1 && regex[len(regex)-1] == '$' && regex[len(regex)-2] != '\\' {
		isEndAnchored = true
		regex = regex[:len(regex)-1]
	}
	return regex, isStartAnchored, isEndAnchored
}

func DetectRepetitions(regex string, isEndAnchored bool) (rune, rune, rune) {
	if len(regex) <= 1 {
		return -1, -1, -1
	}
	index, shiftToBorder := 1, 1
	if isEndAnchored {
		index, shiftToBorder = len(regex)-1, -2
	}
	metacharacters := regex[index] == '?' || regex[index] == '*' || regex[index] == '+'
	switch {
	case len(regex) > 2 && metacharacters:
		// * / + / ?, which rune we repeat and the border rune
		return rune(regex[index]), rune(regex[index-1]), rune(regex[index+shiftToBorder])
	case metacharacters:
		// * / + / ?, which rune we repeat and the dummy border rune
		return rune(regex[index]), rune(regex[index-1]), -1
	default:
		return -1, -1, -1
	}
}

func ProcessRepetitions(regex, word string, isEndAnchored bool) (string, string) {
	metacharacter, repeatedRune, borderRune := DetectRepetitions(regex, isEndAnchored)
	if metacharacter == -1 || len(word) == 0 {
		return regex, word
	}

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
		} else {
			return ProcessRepetitions(regex[2-shiftRegex:len(regex)-shiftRegex], word, isEndAnchored)
		}
	case '*', '+':
		regex, word = MultipleRepInPlace(regex, word, string([]rune{metacharacter, repeatedRune, borderRune}),
			shiftWord, shiftRegex, isEndAnchored)
		return ProcessRepetitions(regex, word, isEndAnchored)
	default:
		return regex, word
	}
}

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
		if (repeatedRune == rune(word[wordIndex]) || repeatedRune == '.') && rune(word[wordIndex]) != borderRune {
			metOnce = true
			word = word[1-shiftWord : len(word)-shiftWord]
		} else {
			break
		}
	}
	if metacharacter == '+' && !metOnce {
		return "dummy", ""
	}
	return regex[2-shiftRegex : len(regex)-shiftRegex], word
}

func processEscapeSequences(regex, word string, isEndAnchored bool) (string, string) {
	escapedIndex, sliceIndex, wordIndex := 1, 0, 0
	if isEndAnchored {
		escapedIndex, wordIndex, sliceIndex = len(regex)-1, len(word)-1, 1
	}
	if len(regex) <= 1 || regex[escapedIndex-1] != '\\' {
		return regex, word
	}
	if rune(regex[escapedIndex]) == rune(word[wordIndex]) {
		return regex[2-2*sliceIndex : len(regex)-2*sliceIndex], word[1-sliceIndex : len(word)-sliceIndex]
	} else {
		return regex, word
	}
}

func compareStrings(regex, word string, isStartAnchored, isEndAnchored bool) bool {
	if len(regex) == 0 {
		return !(isStartAnchored && isEndAnchored && len(word) > 0)
	}
	if len(word) == 0 {
		return false
	}
	regex, word = processEscapeSequences(regex, word, isEndAnchored)
	regex, word = ProcessRepetitions(regex, word, isEndAnchored)
	if len(regex) == 0 {
		return !(isStartAnchored && isEndAnchored && len(word) > 0)
	}
	if len(word) == 0 {
		return false
	}
	if isEndAnchored && (regex[len(regex)-1] == '.' || (len(word) > 0 && regex[len(regex)-1] == word[len(word)-1])) {
		return compareStrings(regex[:len(regex)-1], word[:len(word)-1], isStartAnchored, isEndAnchored)
	}

	if !isEndAnchored && (regex[0] == '.' || (len(word) > 0 && regex[0] == word[0])) {
		return compareStrings(regex[1:], word[1:], isStartAnchored, isEndAnchored)
	}
	if isStartAnchored || isEndAnchored || metaCharWasMet {
		return false
	}
	if len(word) > 1 {
		return compareStrings(regex, word[1:], false, false)
	}
	return false
}

func main() {
	reader := bufio.NewReader(os.Stdin)
	raw, err := reader.ReadString('\n')
	if err != nil {
		return
	}
	parts := strings.Split(strings.TrimSpace(raw), "|")
	regex, word := parts[0], parts[1]
	parsedRegex, isStartAnchored, isEndAnchored := ParseRegex(regex)
	switch {
	case regex == "":
		fmt.Println(true)
	case word == "":
		fmt.Println(false)
	default:
		fmt.Println(compareStrings(parsedRegex, word, isStartAnchored, isEndAnchored))
	}
}
