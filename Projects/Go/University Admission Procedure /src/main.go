package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"os"
	"sort"
	"strconv"
	"strings"
)

// Структура для представления студента
type Student struct {
	fullName             string             // Полное имя студента
	exams                map[string]float64 // Оценки за экзамены
	specialAdmissionExam float64            // Балл за специальный экзамен
	departments          []string           // Предпочитаемые департаменты (в порядке приоритета)
}

// Структура для представления департамента
type Department struct {
	name             string    // Название департамента
	enrolledStudents []Student // Список зачисленных студентов
}

// Функция для парсинга строки с данными о студенте
func parseStudent(line string) (Student, error) {
	parts := strings.Fields(line) // Разбиваем строку на части
	if len(parts) < 9 {           // Убедимся, что строка содержит все необходимые поля
		return Student{}, fmt.Errorf("некорректный формат строки студента")
	}

	// Объединяем имя и фамилию
	fullName := parts[0] + " " + parts[1]

	// Парсим оценки за экзамены
	physics, err := strconv.ParseFloat(parts[2], 64)
	if err != nil {
		return Student{}, fmt.Errorf("некорректный балл по физике: %v", err)
	}
	chemistry, err := strconv.ParseFloat(parts[3], 64)
	if err != nil {
		return Student{}, fmt.Errorf("некорректный балл по химии: %v", err)
	}
	math, err := strconv.ParseFloat(parts[4], 64)
	if err != nil {
		return Student{}, fmt.Errorf("некорректный балл по математике: %v", err)
	}
	cs, err := strconv.ParseFloat(parts[5], 64)
	if err != nil {
		return Student{}, fmt.Errorf("некорректный балл по информатике: %v", err)
	}
	specialAdmissionExam, err := strconv.ParseFloat(parts[6], 64)
	if err != nil {
		return Student{}, fmt.Errorf("некорректный балл за специальный экзамен: %v", err)
	}

	// Список предпочитаемых департаментов
	departments := parts[7:10]

	return Student{
		fullName: fullName,
		exams: map[string]float64{
			"physics":          physics,
			"chemistry":        chemistry,
			"math":             math,
			"computer science": cs,
		},
		specialAdmissionExam: specialAdmissionExam,
		departments:          departments,
	}, nil
}

// Сортировка студентов по среднему баллу и имени
func sortStudents(students []Student, exams []string) {
	sort.Slice(students, func(i, j int) bool {
		avgI, avgJ := 0.0, 0.0
		for _, exam := range exams {
			avgI += students[i].exams[exam]
			avgJ += students[j].exams[exam]
		}

		// Выбираем максимум между средним баллом и баллом за специальный экзамен
		avgI = math.Max(avgI/float64(len(exams)), students[i].specialAdmissionExam)
		avgJ = math.Max(avgJ/float64(len(exams)), students[j].specialAdmissionExam)

		// Сравниваем по среднему баллу, а затем по имени
		if avgI != avgJ {
			return avgI > avgJ
		}
		return students[i].fullName < students[j].fullName
	})
}

// Вычисление среднего балла студента
func calculateAverage(student Student, exams []string) float64 {
	sum := 0.0
	for _, exam := range exams {
		sum += student.exams[exam]
	}
	return sum / float64(len(exams))
}

func main() {
	// Считываем вместимость департаментов
	var departmentCapacity int
	_, err := fmt.Scan(&departmentCapacity)
	if err != nil {
		log.Fatal(err)
	}

	// Открываем файл с данными о студентах
	file, err := os.Open("applicants.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// Считываем студентов из файла
	var applicants []Student
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		student, err := parseStudent(scanner.Text())
		if err != nil {
			log.Fatal(err)
		}
		applicants = append(applicants, student)
	}

	// Инициализация департаментов
	departments := map[string]*Department{
		"Biotech":     {name: "Biotech"},
		"Chemistry":   {name: "Chemistry"},
		"Engineering": {name: "Engineering"},
		"Mathematics": {name: "Mathematics"},
		"Physics":     {name: "Physics"},
	}

	// Связь департаментов с экзаменами
	departmentsToExams := map[string][]string{
		"Biotech":     {"chemistry", "physics"},
		"Chemistry":   {"chemistry"},
		"Physics":     {"physics", "math"},
		"Mathematics": {"math"},
		"Engineering": {"computer science", "math"},
	}

	// Создаём копию списка студентов для дальнейших итераций
	remaining := make([]Student, len(applicants))
	copy(remaining, applicants)

	// Распределение студентов по департаментам
	for priority := 0; priority < 3; priority++ {
		deptOrder := []string{"Biotech", "Chemistry", "Engineering", "Mathematics", "Physics"}
		for _, deptName := range deptOrder {
			dept := departments[deptName]
			var candidates []Student

			// Отбираем студентов, которые указали текущий департамент в качестве приоритета
			for _, s := range remaining {
				if s.departments[priority] == deptName {
					candidates = append(candidates, s)
				}
			}

			// Сортируем студентов по баллам
			exams := departmentsToExams[deptName]
			sortStudents(candidates, exams)

			// Определяем доступные места
			availableSpots := departmentCapacity - len(dept.enrolledStudents)
			if availableSpots <= 0 {
				continue
			}
			numToTake := availableSpots
			if len(candidates) < numToTake {
				numToTake = len(candidates)
			}

			// Зачисляем студентов
			enrolling := candidates[:numToTake]
			dept.enrolledStudents = append(dept.enrolledStudents, enrolling...)

			// Убираем зачисленных студентов из списка оставшихся
			enrolledSet := make(map[string]struct{})
			for _, s := range enrolling {
				enrolledSet[s.fullName] = struct{}{}
			}
			var newRemaining []Student
			for _, s := range remaining {
				if _, ok := enrolledSet[s.fullName]; !ok {
					newRemaining = append(newRemaining, s)
				}
			}
			remaining = newRemaining
		}
	}

	// Записываем результаты в файлы для каждого департамента
	for deptName, dept := range departments {
		exams := departmentsToExams[deptName]
		sortStudents(dept.enrolledStudents, exams)

		// Создаём файл для департамента
		filename := strings.ToLower(deptName) + ".txt"
		file, err := os.Create(filename)
		if err != nil {
			log.Fatal(err)
		}
		defer file.Close()

		// Записываем студентов в файл
		for _, s := range dept.enrolledStudents {
			avg := calculateAverage(s, exams)
			line := fmt.Sprintf("%s %.1f\n", s.fullName, math.Max(avg, s.specialAdmissionExam))
			_, err := file.WriteString(line)
			if err != nil {
				log.Fatal(err)
			}
		}
	}
}
