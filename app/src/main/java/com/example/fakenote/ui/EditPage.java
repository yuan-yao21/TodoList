package com.example.fakenote.ui;

import static android.os.Environment.DIRECTORY_DCIM;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.fakenote.ui.view.DelEditText;
import com.example.fakenote.MainActivity;
import com.example.fakenote.data.DataManager;
import com.example.fakenote.data.NotePage;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fakenote.R;
import com.example.fakenote.databinding.ActivityEditPageBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditPage extends AppCompatActivity {

    private ActivityEditPageBinding binding;
    private NotePage _page;
    private CollapsingToolbarLayout _toolbarLayout;
    private LinearLayout _mainContentLayout;
    private int mLastHeight = 0;
    private View _inputToolkit;
    private ActivityResultLauncher<Intent> _pickImageLauncher;
    private ActivityResultLauncher<Intent> _cameraLauncher;
    private Bitmap _newBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DelEditText.EditingLine = null;

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.action_rename){
                    actionRename();
                }else if (menuItem.getItemId()==R.id.action_addPic){
                    actionAddPicture();
                }else if (menuItem.getItemId()==R.id.action_addPicFromCam){
                    actionTakePicture();
                } else if (menuItem.getItemId() == R.id.action_addAudio) {
                    actionAddAudio();
                }
                return true;
            }
        });

        _toolbarLayout = binding.toolbarLayout;
        _toolbarLayout.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        _mainContentLayout = findViewById(R.id.mainContentLayout);

        ArrayList<String> pageInfo = (ArrayList<String>) getIntent().getStringArrayListExtra(MainActivity.EXTRA_PAGE_INFO);
        _page = DataManager.Instance.Data.GetPage(pageInfo);
        _toolbarLayout.setTitle(_page.Name);

        _mainContentLayout.addView(new DelEditText(_mainContentLayout,null));

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            // TODO: 保存笔记内容……自动保存！
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        _pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                _newBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                DelEditText.EditingLine.AddPicture(_newBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        _cameraLauncher = registerForActivityResult(
                // 回调函数的参数将是一个ActivityResult
                new ActivityResultContracts.StartActivityForResult(),
                // 回调函数
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Uri uri = data.getData();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                DelEditText.EditingLine.AddPicture(imageBitmap);
                            }
                        }
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //在return之前加载出toolbar要加载的资源文件
        getMenuInflater().inflate(R.menu.menu_edit_page, menu);

        // 设置图标颜色
        setColorForMenuItem(menu, R.id.action_rename, R.color.basic_color);
        setColorForMenuItem(menu, R.id.action_addPicFromCam, R.color.basic_color);
        setColorForMenuItem(menu, R.id.action_addPic, R.color.basic_color);
        setColorForMenuItem(menu, R.id.action_addAudio, R.color.basic_color);
        setColorForMenuItem(menu, R.id.action_delete, R.color.delete_color);

        return true;
    }

    private void setColorForMenuItem(Menu menu, int menuItemId, int color) {
        MenuItem item = menu.findItem(menuItemId);
        Drawable drawable = DrawableCompat.wrap(item.getIcon());
        DrawableCompat.setTint(drawable, getResources().getColor(color));
        item.setIcon(drawable);
    }

    private void actionRename(){
        final EditText editText = new EditText(EditPage.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditPage.this);
        dialog.setTitle("重命名：");
        dialog.setView(editText);
        dialog.setCancelable(false);

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                if (newName.isEmpty()){
                    Toast.makeText(EditPage.this, "名称不能为空！",Toast.LENGTH_SHORT).show();
                }else{
                    boolean renameSuc = false;
                    renameSuc = _page.Rename(newName);
                    if (renameSuc)
                        _toolbarLayout.setTitle(_page.Name);
                    else
                        Toast.makeText(EditPage.this, "名称重复！",Toast.LENGTH_SHORT).show();

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
    private void actionAddPicture(){
        if(DelEditText.EditingLine==null){
            Toast.makeText(this, "请先选择添加位置",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        _pickImageLauncher.launch(intent);
    }
    // TODO: 修复拍照功能
    private void actionTakePicture(){
        if (_cameraLauncher != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                _cameraLauncher.launch(takePictureIntent);
            }else{
                Toast.makeText(this, "找不到相机，请打开相机拍照后导入",Toast.LENGTH_SHORT).show();
            }
        }
    }
    // TODO: 添加音频
    private void actionAddAudio(){

    }




}