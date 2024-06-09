package com.example.fakenote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fakenote.data.DataManager;
import com.example.fakenote.data.NoteBook;

import com.example.fakenote.ui.fakenote.FakenoteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fakenote.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PAGE_INFO
            = "com.example.android.FakeNote.extra.pageInfo";
    public FakenoteFragment NoteFragment;
    public MenuItem RenameItem;

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_fakeNote, R.id.navigation_search, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_async) {
                    // TODO:同步数据
                    // 确认已经登录
                    if (!DataManager.Instance.isLoggedIn()) {
                        Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(MainActivity.this, "正在同步...", Toast.LENGTH_SHORT).show();
                    }
                } else if (menuItem.getItemId() == R.id.action_rename) {
                    ActionRename();
                }
                return true;
            }
        });

        if (DataManager.Instance == null)
            DataManager.Instance = new DataManager();
        DataManager.Instance.dbManager.setContext(this);
        DataManager.Instance.dbManager.open();
        DataManager.Instance.Initialize();
        // 测试数据
        NoteBook b1 = DataManager.Instance.Data.AddNoteBook("Test Notebook 1");
        b1.AddPage("Test Page 1");
        b1.AddPage("Test Page 2");
        NoteBook b2 = DataManager.Instance.Data.AddNoteBook("Test Notebook 2");
        b2.AddPage("Test Page 1");
        b2.AddPage("Test Page 2");
    }
    public void onAddClicked(View view) {
        ActionAdd();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //在return之前加载出toolbar要加载的资源文件
        getMenuInflater().inflate(R.menu.fakenote_toolbar_menu, menu);
        RenameItem = menu.getItem(0);
        if (RenameItem != null)
            RenameItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    //这个方法是菜单的点击监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NoteFragment.RecycAdapter.BackState();
            if (NoteFragment.RecycAdapter.GetState() == FakenoteFragment.ItemState.NOTEBOOK) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                RenameItem.setVisible(false);
            }

            getSupportActionBar().setTitle(NoteFragment.GetCurrentName()
            );
        }
        return super.onOptionsItemSelected(item);
    }

    public void ActionAdd() {
        final EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        switch (NoteFragment.RecycAdapter.GetState()) {
            case NOTEBOOK:
                dialog.setTitle("新笔记本名");
                break;

            case PAGE:
                dialog.setTitle("新页面名");
                break;
        }

        dialog.setView(editText);
        dialog.setCancelable(false);

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                if (newName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "名称不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    boolean addFail = false;
                    switch (NoteFragment.RecycAdapter.GetState()) {
                        case NOTEBOOK:
                            addFail = DataManager.Instance.Data.AddNoteBook(newName) == null;
                            break;
                        case PAGE:
                            addFail = NoteFragment.CurrentBook.AddPage(newName) == null;
                            break;
                    }
                    if (addFail)
                        Toast.makeText(MainActivity.this, "名称重复！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
        editText.requestFocus();
    }

    public void ActionRename() {
        final EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("重命名：");
        dialog.setView(editText);
        dialog.setCancelable(false);

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                if (newName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "名称不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    boolean renameSuc = false;
                    switch (NoteFragment.RecycAdapter.GetState()) {

                        case PAGE:
                            renameSuc = NoteFragment.CurrentBook.Rename(newName);
                            break;
                    }
                    if (renameSuc)
                        getSupportActionBar().setTitle(newName);
                    else
                        Toast.makeText(MainActivity.this, "名称重复！", Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
        editText.requestFocus();
    }
}