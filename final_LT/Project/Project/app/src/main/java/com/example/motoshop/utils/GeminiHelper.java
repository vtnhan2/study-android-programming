package com.example.motoshop.utils;

import android.util.Log;
import com.example.motoshop.BuildConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Lớp hỗ trợ xử lý chức năng AI Gemini.
 */
public class GeminiHelper {

    private static final String TAG = "GEMINI";
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private static final String MODEL_NAME = "gemini-2.5-flash";

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL_NAME
                    + ":generateContent?key=" + API_KEY;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    private static void runInBackground(Runnable task) {
        new Thread(task).start();
    }

    public static void ask(String userMessage, GeminiCallback callback) {
        runInBackground(() -> {
            try {
                JSONObject body = new JSONObject();
                JSONArray contents = new JSONArray();
                contents.put(buildContent("user", userMessage));
                body.put("contents", contents);

                String result = callApi(body.toString());
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void chat(JSONArray history, String newMessage, GeminiCallback callback) {
        runInBackground(() -> {
            try {
                history.put(buildContent("user", newMessage));
                JSONObject body = new JSONObject();
                body.put("contents", history);
                JSONObject sysInst = new JSONObject();
                JSONArray sysParts = new JSONArray();
                sysParts.put(new JSONObject().put("text", "Bạn là trợ lý AI thông minh của MotoShop."));
                sysInst.put("parts", sysParts);
                body.put("system_instruction", sysInst);

                String responseText = callApi(body.toString());
                history.put(buildContent("model", responseText));
                callback.onSuccess(responseText);
            } catch (Exception e) {
                Log.e(TAG, "Chat error", e);
                callback.onError("Lỗi Chat: " + e.getMessage());
            }
        });
    }

    private static JSONObject buildContent(String role, String text) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("role", role);
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", text);
        parts.put(part);
        content.put("parts", parts);
        return content;
    }

    private static String callApi(String jsonBody) throws Exception {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new Exception("Chưa cấu hình API Key.");
        }

        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(API_URL).post(requestBody).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                Log.e(TAG, "Error Response: " + responseBody);
                if (response.code() == 429) {
                    throw new Exception("Hệ thống AI đang bận (quá nhiều yêu cầu). Vui lòng thử lại sau 1 phút.");
                }
                throw new Exception("Lỗi " + response.code());
            }
            JSONObject json = new JSONObject(responseBody);
            if (!json.has("candidates") || json.getJSONArray("candidates").length() == 0) {
                throw new Exception("AI không trả về kết quả.");
            }
            return json.getJSONArray("candidates").getJSONObject(0)
                    .getJSONObject("content").getJSONArray("parts")
                    .getJSONObject(0).getString("text").trim();
        }
    }

    public static void suggestRepairs(String issueDescription, String servicesJson, GeminiCallback callback) {
        String prompt = "Bạn là chuyên gia kỹ thuật sửa chữa xe máy. Khách hàng mô tả lỗi: '" + issueDescription + "'. " +
                "Dựa vào danh sách dịch vụ của cửa hàng (JSON array chứa documentId, name, description): " + servicesJson + ", hãy chọn ra các dịch vụ phù hợp nhất để xử lý lỗi này. " +
                "Yêu cầu nghiêm ngặt: CHỈ trả về một JSON array chứa các documentId, ví dụ: [\"id1\", \"id2\"]. " +
                "Không giải thích, không dùng markdown code blocks, không trả về bất kỳ văn bản nào khác ngoài mảng JSON.";
        ask(prompt, callback);
    }

    public static void searchMotorcycle(String description, String inventoryJson, GeminiCallback callback) {
        String prompt = "Tìm xe phù hợp: " + description + ". Kho xe: " + inventoryJson + ". Trả về JSON array ID của xe.";
        ask(prompt, callback);
    }
}
