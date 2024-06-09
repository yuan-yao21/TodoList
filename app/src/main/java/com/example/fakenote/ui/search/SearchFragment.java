package com.example.fakenote.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.fakenote.R;
import com.example.fakenote.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchResultAdapter adapter;
    private List<Note> searchResults;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchResults = new ArrayList<>();
        adapter = new SearchResultAdapter(searchResults);
        binding.recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSearchResults.setAdapter(adapter);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;  // 处理查询请求
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 在这里实现实时搜索建议
                return false;
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performSearch(String query) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://116.198.232.203:8000/api/v1/note/search?keyword=" + Uri.encode(query);  // 使用Uri.encode确保查询参数正确编码

        // 从SharedPreferences获取JWT令牌
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saved_jwt", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JWT_TOKEN", "");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "请求失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                JSONArray notesArray = jsonObject.getJSONArray("notes");
                                int totalNotes = jsonObject.getInt("total");
                                updateUIWithResults(notesArray, totalNotes);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "请求错误: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void updateUIWithResults(JSONArray notesArray, int totalNotes) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    List<Note> notesList = new ArrayList<>();
                    for (int i = 0; i < notesArray.length(); i++) {
                        JSONObject noteObject = notesArray.getJSONObject(i);
                        String category = noteObject.optString("category");
                        String title = noteObject.optString("title");
                        String content = noteObject.optString("content");
                        String updateTime = noteObject.optString("updateTime");

                        Note note = new Note(category, title, content, updateTime);
                        notesList.add(note);
                    }

                    // 更新 RecyclerView 的适配器数据
                    if (binding != null) {
                        SearchResultAdapter adapter = (SearchResultAdapter) binding.recyclerViewSearchResults.getAdapter();
                        if (adapter != null) {
                            adapter.updateData(notesList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}