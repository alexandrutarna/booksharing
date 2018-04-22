package it.polito.mad.lab1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import java.io.File;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import java.io.IOException;
import android.content.pm.PackageManager;
import android.net.Uri;

import static android.content.Intent.createChooser;

public class editProfile extends AppCompatActivity {

    static final int GALLERY_REQ = 0;
    static final int CAMERA_REQ = 1;

    protected static final int REQUEST_GALLERY_PERMISSION = 3;
    protected static final int REQUEST_CAMERA_PERMISSION = 4;

    public Uri camera_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        String name_saved = null;
        String mail_saved = null;
        String bio_saved = null;
        String photo_saved = null;

        EditText name = findViewById(R.id.name);
        EditText mail = findViewById(R.id.mail);
        EditText bio = findViewById(R.id.bio);

        if (savedInstanceState != null) {
            name_saved = savedInstanceState.getString("name");
            mail_saved = savedInstanceState.getString("mail");
            bio_saved = savedInstanceState.getString("bio");
        }else {
            SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
            name_saved = sharedPref.getString("name", null);
            mail_saved = sharedPref.getString("mail", null);
            bio_saved = sharedPref.getString("bio", null);
            photo_saved = sharedPref.getString("photo", null);
        }

        name.setText(name_saved);
        mail.setText(mail_saved);
        bio.setText(bio_saved);

        Bitmap bitmap = null;


        if (photo_saved != null) {
            try {
                Uri saved_uri = Uri.parse(photo_saved);
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), saved_uri);
                final ImageView img = findViewById(R.id.img);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        final Button photoButton = findViewById(R.id.photoSel);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                selectImage();
            }

        });

    }

    public void saveData() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
        SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences

        EditText edit_name = findViewById(R.id.name);   //edit text object instances
        EditText edit_mail = findViewById(R.id.mail);
        EditText edit_bio = findViewById(R.id.bio);

        editor.putString("name", edit_name.getText().toString());
        editor.putString("mail", edit_mail.getText().toString());
        editor.putString("bio", edit_bio.getText().toString());

        Toast.makeText(getApplicationContext(), R.string.saveMessage, Toast.LENGTH_SHORT).show();

        editor.apply();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.doneButton:
                saveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        EditText edit_name = findViewById(R.id.name);
        EditText edit_mail = findViewById(R.id.mail);
        EditText edit_bio = findViewById(R.id.bio);

        outState.putString("name", edit_name.getText().toString());
        outState.putString("mail", edit_mail.getText().toString());
        outState.putString("bio", edit_bio.getText().toString());

    }

    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.gallery),
                getResources().getString(R.string.photo),
                getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(editProfile.this);
        //builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                //camera
                if (item == GALLERY_REQ) gallery_permission();
                //gallery
                if (item == CAMERA_REQ) camera_permission();

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void gallery_permission() {

        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionReadExternalStorage == PackageManager.PERMISSION_GRANTED && permissionWriteExternalStorage == PackageManager.PERMISSION_GRANTED)
        {
            gallery();

        } else if (permissionReadExternalStorage == PackageManager.PERMISSION_DENIED || permissionWriteExternalStorage == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(editProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);

    }

    private void gallery ()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(createChooser(galleryIntent, "Select File"),GALLERY_REQ);
    }

    private void camera_permission(){

        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheckCamera = ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.CAMERA);

        if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED && permissionReadExternalStorage == PackageManager.PERMISSION_GRANTED) {
            camera();
        } else if (permissionCheckCamera == PackageManager.PERMISSION_DENIED || permissionReadExternalStorage == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(editProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

    }

    private void camera () {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_uri = Uri.fromFile(getCameraMediaFile()); // Camera uri path
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);

        startActivityForResult(takePictureIntent, CAMERA_REQ);
    }

    private static File getCameraMediaFile(){


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG1"+ ".png");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.print("onActivivtyResult\n\n\n");
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK)
        {
            Uri gallery_uri = data.getData();

            SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
            SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences

            editor.putString("photo", gallery_uri.toString());
            editor.apply();


            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), gallery_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final ImageView img = findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);

        }

        else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK)
        {
            SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
            SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences

            editor.putString("photo", camera_uri.toString());
            editor.apply();

            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), camera_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final ImageView img = findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera();
                }
                return;
            }
            case REQUEST_GALLERY_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallery();
                }
                return;
            }
        }
    }


}
