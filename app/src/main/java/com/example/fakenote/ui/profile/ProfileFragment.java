package com.example.fakenote.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fakenote.R;
import com.example.fakenote.databinding.FragmentProfileBinding;
import com.example.fakenote.data.DataManager;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private LinearLayout settingsContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel stickyNoteViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        settingsContainer = binding.settingsLayout;

        View root = binding.getRoot();
        // 先判断有没有登录
        if (DataManager.Instance.isLoggedIn()) {
            // 已经登陆，显示用户信息，并显示修改按钮
            postLoggedIn();
        } else {
            // 未登录，显示登录按钮
            notLoggedIn();
        }


        return root;
    }

    private void notLoggedIn() {
        // 未登录，显示登录按钮
        addSettingsOption(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开登录对话框
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("登录/注册");

        // 使用布局填充器来获取自定义对话框视图
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.username_edit_text);
        EditText passwordEditText = dialogView.findViewById(R.id.password_edit_text);

        // 设置对话框按钮
        builder.setNeutralButton("没有用户？注册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 使用OkHttp发送POST请求
                OkHttpClient client = new OkHttpClient();
                String url = "http://116.198.232.203:8000/api/v1/register";

                // 创建请求体
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON error", e);
                }

                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // 异步请求，避免阻塞UI线程
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "请求失败: " + call.request().url(), e);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "网络请求失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }


                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        // 在UI线程上运行
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "注册失败: " + responseData, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("忘记密码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理忘记密码逻辑
                Toast.makeText(getContext(), "忘记密码按钮点击", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                OkHttpClient client = new OkHttpClient();
                String url = "http://116.198.232.203:8000/api/v1/login";

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON error", e);
                }

                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .patch(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "登录请求失败: " + call.request().url(), e);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "登录请求失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(responseData);
                                        String jwt = jsonResponse.getString("jwt");
                                        String nickname = jsonResponse.getString("nickname");
                                        Toast.makeText(getContext(), "登录成功，欢迎 " + nickname, Toast.LENGTH_LONG).show();
                                        // TODO: 保存jwt到SharedPreferences等持久存储
                                        if (getContext() != null) {
                                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("saved_jwt", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("JWT_TOKEN", jwt);
                                            editor.apply();  // 或者 editor.commit(); commit()是同步的，apply()是异步的

                                            // 更新登录状态，表示用户不是游客
                                            DataManager.Instance.Data._isGuest = false;
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "解析登录响应失败", e);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "登录失败: " + responseData, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        builder.show();
    }

    private void postLoggedIn() {
        // 已经登陆，显示用户信息，并显示修改按钮
        fetchUserInfo();
        addSettingsOption_update(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开登录对话框
                showUpdateDialog();
            }
        });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("更新个人信息");

        // 使用布局填充器来获取自定义对话框视图
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
        builder.setView(dialogView);

        EditText passwordEditText = dialogView.findViewById(R.id.password_edit_text);
        EditText nicknameEditText = dialogView.findViewById(R.id.nickname_edit_text);
        EditText mobileEditText = dialogView.findViewById(R.id.mobile_edit_text);
        EditText bioEditText = dialogView.findViewById(R.id.bio_edit_text);

        builder.setNeutralButton("更新个人信息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 从EditText获取数据
                String password = passwordEditText.getText().toString();
                String nickname = nicknameEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                String bio = bioEditText.getText().toString();

                String url = "http://116.198.232.203:8000/api/v1/update";

                // 创建JSONObject
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("password", password);
                    jsonObject.put("nickname", nickname);
                    jsonObject.put("mobile", mobile);
                    jsonObject.put("bio", bio);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON error", e);
                }

                // 发送POST请求更新用户信息
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

                // 从SharedPreferences获取JWT令牌
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("saved_jwt", Context.MODE_PRIVATE);
                String jwtToken = sharedPreferences.getString("JWT_TOKEN", "");

                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + jwtToken)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "请求失败: " + call.request().url(), e);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "网络请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        // 在UI线程上运行
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "更新个人信息成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "更新个人信息失败: " + responseData, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        builder.show();
    }

    public void fetchUserInfo() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://116.198.232.203:8000/api/v1/user";

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved_jwt", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JWT_TOKEN", "");

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + jwtToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                // 处理失败的情况
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "请求失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                ((TextView) getActivity().findViewById(R.id.user_username)).setText("用户名: " + jsonObject.getString("username"));
                                ((TextView) getActivity().findViewById(R.id.user_password)).setText("密码: " + "******");
                                ((TextView) getActivity().findViewById(R.id.user_nickname)).setText("昵称: " + jsonObject.getString("nickname"));
                                ((TextView) getActivity().findViewById(R.id.user_mobile)).setText("电话号码: " + jsonObject.getString("mobile"));
                                ((TextView) getActivity().findViewById(R.id.user_bio)).setText("个性签名: " + jsonObject.getString("bio"));
                                ((TextView) getActivity().findViewById(R.id.user_created_time)).setText("创建时间: " + jsonObject.getString("created"));
                                String imageUrl = "http://116.198.232.203:8000" + jsonObject.optString("head_image");
                                Picasso.get().load(imageUrl).into((ImageView) getActivity().findViewById(R.id.user_avatar));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "请求错误: " + response.code(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }

    private void addSettingsOption(View.OnClickListener listener) {
        TextView textView = new TextView(getContext(), null, 0, R.style.ProfileSettingOptionStyle);
        textView.setText("登录/注册");
        textView.setOnClickListener(listener);
        settingsContainer.addView(textView);
    }

    private void addSettingsOption_update(View.OnClickListener listener) {
        TextView textView = new TextView(getContext(), null, 0, R.style.ProfileSettingOptionStyle);
        textView.setText("修改个人信息");
        textView.setOnClickListener(listener);
        settingsContainer.addView(textView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}