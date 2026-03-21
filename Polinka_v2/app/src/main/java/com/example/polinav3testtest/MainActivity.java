package com.example.polinav3testtest;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.sanbot.opensdk.base.TopBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends TopBaseActivity {

    private Spinner modelSpinner;
    private TextView chatLog;
    private EditText messageInput;
    private Button btnSend;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register(MainActivity.class);
        setContentView(R.layout.activity_main);

        // 1. Inicjalizacja widoków
        modelSpinner = findViewById(R.id.modelSpinner);
        chatLog = findViewById(R.id.chatLog);
        messageInput = findViewById(R.id.messageInput);
        btnSend = findViewById(R.id.btnSend);
        btnClear = findViewById(R.id.btnClear);

        // 2. Konfiguracja Spinnera (Dropdownu modeli)
        String[] models = {"DeepSeek V3", "Venice (Dolphin Mistral)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

        // 3. Obsługa przycisku "Wyczyść"
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatLog.setText("Witaj w AI Messenger!\n");
            }
        });

        // 4. Obsługa przycisku "Wyślij"
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String userText = messageInput.getText().toString().trim();
        if (userText.isEmpty()) {
            return;
        }

        // Dodanie wiadomości użytkownika do logu
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        appendMessage(time, "Ty", userText);
        messageInput.setText("");

        // TODO: Tutaj w przyszłości dodamy wywołanie API OpenRouter!
        appendMessage(time, "System", "Oczekuję na podpięcie API...");
    }

    private void appendMessage(String time, String sender, String text) {
        String currentText = chatLog.getText().toString();
        String newMessage = "[" + time + "] " + sender + ": " + text + "\n";
        chatLog.setText(currentText + newMessage);
    }

    @Override
    protected void onMainServiceConnected() {
        // Specyficzne dla Sanbota - gotowe do użycia, np. do sterowania ruchem, jeśli chcesz
    }
}