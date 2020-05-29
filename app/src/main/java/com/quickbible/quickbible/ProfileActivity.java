package com.quickbible.quickbible;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    DatabaseReference database;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid = "init";
    EditText usernameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    Button signUpButton;
    Button signInButton;
    Button signOutButton;
    Button updateButton;
    Button toggleNight;
    Button resetPassword;
    Button goToBible;
    RelativeLayout layout;
    String username = "init";
    String email = "init";
    String password = "init";
    String currentFontFamily = "Poppins-Regular";
    Boolean night = false;
    Boolean userIsSignedIn = false;
    Boolean isProfilePage = true;
    int iceland = R.font.iceland;
    int lora = R.font.lora;
    int oswald = R.font.oswald;
    int playfair_display = R.font.playfair_display;
    int poppins = R.font.poppins;
    int raleway = R.font.raleway;
    int roboto = R.font.roboto;
    int satisfy = R.font.satisfy;
    int source_code_pro = R.font.source_code_pro;
    int ubuntu = R.font.ubuntu;
    int currentFontFamilyInt = poppins;
    Long currentFontSize = 18L;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            applyOverrideConfiguration(override);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        if (userIsSignedIn) {
            uid = user.getUid();
        }
        layout = findViewById(R.id.Layout);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signUp);
        signInButton = findViewById(R.id.signIn);
        signOutButton = findViewById(R.id.signOut);
        updateButton = findViewById(R.id.update);
        goToBible = findViewById(R.id.goToBiblePage);
        resetPassword = findViewById(R.id.resetPassword);
        toggleNight = findViewById(R.id.toggleNight);
        checkIfUserIsSignedIn();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        emailEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                                                    @Override
                                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                                            if (event == null || !event.isShiftPressed()) {
                                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                if (imm != null) {
                                                                    imm.hideSoftInputFromWindow((ProfileActivity.this.getCurrentFocus()).getWindowToken(), 0);
                                                                }
                                                                return true;
                                                            }
                                                        }
                                                        return false;
                                                    }
                                                }
        );
        if (userIsSignedIn) {
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String currentPage = (String) dataSnapshot.child("users").child(uid).child("currentPage").getValue();
                    currentFontFamily = (String) dataSnapshot.child("users").child(user.getUid()).child("currentFontFamily").getValue();
                    currentFontSize = (Long) dataSnapshot.child("users").child(user.getUid()).child("currentFontSize").getValue();
                    if (currentPage != null) {
                        if (currentPage.equals("bible")) {
                            goToBible();
                        } else {
                            setStyle();
                        }
                    }
                    username = (String) ((dataSnapshot.child("users").child(uid).child("username").getValue()));
                    if (username != null) {
                        username = username.trim();
                    }
                    usernameEditText.setText(username);
                    email = (String) ((dataSnapshot.child("users").child(uid).child("email").getValue()));
                    if (email != null) {
                        email = email.trim();
                    }
                    emailEditText.setText(email);
                    night = (Boolean) dataSnapshot.child("users").child(uid).child("night").getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    setStyle();
                }
            });
        }
        setStyle();
        database.child("users/" + uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkIfUserIsSignedIn();
                if (userIsSignedIn) {
                    if (!uid.equals("init")) {
                        String currentPage;
                        try {
                            currentPage = (String) (dataSnapshot.child("currentPage").getValue());
                            if (currentPage != null) {
                                if (currentPage.equals("bible")) {
                                    goToBible();
                                } else {
                                    setStyle();
                                }
                            }
                            username = (String) (dataSnapshot.child("username").getValue());
                            if (username != null) {
                                username = username.trim();
                            }
                            usernameEditText.setText(username);
                            email = (String) (dataSnapshot.child("email").getValue());
                            if (email != null) {
                                email = email.trim();
                            }
                            emailEditText.setText(email);
                            night = (Boolean) dataSnapshot.child("night").getValue();
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        passwordEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    enterKey();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7) {
            database.child("users").child(uid).child("currentPage").setValue("profile");
        }
    }

    public void signUpButtonClicked(View view) {
        signUp();
    }

    public void signInButtonClicked(View view) {
        signIn();
    }

    public void signOutButtonClicked(View view) {
        signOut();
    }

    public void updateButtonClicked(View view) {
        update();
    }

    public void toggleNightButtonClicked(View view) {
        if (night != null) {
            toggleNight();
        }

    }

    public void resetPasswordClicked(View view) {
        resetPassword();
    }

    public void goToBiblePageButtonClicked(View view) {
        goToBible();
    }

    public void checkIfUserIsSignedIn() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userIsSignedIn = true;
            uid = user.getUid();
        } else {
            userIsSignedIn = false;
            updateButton.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
            toggleNight.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
            goToBible.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
            uid = "init";
        }
    }

    public void signUp() {
        username = usernameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        night = false;
        if (!username.equals("") && !email.equals("") && !password.equals("")) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        if (user != null) {
                            uid = (user).getUid();
                        }
                        database.child("users").child(uid).child("username").setValue(username);
                        database.child("users").child(uid).child("email").setValue(email);
                        database.child("users").child(uid).child("currentPage").setValue("bible");
                        database.child("users").child(uid).child("night").setValue(night);
                        database.child("users").child(uid).child("bottomNavigationMode").setValue("Verse");
                        database.child("users").child(uid).child("currentBook").setValue(1);
                        database.child("users").child(uid).child("currentChapter").setValue(1);
                        database.child("users").child(uid).child("currentVerse").setValue(1);
                        database.child("users").child(uid).child("currentFontFamily").setValue("Poppins-Regular");
                        database.child("users").child(uid).child("currentFontSize").setValue(18);
                        database.child("users").child(uid).child("selectedVerses/0").setValue("x");
                    } else {
                        toast("User not created");
                    }
                }
            });
        } else {
            toast("please type username, email, and password");
        }
    }

    public void signIn() {
        username = usernameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        if ((!email.equals("") && !password.equals(""))) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                checkIfUserIsSignedIn();
                                if (userIsSignedIn) {
                                    username = (String) (dataSnapshot.child("users").child(uid).child("username").getValue());
                                    usernameEditText.setText(username);
                                    String currentPage = (String) (dataSnapshot.child("users").child(uid).child("currentPage").getValue());
                                    night = (Boolean) dataSnapshot.child("users").child(uid).child("night").getValue();
                                    setStyle();
                                    if (currentPage != null && currentPage.equals("bible")) {
                                        goToBible();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        toast("ERROR: " + (task.getException()).getMessage());
                        signUp();
                    }
                }
            });
        } else {
            toast("please type email and password");
        }
    }

    public void signOut() {
        night = false;
        setStyle();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            auth.signOut();
            usernameEditText.setText("");
            emailEditText.setText("");
            passwordEditText.setText("");
            checkIfUserIsSignedIn();
            night = false;
            currentFontFamily = "Poppins-Regular";
            currentFontSize = 18L;
            setStyle();
        } else {
            toast("No user is signed in");
        }
    }

    public void update() {
        username = usernameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        if (userIsSignedIn) {
            if ((!username.equals("") && !email.equals(""))) {
                database.child("users").child(uid).child("username").setValue(usernameEditText.getText().toString());
                database.child("users").child(uid).child("email").setValue(emailEditText.getText().toString());
                user.updateEmail(email);
            } else {
                toast("please type username and email");
            }
        } else {
            toast("No user is signed in");
        }
    }

    public void toggleNight() {
        if (userIsSignedIn) {
            night = !night;
            database.child("users").child(uid).child("night").setValue(night);
            setStyle();
        } else {
            toast("No user is signed in");
        }
    }

    public void resetPassword() {
        email = emailEditText.getText().toString().trim();
        if (!email.equals("")) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        toast("Email sent.");
                    }
                }
            });
        } else {
            toast("please type email");
        }
    }

    public void goToBible() {
        checkIfUserIsSignedIn();
        if (userIsSignedIn) {
            if (isProfilePage) {
                isProfilePage = false;
                Intent intent = new Intent(this, BibleActivity.class);
                startActivityForResult(intent, 7);
            }
        } else {
            toast("No user is signed in");
        }
    }

    public void setStyle() {
        checkIfUserIsSignedIn();
        if (night == null) {
            night = false;
        }
        if (night) {
            toggleNight.setText(R.string.Night);
            layout.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            usernameEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_night));
            emailEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_night));
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_night));
            usernameEditText.setTextColor(getResources().getColor(R.color.colorWhite));
            emailEditText.setTextColor(getResources().getColor(R.color.colorWhite));
            passwordEditText.setTextColor(getResources().getColor(R.color.colorWhite));
            signUpButton.setBackground(getResources().getDrawable(R.drawable.button_night));
            signInButton.setBackground(getResources().getDrawable(R.drawable.button_night));
            signOutButton.setBackground(getResources().getDrawable(R.drawable.button_night));
            updateButton.setBackground(getResources().getDrawable(R.drawable.button_night));
            toggleNight.setBackground(getResources().getDrawable(R.drawable.button_night));
            goToBible.setBackground(getResources().getDrawable(R.drawable.button_night));
            resetPassword.setBackground(getResources().getDrawable(R.drawable.button_night));
            signUpButton.setTextColor(getResources().getColor(R.color.colorBlack));
            signInButton.setTextColor(getResources().getColor(R.color.colorBlack));
            signOutButton.setTextColor(getResources().getColor(R.color.colorBlack));
            updateButton.setTextColor(getResources().getColor(R.color.colorBlack));
            toggleNight.setTextColor(getResources().getColor(R.color.colorBlack));
            goToBible.setTextColor(getResources().getColor(R.color.colorBlack));
            resetPassword.setTextColor(getResources().getColor(R.color.colorBlack));
        } else {
            toggleNight.setText(R.string.Day);
            layout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            usernameEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_day));
            emailEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_day));
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.edit_text_day));
            usernameEditText.setTextColor(getResources().getColor(R.color.colorBlack));
            emailEditText.setTextColor(getResources().getColor(R.color.colorBlack));
            passwordEditText.setTextColor(getResources().getColor(R.color.colorBlack));
            signUpButton.setBackground(getResources().getDrawable(R.drawable.button_day));
            signInButton.setBackground(getResources().getDrawable(R.drawable.button_day));
            signOutButton.setBackground(getResources().getDrawable(R.drawable.button_day));
            updateButton.setBackground(getResources().getDrawable(R.drawable.button_day));
            toggleNight.setBackground(getResources().getDrawable(R.drawable.button_day));
            goToBible.setBackground(getResources().getDrawable(R.drawable.button_day));
            resetPassword.setBackground(getResources().getDrawable(R.drawable.button_day));
            signUpButton.setTextColor(getResources().getColor(R.color.colorWhite));
            signInButton.setTextColor(getResources().getColor(R.color.colorWhite));
            signOutButton.setTextColor(getResources().getColor(R.color.colorWhite));
            updateButton.setTextColor(getResources().getColor(R.color.colorWhite));
            toggleNight.setTextColor(getResources().getColor(R.color.colorWhite));
            goToBible.setTextColor(getResources().getColor(R.color.colorWhite));
            resetPassword.setTextColor(getResources().getColor(R.color.colorWhite));
            if (!userIsSignedIn) {
                updateButton.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
                toggleNight.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
                goToBible.setBackground(getResources().getDrawable(R.drawable.button_day_gray));
                resetPassword.setBackground(getResources().getDrawable(R.drawable.button_day));
            }
            usernameEditText.setHintTextColor(getResources().getColor(R.color.colorGray));
            emailEditText.setHintTextColor(getResources().getColor(R.color.colorGray));
            passwordEditText.setHintTextColor(getResources().getColor(R.color.colorGray));
        }
        if (currentFontFamily.equals("Iceland")) {
            currentFontFamilyInt = iceland;
        }
        if (currentFontFamily.equals("Lora-Regular")) {
            currentFontFamilyInt = lora;
        }
        if (currentFontFamily.equals("Oswald-Regular")) {
            currentFontFamilyInt = oswald;
        }
        if (currentFontFamily.equals("PlayfairDisplay-Regular")) {
            currentFontFamilyInt = playfair_display;
        }
        if (currentFontFamily.equals("Poppins-Regular")) {
            currentFontFamilyInt = poppins;
        }
        if (currentFontFamily.equals("Raleway-Regular")) {
            currentFontFamilyInt = raleway;
        }
        if (currentFontFamily.equals("Roboto-Regular")) {
            currentFontFamilyInt = roboto;
        }
        if (currentFontFamily.equals("Satisfy-Regular")) {
            currentFontFamilyInt = satisfy;
        }
        if (currentFontFamily.equals("SourceCodePro-Regular")) {
            currentFontFamilyInt = source_code_pro;
        }
        if (currentFontFamily.equals("Ubuntu-Regular")) {
            currentFontFamilyInt = ubuntu;
        }
        usernameEditText.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        usernameEditText.setTextSize(currentFontSize);
        emailEditText.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        emailEditText.setTextSize(currentFontSize);
        passwordEditText.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        passwordEditText.setTextSize(currentFontSize);
        signUpButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        signUpButton.setTextSize(currentFontSize);
        signUpButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        signUpButton.setTextSize(currentFontSize);
        signInButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        signInButton.setTextSize(currentFontSize);
        signOutButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        signOutButton.setTextSize(currentFontSize);
        updateButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        updateButton.setTextSize(currentFontSize);
        toggleNight.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        toggleNight.setTextSize(currentFontSize);
        goToBible.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        goToBible.setTextSize(currentFontSize);
        resetPassword.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
        resetPassword.setTextSize(currentFontSize);
    }

    public void enterKey() {
        if (userIsSignedIn) {
            update();
        } else {
            signIn();
        }
    }

    public void toast(String string) {
        Toast toast = Toast.makeText(ProfileActivity.this, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}