package edu.ranken.mychal_clark.gamelibrary.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import edu.ranken.mychal_clark.gamelibrary.R;

public class UserListFragment extends Fragment {

    private static final String LOG_TAG = UserListFragment.class.getSimpleName();

    //Views
    private RecyclerView recyclerView;

    //states
    private UserListViewModel model;
    private UserListAdapter userListAdapter;

    public UserListFragment() {
        super(R.layout.user_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
Log.i(LOG_TAG, "hello");
        //findViews
        recyclerView = view.findViewById(R.id.userListRecycler);

        //get Activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        //setup recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        //recycler and adapter attach
        model = new ViewModelProvider(this).get(UserListViewModel.class);
        userListAdapter = new UserListAdapter(activity, model);
        recyclerView.setAdapter(userListAdapter);

model.getUsers().observe(lifecycleOwner, (users) ->{
userListAdapter.setItems(users);
});
        model.getSnackbarMessage().observe(lifecycleOwner, (snackbarMessage) -> {
            if (snackbarMessage != null) {
                Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });


    }
    //
}
