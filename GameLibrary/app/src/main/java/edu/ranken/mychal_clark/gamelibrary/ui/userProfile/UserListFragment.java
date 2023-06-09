package edu.ranken.mychal_clark.gamelibrary.ui.userProfile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private TextView errorMessage;

    public UserListFragment() {
        super(R.layout.user_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //findViews
        recyclerView = view.findViewById(R.id.userListRecycler);
errorMessage = view.findViewById(R.id.userListError);
        //get Activity
        FragmentActivity activity = getActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        //setup recycler
        int columns = getResources().getInteger(R.integer.userListColumns);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, columns));

        //recycler and adapter attach
        model = new ViewModelProvider(activity).get(UserListViewModel.class);
        userListAdapter = new UserListAdapter(activity, model);
        recyclerView.setAdapter(userListAdapter);


model.getUsers().observe(lifecycleOwner, (users) ->{
userListAdapter.setItems(users);
});
        model.getSnackbarMessage().observe(lifecycleOwner, (messageId) -> {
            if (messageId != null) {
                Snackbar.make(recyclerView, messageId, Snackbar.LENGTH_SHORT).show();
                model.clearSnackbar();
            }
        });
        model.getErrorMessage().observe(lifecycleOwner, (error)->{

            if(error != null){
                errorMessage.setText(error);
                errorMessage.setVisibility(View.VISIBLE);
            }else{
                errorMessage.setText(null);
                errorMessage.setVisibility(View.GONE);

            }
        });


    }
    //
}
