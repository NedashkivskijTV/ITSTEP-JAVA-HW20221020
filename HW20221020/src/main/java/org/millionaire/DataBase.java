package org.millionaire;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.millionaire.entity.Question;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataBase {
    // SQL скрипти, щодо створення БД - sqlquery.txt
    private static final String CONFIGURATION_FILE_NAME = "hibernate_m.cfg.xml"; // ім'я конфігураційного файлу

    public static Question getQuestionById(int id) { // отримання 1 питання за id
        SessionFactory factory = new Configuration()
                .configure(CONFIGURATION_FILE_NAME) // конфігураційний файл
                .addAnnotatedClass(Question.class) // використовувані сутності
                .buildSessionFactory();
        Session session = factory.getCurrentSession(); // створення сесії
        session.beginTransaction(); // відкриття сесії
        Question question = session.get(Question.class, id);
        session.getTransaction().commit(); // закриття сесії
        return question;
    }

    public static List<Question> getQustionList() { // отримання списку питань з БД
        SessionFactory factory = new Configuration()
                .configure(CONFIGURATION_FILE_NAME) // конфігураційний файл
                .addAnnotatedClass(Question.class) // використовувані сутності
                .buildSessionFactory();
        Session session = factory.getCurrentSession(); // створення сесії
        session.beginTransaction(); // відкриття сесії
        List<Question> questions = session.createQuery("from Question").getResultList();
        session.getTransaction().commit(); // закриття сесії
        return questions;
    }

    public static void insertQuestion(Question question) { // збереження 1 питання до БД
        SessionFactory factory = new Configuration()
                .configure(CONFIGURATION_FILE_NAME) // конфігураційний файл
                .addAnnotatedClass(Question.class) // використовувані сутності
                .buildSessionFactory();
        Session session = factory.getCurrentSession(); // створення сесії
        session.beginTransaction(); // відкриття сесії
        session.save(question);
        session.getTransaction().commit(); // закриття сесії
    }

    public static void deleteQuestionById(int id) { // видалення питання за id
        SessionFactory factory = new Configuration()
                .configure(CONFIGURATION_FILE_NAME) // конфігураційний файл
                .addAnnotatedClass(Question.class) // використовувані сутності
                .buildSessionFactory();
        Session session = factory.getCurrentSession(); // створення сесії
        session.beginTransaction(); // відкриття сесії
        session.createQuery("delete Question where id = '" + id + "'").executeUpdate();
        session.getTransaction().commit(); // закриття сесії
    }

    public static void loadQuestionsFromFileToDB() { // Завантаження списку питань з файлу до БД (завантаження наявного списку питань до БД)
        List<Question> questions = getQuestionFromFile();
        for (Question question : questions) {
            insertQuestion(question);
        }
    }

    // - метод з первинної ралізації проекту ---------------------------------------------------------------------------
    public static List<Question> getQuestionFromFile() { // отримання списку питань з файлу
        List<Question> questions = new ArrayList<>();
        String line;
        int startPos = 0, endPos;
        ArrayList<String> questionArr = new ArrayList<>();
        try {
            //Scanner scanner = new Scanner(new FileInputStream("./src/edu/itstep/hw20220922/questions_base/questions.txt"));
            Scanner scanner = new Scanner(new FileInputStream("./questions_base/questions.txt"));
            while (scanner.hasNextLine()) {
                questionArr.clear();
                line = scanner.nextLine();
                for (int i = 0; i < 5; i++) {
                    endPos = line.indexOf(";", startPos);
                    questionArr.add(line.substring(startPos, endPos));
                    startPos = endPos + 1;
                }
                startPos = 0;
                questions.add(new Question(questionArr.get(0), questionArr.get(1), questionArr.get(2), questionArr.get(3), questionArr.get(4)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
