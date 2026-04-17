package com.example.polinav3testtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.sanbot.opensdk.base.TopBaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends TopBaseActivity {

    private Spinner modelSpinner;
    private TextView chatLog;
    private EditText messageInput;
    private Button btnSend;
    private Button btnClear;

    private final String API_KEY = "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";
    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Mapowanie nazw modeli na identyfikatory OpenRouter
    private final String[] modelIds = {
            "deepseek/deepseek-chat",
            "venice-ai/dolphin-mistral-24b-venice-edition:free"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register(MainActivity.class);
        setContentView(R.layout.activity_main);

        modelSpinner = findViewById(R.id.modelSpinner);
        chatLog = findViewById(R.id.chatLog);
        messageInput = findViewById(R.id.messageInput);
        btnSend = findViewById(R.id.btnSend);
        btnClear = findViewById(R.id.btnClear);

        String[] models = {"DeepSeek V3", "Venice (Dolphin Mistral)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

        btnClear.setOnClickListener(v -> chatLog.setText("Witaj w AI Messenger!\n"));

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String userText = messageInput.getText().toString().trim();
        if (userText.isEmpty()) return;

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        appendMessage(time, "Ty", userText);
        messageInput.setText("");
        btnSend.setEnabled(false);

        String selectedModel = modelIds[modelSpinner.getSelectedItemPosition()];

        executor.execute(() -> {
            String response = callOpenRouter(userText, selectedModel);
            String replyTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            mainHandler.post(() -> {
                appendMessage(replyTime, "AI", response);
                btnSend.setEnabled(true);
            });
        });
    }

    private String callOpenRouter(String userMessage, String model) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("HTTP-Referer", "https://sanbot.local");
            conn.setDoOutput(true);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", userMessage);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject body = new JSONObject();
            body.put("model", model);
            body.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            BufferedReader reader;
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) errBuilder.append(line);
                return "Błąd API (" + responseCode + "): " + errBuilder;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JSONObject jsonResponse = new JSONObject(sb.toString());
            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            return "Błąd połączenia: " + e.getMessage();
        }
    }

    private void appendMessage(String time, String sender, String text) {
        String currentText = chatLog.getText().toString();
        chatLog.setText(currentText + "[" + time + "] " + sender + ": " + text + "\n");
    }

    @Override
    protected void onMainServiceConnected() {}
}