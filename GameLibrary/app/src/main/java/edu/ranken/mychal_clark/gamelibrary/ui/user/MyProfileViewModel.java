package edu.ranken.mychal_clark.gamelibrary.ui.user;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import edu.ranken.mychal_clark.gamelibrary.data.Library;
import edu.ranken.mychal_clark.gamelibrary.data.WishList;

public class MyProfileViewModel extends ViewModel {


    private static final String LOG_TAG = "MyProfileViewModel";

    //firebase
    private final FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser user;

    //listeners
    private ListenerRegistration libraryRegistration;
    private ListenerRegistration wishListRegistration;

    //live data
    private final MutableLiveData<String> snackbarMessage;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Library>> librarys;
    private final MutableLiveData<List<WishList>> wishlists;
    private MutableLiveData<String> uploadErrorMessage;
    private MutableLiveData<Uri> downloadUrl;

    public MyProfileViewModel(){
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //add live data
        errorMessage = new MutableLiveData<>(null);
        snackbarMessage = new MutableLiveData<>(null);
        librarys = new MutableLiveData<>(null);
        wishlists = new MutableLiveData<>(null);
        uploadErrorMessage = new MutableLiveData<>();
        downloadUrl = new MutableLiveData<>();


       libraryRegistration =
           db.collection("userLibrary")
               .whereEqualTo("userId", user.getUid())
               .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                   if (error != null) {
                       // show error...
                       Log.e(LOG_TAG, "Error getting Library.", error);
                       snackbarMessage.postValue("Error getting Library.");
                   } else {
                       List<Library> newLibrary =
                           querySnapshot != null ? querySnapshot.toObjects(Library.class) : null;
                       librarys.postValue(newLibrary);
                       snackbarMessage.postValue("Library Found.");
                   }
               });
        wishListRegistration =
            db.collection("userWishlist")
                .whereEqualTo("userId", user.getUid())
                .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        // show error...
                        Log.e(LOG_TAG, "Error getting Wishlist.", error);
                        snackbarMessage.postValue("Error getting Wishlist.");
                    } else {
                        List<WishList> newWishlist =
                            querySnapshot != null ? querySnapshot.toObjects(WishList.class) : null;
                        wishlists.postValue(newWishlist);
                        snackbarMessage.postValue("Wishlist Found.");
                    }
                });


    }
    public LiveData<List<Library>> getLibrary(){return librarys;}
    public LiveData<List<WishList>> getWishlist(){return wishlists;}
    public LiveData<String> getUploadErrorMessage() {
        return uploadErrorMessage;
    }

    public LiveData<Uri> getDownloadUrl() {
        return downloadUrl;
    }

    public void uploadProfileImage(Uri profileImageUri) {

        String userId = user.getUid();
        StorageReference storageRef =
            storage.getReference("/user/" + userId + "/profilePhoto.png");

        storageRef
            .putFile(profileImageUri)
            .addOnCompleteListener((task) -> {
                if (!task.isSuccessful()) {
                    Log.e(LOG_TAG, "failed to upload image to: " + storageRef.getPath(), task.getException());
                    uploadErrorMessage.postValue("Failed to upload.");
                } else {
                    Log.i(LOG_TAG, "image uploaded to: " + storageRef.getPath());
                    getProfileImageDownloadUrl(storageRef);
                }
            });
    }

    private void getProfileImageDownloadUrl(StorageReference storageRef) {
        storageRef
            .getDownloadUrl()
            .addOnCompleteListener((downloadTask) -> {
                if (!downloadTask.isSuccessful()) {
                    Log.e(LOG_TAG, "failed to get download url for: " + storageRef.getPath(), downloadTask.getException());
                    uploadErrorMessage.postValue("Failed to get download URL.");
                } else {
                    Uri downloadUrl = downloadTask.getResult();
                    Log.i(LOG_TAG, "download url: " + downloadUrl);
                    this.uploadErrorMessage.postValue(null);
                    this.downloadUrl.postValue(downloadUrl);

                    // update auth database ...
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUrl)
                        .build();

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                snackbarMessage.postValue("User auth profile not updated.");
                                Log.d(LOG_TAG, "User auth profile  not updated.");
                            }else{
                                snackbarMessage.postValue("User auth profile updated.");
                                Log.d(LOG_TAG, "User auth profile updated.");
                            }
                        });
                    // update firestore database ...
                    db.collection("users")
                        .document(user.getUid())
                        .update("profilePictureUrl", downloadUrl.toString())
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                snackbarMessage.postValue("User data profile not updated.");
                                Log.d(LOG_TAG, "User data profile  not updated.");
                            }else{
                                snackbarMessage.postValue("User data profile updated.");
                                Log.d(LOG_TAG, "User data profile updated.");
                            }
                        });
                }
            });
    }

    }
