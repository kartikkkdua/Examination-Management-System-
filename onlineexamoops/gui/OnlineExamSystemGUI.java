// onlineexam/gui/OnlineExamSystemGUI.java
package onlineexamoops.gui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Question;
import onlineexamoops.util.ExamFileManager;

public class OnlineExamSystemGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private List<Question> questions = new ArrayList<>();
    private Iterator<Question> questionIterator;
    private Question currentQuestion;
    private int score = 0;
    private int totalQuestions = 0;
    private String currentStudentID = "";
    private javax.swing.Timer timer;
    private int timeLeft = 30;
    private final Map<String, String> studentCredentials = new HashMap<>();
    private JLabel questionLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton nextButton;
    private JLabel resultLabel;
    private ExamFileManager fileManager = new ExamFileManager(this);

    public OnlineExamSystemGUI() {
        setTitle("Online Examination System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
        studentCredentials.put("kartik", "kartik123");
        studentCredentials.put("harshika", "harshika123");
        initLoginPanel();
        initExamPanel();
        initResultPanel();
        fileManager.loadQuestionsFromFile(questions);
        initAdminDashboard();
        initStudentDashboard(); // Added student dashboard initialization
    }

    private void initLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        mainPanel.add(loginPanel, "Login");
    }

    private void initStudentDashboard() {
        JPanel studentPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Increased rows for retest
        studentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        JLabel welcomeLabel = new JLabel("Welcome, Student!", SwingConstants.CENTER);
        JButton takeExamButton = new JButton("Take Exam");
        JButton retakeExamButton = new JButton("Retake Exam"); // Added retest button
        JButton logoutButton = new JButton("Logout");
        takeExamButton.addActionListener(e -> {
            startExam();
            cardLayout.show(mainPanel, "Exam");
        });
        retakeExamButton.addActionListener(e -> retakeExam()); // Action for retest
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out.");
            cardLayout.show(mainPanel, "Login");
        });
        studentPanel.add(welcomeLabel);
        studentPanel.add(takeExamButton);
        studentPanel.add(retakeExamButton); // Added retest button
        studentPanel.add(logoutButton);
        mainPanel.add(studentPanel, "StudentDashboard");
    }

    private void initExamPanel() {
        JPanel examPanel = new JPanel(new BorderLayout(10, 10));
        examPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        questionLabel = new JLabel("Question will appear here");
        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();
        optionsGroup = new ButtonGroup();
        nextButton = new JButton("Next");
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);
        nextButton.addActionListener(e -> checkAnswer());
        examPanel.add(questionLabel, BorderLayout.NORTH);
        examPanel.add(optionsPanel, BorderLayout.CENTER);
        examPanel.add(nextButton, BorderLayout.SOUTH);
        mainPanel.add(examPanel, "Exam");
    }

    private void initResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultLabel = new JLabel("", SwingConstants.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout for buttons
        JButton backButton = new JButton("Back to Dashboard");
        JButton feedbackButton = new JButton("Submit Feedback"); // Added feedback button
        JButton exitButton = new JButton("Exit");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "StudentDashboard"));
        feedbackButton.addActionListener(e -> submitFeedback()); // Action for feedback
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(backButton);
        buttonPanel.add(feedbackButton); // Added feedback button
        buttonPanel.add(exitButton);
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(resultPanel, "Result");
    }

    private void submitFeedback() {
        String feedback = JOptionPane.showInputDialog(this, "Please provide your feedback on the exam:");
        if (feedback != null && !feedback.trim().isEmpty()) {
            fileManager.saveFeedback(currentStudentID, feedback);
            JOptionPane.showMessageDialog(this, "Thank you for your feedback!");
        } else if (feedback != null) {
            JOptionPane.showMessageDialog(this, "Feedback cannot be empty.");
        }
        cardLayout.show(mainPanel, "StudentDashboard"); // Go back to dashboard after feedback
    }

    private void retakeExam() {
        startExam();
        cardLayout.show(mainPanel, "Exam");
    }

    private void initAdminDashboard() {
        JPanel adminPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        JButton addQuestionButton = new JButton("1. Add Question");
        JButton viewQuestionsButton = new JButton("2. View Questions");
        JButton saveQuestionsButton = new JButton("3. Save Questions");
        JButton loadQuestionsButton = new JButton("4. Load Questions");
        JButton viewResultsButton = new JButton("5. View Results");
        JButton viewFeedbackButton = new JButton("6. View Feedback");
        JButton logoutButton = new JButton("7. Logout");
        addQuestionButton.addActionListener(e -> addQuestion());
        viewQuestionsButton.addActionListener(e -> viewQuestions());
        saveQuestionsButton.addActionListener(e -> saveQuestions());
        loadQuestionsButton.addActionListener(e -> fileManager.loadQuestionsFromFile(questions));
        viewResultsButton.addActionListener(e -> viewResults());
        viewFeedbackButton.addActionListener(e -> viewFeedback());
        logoutButton.addActionListener(e -> logoutAdmin());
        adminPanel.add(addQuestionButton);
        adminPanel.add(viewQuestionsButton);
        adminPanel.add(saveQuestionsButton);
        adminPanel.add(loadQuestionsButton);
        adminPanel.add(viewResultsButton);
        adminPanel.add(viewFeedbackButton);
        adminPanel.add(logoutButton);
        mainPanel.add(adminPanel, "AdminDashboard");
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.equals("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            cardLayout.show(mainPanel, "AdminDashboard");
        } else if (studentCredentials.containsKey(username) && studentCredentials.get(username).equals(password)) {
            currentStudentID = username;
            JOptionPane.showMessageDialog(this, "Login successful!");
            cardLayout.show(mainPanel, "StudentDashboard"); // Go to student dashboard after login
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void addQuestion() {
        String text = JOptionPane.showInputDialog("Enter the question text:");
        String optionA = JOptionPane.showInputDialog("Enter option A:");
        String optionB = JOptionPane.showInputDialog("Enter option B:");
        String optionC = JOptionPane.showInputDialog("Enter option C:");
        String optionD = JOptionPane.showInputDialog("Enter option D:");
        String answer = JOptionPane.showInputDialog("Enter the correct answer (A, B, C, D):");
        questions.add(new Question(text, optionA, optionB, optionC, optionD, answer));
        JOptionPane.showMessageDialog(this, "Question added successfully.");
    }

    private void viewQuestions() {
        StringBuilder questionList = new StringBuilder("Questions:\n");
        for (Question q : questions) {
            questionList.append(q.getText()).append("\n");
        }
        JOptionPane.showMessageDialog(this, questionList.toString());
    }

    private void saveQuestions() {
        fileManager.saveQuestionsToFile(questions);
    }

    void viewResults() {
        fileManager.viewResults();
    }

    void viewFeedback() {
        fileManager.viewFeedback();
    }

    private void logoutAdmin() {
        JOptionPane.showMessageDialog(this, "Logging out admin.");
        cardLayout.show(mainPanel, "Login");
    }

    private void startExam() {
        questionIterator = questions.iterator();
        totalQuestions = questions.size();
        score = 0;
        nextQuestion();
    }

    private void nextQuestion() {
        if (questionIterator.hasNext()) {
            currentQuestion = questionIterator.next();
            displayCurrentQuestion();
            startTimer();
        } else {
            showResult();
        }
    }

    private void displayCurrentQuestion() {
        questionLabel.setText("<html><h3>" + currentQuestion.getText() + "</h3></html>");
        optionA.setText("A. " + currentQuestion.getOptionA());
        optionB.setText("B. " + currentQuestion.getOptionB());
        optionC.setText("C. " + currentQuestion.getOptionC());
        optionD.setText("D. " + currentQuestion.getOptionD());
        optionsGroup.clearSelection();
    }

    private void startTimer() {
        timeLeft = 30;
        updateTimerLabel();
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                checkAnswer();
            }
        });
        timer.start();
    }

    private void updateTimerLabel() {
        questionLabel.setText("<html><h3>" + currentQuestion.getText() + "</h3><h4>Time left: " + timeLeft + "s</h4></html>");
    }

    private void checkAnswer() {
        String selected = null;
        if (optionA.isSelected()) selected = "A";
        else if (optionB.isSelected()) selected = "B";
        else if (optionC.isSelected()) selected = "C";
        else if (optionD.isSelected()) selected = "D";
        if (selected != null && currentQuestion.getAnswer().equalsIgnoreCase(selected)) {
            score++;
        }
        timer.stop();
        nextQuestion();
    }

    private void showResult() {
        resultLabel.setText("<html><h1>Exam Finished</h1><h2>Score: " + score + "/" + totalQuestions + "</h2></html>");
        fileManager.saveResult(currentStudentID, score, totalQuestions);
        cardLayout.show(mainPanel, "Result");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OnlineExamSystemGUI().setVisible(true));
    }
}