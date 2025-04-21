package main

import (
	"crypto/sha256"
	"fmt"
	"log"
	"os"
	"strings"
)

// Карта команд с их описанием
var commandToDescription = map[string]string{
	"config":   "Get and set a username.",
	"add":      "Add a file to the index.",
	"log":      "Show commit logs.",
	"commit":   "Save changes.",
	"checkout": "Restore a file.",
}

// Порядок вывода команд
var commandOrder = []string{"config", "add", "log", "commit", "checkout"}

// setupVCS инициализирует файловую структуру для системы контроля версий
func setupVCS() error {
	directories := []string{"./vcs", "./vcs/commits"}
	files := []string{"./vcs/index.txt", "./vcs/config.txt", "./vcs/log.txt"}

	// Создаем необходимые директории
	for _, dir := range directories {
		if err := os.MkdirAll(dir, os.ModePerm); err != nil && !os.IsExist(err) {
			return err
		}
	}

	// Создаем необходимые файлы
	for _, file := range files {
		if _, err := os.Stat(file); os.IsNotExist(err) {
			if f, err := os.Create(file); err == nil {
				if closeErr := f.Close(); closeErr != nil {
					return closeErr
				}
			} else {
				return err
			}
		}
	}
	return nil
}

// getCommandDescription выводит описание команды
func getCommandDescription(command string) {
	if description, exists := commandToDescription[command]; exists {
		fmt.Println(description)
	} else {
		fmt.Printf("'%s' is not a SVCS command.\n", command)
	}
}

// processCommand обрабатывает введенную команду
func processCommand(args []string) {
	switch cmd := args[0]; cmd {
	case "config":
		handleConfig(args)
	case "add":
		handleAdd(args)
	case "log":
		handleLog()
	case "commit":
		handleCommit(args)
	case "checkout":
		handleCheckout(args)
	default:
		getCommandDescription(cmd)
	}
}

// handleConfig обрабатывает команду config для установки или получения имени пользователя
func handleConfig(args []string) {
	content, _ := os.ReadFile("./vcs/config.txt")
	if len(args) == 1 {
		if len(content) == 0 {
			fmt.Println("Please, tell me who you are.")
		} else {
			fmt.Printf("The username is %s.\n", strings.TrimSpace(string(content)))
		}
	} else {
		err := os.WriteFile("./vcs/config.txt", []byte(args[1]), 0644)
		if err != nil {
			return
		}
		fmt.Printf("The username is %s.\n", args[1])
	}
}

