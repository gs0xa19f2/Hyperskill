package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)

	fmt.Print("Enter the maximum number of notes: ")
	if !scanner.Scan() {
		return
	}
	maxInput := strings.TrimSpace(scanner.Text())
	maxSize, err := strconv.Atoi(maxInput)
	if err != nil || maxSize < 1 {
		return
	}

	notes := make([]string, 0, maxSize)

	for {
		fmt.Print("\nEnter a command and data: ")
		if !scanner.Scan() {
			return
		}
		input := scanner.Text()
		parts := strings.SplitN(input, " ", 2)
		command := parts[0]
		data := ""
		if len(parts) > 1 {
			data = strings.TrimSpace(parts[1])
		}

		switch command {
		case "exit":
			fmt.Println("[Info] Bye!")
			return

		case "create":
			if data == "" {
				fmt.Println("[Error] Missing note argument")
				continue
			}
			if len(notes) < maxSize {
				notes = append(notes, data)
				fmt.Println("[OK] The note was successfully created")
			} else {
				fmt.Println("[Error] Notepad is full")
			}

		case "list":
			if len(notes) == 0 {
				fmt.Println("[Info] Notepad is empty")
				continue
			}
			for i, note := range notes {
				fmt.Printf("[Info] %d: %s\n", i+1, note)
			}

		case "clear":
			notes = notes[:0]
			fmt.Println("[OK] All notes were successfully deleted")

		case "update":
			if data == "" {
				fmt.Println("[Error] Missing position argument")
				continue
			}
			updateParts := strings.SplitN(data, " ", 2)
			if len(updateParts) < 2 {
				if len(updateParts[0]) == 0 {
					fmt.Println("[Error] Missing position argument")
				} else {
					fmt.Println("[Error] Missing note argument")
				}
				continue
			}
			posStr, noteText := updateParts[0], updateParts[1]
			pos, convErr := strconv.Atoi(posStr)
			if convErr != nil {
				fmt.Printf("[Error] Invalid position: %s\n", posStr)
				continue
			}
			if pos < 1 || pos > maxSize {
				fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", pos, maxSize)
				continue
			}
			if pos > len(notes) {
				fmt.Println("[Error] There is nothing to update")
				continue
			}
			notes[pos-1] = noteText
			fmt.Printf("[OK] The note at position %d was successfully updated\n", pos)

		case "delete":
			if data == "" {
				fmt.Println("[Error] Missing position argument")
				continue
			}
			pos, convErr := strconv.Atoi(data)
			if convErr != nil {
				fmt.Printf("[Error] Invalid position: %s\n", data)
				continue
			}
			if pos < 1 || pos > maxSize {
				fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", pos, maxSize)
				continue
			}
			if pos > len(notes) {
				fmt.Println("[Error] There is nothing to delete")
				continue
			}
			notes = append(notes[:pos-1], notes[pos:]...)
			fmt.Printf("[OK] The note at position %d was successfully deleted\n", pos)

		default:
			if command == "" {
				continue
			}
			fmt.Println("[Error] Unknown command")
		}
	}
}
