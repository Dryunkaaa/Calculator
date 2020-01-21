package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {

    // height and width
    private final int BUTTON_SIZE = 50;

    private final String[] BUTTON_NAMES = new String[]{
            "AC", "<", "%", "/", "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "0", "^2", ".", "="
    };

    private final String[] STANDARD_MATH_OPERATIONS = new String[]{
            "+", "-", "/", "*"
    };

    private Map<String, MathOperation> mathOperations = new HashMap<>();
    private Map<String, AdditionalMathOperation> otherOperations = new HashMap<>();
    private List<Button> buttons = new ArrayList<>();
    private TextField textField;
    private String currentOperation;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        initOperationsMap();
        createTextField(root);
        createButtons();
        setButtonLocation(root);
        primaryStage.setScene(new Scene(root, 200, 290));
        primaryStage.show();
    }

    private void initOperationsMap() {
        mathOperations.put("+", new PlusOperation());
        mathOperations.put("-", new MinusOperation());
        mathOperations.put("*", new MultiplyOperation());
        mathOperations.put("/", new DivideOperation());
        otherOperations.put("^2", new SquaringOperation());
        otherOperations.put("%", new FindPercentOperation());
    }

    private void createButtons() {
        for (String buttonName : BUTTON_NAMES) {
            Button button = new Button(buttonName);
            buttons.add(button);
            if (Character.isDigit(button.getText().charAt(0))) {
                button.setOnAction(event -> {
                    if (textField.getText().equals("0")) textField.clear();
                    textField.setText(textField.getText() + button.getText());
                });

            } else {
                checkIfStandardMathOperationSelected(button);

                if (button.getText().equals(".")) {
                    button.setOnAction(event -> launchPointClickEvent());

                } else if (button.getText().equals("AC")) {
                    button.setOnAction(event -> textField.setText("0"));

                } else if (button.getText().equals("<")) {
                    button.setOnAction(event -> eraseSymbol());

                } else if (otherOperations.containsKey(button.getText())) {
                    button.setOnAction(event -> launchAdditionalOperation(button));
                }
            }
        }
    }

    private void launchAdditionalOperation(Button button){
        AdditionalMathOperation otherOperation = otherOperations.get(button.getText());
        float numberToCalc = Float.parseFloat(textField.getText());
        textField.setText(String.valueOf(otherOperation.calculate(numberToCalc)));
    }

    private void launchPointClickEvent() {
        String text = textField.getText();
        if (text.length() > 0) {
            boolean lastCharacterIsDigit = Character.isDigit(text.charAt(text.length() - 1));
            if (lastCharacterIsDigit) {
                String currentNumber = getFirstNumber();
                if (containsSecondNumber()) currentNumber = getSecondNumber();
                if (!currentNumber.contains(".")) textField.setText(text + ".");
            }
        }
    }

    private void eraseSymbol(){
        if (textField.getText().length() > 0) {
            textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
            if (textField.getText().isEmpty()) textField.setText("0");
        }
    }

    private void checkIfStandardMathOperationSelected(Button button) {
        for (String operation : STANDARD_MATH_OPERATIONS) {
            if (button.getText().equals(operation)) button.setOnAction(event -> {
                if (!textField.getText().isEmpty() && !textField.getText().endsWith(button.getText()) && !containsSecondNumber()) {
                    currentOperation = button.getText();
                    textField.setText(textField.getText() + currentOperation);
                }
            });
        }
        if (button.getText().equals("=")) {
            button.setOnAction(event -> {
                if (!isEndWithAnOperationCharacter() && containsSecondNumber()) {
                    MathOperation mathOperation = mathOperations.get(currentOperation);
                    textField.setText(String.valueOf(mathOperation.calculate(Float.parseFloat(getFirstNumber()), Float.parseFloat(getSecondNumber()))));
                }
            });
        }
    }

    private String getFirstNumber() {
        String result = textField.getText();
        if (currentOperation != null) {
            result = textField.getText().split("\\" + currentOperation)[0];
        }
        return result;
    }

    private String getSecondNumber() {
        return textField.getText().split("\\" + currentOperation)[1];
    }

    private void setButtonLocation(Pane root) {
        int startX = 0;
        int startY = (int) (textField.getTranslateY()+textField.getPrefHeight());
        int index = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                Button button = buttons.get(index);
                button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
                button.setTranslateX(startX);
                button.setTranslateY(startY);
                root.getChildren().add(button);
                startX += BUTTON_SIZE;
                index++;
            }
            startX = 0;
            startY += BUTTON_SIZE;
        }
    }

    private void createTextField(Pane root) {
        textField = new TextField();
        textField.setText("0");
        textField.setPrefSize(200, 40);
        textField.setFont(new Font(20));
        textField.setTranslateX(0);
        textField.setTranslateY(0);
        textField.setEditable(false);
        root.getChildren().add(textField);
    }

    private boolean containsSecondNumber() {
        if (currentOperation == null) return false;
        return textField.getText().contains(currentOperation);
    }

    private boolean isEndWithAnOperationCharacter() {
        if (currentOperation == null) return true;
        if (textField.getText().lastIndexOf(currentOperation) == textField.getText().length() - 1) return true;
        return false;
    }

}
