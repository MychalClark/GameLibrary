package edu.ranken.mychal_clark.gamelibrary.userProfile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.R;
import edu.ranken.mychal_clark.gamelibrary.data.User;

public class UserListViewModel extends ViewModel {
    //Fire base
    private String LOG_TAG = UserListViewModel.class.getSimpleName();
    private final FirebaseFirestore db;
    private ListenerRegistration userRegistration;

    //Live Data
    private final MutableLiveData<List<User>> users;
    private final MutableLiveData<Integer> errorMessage;
    private final MutableLiveData<Integer> snackbarMessage;
    private final MutableLiveData<User> selectedUser;

    public UserListViewModel() {
        db = FirebaseFirestore.getInstance();

        //live Data

        users = new MutableLiveData<>(null);
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        selectedUser = new MutableLiveData<>(null);


        userRegistration =
            db.collection("users")
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting Users.", error);
                        snackbarMessage.postValue(R.string.errorGettingUsers);
                        errorMessage.postValue(R.string.errorGettingUsers);

                    } else {
                        List<User> newUsers =
                            querySnapshot != null ? querySnapshot.toObjects(User.class) : null;
                        users.postValue(newUsers);
                        snackbarMessage.postValue(R.string.userUpdated);
                        errorMessage.postValue(null);
                    }
                });
    }
    public void clearSnackbar() {
        snackbarMessage.postValue(null);
    }

    @Override
    protected void onCleared() {
        if (userRegistration != null) {
            userRegistration.remove();
        }
        super.onCleared();
    }

    //Get Live Data
    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<Integer> getSnackbarMessage() {
        return snackbarMessage;
    }

    public LiveData<User> getSelectedUser(){return selectedUser;}

    public LiveData<Integer> getErrorMessage(){return errorMessage;}

    public void setSelectedUser(User user) {
        this.selectedUser.postValue(user);
    }
}