// handleAdd обрабатывает команду add для добавления файлов в индекс
func handleAdd(args []string) {
	content, _ := os.ReadFile("./vcs/index.txt")
	if len(args) == 1 {
		if len(content) == 0 {
			fmt.Println("Add a file to the index.")
		} else {
			fmt.Printf("Tracked files:\n%s", string(content))
		}
	} else {
		if _, err := os.Stat(args[1]); os.IsNotExist(err) {
			fmt.Printf("Can't find '%s'.\n", args[1])
			return
		}
		f, _ := os.OpenFile("./vcs/index.txt", os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
		defer f.Close()
		_, err := f.WriteString(args[1] + "\n")
		if err != nil {
			return
		}
		fmt.Printf("The file '%s' is tracked.\n", args[1])
	}
}

// handleLog обрабатывает команду log для отображения истории коммитов
func handleLog() {
	content, _ := os.ReadFile("./vcs/log.txt")
	if len(content) == 0 {
		fmt.Println("No commits yet.")
		return
	}

	blocks := strings.Split(strings.TrimSpace(string(content)), "\n\n")
	for i, j := 0, len(blocks)-1; i < j; i, j = i+1, j-1 {
		blocks[i], blocks[j] = blocks[j], blocks[i]
	}

	fmt.Println(strings.Join(blocks, "\n\n"))
}

// handleCommit обрабатывает команду commit для сохранения изменений
func handleCommit(args []string) {
	if len(args) < 2 {
		fmt.Println("Message was not passed.")
		return
	}

	indexContent, _ := os.ReadFile("./vcs/index.txt")
	if len(indexContent) == 0 {
		fmt.Println("Nothing to commit.")
		return
	}

	trackedFiles := strings.Split(strings.TrimSpace(string(indexContent)), "\n")
	hasChanges, err := detectChanges(trackedFiles)
	if err != nil {
		fmt.Printf("Error while detecting changes: %v\n", err)
		return
	}

	if !hasChanges {
		fmt.Println("Nothing to commit.")
		return
	}

	commitID := generateCommitID()
	commitDir := fmt.Sprintf("./vcs/commits/%s", commitID)

	if err := os.MkdirAll(commitDir, os.ModePerm); err != nil {
		fmt.Printf("Error creating commit directory: %v\n", err)
		return
	}

	for _, file := range trackedFiles {
		data, err := os.ReadFile(file)
		if err != nil {
			fmt.Printf("Error reading file '%s': %v\n", file, err)
			return
		}

		destFile := fmt.Sprintf("%s/%s", commitDir, file)
		if err := os.MkdirAll(strings.TrimSuffix(destFile, "/"+file), os.ModePerm); err != nil {
			fmt.Printf("Error creating subdirectory for '%s': %v\n", file, err)
			return
		}

		err = os.WriteFile(destFile, data, 0644)
		if err != nil {
			fmt.Printf("Error writing file '%s': %v\n", destFile, err)
			return
		}
	}

	if err := writeLogEntry(commitID, args[1:]); err != nil {
		fmt.Printf("Error writing to log file: %v\n", err)
		return
	}

	fmt.Println("Changes are committed.")
}

// detectChanges проверяет наличие изменений в отслеживаемых файлах
func detectChanges(trackedFiles []string) (bool, error) {
	lastCommitDir := getLastCommitDir()
	if lastCommitDir == "" {
		return true, nil
	}

	for _, file := range trackedFiles {
		data, err := os.ReadFile(file)
		if err != nil {
			return false, err
		}
		newHash := fmt.Sprintf("%x", sha256.Sum256(data))

		oldData, err := os.ReadFile(fmt.Sprintf("%s/%s", lastCommitDir, file))
		if err != nil {
			return true, nil
		}
		oldHash := fmt.Sprintf("%x", sha256.Sum256(oldData))

		if newHash != oldHash {
			return true, nil
		}
	}
	return false, nil
}

// getLastCommitDir возвращает директорию последнего коммита
func getLastCommitDir() string {
	content, _ := os.ReadFile("./vcs/log.txt")
	if len(content) == 0 {
		return ""
	}

	blocks := strings.Split(strings.TrimSpace(string(content)), "\n\n")
	if len(blocks) == 0 {
		return ""
	}

	lastBlock := blocks[len(blocks)-1]
	lines := strings.Split(lastBlock, "\n")

	if len(lines) > 0 && strings.HasPrefix(lines[0], "commit ") {
		commitID := strings.TrimSpace(strings.TrimPrefix(lines[0], "commit "))
		return "./vcs/commits/" + commitID
	}

	return ""
}

// generateCommitID генерирует уникальный идентификатор коммита
func generateCommitID() string {
	return fmt.Sprintf("%x", sha256.Sum256([]byte(fmt.Sprintf("%d", os.Getpid()+os.Getuid()+os.Geteuid()))))[:40]
}

// writeLogEntry записывает запись в лог коммитов
func writeLogEntry(commitID string, messageParts []string) error {
	f, err := os.OpenFile("./vcs/log.txt", os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if err != nil {
		return err
	}
	defer f.Close()

	username, _ := os.ReadFile("./vcs/config.txt")
	logMessage := fmt.Sprintf("commit %s\nAuthor: %s\n%s\n\n", commitID, strings.TrimSpace(string(username)),
		strings.Join(messageParts, " "))
	if _, err := f.WriteString(logMessage); err != nil {
		return err
	}
	return nil
}

// handleCheckout обрабатывает команду checkout для восстановления файлов
func handleCheckout(args []string) {
	if len(args) < 2 {
		fmt.Println("Commit id was not passed.")
		return
	}

	commitID := args[1]
	commitDir := fmt.Sprintf("./vcs/commits/%s", commitID)

	if _, err := os.Stat(commitDir); os.IsNotExist(err) {
		fmt.Println("Commit does not exist.")
		return
	}

	files, err := os.ReadDir(commitDir)
	if err != nil {
		fmt.Printf("Error reading commit directory: %v\n", err)
		return
	}

	for _, file := range files {
		srcFile := fmt.Sprintf("%s/%s", commitDir, file.Name())
		destFile := fmt.Sprintf("./%s", file.Name())

		if err := os.Remove(destFile); err != nil && !os.IsNotExist(err) {
			fmt.Printf("Error removing file '%s': %v\n", destFile, err)
			return
		}

		if err := os.Rename(srcFile, destFile); err != nil {
			fmt.Printf("Error restoring file '%s': %v\n", destFile, err)
			return
		}
	}

	fmt.Printf("Switched to commit %s.\n", commitID)
}

func main() {
	if err := setupVCS(); err != nil {
		log.Fatal(err)
	}

	args := os.Args[1:]
	if len(args) == 0 || args[0] == "--help" {
		fmt.Println("These are SVCS commands:")
		for _, cmd := range commandOrder {
			fmt.Printf("%-10s %s\n", cmd, commandToDescription[cmd])
		}
	} else {
		processCommand(args)
	}
}
