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

type Student struct {
	fullName             string
	exams                map[string]float64
	specialAdmissionExam float64
	departments          []string
}

type Department struct {
	name             string
	enrolledStudents []Student
}

func parseStudent(line string) (Student, error) {
	parts := strings.Fields(line)
	if len(parts) < 9 {
		return Student{}, fmt.Errorf("invalid student line format")
	}

	fullName := parts[0] + " " + parts[1]

	physics, err := strconv.ParseFloat(parts[2], 64)
	if err != nil {
		return Student{}, fmt.Errorf("invalid physics score: %v", err)
	}
	chemistry, err := strconv.ParseFloat(parts[3], 64)
	if err != nil {
		return Student{}, fmt.Errorf("invalid chemistry score: %v", err)
	}
	math, err := strconv.ParseFloat(parts[4], 64)
	if err != nil {
		return Student{}, fmt.Errorf("invalid math score: %v", err)
	}
	cs, err := strconv.ParseFloat(parts[5], 64)
	if err != nil {
		return Student{}, fmt.Errorf("invalid computer science score: %v", err)
	}
	specialAdmissionExam, err := strconv.ParseFloat(parts[6], 64)
	if err != nil {
		return Student{}, fmt.Errorf("invalid special admission exam score: %v", err)
	}

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

func sortStudents(students []Student, exams []string) {
	sort.Slice(students, func(i, j int) bool {
		avgI, avgJ := 0.0, 0.0
		for _, exam := range exams {
			avgI += students[i].exams[exam]
			avgJ += students[j].exams[exam]
		}
		avgI = math.Max(avgI/float64(len(exams)), students[i].specialAdmissionExam)
		avgJ = math.Max(avgJ/float64(len(exams)), students[j].specialAdmissionExam)

		if avgI != avgJ {
			return avgI > avgJ
		}
		return students[i].fullName < students[j].fullName
	})
}

func calculateAverage(student Student, exams []string) float64 {
	sum := 0.0
	for _, exam := range exams {
		sum += student.exams[exam]
	}
	return sum / float64(len(exams))
}

func main() {
	var departmentCapacity int
	_, err := fmt.Scan(&departmentCapacity)
	if err != nil {
		log.Fatal(err)
	}

	file, err := os.Open("applicants.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(file)

	var applicants []Student
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		student, err := parseStudent(scanner.Text())
		if err != nil {
			log.Fatal(err)
		}
		applicants = append(applicants, student)
	}

	departments := map[string]*Department{
		"Biotech":     {name: "Biotech"},
		"Chemistry":   {name: "Chemistry"},
		"Engineering": {name: "Engineering"},
		"Mathematics": {name: "Mathematics"},
		"Physics":     {name: "Physics"},
	}

	departmentsToExams := map[string][]string{
		"Biotech":     {"chemistry", "physics"},
		"Chemistry":   {"chemistry"},
		"Physics":     {"physics", "math"},
		"Mathematics": {"math"},
		"Engineering": {"computer science", "math"},
	}

	remaining := make([]Student, len(applicants))
	copy(remaining, applicants)

	for priority := 0; priority < 3; priority++ {
		deptOrder := []string{"Biotech", "Chemistry", "Engineering", "Mathematics", "Physics"}
		for _, deptName := range deptOrder {
			dept := departments[deptName]
			var candidates []Student
			for _, s := range remaining {
				if s.departments[priority] == deptName {
					candidates = append(candidates, s)
				}
			}
			exams := departmentsToExams[deptName]
			sortStudents(candidates, exams)

			availableSpots := departmentCapacity - len(dept.enrolledStudents)
			if availableSpots <= 0 {
				continue
			}
			numToTake := availableSpots
			if len(candidates) < numToTake {
				numToTake = len(candidates)
			}
			enrolling := candidates[:numToTake]

			dept.enrolledStudents = append(dept.enrolledStudents, enrolling...)

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

	for deptName, dept := range departments {
		exams := departmentsToExams[deptName]
		sortStudents(dept.enrolledStudents, exams)
		filename := strings.ToLower(deptName) + ".txt"
		file, err := os.Create(filename)
		if err != nil {
			log.Fatal(err)
		}
		defer func(file *os.File) {
			err := file.Close()
			if err != nil {
				log.Fatal(err)
			}
		}(file)

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
