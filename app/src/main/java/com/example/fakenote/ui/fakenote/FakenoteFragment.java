package com.example.fakenote.ui.fakenote;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakenote.MainActivity;
import com.example.fakenote.R;
import com.example.fakenote.data.DataManager;
import com.example.fakenote.data.NoteBook;
import com.example.fakenote.data.NotePage;
import com.example.fakenote.databinding.FragmentFakenoteBinding;
import com.example.fakenote.ui.EditPage;

import java.util.ArrayList;

public class FakenoteFragment extends Fragment {
    public enum ItemState{
        NOTEBOOK,
        PAGE
    }
    public MyAdapter RecycAdapter;

    private MainActivity _mainActivity;
    public NoteBook CurrentBook;
    public NotePage CurrentPage;

    private FragmentFakenoteBinding binding;
    private RecyclerView _recyclerView;

    public String GetCurrentName(){
        switch (RecycAdapter._currentItemState){
            case NOTEBOOK:
                return "TODO 清单";

            case PAGE:
                return CurrentBook.Name;
        }
        return "TODO 清单";
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _mainActivity = (MainActivity)getActivity();
        _mainActivity.NoteFragment = this;
        FakenoteViewModel dashboardViewModel =
                new ViewModelProvider(this).get(FakenoteViewModel.class);

        binding = FragmentFakenoteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        _recyclerView = root.findViewById(R.id.recyclerView);
        RecycAdapter = new MyAdapter();
        _recyclerView.setAdapter(RecycAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        _recyclerView.setLayoutManager(layoutManager);

        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        private ItemState _currentItemState = ItemState.NOTEBOOK;
        public ItemState GetState(){return _currentItemState;}

        public void ChangeState(ItemState state){
            _currentItemState = state;
            notifyDataSetChanged();
        }
        public void BackState(){
            switch (_currentItemState){
                case NOTEBOOK:
                    break;
                case PAGE:
                    ChangeState(ItemState.NOTEBOOK);
                    break;
            }
        }
        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getActivity().getApplicationContext(), R.layout.notebook_list_item, null);
            MyViewHoder myViewHoder = new MyViewHoder(view);
            return myViewHoder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            String itemName = null;
            NoteBook holderBook = null;
            NotePage holderPage = null;
            switch (_currentItemState){
                case NOTEBOOK:
                    holderBook = DataManager.Instance.Data.GetNoteBookAt(position);
                    itemName = holderBook.Name;
                    holder.mImage.setImageResource(R.drawable.ic_folder);
                    break;
                case PAGE:
                    holderPage = CurrentBook.GetPageAt(position);
                    holder.mImage.setImageDrawable(null);
                    itemName = holderPage.Name;
                    break;
            }

            holder.mNotebookName.setText(itemName);
            ViewGroup.LayoutParams layoutParams = holder.mImage.getLayoutParams();
            layoutParams.width = (int) (62*getResources().getDisplayMetrics().density);
            layoutParams.height = layoutParams.width;
            holder.mImage.setLayoutParams(layoutParams);
            NoteBook finalHolderBook = holderBook;
            NotePage finalHolderPage = holderPage;
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    _mainActivity.RenameItem.setVisible(true);
                    switch (_currentItemState){
                        case NOTEBOOK:
                            _currentItemState = ItemState.PAGE;
                            CurrentBook = finalHolderBook;
                            notifyDataSetChanged();
                            break;
                        case PAGE:
                            CurrentPage = finalHolderPage;
                            Intent intent = new Intent(getActivity(), EditPage.class);
                            ArrayList<String> pageInfo = new ArrayList<>();
                            pageInfo.add(CurrentBook.Name);
                            pageInfo.add(CurrentPage.Name);
                            intent.putStringArrayListExtra(MainActivity.EXTRA_PAGE_INFO,pageInfo);
                            activityResultLauncher.launch(intent);
                            break;
                    }
                    _mainActivity.getSupportActionBar().setTitle(GetCurrentName()
                    );
                }
            });
        }
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        notifyDataSetChanged();
                    }
                });

        public int getItemCount() {
            switch (_currentItemState) {
                case NOTEBOOK:
                    return DataManager.Instance.Data.GetNoteBookCount();
                case PAGE:
                    if (CurrentBook != null) {
                        return CurrentBook.GetPageCount(); // Ensure CurrentBook is initialized
                    } else {
                        return 0; // Safe fallback if CurrentBook is not initialized
                    }
                default:
                    return 0; // Safe fallback return value
            }
        }
    }

    class MyViewHoder extends RecyclerView.ViewHolder {
        TextView mNotebookName;
        Button mButton;
        ImageView mImage;

        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            mNotebookName = itemView.findViewById(R.id.notebookName);
            mButton = itemView.findViewById(R.id.button4);
            mImage = itemView.findViewById(R.id.notebookIcon);
        }
    }
}