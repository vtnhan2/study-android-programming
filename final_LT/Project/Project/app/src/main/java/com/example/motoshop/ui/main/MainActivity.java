package com.example.motoshop.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private UserSession session;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAiChat;

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
        bottomNav = findViewById(R.id.bottom_nav);
        fabAiChat = findViewById(R.id.fabAiChat);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                navGraph.setStartDestination(R.id.userDashboardFragment);
            } else {
                navGraph.setStartDestination(R.id.nav_dashboard);
            }
            navController.setGraph(navGraph);

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

            NavigationUI.setupWithNavController(bottomNav, navController);
            setupAiFab();
        }

        setupDrawer();
        setupMenuByRole();
    }

    private void setupAiFab() {
        fabAiChat.setOnClickListener(v -> {
            if (navController != null) navController.navigate(R.id.nav_ai);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Hiển thị bong bóng AI chỉ khi ở màn hình Dashboard chính.
            // Khi chuyển sang các tab khác (Kho xe, Khách hàng, Báo cáo...), bong bóng sẽ tự động "thu về" (ẩn đi).
            boolean isDashboard = destination.getId() == R.id.nav_dashboard || 
                                 destination.getId() == R.id.userDashboardFragment;
            
            if (isDashboard) {
                fabAiChat.show();
            } else {
                fabAiChat.hide();
            }
        });
    }

    private void setupDrawer() {
        NavigationView navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView tvDrawerWelcome = headerView.findViewById(R.id.tvDrawerWelcome);
            tvDrawerWelcome.setText("Welcome " + session.getUserName() + "!");
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.drawer_home) {
                // Pop back to start destination without adding to back stack
                if (navController != null) {
                    navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
                }
            } else if (id == R.id.drawer_best_deal) {
                // Use bottomNav to switch tab — avoids broken back stack
                bottomNav.setSelectedItemId(R.id.nav_inventory);
            } else if (id == R.id.drawer_notifications) {
                Toast.makeText(this, "Notifications - Coming soon!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_rate_us) {
                Toast.makeText(this, "Rate Us - Thank you!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_help) {
                Toast.makeText(this, "Help Center - Coming soon!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_import_data) {
                FirebaseSeeder.uploadSeedData(this);
            } else if (id == R.id.drawer_profile) {
                if (navController != null) navController.navigate(R.id.nav_user_profile);
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

        if (UserSession.ROLE_ADMIN.equals(role)) return;

        if (UserSession.ROLE_SALES.equals(role)) {
            menu.findItem(R.id.drawer_import_data).setVisible(false);
            return;
        }

        if (UserSession.ROLE_TECHNICIAN.equals(role)) {
            menu.findItem(R.id.drawer_import_data).setVisible(false);
            return;
        }

        if (UserSession.ROLE_USER.equals(role)) {
            menu.findItem(R.id.drawer_import_data).setVisible(false);
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

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
