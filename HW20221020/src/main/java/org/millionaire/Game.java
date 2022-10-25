package org.millionaire;

import org.millionaire.entity.Question;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private User user = new User();
    private List<QuestionDifficulty> questionsDifficultyOll = new ArrayList<>(); // завантажена колекція питань  з БД
    // (для уникнення необхідності повторного звертання до БД при генерування нової гри,
    // при використанні підказки -заміна питання-)
    private List<QuestionDifficulty> questions = new ArrayList<>(); // колекція питань на 1 гру
    private List<String> answers = new ArrayList<>(); // колекція відповідей на поточне питання, попередньо змішана рандомку кількість раз
    private final String[][] systemLines = new String[][]{
            new String[]{"yes", "no", "y", "n", ""},
            new String[]{"y", "yes", ""},
            new String[]{"A", "B", "C", "D"},
            new String[]{"A", "B", "C", "D", "P"},
    };
    //підказки
    // - 50Х50
    private boolean prompt50x50 = true;
    // - повна заміна питання
    private boolean promptQuestionReplacing = true;
    // - дзвінок другу
    private boolean promptCallToFriend = true;
    // - +1 - підказка від спонсора (завжди правильне)
    private boolean promptFromSponsor = true;


    public void start() {
        boolean isFirstEnter = true;
        System.err.println("\n\nІНТЕЛЕКТУАЛЬНА ГРА \"ХТО ХОЧЕ СТАТИ МІЛЬЙОНЕРОМ\"");
        System.out.print("\n\nБажаєте випробувати себе ? (y/n) - ");

        while (lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", ""

            //початок гри - правила, реєстрація гравця
            if (isFirstEnter) {
                userRegistration();
                isFirstEnter = false;
            }
            newGameSettings();
            showGameRules();

            // процес гри - питання/відповідь
            game();

            // виведення результатів
            // завершення гри
            gameResult();

            System.out.print("\nБАЖАЄТЕ ЗІГРАТИ ЩЕ РАЗ ? (y/n) - ");
        }
    }

    public void newGameSettings() {
        prompt50x50 = true;
        promptCallToFriend = true;
        promptQuestionReplacing = true;
        promptFromSponsor = true;
    }

    public void showGameRules() {
        System.out.print("Бажаєте ознайомитись з правилами гри, " + user.getName() + " ? (y/n) - ");
        if (lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", ""
            System.out.println("\nПРАВИЛА ГРИ :\nучасник має відповісти на п'ятнадцять різноманітних запитань. " +
                    "\nКожна правильна відповідь примножує виграш, а кожна неправильна – " +
                    "\nпозбавляє усієї заробленої суми та завершує гру." +
                    "\nГра завершиться для гравця, якщо він забере виграні гроші, дасть невірну відповідь " +
                    "\nабо зможе відповісти вірно на всі питання" +
                    "\n\nДля продовження натисніть Enter");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        }
    }

    public void userRegistration() {
        System.out.print("Введіть Ваше ім'я або натисніть Enter, \nщоб залишити значення за замовчуванням (" + user.getName() + ") - ");

        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();

        if (!userName.equals("")) {
            this.getUser().setName(userName);
        }
    }

    public void game() {
        downloadQuestions();
        System.out.println("\nРозпочнемо гру! Мільйонером бажає стати " + user.getName());
        System.out.println("Сума, якою володіє гравець - $" + user.getPrizeMoney() + "\n");

        for (QuestionDifficulty question : questions) {
            // поточна інф про гравця
            answers = null;
            System.out.println("На даний момент отримано правильних відповідей - " + question.getDifficulty().ordinal());
            System.out.println("Інтелект гравця допоміг йому заробити - $" +
                    (question.getDifficulty().ordinal() == 0 ? 0 : question.getDifficulty().values()[question.getDifficulty().ordinal() - 1].getPrice()));

            System.out.println("Наступне питання на $" + question.getDifficulty().getPrice() + " :");
            // ОГОЛОСИТИ запитання, показати варіанти відповідей -------------------------------------------------------

            // отримання списку відповідей
            setAnswers(question);

            // цикл - робота з підказками
            boolean promptFlag = false;
            String userChoice, validText;
            String[] validStrings = systemLines[2];
            do {
                if (prompt50x50 || promptQuestionReplacing || promptCallToFriend || promptFromSponsor) {
                    promptFlag = true;
                } else {
                    promptFlag = false;
                }

                validText = "Допустимі відповіді: A, B, C, D";
                validStrings = systemLines[2];
                // виведення варіантів відповіді ---------------------------------------------------------------------------
                //showAnswers();
                System.out.println(question.getQuestion().getQuestion());
                System.out.println("A : " + answers.get(0));
                System.out.println("B : " + answers.get(1));
                System.out.println("C : " + answers.get(2));
                System.out.println("D : " + answers.get(3));
                if (promptFlag) {
                    System.out.println("\nP - використати підказку");
                    validText = "Допустимі відповіді: A, B, C, D, P";
                    validStrings = systemLines[3];
                    //System.out.println(validText);
                }
                System.out.println("\nДля відповіді натисніть відповідну літеру та Enter");
                userChoice = lineInputControl(validText, validStrings).toUpperCase();
                if (userChoice.equals("P")) {
                    question = usePrompt(question);
                } else {
                    promptFlag = false;
                }
            } while (promptFlag);

            // відповідь гравця ----------------------------------------------------------------------------------------
            int answerPos = Arrays.asList(systemLines[2]).indexOf(userChoice);

            // перевірка правильності відповіді ------------------------------------------------------------------------
            if (!answers.get(answerPos).equals(question.getQuestion().getRightAnswer())) {
                int lastFireproofPrice = getLastFireproofPrice(question);
                System.out.println("Нажаль Ви дали неправильну відповідь, ваш виграш " +
                        "\nдорівнюватиме останній заробленій сумі що не згорає - $" +
                        lastFireproofPrice);
                // додати крайню захищену суму
                user.setPrizeMoney(user.getPrizeMoney() + lastFireproofPrice);
                break;
            }
            System.out.println("Вітаємо, відповідь вірна. Ви заробили $" +
                    question.getDifficulty().values()[question.getDifficulty().ordinal()].getPrice());
            System.out.println("Для продовження натисніть Enter");
            lineInputControl("");

            // якщо гравець відповідає на усі запитання до його призової суми додається максимальний передбачений приз
            if (question.getDifficulty().ordinal() == questions.size() - 1) {
                user.setPrizeMoney(user.getPrizeMoney() + Difficulty.values()[Difficulty.values().length - 1].getPrice());
            }
        }
    }

    public QuestionDifficulty usePrompt(QuestionDifficulty questionDifficulty) {
        QuestionDifficulty questionTemp = questionDifficulty;
        String userChoice;
        ArrayList<String> validStrings = new ArrayList<>();
        String validStringsText1 = prompt50x50 ? " 1" : "",
                validStringsText2 = promptCallToFriend ? " 2" : "",
                validStringsText3 = promptQuestionReplacing ? " 3" : "",
                validStringsText4 = promptFromSponsor ? " 4" : "";
        Random random = new Random();
        System.out.println("Оберіть підказку :");
        if (prompt50x50) {
            System.out.println("1 - 50 Х 50");
            validStrings.add("1");
        }
        if (promptCallToFriend) {
            System.out.println("2 - Дзвінок другу");
            validStrings.add("2");
        }
        if (promptQuestionReplacing) {
            System.out.println("3 - Заміна питання");
            validStrings.add("3");
        }
        if (promptFromSponsor) {
            System.out.println("4 - Підказка від спонсора");
            validStrings.add("4");
        }
        String validStringsText = "Допустимі значення :".concat(validStringsText1).concat(validStringsText2).concat(validStringsText3).concat(validStringsText4);
        String[] validStringsArr = validStrings.toArray(new String[0]);
        userChoice = lineInputControl(validStringsText, validStringsArr);
        switch (userChoice) {
            case "1": {
                //50 Х 50
                int countWrongAnswers = 2, wrongAnswerPos, repeatedInt = -1;
                while (countWrongAnswers > 0) {
                    do {
                        wrongAnswerPos = random.nextInt(4); // 0 - 3
                    } while (wrongAnswerPos == repeatedInt || answers.get(wrongAnswerPos).equals(questionDifficulty.getQuestion().getRightAnswer()));
                    answers.set(wrongAnswerPos, "");
                    repeatedInt = wrongAnswerPos;
                    --countWrongAnswers;
                }
                prompt50x50 = false;
                break;
            }
            case "2": {
                //Дзвінок другу
                // до списку відповідей додається правильна відповідь n-раз для підвищення вірогідності
                // вибору саме правильної відповіді
                ArrayList<String> answersTemp = (new ArrayList<>(answers));
                answersTemp.add(questionDifficulty.getQuestion().getRightAnswer());
                answersTemp.add(questionDifficulty.getQuestion().getRightAnswer());
                answersTemp.add(questionDifficulty.getQuestion().getRightAnswer());
                answersTemp.add(questionDifficulty.getQuestion().getRightAnswer());
                answersTemp.add(questionDifficulty.getQuestion().getRightAnswer());
                Collections.shuffle(answersTemp);
                int friendRightAnswer;
                do {
                    friendRightAnswer = random.nextInt(answersTemp.size());
                } while (answersTemp.get(friendRightAnswer).equals(""));
                System.out.println("Ваш друг вважає, що правильна відповідь " + answersTemp.get(friendRightAnswer) + " ... Ви йому довіряєте ?\n");

                promptCallToFriend = false;
                break;
            }
            case "3": {
                //Заміна питання
                //використовується раніше отримання колекція усіх запитань з БД - змінна questionsDifficultyOll
                List<QuestionDifficulty> questionsByDifficultyOll = questionsDifficultyOll
                        .stream()
                        .filter(q -> q.getDifficulty() == questionDifficulty.getDifficulty())
                        .collect(Collectors.toList());
                int index = -1;
                do {
                    index = random.nextInt(questionsByDifficultyOll.size());
                } while (questionsByDifficultyOll.get(index).getQuestion().getQuestion().equals(questionDifficulty.getQuestion().getQuestion()));
                questionTemp = questionsByDifficultyOll.get(index);
                setAnswers(questionTemp);
                promptQuestionReplacing = false;
                break;
            }
            case "4": {
                //Підказка від спонсора
                System.out.println("Підказка від спонсора :\nправильна відповідь - " + questionDifficulty.getQuestion().getRightAnswer() + "\n");
                promptFromSponsor = false;
                break;
            }
        }

        return questionTemp;
    }

    public void gameResult() {
        System.out.println("\nГРУ ЗАВЕРШЕНО, " + user.getName());
        System.out.println("\nВи заробили наступну суму грошей - $" + user.getPrizeMoney());
    }

    public String lineInputControl(String message, String... correctLine) {
        if (message == null || message.length() == 0) {
            message = "Введіть yes/no та Enter";
        }
        if (correctLine == null || correctLine.length == 0) {
            correctLine = systemLines[0]; // "yes", "no", "y", "n", ""
        }
        Scanner scanner = new Scanner(System.in);
        String userLine = scanner.nextLine();
        while (!lineCheck(userLine, correctLine)) {
            System.out.println("\n" + message);
            userLine = scanner.nextLine();
        }
        return userLine;
    }

    public boolean lineCheck(String line, String... possibleLines) {
        //return List.of(possibleLines).stream().anyMatch(l -> l.equalsIgnoreCase(line));
        for (int i = 0; i < possibleLines.length; i++) {
            if (possibleLines[i].equalsIgnoreCase(line)) {
                return true;
            }
        }
        return false;
    }

    public void downloadQuestions() {
        //отримання усіх запитань з БД
        // завантажена з БД колекція питань зберігається у змінній questionsDifficultyOll
        // якщо довжина колекції у змінній questionsDifficultyOll == 0 (колекція питань пуста) - викликається алгоритм завантаження даних з БД
        // після завантаження колекції питань з БД на початку гри при необхідності отримання нового питання/списку питань -
        // алгоритм звертається не до БД, а до вже завантажених даних (змінна questionsDifficultyOll)

        if (questionsDifficultyOll.size() == 0) {
            //List<Question> questionsTemp = DataBase.getQuestionFromFile();
            List<Question> questionsTemp = DataBase.getQustionList();

            if(questionsTemp.size() == 0 || questionsTemp == null){ // у разі відсутності даних в БД, вона наповнюється з файлу, список питань також завантажується з файлу
                DataBase.loadQuestionsFromFileToDB();
                questionsTemp = DataBase.getQuestionFromFile();
            }

            // у разі відсутності достатньої кількості питань (мінімум 2 питання кожного рівня важкості) підказка -заміна питання- не активується
            if (1.0 * questionsTemp.size() / Difficulty.values().length < 2) {
                setPromptQuestionReplacing(false);
            }

            // створення повного масиву об'єктів QuestionDifficulty (усі запитання за складністю)
            int[] countDifficulty = new int[]{0};
            //List<QuestionDifficulty> questionsDifficultyOll = questionsTemp
            questionsDifficultyOll = questionsTemp
                    .stream()
                    .map(q -> {
                        QuestionDifficulty questionDifficulty = new QuestionDifficulty(q, Difficulty.values()[countDifficulty[0]]);
                        countDifficulty[0] = (countDifficulty[0] == Difficulty.values().length - 1 ? 0 : countDifficulty[0] + 1);
                        return questionDifficulty;
                    })
                    .collect(Collectors.toList());
        }

        // створення масиву з 15 запитань з використанням рандомного вибору
        // по кожній групі запитань з однаковою складністю
        questions.clear();
        Random random = new Random();
        for (Difficulty value : Difficulty.values()) {
            List<QuestionDifficulty> questionsByDifficulty = questionsDifficultyOll
                    .stream()
                    .filter(q -> q.getDifficulty() == value)
                    .collect(Collectors.toList());
            int index = random.nextInt(questionsByDifficulty.size());
            questions.add(questionsByDifficulty.get(index));
        }
    }

    public int getLastFireproofPrice(QuestionDifficulty questionDifficulty) {
        if (questionDifficulty.getDifficulty().isFireproof()) {
            return questionDifficulty.getDifficulty().getPrice();
        }
        for (int i = questionDifficulty.getDifficulty().ordinal() - 1; i >= 0; i--) {
            if (Difficulty.values()[i].isFireproof()) {
                return Difficulty.values()[i].getPrice();
            }
        }
        return 0;
    }

    public User getUser() {
        return user;
    }

    public List<QuestionDifficulty> getQuestions() {
        return questions;
    }

    public String[][] getSystemLines() {
        return systemLines;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setAnswers(QuestionDifficulty question) {
        answers = Arrays.asList(question.getQuestion().getRightAnswer(),
                question.getQuestion().getAnswer1(),
                question.getQuestion().getAnswer2(),
                question.getQuestion().getAnswer3());
        // перемішування питань у списку рандомну кількість раз
        Random random = new Random();
        for (int i = 0; i < (random.nextInt(11) + 1); i++) {
            Collections.shuffle(answers);
        }
    }

    public boolean isPrompt50x50() {
        return prompt50x50;
    }

    public void setPrompt50x50(boolean prompt50x50) {
        this.prompt50x50 = prompt50x50;
    }

    public boolean isPromptQuestionReplacing() {
        return promptQuestionReplacing;
    }

    public void setPromptQuestionReplacing(boolean promptQuestionReplacing) {
        this.promptQuestionReplacing = promptQuestionReplacing;
    }

    public boolean isPromptCallToFriend() {
        return promptCallToFriend;
    }

    public void setPromptCallToFriend(boolean promptCallToFriend) {
        this.promptCallToFriend = promptCallToFriend;
    }

    public boolean isPromptFromSponsor() {
        return promptFromSponsor;
    }

    public void setPromptFromSponsor(boolean promptFromSponsor) {
        this.promptFromSponsor = promptFromSponsor;
    }
}
