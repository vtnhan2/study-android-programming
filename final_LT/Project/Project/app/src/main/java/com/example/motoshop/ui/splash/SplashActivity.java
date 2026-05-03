package com.example.motoshop.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.motoshop.R;
import com.example.motoshop.ui.login.LoginActivity;
import com.example.motoshop.ui.main.MainActivity;
import com.example.motoshop.utils.LocaleHelper;
import com.example.motoshop.utils.ThemeHelper;
import com.example.motoshop.utils.UserSession;

// Màn hình xử lý chức năng chính tương ứng với tên Activity này.
public class SplashActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Áp dụng ngôn ngữ trước khi màn hình được tạo.
    @Override
    protected void attachBaseContext(Context newBase) {
        // Áp dụng ngôn ngữ ngay khi khởi động Splash
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    // Khởi tạo màn hình khi Activity được mở.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng theme (Sáng/Tối) ngay khi khởi động Splash
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        if (ivLogo != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(1500);
            ivLogo.startAnimation(fadeIn);
        }


        handler.postDelayed(() -> {
            UserSession session = new UserSession(this);
            if (session.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }

    // Chuyển từ màn hình splash sang màn hình đăng nhập.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
