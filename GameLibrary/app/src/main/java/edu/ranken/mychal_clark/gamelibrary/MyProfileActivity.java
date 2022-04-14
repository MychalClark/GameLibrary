package edu.ranken.mychal_clark.gamelibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.ranken.mychal_clark.gamelibrary.ui.myProfile.MyProfileViewModel;
import edu.ranken.mychal_clark.gamelibrary.ui.myProfile.ProfileLibraryGameAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.myProfile.ProfileWishlistGameAdapter;
import edu.ranken.mychal_clark.gamelibrary.ui.utils.ConsoleChooserDialog;

public class MyProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = MyProfileActivity.class.getSimpleName();

    private static final String FILE_PROVIDER_AUTHORITY =
        "com.example.myapp.fileprovider";

    private Uri fileToUri(File file) {
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file);
    }

    File publicPicturesDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    //States
    private MyProfileViewModel model;
    private Picasso picasso;
    private ProfileLibraryGameAdapter profileLibraryGameAdapter;
    private ProfileWishlistGameAdapter profileWishlistGameAdapter;
    private RecyclerView libraryRecycler;
    private RecyclerView wishlistRecycler;

    // Camera States
    private File outputImageFile;
    private Uri outputImageUri;

    //Create Views
    private TextView emailText;
    private TextView displayNameText;
    private TextView userIdText;
    private TextView downloadUrlText;
    private ImageView xboxIcon;
    private ImageView windowsIcon;
    private ImageView playstationIcon;
    private ImageView nintendoIcon;
    private ImageView userImage;
    private Button cameraBtn;
    private Button galleryBtn;
    private Button changeConsoleBtn;


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
    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            (Boolean result) -> {
                if (Objects.equals(result, Boolean.TRUE)) {
                    Log.i(LOG_TAG, "permission granted");
                } else {
                    Log.e(LOG_TAG, "failed to get permission");
                }
            }
        );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_scroll);

        libraryRecycler = findViewById(R.id.libraryRecycler);
        wishlistRecycler = findViewById(R.id.wishlistRecycler);
        //create adapter
        profileLibraryGameAdapter = new ProfileLibraryGameAdapter(this, null);
        profileWishlistGameAdapter = new ProfileWishlistGameAdapter(this, null);

        // setup recycler view

        libraryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        libraryRecycler.setAdapter(profileLibraryGameAdapter);
        wishlistRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        wishlistRecycler.setAdapter(profileWishlistGameAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //find views
        emailText = findViewById(R.id.profileEmailText);
        userIdText = findViewById(R.id.ProfileUserIdText);
        displayNameText = findViewById(R.id.ProfileNameText);
        userImage = findViewById(R.id.profileImage);
        cameraBtn = findViewById(R.id.profileCameraBtn);
        galleryBtn = findViewById(R.id.profileGalleryBtn);
        xboxIcon = findViewById(R.id.profileGameConsole1);
        playstationIcon = findViewById(R.id.profileGameConsole2);
        windowsIcon = findViewById(R.id.profileGameConsole3);
        nintendoIcon = findViewById(R.id.profileGameConsole4);
        changeConsoleBtn = findViewById(R.id.profileChangeConsolesBtn);


        // get picasso
        picasso = Picasso.get();

        //bind model
        model = new ViewModelProvider(this).get(MyProfileViewModel.class);
        model.getLibrary().observe(this, (librarys) -> {
            profileLibraryGameAdapter.setItems(librarys);
        });
        model.getWishlist().observe(this, (wishlists) -> {
            profileWishlistGameAdapter.setItems(wishlists);
        });
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

        model.getCurrentUser().observe(this,(currentUser)->{

            if(currentUser != null) {

                if (currentUser.preferredConsoles != null) {
                    Log.i(LOG_TAG, "got the user brah");

                    if (currentUser.preferredConsoles.containsKey("xbox")) {
                        xboxIcon.setVisibility(View.VISIBLE);
                    } else {
                        xboxIcon.setVisibility(View.INVISIBLE);
                    }
                    if (currentUser.preferredConsoles.containsKey("playstation")) {

                        playstationIcon.setVisibility(View.VISIBLE);
                    } else {
                        playstationIcon.setVisibility(View.INVISIBLE);
                    }
                    if (currentUser.preferredConsoles.containsKey("nintendo")) {

                        nintendoIcon.setVisibility(View.VISIBLE);
                    } else {
                        nintendoIcon.setVisibility(View.INVISIBLE);
                    }
                    if (currentUser.preferredConsoles.containsKey("windows")) {

                        windowsIcon.setVisibility(View.VISIBLE);
                    } else {
                        windowsIcon.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        //register Listeners

        cameraBtn.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "camera");
            try {
                String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                    // launch permission request
                    requestPermissionLauncher.launch(permission);
                } else {
                    boolean hasCamera = Camera.getNumberOfCameras() > 0;
                    if (hasCamera) {

                        outputImageFile = createImageFile();
                        Log.i(LOG_TAG, "outputImageFile = " + outputImageFile);

                        outputImageUri = fileToUri(outputImageFile);
                        Log.i(LOG_TAG, "outputImageUri = " + outputImageUri);

                        takePictureLauncher.launch(outputImageUri);
                    }
                }
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
        changeConsoleBtn.setOnClickListener(view -> {

                Map<String, Boolean> consoles = new HashMap<>();
                consoles.put("xbox", true);
                consoles.put("windows", true);
                consoles.put("playstation", true);
                consoles.put("nintendo", true);


                ConsoleChooserDialog consoleChooserDialog = new ConsoleChooserDialog(
                    MyProfileActivity.this,
                    "Choose Consoles",
                    consoles,
                    null,
                    (selected) -> {
                        if (!selected.isEmpty()) {
                            Map<String, Boolean> selectedConsoles = new HashMap<>();
                            if (selected.containsKey("xbox")) {
                                selectedConsoles.put("xbox", Boolean.TRUE);
                            }
                            if (selected.containsKey("windows")) {
                                selectedConsoles.put("windows", Boolean.TRUE);
                            }
                            if (selected.containsKey("playstation")) {
                                selectedConsoles.put("playstation", Boolean.TRUE);
                            }
                            if (selected.containsKey("nintendo")) {
                                selectedConsoles.put("nintendo", Boolean.TRUE);
                            }

                            model.addPreferredGames(selectedConsoles);
                        } else {
                            Toast.makeText(view.getContext(), "No Console selected! Item not added.",
                                Toast.LENGTH_LONG).show();
                        }

                    });
            consoleChooserDialog.show();
            });

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
            .resize(400, 400)
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