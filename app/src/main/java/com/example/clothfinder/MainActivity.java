package com.example.clothfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MenuItem logoutItem;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private FirebaseAuth mAuth;
    private static GoogleSignInClient mGoogleSignInClient;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            loadImage(result.getData());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button imageButton = findViewById(R.id.main_button);
        mAuth = FirebaseAuth.getInstance();
        imageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonClickHandler();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        logoutItem = menu.findItem(R.id.logoutButton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logoutButton:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void imageButtonClickHandler(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityResult.launch(intent);
    }

    private void loadImage(Intent intent) throws InterruptedException {
        ImageView imageView = findViewById(R.id.main_image);

        Uri targetImageUri = intent.getData();
        Glide.with(getApplicationContext()).load(targetImageUri).override(500).into(imageView);

        LinearLayout view = (LinearLayout) findViewById(R.id.main_layout);

        view.removeAllViews();

        Transfer trans = new Transfer();
        trans.sendImageToServer(intent, getContentResolver());

        while(true){
            if (trans.getResult() != null) break;
        }

        String result = trans.getResult();

        String[] classes = trans.getResult().split("&"); // "class1/class2/class3"

        for (int i = 0; i < classes.length; i++) {
            makeDetectedClassButton(classes[i], targetImageUri);
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }

    public void makeDetectedClassButton(String detectedClass, Uri targetImageUri) {
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productSelect = new Intent(MainActivity.this, ProductSelectActivity.class);

                String selectedClass = (String) ((Button) view).getText();

                Transfer transfer = new Transfer();

                transfer.sendStringToServer(selectedClass);

                while(true){
                    if (transfer.getResult() != null) break;
                }
                System.out.println(transfer.getResult());

                productSelect.putExtra("selectedClass", selectedClass);
                productSelect.putExtra("targetImageUri", targetImageUri);

                String result = transfer.getResult();

                String[] products = result.split("&");

                ArrayList<Product> productArrayList = new ArrayList<>();

                for (String product : products) {
                    Product temp = new Product();
                    String[] labels = product.split(">");
                    temp.setName(labels[0]);
                    temp.setLinkUri(labels[1]);
                    temp.setSimilarity(Float.parseFloat(labels[2]));
                    temp.setImageUri(labels[3]);
                    temp.setPrice(Integer.parseInt(labels[4]));
                    productArrayList.add(temp);
                }

                productSelect.putExtra("products", productArrayList);

                startActivity(productSelect);
            }
        });
        LinearLayout view = (LinearLayout) findViewById(R.id.main_layout);
        button.setText(detectedClass);

        view.addView(button);
    }
}