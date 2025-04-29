package onlineexamoops.util;

import model.Question;
import javax.swing.*;
import java.io.*;
import java.util.List;

public class ExamFileManager {
    private JFrame parentFrame;
    private final String QUESTIONS_FILE = "questions.txt";
    private final String RESULTS_FILE = "results.txt";
    private final String FEEDBACK_FILE = "feedback.txt";

    public ExamFileManager(JFrame frame) {
        this.parentFrame = frame;
    }

    public void saveQuestionsToFile(List<Question> questions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTIONS_FILE, false))) {
            for (Question q : questions) {
                writer.write(q.getText() + "," + q.getOptionA() + "," + q.getOptionB() + "," + q.getOptionC() + "," + q.getOptionD() + "," + q.getAnswer());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(parentFrame, "Questions saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error saving questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadQuestionsFromFile(List<Question> questions) {
        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            questions.clear();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    try {
                        questions.add(new Question(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(parentFrame, "Error loading question from line: " + line + ". Skipping this question.", "File Load Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Invalid question format in file: " + line + ". Skipping this line.", "File Format Error", JOptionPane.WARNING_MESSAGE);
                }
            }
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No questions loaded from file or file was empty.", "No Questions", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Questions loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(parentFrame, "Questions file not found. A new one will be created on save.", "File Not Found", JOptionPane.INFORMATION_MESSAGE);
            // It's okay if the file doesn't exist on the first run.
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error loading questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveResult(String studentID, int score, int totalQuestions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULTS_FILE, true))) {
            writer.write("Student ID: " + studentID + ", Score: " + score + "/" + totalQuestions);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error saving result: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewResults() {
        StringBuilder resultData = new StringBuilder("----- Exam Results -----\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(RESULTS_FILE))) {
            String line;
            boolean resultsFound = false;
            while ((line = reader.readLine()) != null) {
                resultData.append(line).append("\n");
                resultsFound = true;
            }
            if (resultsFound) {
                JTextArea textArea = new JTextArea(resultData.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(parentFrame, scrollPane, "Exam Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "No exam results available yet.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(parentFrame, "No exam results available yet.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error reading results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFeedback(String studentID, String feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FEEDBACK_FILE, true))) {
            writer.write("Student ID: " + studentID + ", Feedback: " + feedback);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error saving feedback: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewFeedback() {
        StringBuilder feedbackData = new StringBuilder("----- Student Feedback -----\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            boolean feedbackFound = false;
            while ((line = reader.readLine()) != null) {
                feedbackData.append(line).append("\n");
                feedbackFound = true;
            }
            if (feedbackFound) {
                JTextArea textArea = new JTextArea(feedbackData.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(parentFrame, scrollPane, "Student Feedback", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "No student feedback available yet.", "No Feedback", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(parentFrame, "No student feedback available yet.", "No Feedback", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error reading feedback: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}