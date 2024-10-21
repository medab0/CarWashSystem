package com.example.carwashsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.concurrent.*;

public class CarWashController {
    @FXML
    private TextField coinInput;
    @FXML
    private Button addCoinButton, waterButton, soapButton, airButton;
    @FXML
    private Label balanceLabel, statusLabel;

    private int balance = 0;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @FXML
    public void initialize() {
        disableAllButtons();
        addCoinButton.setDisable(false);
    }

    @FXML
    private void handleAddCoin() {
        try {
            int coin = Integer.parseInt(coinInput.getText());

            switch (coin) {
                case 1:
                    balance += 1;
                    updateBalanceLabel();
                    statusLabel.setText("Coin added: ₱" + 1);
                    enableAvailableServices();
                    break;
                case 5:
                    balance += 5;
                    updateBalanceLabel();
                    statusLabel.setText("Coin added: ₱" + 5);
                    enableAvailableServices();
                    break;
                case 10:
                    balance += 10;
                    updateBalanceLabel();
                    statusLabel.setText("Coin added: ₱" + 10);
                    enableAvailableServices();
                    break;
                case 20:
                    balance += 20;
                    updateBalanceLabel();
                    statusLabel.setText("Coin added: ₱" + 20);
                    enableAvailableServices();
                    break;
                default:
                    statusLabel.setText("Invalid coin! Only 1, 5, 10, or 20 accepted.");
                    break;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid number.");
        }

        coinInput.clear();
    }


    @FXML
    private void handleWater() {
        startService("Water", 5, 2);
    }

    @FXML
    private void handleSoap() {
        startService("Soap", 10, 1);
    }

    @FXML
    private void handleAir() {
        startService("Air", 15, 3);
    }

    private void startService(String service, int cost, int minutes) {
        if (balance < cost) {
            statusLabel.setText("Insufficient balance for " + service + "!");
            return;
        }

        balance -= cost;
        updateBalanceLabel();
        statusLabel.setText(service + " running for " + minutes + " minute(s)...");

        disableAllButtons();

        executor.schedule(() -> {
            Platform.runLater(() -> {
                statusLabel.setText(service + " completed!");

                if (balance > 0) {
                    showReturnBalanceDialog();
                }

                enableAvailableServices();
            });
        }, minutes, TimeUnit.MINUTES);
    }


    private void showReturnBalanceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Return Balance");
        alert.setHeaderText("Service completed!");
        alert.setContentText("You have a remaining balance of ₱" + balance +
                ". Do you want to return it?");

        ButtonType returnButton = new ButtonType("Return Balance", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(returnButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == returnButton) {
                statusLabel.setText("Returning balance: ₱" + balance);
                balance = 0;
                updateBalanceLabel();
                disableAllButtons();
                addCoinButton.setDisable(false);
            } else if (response == cancelButton) {
                statusLabel.setText("Balance retained: ₱" + balance);
            }
        });
    }



    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: ₱" + balance);
    }

    private void disableAllButtons() {
        waterButton.setDisable(true);
        soapButton.setDisable(true);
        airButton.setDisable(true);
    }

    private void enableAvailableServices() {
        if (balance >= 5) waterButton.setDisable(false);
        if (balance >= 10) soapButton.setDisable(false);
        if (balance >= 15) airButton.setDisable(false);
    }

    public void stop() {
        executor.shutdown();
    }
}
