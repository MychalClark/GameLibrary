package edu.ranken.mychal_clark.gamelibrary;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.user.MyProfileViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.user.ProfileGameAdapter;

public class MyProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GameListActivity";

    //States
    private MyProfileViewModel model;
    private Picasso picasso;
    private ProfileGameAdapter profileGameAdapter;
    private RecyclerView libraryRecycler;
    // Camera States
    private File outputImageFile;
    private Uri outputImageUri;

    //Create Views
    private TextView emailText;
    private TextView displayNameText;
    private TextView userIdText;
    private ImageView userImage;
    private Button cameraBtn;
    private Button galleryBtn;
    private TextView downloadUrlText;

    // launchers
    private final ActivityResultLauncher<String> getContentLauncher =
        registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            (Uri uri) -> {
                if (uri != null) {
                    uploadImage(uri);
                }
            }
        );
    private final ActivityResultLauncher<Uri> takePictureLauncher =
        registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            (Boolean result) -> {
                Log.i(LOG_TAG, "take picture result: " + result);
                if (Objects.equals(result, Boolean.TRUE)) {
                    uploadImage(outputImageUri);
                } else {
                    Log.e(LOG_TAG, "failed to return picture");
                }
            }
        );
    



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        libraryRecycler = findViewById(R.id.libraryRecycler);
        //create adapter
        profileGameAdapter = new ProfileGameAdapter(this, null);

        // setup recycler view
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        libraryRecycler.setAdapter(profileGameAdapter);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //find views
        emailText = findViewById(R.id.profileEmailText);
        userIdText = findViewById(R.id.ProfileUserIdText);
        displayNameText = findViewById(R.id.ProfileNameText);
        userImage = findViewById(R.id.profileImage);
        cameraBtn = findViewById(R.id.profileCameraBtn);
        galleryBtn = findViewById(R.id.profileGalleryBtn);
        downloadUrlText = findViewById(R.id.downloadTextUrl);
        


        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(this).get(MyProfileViewModel.class);
        model.getLibrary().observe(this,(librarys) -> {profileGameAdapter.setItems(librarys);});
        model.getUploadErrorMessage().observe(this, (message) -> {
            //errorText.setText(message);
        });
        model.getDownloadUrl().observe(this, (downloadUrl) -> {
            if (downloadUrl != null) {
                downloadUrlText.setText(downloadUrl.toString());
            } else {
                downloadUrlText.setText("");
            }
        });
        //register Listeners
        userImage.setOnClickListener(view -> {
galleryBtn.setVisibility(View.VISIBLE);
            cameraBtn.setVisibility(View.VISIBLE);
        });
        cameraBtn.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "camera");
            try {
                outputImageFile = createImageFile();
                Log.i(LOG_TAG, "outputImageFile = " + outputImageFile);
                outputImageUri = fileToUri(outputImageFile);
                Log.i(LOG_TAG, "outputImageUri = " + outputImageUri);
                takePictureLauncher.launch(outputImageUri);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "take picture failed", ex);
            }
        });
        galleryBtn.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "gallery");

            getContentLauncher.launch("image/*");
        });

            // set Text
        emailText.setText(user.getEmail());
        userIdText.setText(user.getUid());
        displayNameText.setText(user.getDisplayName());


        if (user.getUid() == null) {
            userImage.setImageResource(R.drawable.no_image);
        } else {
            userImage.setImageResource(R.drawable.no_image);
            picasso.load(user.getPhotoUrl())
                .noPlaceholder()
                //.placeholder(R.drawable.ic_downloading)
                .error(R.drawable.no_image)
                .resize(200, 300)
                .centerCrop()
                .into(userImage);
        }

//camera

        // disable the camera if not available
        boolean hasCamera =
            this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        cameraBtn.setVisibility(hasCamera ? View.VISIBLE : View.GONE);
    }
    //voids
    public void uploadImage(Uri uri) {
        Log.i(LOG_TAG, "upload image: " + uri);

        Picasso
            .get()
            .load(uri)
            .resize(400,400)
            .centerCrop()
            .into(userImage);

        model.uploadProfileImage(uri);
    }

    private File createImageFile() throws IOException {
        // create file name
        Calendar now = Calendar.getInstance();
        String fileName = String.format(Locale.US, "image_%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS.jpg", now);

        // create paths
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        // return File object
        return imageFile;
    }

    private static final String FILE_PROVIDER_AUTHORITY = "edu.ranken.mychal_clark.gamelibrary.fileprovider";

    private Uri fileToUri(File file) {
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // force up navigation to have the same behavior as temporal navigation
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}