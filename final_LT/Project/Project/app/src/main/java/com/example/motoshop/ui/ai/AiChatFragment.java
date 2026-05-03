package com.example.motoshop.ui.ai;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motoshop.R;
import com.example.motoshop.data.model.Motorcycle;
import com.example.motoshop.utils.GeminiHelper;
import com.example.motoshop.viewmodel.MotorcycleViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị giao diện trò chuyện với trợ lý AI.
 * Đã gỡ bỏ Toolbar nội bộ để dùng Toolbar chung của hệ thống, tránh lỗi crash và 2 nút Back.
 */
public class AiChatFragment extends Fragment {

    private MotorcycleViewModel motorViewModel;
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnAiSearch;
    private ProgressBar progressBar;

    private final JSONArray chatHistory = new JSONArray();
    private List<Motorcycle> currentInventory = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        motorViewModel = new ViewModelProvider(this).get(MotorcycleViewModel.class);
        motorViewModel.allMotorcycles.observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                currentInventory = list;
            }
        });

        initViews(view);
        setupRecyclerView();
        setupQuickChips(view);

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString().trim()));
        btnAiSearch.setOnClickListener(v -> showAiSearchDialog());

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                androidx.navigation.Navigation.findNavController(view).navigateUp();
            });
        }

        if (adapter.getItemCount() == 0) {
            addMessage(getString(R.string.welcome_ai), false);
        }
    }

    private void initViews(View v) {
        rvChat = v.findViewById(R.id.rvChat);
        etMessage = v.findViewById(R.id.etMessage);
        btnSend = v.findViewById(R.id.btnSend);
        btnAiSearch = v.findViewById(R.id.btnAiSearch);
        progressBar = v.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter();
        if (rvChat != null) {
            rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
            rvChat.setAdapter(adapter);
        }
    }

    private void setupQuickChips(View v) {
        Chip chipBestSeller = v.findViewById(R.id.chipBestSeller);
        Chip chipMaintenance = v.findViewById(R.id.chipMaintenance);
        Chip chipConsult = v.findViewById(R.id.chipConsult);
        Chip chipStockSuggest = v.findViewById(R.id.chipStockSuggest);

        View.OnClickListener clickListener = view -> sendMessage(((Chip) view).getText().toString());

        if (chipBestSeller != null) chipBestSeller.setOnClickListener(clickListener);
        if (chipMaintenance != null) chipMaintenance.setOnClickListener(clickListener);
        if (chipConsult != null) chipConsult.setOnClickListener(clickListener);
        if (chipStockSuggest != null) {
            chipStockSuggest.setOnClickListener(v1 -> {
                String inventoryJson = new Gson().toJson(currentInventory);
                analyzeInventory(inventoryJson);
            });
        }
    }

    private void analyzeInventory(String inventoryJson) {
        addMessage("📊 Đang phân tích tồn kho...", true);
        setLoading(true);

        String prompt = "Bạn là chuyên gia quản lý cửa hàng. Tồn kho (JSON): " + inventoryJson
                + "\nPhân tích ngắn gọn: xe nào hết, xe nào tồn nhiều, nên nhập gì?";

        GeminiHelper.ask(prompt, new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    addMessage(response, false);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendMessage(String text) {
        if (TextUtils.isEmpty(text)) return;

        addMessage(text, true);
        if (etMessage != null) etMessage.setText("");
        setLoading(true);

        GeminiHelper.chat(chatHistory, text, new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    addMessage(response, false);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        if (adapter != null && rvChat != null) {
            adapter.addMessage(new ChatMessage(text, isUser));
            rvChat.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (btnSend != null) btnSend.setEnabled(!isLoading);
        if (etMessage != null) etMessage.setEnabled(!isLoading);
        if (btnAiSearch != null) btnAiSearch.setEnabled(!isLoading);
        
        // Vô hiệu hóa các chip để tránh spam API gây lỗi 429
        View v = getView();
        if (v != null) {
            int[] chipIds = {R.id.chipBestSeller, R.id.chipMaintenance, R.id.chipConsult, R.id.chipStockSuggest};
            for (int id : chipIds) {
                View chip = v.findViewById(id);
                if (chip != null) chip.setEnabled(!isLoading);
            }
        }
    }

    private void showAiSearchDialog() {
        EditText etInput = new EditText(getContext());
        etInput.setHint("Ví dụ: Tìm xe màu đỏ dưới 50 triệu...");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("🔍 Tìm xe bằng AI")
                .setView(etInput)
                .setPositiveButton("Tìm", (dialog, which) -> {
                    String req = etInput.getText().toString().trim();
                    if (!req.isEmpty()) {
                        performAiSearch(req);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performAiSearch(String request) {
        setLoading(true);
        String inventoryJson = new Gson().toJson(currentInventory);

        GeminiHelper.searchMotorcycle(request, inventoryJson, new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    try {
                        String clean = response
                                .replaceAll("```json", "")
                                .replaceAll("```", "")
                                .trim();

                        List<Integer> ids = new Gson().fromJson(
                                clean,
                                new TypeToken<List<Integer>>() {}.getType()
                        );
                        showSearchResults(ids);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "AI trả về kết quả không đúng định dạng", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showSearchResults(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy xe phù hợp", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Motorcycle> results = new ArrayList<>();
        for (Motorcycle m : currentInventory) {
            if (ids.contains(m.id)) {
                results.add(m);
            }
        }

        AiSearchResultAdapter resAdapter = new AiSearchResultAdapter(results);
        RecyclerView rvResults = new RecyclerView(requireContext());
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResults.setAdapter(resAdapter);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Kết quả tìm kiếm")
                .setView(rvResults)
                .setPositiveButton("OK", null)
                .show();
    }
}
