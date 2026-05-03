package com.example.motoshop.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.motoshop.R;
import com.example.motoshop.ui.login.LoginActivity;
import com.example.motoshop.utils.FirebaseSeeder;
import com.example.motoshop.utils.LocaleHelper;
import com.example.motoshop.utils.ThemeHelper;
import com.example.motoshop.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * MainActivity: Quản lý DrawerLayout, BottomNavigation và điều hướng.
 * Giao diện Bike App với Side Menu Drawer.
 */
public class MainActivity extends AppCompatActivity {

    private UserSession session;
    private NavController navController;
    private DrawerLayout drawerLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSession(this);
        drawerLayout = findViewById(R.id.drawerLayout);

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Cấu hình lại Start Destination dựa trên Role
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                navGraph.setStartDestination(R.id.userDashboardFragment);
            } else {
                navGraph.setStartDestination(R.id.nav_dashboard);
            }
            navController.setGraph(navGraph);

            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            
            // Thay đổi Menu BottomNav theo Role
            bottomNav.getMenu().clear();
            if (UserSession.ROLE_ADMIN.equals(session.getUserRole())) {
                bottomNav.inflateMenu(R.menu.menu_admin);
            } else if (UserSession.ROLE_SALES.equals(session.getUserRole())) {
                bottomNav.inflateMenu(R.menu.menu_sales);
            } else if (UserSession.ROLE_TECHNICIAN.equals(session.getUserRole())) {
                bottomNav.inflateMenu(R.menu.menu_tech);
            } else {
                bottomNav.inflateMenu(R.menu.menu_user);
            }

            // Setup BottomNav with NavController
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

        // Setup Drawer
        setupDrawer();
        setupMenuByRole();
    }

    // Chuẩn bị view, dữ liệu hoặc sự kiện cần dùng cho màn hình.
    private void setupDrawer() {
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Cập nhật header drawer với tên user
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView tvDrawerWelcome = headerView.findViewById(R.id.tvDrawerWelcome);
            tvDrawerWelcome.setText("Welcome " + session.getUserName() + "!");
        }

        // Xử lý click menu item trong drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.drawer_home) {
                // Chuyển về Home (Dashboard)
                if (navController != null) {
                    navController.navigate(R.id.nav_dashboard);
                }
            } else if (id == R.id.drawer_best_deal) {
                // Chuyển sang Inventory (Search)
                if (navController != null) {
                    navController.navigate(R.id.nav_inventory);
                }
            } else if (id == R.id.drawer_notifications) {
                Toast.makeText(this, "Notifications - Coming soon!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_rate_us) {
                Toast.makeText(this, "Rate Us - Thank you!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_help) {
                Toast.makeText(this, "Help Center - Coming soon!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_import_data) {
                FirebaseSeeder.uploadSeedData(this);
            } else if (id == R.id.drawer_profile) {
                if (navController != null) {
                    navController.navigate(R.id.nav_user_profile);
                }
            } else if (id == R.id.drawer_sign_out) {
                session.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupMenuByRole() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        Menu menu = navigationView.getMenu();
        String role = session.getUserRole();

        // 1. Quản lý (Admin): Thấy tất cả
        if (UserSession.ROLE_ADMIN.equals(role)) {
            return;
        }

        // 2. Nhân viên bán hàng (Sales)
        if (UserSession.ROLE_SALES.equals(role)) {
            // Thấy tất cả trừ Import dữ liệu (tuỳ chọn)
            // menu.findItem(R.id.drawer_import_data).setVisible(false);
            return;
        }

        // 3. Kỹ thuật viên (Technician)
        if (UserSession.ROLE_TECHNICIAN.equals(role)) {
            menu.findItem(R.id.drawer_import_data).setVisible(false);
            // Có thể ẩn thêm các mục kinh doanh nếu cần
            return;
        }

        // 4. Khách hàng (User)
        if (UserSession.ROLE_USER.equals(role)) {
            menu.findItem(R.id.drawer_import_data).setVisible(false);
            // Ẩn các mục nội bộ của cửa hàng
            // menu.findItem(R.id.drawer_notifications).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }

    // Mở drawer từ bên ngoài (DashboardFragment).
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
