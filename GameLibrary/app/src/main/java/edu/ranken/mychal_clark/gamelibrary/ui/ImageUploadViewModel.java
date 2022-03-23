package edu.ranken.mychal_clark.gamelibrary.ui;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUploadViewModel extends ViewModel {

    //constants
    private static final String LOG_TAG = ImageUploadViewModel.class.getSimpleName();

    //misc
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseAuth auth;

    //live data
    private MutableLiveData<String> uploadErrorMessage;
    private MutableLiveData<Uri> downloadUrl;

    public ImageUploadViewModel() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //live data
        uploadErrorMessage = new MutableLiveData<>();
        downloadUrl = new MutableLiveData<>();

    }

    public LiveData<String> getUploadErrorMessage() {
        return uploadErrorMessage;
    }

    public LiveData<Uri> getDownloadUri() {
        return downloadUrl;
    }

    public void uploadProfileImage(Uri profileImageUri) {
        StorageReference storageRef = storage.getReference("/user/" + user.getUid() + "/profilePhoto.png");

        storageRef.putFile(profileImageUri).addOnCompleteListener((task) -> {

            if (!task.isSuccessful()) {
                Log.e(LOG_TAG, "failed to upload image to: " + storageRef.getPath(), task.getException());
                uploadErrorMessage.postValue("Failed to upload");
            } else {
                Log.i(LOG_TAG, "image uploaded to: " + storageRef.getPath());
                storageRef.getDownloadUrl().addOnCompleteListener((downloadTask) -> {
                    if (!downloadTask.isSuccessful()) {
                        Log.e(LOG_TAG, "failed to get download url for: " + storageRef.getPath());
                    } else {
                        Uri downloadUrl = downloadTask.getResult();
                        Log.i(LOG_TAG, "image uploaded to: " + storageRef.getPath());
                    }
                });
            }
        });
    }
}
