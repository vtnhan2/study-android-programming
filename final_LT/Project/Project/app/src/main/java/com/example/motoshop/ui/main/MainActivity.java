package com.example.motoshop.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.motoshop.R;
import com.example.motoshop.ui.login.LoginActivity;
import com.example.motoshop.ui.sales.CreateSaleActivity;
import com.example.motoshop.ui.inventory.AddEditMotorcycleActivity;
import com.example.motoshop.ui.repair.CreateRepairActivity;
import com.example.motoshop.utils.FirebaseSeeder;
import com.example.motoshop.utils.LocaleHelper;
import com.example.motoshop.utils.ThemeHelper;
import com.example.motoshop.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * MainActivity: Quản lý Toolbar và Điều hướng chuẩn.
 * - Khôi phục nút Back cho Kho xe và Khách hàng.
 * - Giữ Dashboard, Sales và Repair là các tab chính.
 */
public class MainActivity extends AppCompatActivity {

    private UserSession session;
    private NavController navController;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

            filterBottomMenu(bottomNav.getMenu());

            // ĐÚNG YÊU CẦU: Chỉ Dashboard, Sales và Repair là trang chính (không hiện nút Back).
            // Inventory và Customer sẽ tự động hiện nút Back để quay về Dashboard.
            AppBarConfiguration.Builder builder = new AppBarConfiguration.Builder(
                    R.id.nav_dashboard, R.id.nav_sales, R.id.nav_repair);

            if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                androidx.navigation.NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
                navGraph.setStartDestination(R.id.nav_user_profile);
                navController.setGraph(navGraph);
                
                builder = new AppBarConfiguration.Builder(R.id.nav_user_profile, R.id.nav_inventory);
            }
            
            AppBarConfiguration appBarConfiguration = builder.build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Cập nhật lại Menu Toolbar và xử lý highlight BottomNav cho User Profile
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                invalidateOptionsMenu();
                if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                    if (destination.getId() == R.id.nav_user_profile) {
                        MenuItem custItem = bottomNav.getMenu().findItem(R.id.nav_customer);
                        if (custItem != null) custItem.setChecked(true);
                    } else if (destination.getId() == R.id.nav_inventory) {
                        MenuItem invItem = bottomNav.getMenu().findItem(R.id.nav_inventory);
                        if (invItem != null) invItem.setChecked(true);
                    }
                }
            });

            // Intercept BottomNav clicks cho ROLE_USER
            bottomNav.setOnItemSelectedListener(item -> {
                if (UserSession.ROLE_USER.equals(session.getUserRole())) {
                    androidx.navigation.NavOptions options = new androidx.navigation.NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setRestoreState(true)
                            .setPopUpTo(navController.getGraph().getStartDestination(), false, true)
                            .build();

                    if (item.getItemId() == R.id.nav_customer) {
                        navController.navigate(R.id.nav_user_profile, null, options);
                        return true;
                    } else if (item.getItemId() == R.id.nav_inventory) {
                        navController.navigate(R.id.nav_inventory, null, options);
                        return true;
                    }
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }

    private void filterBottomMenu(Menu menu) {
        String role = session.getUserRole();
        MenuItem dash = menu.findItem(R.id.nav_dashboard);
        MenuItem inv = menu.findItem(R.id.nav_inventory);
        MenuItem cust = menu.findItem(R.id.nav_customer);
        MenuItem sale = menu.findItem(R.id.nav_sales);
        MenuItem rep = menu.findItem(R.id.nav_repair);

        if (UserSession.ROLE_USER.equals(role)) {
            if (cust != null) {
                cust.setTitle("Cá nhân");
                cust.setIcon(R.drawable.ic_customer);
            }
            if (inv != null) inv.setTitle("Tham khảo xe");
            if (dash != null) dash.setVisible(false);
            if (sale != null) sale.setVisible(false);
            if (rep != null) rep.setVisible(false);
        } else if (UserSession.ROLE_SALES.equals(role)) {
            if (rep != null) rep.setVisible(false);
        } else if (UserSession.ROLE_TECHNICIAN.equals(role)) {
            if (inv != null) inv.setVisible(false);
            if (sale != null) sale.setVisible(false);
            if (cust != null) cust.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem = menu.findItem(R.id.action_add_general);
        if (addItem != null && navController != null && navController.getCurrentDestination() != null) {
            int destId = navController.getCurrentDestination().getId();
            // Hiện nút (+) trên Toolbar ở các tab Bán hàng, Kho và Sửa chữa cho Admin/Sale
            boolean canAdd = (destId == R.id.nav_sales || destId == R.id.nav_inventory || destId == R.id.nav_repair);
            addItem.setVisible(canAdd && !UserSession.ROLE_USER.equals(session.getUserRole()));
        }

        boolean isAdmin = UserSession.ROLE_ADMIN.equals(session.getUserRole());
        MenuItem aiItem = menu.findItem(R.id.action_ai);
        if (aiItem != null) aiItem.setVisible(isAdmin || UserSession.ROLE_SALES.equals(session.getUserRole()));
        MenuItem importItem = menu.findItem(R.id.action_import_data);
        if (importItem != null) importItem.setVisible(isAdmin);
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_general) {
            handleQuickAdd();
            return true;
        } else if (id == R.id.action_ai) {
            navController.navigate(R.id.nav_ai);
            return true;
        } else if (id == R.id.action_import_data) {
            FirebaseSeeder.uploadSeedData(this);
            return true;
        } else if (id == R.id.action_logout) {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleQuickAdd() {
        if (navController == null || navController.getCurrentDestination() == null) return;
        int destId = navController.getCurrentDestination().getId();

        if (destId == R.id.nav_sales) {
            startActivity(new Intent(this, CreateSaleActivity.class));
        } else if (destId == R.id.nav_inventory) {
            startActivity(new Intent(this, AddEditMotorcycleActivity.class));
        } else if (destId == R.id.nav_repair) {
            startActivity(new Intent(this, CreateRepairActivity.class));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void showSettingsDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        MaterialButtonToggleGroup toggleTheme = view.findViewById(R.id.toggleTheme);

        if (ThemeHelper.isDarkMode(this)) toggleTheme.check(R.id.btnDarkTheme);
        else toggleTheme.check(R.id.btnLightTheme);

        new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setPositiveButton(R.string.btn_ok, (dialog, which) -> {
                    int selectedTheme = (toggleTheme.getCheckedButtonId() == R.id.btnDarkTheme) ?
                            AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                    ThemeHelper.saveTheme(this, selectedTheme);
                    recreate();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
