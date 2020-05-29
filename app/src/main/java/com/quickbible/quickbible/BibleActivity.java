package com.quickbible.quickbible;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.os.Vibrator;

import static java.lang.Integer.parseInt;

public class BibleActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
	DatabaseReference database;
	FirebaseUser user;
	Context context = this;
	ConstraintLayout layout;
	AlertDialog.Builder builder;
	Handler handler = new Handler();
	View menuBackground, menuProfile, menuSettings, menuVersions, menuHistory, menuHighlights, menuReferences, menuAudio, menuHighlight, menuCopy;
	TableLayout content;
	BounceScrollView bounce;
	ImageView menuProfileImage, menuSettingsImage, menuVersionsImage, menuHistoryImage, menuHighlightsImage, menuReferencesImage, menuAudioImage, menuHighlightImage, menuCopyImage;
	ImageButton nightMode, random, left, right;
	TextView topLabel, bottomLabel, menuProfileLabel, menuSettingsLabel, menuVersionsLabel, menuHistoryLabel, menuHighlightsLabel, menuReferencesLabel, menuAudioLabel, menuHighlightLabel, menuCopyLabel;
	String[] booksOfBible = new String[]{"Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", "1 Kings", "2 Kings", "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", "3 John", "Jude", "Revelation"}, booksOfBiblePaths = new String[]{"Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "1_Samuel", "2_Samuel", "1_Kings", "2_Kings", "1_Chronicles", "2_Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "Songs", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1_Corinthians", "2_Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1_Thessalonians", "2_Thessalonians", "1_Timothy", "2_Timothy", "Titus", "Philemon", "Hebrews", "James", "1_Peter", "2_Peter", "1_John", "2_John", "3_John", "Jude", "Revelation"};
	int[] numberOfChaptersInBooksOfBible = new int[]{50, 40, 27, 36, 34, 24, 21, 4, 31, 24, 22, 25, 29, 36, 10, 13, 10, 42, 150, 31, 12, 8, 66, 52, 5, 48, 12, 14, 3, 9, 1, 4, 7, 3, 3, 3, 2, 14, 4, 28, 16, 24, 21, 28, 16, 16, 13, 6, 6, 4, 4, 5, 3, 6, 4, 3, 1, 13, 5, 5, 3, 5, 1, 1, 1, 22};
	ArrayList<String> selectedVerses = new ArrayList<>(), formerSelectedVerses = new ArrayList<>(), versesInCurrentChapter = new ArrayList<>(), scriptureHistory = new ArrayList<>(), historyTitles = new ArrayList<>(), referent = new ArrayList<>(), referenceRelevance = new ArrayList<>(), crossReferences = new ArrayList<>();
	ArrayList<Integer> highlights = new ArrayList<>(), formerHighlights = new ArrayList<>();
	String uid, currentPage = "bible", currentFontFamily = "Poppins-Regular", bottomNavigationMode = "Verse", contentMode = "Verses", scrollCommand = "Stay";
	boolean online = false, touchBottomNavigationIsActive = true, touchTopNavigationIsActive = false, touchesEnded = true, isShaking = true, shouldVibe = true, shouldShake = true, shouldCheckBook = true, touchPositionIsConstantEnough = true, night = false, bookFreeze = false, shouldClearSelection = false, menuIsActive = false, stopMenuTimer = false, menuTimerIsActive = false;
	int screenWidth, screenHeight, bookFormer = 1, chapterFormer = 1, verseFormer = 1, formerBook = 0, formerChapter = 0, formerVerse = 0, currentBookValue = 1, currentChapterValue = 1, currentBook = 1, currentChapter = 1, currentVerse = 1, highlightsLocationsSize = 0, referenceTitlesSize = 0, lightFeedback = 10, mediumFeedback = 20, heavyFeedback = 25, iceland = R.font.iceland, lora = R.font.lora, oswald = R.font.oswald, playfair_display = R.font.playfair_display, poppins = R.font.poppins, raleway = R.font.raleway, roboto = R.font.roboto, satisfy = R.font.satisfy, source_code_pro = R.font.source_code_pro, ubuntu = R.font.ubuntu, currentFontFamilyInt = poppins;
	long touchTopNavigationTimeDown, currentFontSize = 18L, lastUpdate = -1;
	double xw, yh;
	float x, y, z, startX, startY, lastX, lastY, lastZ, contentOnTouchX, contentOnTouchY, contentOnTouchStartX, contentOnTouchStartY;
	Vibrator vibe;

	//MARK: Launch

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bible);

		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}

		//MARK: Launch Firebase

		database = FirebaseDatabase.getInstance().getReference();
		user = FirebaseAuth.getInstance().getCurrentUser();
		database.child("users").child(user.getUid()).child("currentPage").setValue("bible");
		uid = user.getUid();

		//MARK: Set Screen Size

		setScreenSize();

		//MARK: Define Views

        content = findViewById(R.id.tableLayout);
        layout = findViewById(R.id.BibleLayout);
		menuBackground = findViewById(R.id.menuBackground);
		menuProfile = findViewById(R.id.menuProfile);
		menuSettings = findViewById(R.id.menuSettings);
		menuVersions = findViewById(R.id.menuVersions);
		menuHistory = findViewById(R.id.menuHistory);
		menuHighlights = findViewById(R.id.menuHighlights);
		menuReferences = findViewById(R.id.menuReferences);
		menuAudio = findViewById(R.id.menuAudio);
		menuHighlight = findViewById(R.id.menuHighlight);
		menuCopy = findViewById(R.id.menuCopy);
		menuProfileImage = findViewById(R.id.profile);
		menuSettingsImage = findViewById(R.id.settings);
		menuVersionsImage = findViewById(R.id.versions);
		menuHistoryImage = findViewById(R.id.history);
		menuHighlightsImage = findViewById(R.id.highlights);
		menuReferencesImage = findViewById(R.id.references);
		menuAudioImage = findViewById(R.id.audio);
		menuHighlightImage = findViewById(R.id.highlight);
		menuCopyImage = findViewById(R.id.copy);
		menuProfileLabel = findViewById(R.id.profileLabel);
		menuSettingsLabel = findViewById(R.id.settingsLabel);
		menuVersionsLabel = findViewById(R.id.versionsLabel);
		menuHistoryLabel = findViewById(R.id.historyLabel);
		menuHighlightsLabel = findViewById(R.id.highlightsLabel);
		menuReferencesLabel = findViewById(R.id.referencesLabel);
		menuAudioLabel = findViewById(R.id.audioLabel);
		menuHighlightLabel = findViewById(R.id.highlightLabel);
		menuCopyLabel = findViewById(R.id.copyLabel);
        bounce = findViewById(R.id.tableScrollView);
        nightMode = findViewById(R.id.nightMode);
        random = findViewById(R.id.random);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        topLabel = findViewById(R.id.topLabelTextView);
        bottomLabel = findViewById(R.id.bottomLabelTextView);
        Button previous = findViewById(R.id.previous), next = findViewById(R.id.next);
		final View navigationDisplayView = findViewById(R.id.navigationDisplayView);
		final TextView previousBookTextViewOldTestament = findViewById(R.id.previousBookTextViewOldTestament);
		final TextView currentBookTextViewOldTestament = findViewById(R.id.currentBookTextViewOldTestament);
		final TextView nextBookTextViewOldTestament = findViewById(R.id.nextBookTextViewOldTestament);
		final TextView previousChapterTextViewOldTestament = findViewById(R.id.previousChapterTextViewOldTestament);
		final TextView currentChapterTextViewOldTestament = findViewById(R.id.currentChapterTextViewOldTestament);
		final TextView nextChapterTextViewOldTestament = findViewById(R.id.nextChapterTextViewOldTestament);
		final TextView previousBookTextViewNewTestament = findViewById(R.id.previousBookTextViewNewTestament);
		final TextView currentBookTextViewNewTestament = findViewById(R.id.currentBookTextViewNewTestament);
		final TextView nextBookTextViewNewTestament = findViewById(R.id.nextBookTextViewNewTestament);
		final TextView previousChapterTextViewNewTestament = findViewById(R.id.previousChapterTextViewNewTestament);
		final TextView currentChapterTextViewNewTestament = findViewById(R.id.currentChapterTextViewNewTestament);
		final TextView nextChapterTextViewNewTestament = findViewById(R.id.nextChapterTextViewNewTestament);

		//MARK: Import Resources

		InputStream is = getResources().openRawResource(R.raw.ref);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, StandardCharsets.UTF_8)
		);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				referent.add(tokens[0]);
				crossReferences.add(tokens[1]);
				referenceRelevance.add(tokens[2]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//MARK: Listen to Firebase

		database.child("users/" + user.getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				currentPage = (String) dataSnapshot.child("currentPage").getValue();
				assert currentPage != null;
				if (currentPage.equals("profile")) {
					goToProfile();
				}
				shouldClearSelection = currentBook != currentBookValue || currentChapter != currentChapterValue;
				ArrayList<String> freshSelectedVerses = (ArrayList<String>) dataSnapshot.child("selectedVerses").getValue();
				if (freshSelectedVerses != null) {
					HashSet<String> hashSet = new HashSet<>(freshSelectedVerses);
					freshSelectedVerses.clear();
					freshSelectedVerses.addAll(hashSet);
					selectedVerses = freshSelectedVerses;
				}
                currentBook = dataSnapshot.child("currentBook").getValue(int.class);
                currentChapter = dataSnapshot.child("currentChapter").getValue(int.class);
                currentVerse = dataSnapshot.child("currentVerse").getValue(int.class);
                bottomNavigationMode = (String) dataSnapshot.child("bottomNavigationMode").getValue();
                currentFontFamily = (String) dataSnapshot.child("currentFontFamily").getValue();
                currentFontSize = (long) dataSnapshot.child("currentFontSize").getValue();
				night = (boolean) dataSnapshot.child("night").getValue();
				setStyle();
				locate(false);
                currentBookValue = currentBook;
                currentChapterValue = currentChapter;
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				formerBook = 0;
				formerChapter = 0;
				formerVerse = 0;
				locate(false);
			}
		}, 75);

		//MARK: Screen - Listeners

		layout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						vibe.vibrate(lightFeedback);
						startX = event.getX();
						startY = event.getY();
						xw = (startX - (v.getWidth() * 0.1)) / (v.getWidth() * (0.8));
						yh = (startY - (v.getHeight() * 0.1)) / (v.getHeight() * (0.8));
						if (xw <= 0) {
							xw = 0;
						} else if (xw > 1) {
							xw = 1;
						}
						if (yh <= 0) {
							yh = 0;
						} else if (yh > 1) {
							yh = 1;
						}
						if (startY < topLabel.getBottom()) {
							touchTopNavigationIsActive = true;
							touchTopNavigationTimeDown = System.currentTimeMillis();
							if (contentMode.equals("Verses")) {
								navigationDisplayView.setAlpha(1);
								previousBookTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								currentBookTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								nextBookTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								previousChapterTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								currentChapterTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								nextChapterTextViewOldTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								previousBookTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								currentBookTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								nextBookTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								previousChapterTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								currentChapterTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								nextChapterTextViewNewTestament.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
								previousBookTextViewOldTestament.setTextSize(currentFontSize);
								currentBookTextViewOldTestament.setTextSize(currentFontSize);
								nextBookTextViewOldTestament.setTextSize(currentFontSize);
								previousChapterTextViewOldTestament.setTextSize(currentFontSize);
								currentChapterTextViewOldTestament.setTextSize(currentFontSize);
								nextChapterTextViewOldTestament.setTextSize(currentFontSize);
								previousBookTextViewNewTestament.setTextSize(currentFontSize);
								currentBookTextViewNewTestament.setTextSize(currentFontSize);
								nextBookTextViewNewTestament.setTextSize(currentFontSize);
								previousChapterTextViewNewTestament.setTextSize(currentFontSize);
								currentChapterTextViewNewTestament.setTextSize(currentFontSize);
								nextChapterTextViewNewTestament.setTextSize(currentFontSize);
								previousBookTextViewOldTestament.setGravity(Gravity.CENTER);
								currentBookTextViewOldTestament.setGravity(Gravity.CENTER);
								nextBookTextViewOldTestament.setGravity(Gravity.CENTER);
								previousChapterTextViewOldTestament.setGravity(Gravity.CENTER);
								currentChapterTextViewOldTestament.setGravity(Gravity.CENTER);
								nextChapterTextViewOldTestament.setGravity(Gravity.CENTER);
								previousBookTextViewNewTestament.setGravity(Gravity.CENTER);
								currentBookTextViewNewTestament.setGravity(Gravity.CENTER);
								nextBookTextViewNewTestament.setGravity(Gravity.CENTER);
								previousChapterTextViewNewTestament.setGravity(Gravity.CENTER);
								currentChapterTextViewNewTestament.setGravity(Gravity.CENTER);
								nextChapterTextViewNewTestament.setGravity(Gravity.CENTER);
								previousBookTextViewOldTestament.setAlpha(1);
								currentBookTextViewOldTestament.setAlpha(1);
								nextBookTextViewOldTestament.setAlpha(1);
								previousChapterTextViewOldTestament.setAlpha(1);
								currentChapterTextViewOldTestament.setAlpha(1);
								nextChapterTextViewOldTestament.setAlpha(1);
								previousBookTextViewNewTestament.setAlpha(1);
								currentBookTextViewNewTestament.setAlpha(1);
								nextBookTextViewNewTestament.setAlpha(1);
								previousChapterTextViewNewTestament.setAlpha(1);
								currentChapterTextViewNewTestament.setAlpha(1);
								nextChapterTextViewNewTestament.setAlpha(1);
							}
							shouldCheckBook = true;
							bookFreeze = false;
							AsyncTask.execute(new Runnable() {
								@Override
								public void run() {
									menuDown();
								}
							});
						}
						break;
					case MotionEvent.ACTION_MOVE:
						touchesEnded = false;
						float movingX = event.getX();
						float movingY = event.getY();
						xw = (movingX - (v.getWidth() * 0.1)) / (v.getWidth() * (0.8));
						yh = (movingY - (v.getHeight() * 0.1)) / (v.getHeight() * (0.8));
						if (xw <= 0) {
							xw = 0;
						} else if (xw > 1) {
							xw = 1;
						}
						if (yh <= 0) {
							yh = 0;
						} else if (yh > 1) {
							yh = 1;
						}
						if (touchTopNavigationIsActive) {
							if (contentMode.equals("Verses")) {
								currentVerse = 1;
								if (!bookFreeze) {
									currentBook = (int) (yh * (float) (booksOfBible.length));
									if (currentBook < 1) {
										currentBook = 1;
									} else if (currentBook > booksOfBible.length) {
										currentBook = booksOfBible.length;
									}
								}
								currentChapter = 1 + (int) (xw * (float) (numberOfChaptersInBooksOfBible[currentBook - 1]));
								if (currentChapter < 1) {
									currentChapter = 1;
								} else if (currentChapter > numberOfChaptersInBooksOfBible[currentBook - 1]) {
									currentChapter = numberOfChaptersInBooksOfBible[currentBook - 1];
								}
								if (startY < topLabel.getBottom()) {
									previousBookTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorGray));
									nextBookTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorGray));
									previousChapterTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorGray));
									nextChapterTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorGray));
									previousBookTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorGray));
									nextBookTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorGray));
									previousChapterTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorGray));
									nextChapterTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorGray));
									if (night) {
										currentBookTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorWhite));
										currentChapterTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorWhite));
										currentBookTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorWhite));
										currentChapterTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorWhite));
									} else {
										currentBookTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorBlack));
										currentChapterTextViewOldTestament.setTextColor(getResources().getColor(R.color.colorBlack));
										currentBookTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorBlack));
										currentChapterTextViewNewTestament.setTextColor(getResources().getColor(R.color.colorBlack));
									}
									if (night) {
										navigationDisplayView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
									} else {
										navigationDisplayView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
									}
									navigationDisplayView.setAlpha((float) 0.95);
									touchTopNavigationIsActive = true;
								}
								String topLabelTextViewText = booksOfBible[currentBook - 1] + " " + currentChapter + ":" + currentVerse;
								topLabel.setText(topLabelTextViewText);
								String previousBookChoice;
								String currentBookChoice = booksOfBible[currentBook - 1];
								String nextBookChoice;
								String previousChapterChoice = "•";
								String currentChapterChoice = currentChapter + "";
								String nextChapterChoice;
								if (currentBook == 1) {
									previousBookChoice = "•";
									nextBookChoice = booksOfBible[currentBook];
								} else if (currentBook == booksOfBible.length) {
									previousBookChoice = booksOfBible[currentBook - 2];
									nextBookChoice = "•";
								} else {
									previousBookChoice = booksOfBible[currentBook - 2];
									nextBookChoice = booksOfBible[currentBook];
								}
								if ((currentChapter == 1) && (currentChapter == numberOfChaptersInBooksOfBible[currentBook - 1])) {
									previousBookChoice = "•";
									nextChapterChoice = "•";
								} else if (currentChapter == 1) {
									previousChapterChoice = "•";
									nextChapterChoice = (currentChapter + 1) + "";
								} else if (currentChapter == numberOfChaptersInBooksOfBible[currentBook - 1]) {
									previousChapterChoice = (currentChapter - 1) + "";
									nextChapterChoice = "•";
								} else {
									previousChapterChoice = (currentChapter - 1) + "";
									nextChapterChoice = (currentChapter + 1) + "";
								}
								currentBookTextViewOldTestament.setAlpha(1);
								currentChapterTextViewOldTestament.setAlpha(1);
								currentBookTextViewNewTestament.setAlpha(1);
								currentChapterTextViewNewTestament.setAlpha(1);
								if (previousBookChoice.equals("•")) {
									previousBookTextViewOldTestament.setAlpha((float) 0.25);
									previousBookTextViewNewTestament.setAlpha((float) 0.25);
								} else {
									previousBookTextViewOldTestament.setAlpha((float) 0.5);
									previousBookTextViewNewTestament.setAlpha((float) 0.5);
								}
								if (previousChapterChoice.equals("•")) {
									previousChapterTextViewOldTestament.setAlpha((float) 0.25);
									previousChapterTextViewNewTestament.setAlpha((float) 0.25);
								} else {
									previousChapterTextViewOldTestament.setAlpha((float) 0.5);
									previousChapterTextViewNewTestament.setAlpha((float) 0.5);
								}
								if (nextBookChoice.equals("•")) {
									nextBookTextViewOldTestament.setAlpha((float) 0.25);
									nextBookTextViewNewTestament.setAlpha((float) 0.25);
								} else {
									nextBookTextViewOldTestament.setAlpha((float) 0.5);
									nextBookTextViewNewTestament.setAlpha((float) 0.5);
								}
								if (nextChapterChoice.equals("•")) {
									nextChapterTextViewOldTestament.setAlpha((float) 0.25);
									nextChapterTextViewOldTestament.setAlpha((float) 0.25);
								} else {
									nextChapterTextViewOldTestament.setAlpha((float) 0.5);
									nextChapterTextViewNewTestament.setAlpha((float) 0.5);
								}
								if (currentBook <= 39) {
									previousBookTextViewOldTestament.setAlpha((float) 0);
									currentBookTextViewOldTestament.setAlpha((float) 0);
									nextBookTextViewOldTestament.setAlpha((float) 0);
									previousChapterTextViewOldTestament.setAlpha((float) 0);
									currentChapterTextViewOldTestament.setAlpha((float) 0);
									nextChapterTextViewOldTestament.setAlpha((float) 0);
								} else {
									previousBookTextViewNewTestament.setAlpha((float) 0);
									currentBookTextViewNewTestament.setAlpha((float) 0);
									nextBookTextViewNewTestament.setAlpha((float) 0);
									previousChapterTextViewNewTestament.setAlpha((float) 0);
									currentChapterTextViewNewTestament.setAlpha((float) 0);
									nextChapterTextViewNewTestament.setAlpha((float) 0);
								}
								previousBookTextViewOldTestament.setText(previousBookChoice);
								currentBookTextViewOldTestament.setText(currentBookChoice);
								nextBookTextViewOldTestament.setText(nextBookChoice);
								previousChapterTextViewOldTestament.setText(previousChapterChoice);
								currentChapterTextViewOldTestament.setText(currentChapterChoice);
								nextChapterTextViewOldTestament.setText(nextChapterChoice);
								previousBookTextViewNewTestament.setText(previousBookChoice);
								currentBookTextViewNewTestament.setText(currentBookChoice);
								nextBookTextViewNewTestament.setText(nextBookChoice);
								previousChapterTextViewNewTestament.setText(previousChapterChoice);
								currentChapterTextViewNewTestament.setText(currentChapterChoice);
								nextChapterTextViewNewTestament.setText(nextChapterChoice);
							}
						}
						break;
					case MotionEvent.ACTION_UP:
						vibe.vibrate(mediumFeedback);
						float endY = event.getY();
						long touchTopNavigationTimeUp = Calendar.getInstance().getTimeInMillis();
						if (touchTopNavigationTimeUp - touchTopNavigationTimeDown < 250) {
							if (contentMode.equals("Verses")) {
								if (touchTopNavigationIsActive) {
									summonBookList();
								}
							} else {
								if (endY < topLabel.getBottom()) {
									contentMode = "Verses";
									if (!online) {
										locate(false);
									}
									storeLocation();
								}
							}
						} else {
							shouldCheckBook = false;
							shouldClearSelection = true;
							if (contentMode.equals("Verses")) {
								if (!online) {
									locate(false);
								}
								storeLocation();
							}
						}
						navigationDisplayView.setAlpha(0);
						previousBookTextViewOldTestament.setAlpha(0);
						currentBookTextViewOldTestament.setAlpha(0);
						nextBookTextViewOldTestament.setAlpha(0);
						previousChapterTextViewOldTestament.setAlpha(0);
						currentChapterTextViewOldTestament.setAlpha(0);
						nextChapterTextViewOldTestament.setAlpha(0);
						previousBookTextViewNewTestament.setAlpha(0);
						currentBookTextViewNewTestament.setAlpha(0);
						nextBookTextViewNewTestament.setAlpha(0);
						previousChapterTextViewNewTestament.setAlpha(0);
						currentChapterTextViewNewTestament.setAlpha(0);
						nextChapterTextViewNewTestament.setAlpha(0);
						touchTopNavigationIsActive = false;
						touchesEnded = true;
						break;
				}
				return true;
			}
		});
		bounce.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				setScreenSize();
				int scrollY = bounce.getScrollY();
				int tableViewHeight = bottomLabel.getTop() - topLabel.getBottom();
				double threshold = currentFontSize * 6;
				int bottomPosition = bounce.getChildAt(bounce.getChildCount() - 1).getBottom() - tableViewHeight;
				if (scrollY < (0 - (threshold))) {
					scrollCommand = "Previous";
					left.setAlpha((float) 0.5);
					right.setAlpha((float) 1);
				} else if (scrollY > (bottomPosition + (threshold))) {
					scrollCommand = "Next";
					left.setAlpha((float) 1);
					right.setAlpha((float) 0.5);
				} else {
					scrollCommand = "Stay";
					left.setAlpha((float) 1);
					right.setAlpha((float) 1);
				}
			}
		});
		bounce.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				contentOnTouchX = event.getX() + bounce.getLeft();
				contentOnTouchY = event.getY() + bounce.getTop();
				int variance = 5;
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					touchPositionIsConstantEnough = !(contentOnTouchX < contentOnTouchStartX + variance) && !(contentOnTouchX > contentOnTouchStartX - variance) && !(contentOnTouchY < contentOnTouchStartY + variance) && !(contentOnTouchY > contentOnTouchStartY - variance);
				}
				if (menuIsActive) {
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						menuMove();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						menuUp();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					left.setAlpha((float) 1);
					right.setAlpha((float) 1);
					if (scrollCommand.equals("Previous")) {
						previousChapter();
					} else if (scrollCommand.equals("Next")) {
						nextChapter();
					}
					menuIsActive = false;
					menuTimerIsActive = false;
					stopMenuTimer = true;
					touchPositionIsConstantEnough = true;
				}
				return menuIsActive;
			}
		});

		//MARK: Top - Listeners

		nightMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				formerBook = 0;
				formerChapter = 0;
				formerVerse = 0;
				night = !night;
				database.child("users").child(uid).child("night").setValue(night);
				setStyle();
				vibe.vibrate(heavyFeedback);
			}
		});
		random.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToRandomVerse();
				vibe.vibrate(heavyFeedback);
			}
		});

		//MARK: Middle - Listeners

		previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				previousChapter();
			}
		});
		previous.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (contentMode.equals("Verses")) {
					if (currentBook != 1) {
						currentBook = currentBook - 1;
						currentVerse = 1;
						currentChapter = 1;
					}
					shouldClearSelection = true;
					if (!online) {
						locate(false);
					}
					storeLocation();
					vibe.vibrate(heavyFeedback);
				}
				return true;
			}
		});
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextChapter();
			}
		});
		next.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (contentMode.equals("Verses")) {
					if (currentBook != booksOfBible.length) {
						currentBook = currentBook + 1;
						currentVerse = 1;
						currentChapter = 1;
					}
					shouldClearSelection = true;
					if (!online) {
						locate(false);
					}
					storeLocation();
					vibe.vibrate(heavyFeedback);
				}
				return true;
			}
		});

		//MARK: Bottom - Listeners

		left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contentMode.equals("Verses")) {
					switch (bottomNavigationMode) {
						case "Verse":
							if (currentVerse != 1) {
								currentVerse = currentVerse - 1;
							}
							vibe.vibrate(lightFeedback);
							break;
						case "Chapter":
							if (currentChapter != 1) {
								currentChapter = currentChapter - 1;
								currentVerse = 1;
							} else if (currentBook != 1) {
								currentBook = currentBook - 1;
								currentVerse = 1;
								currentChapter = numberOfChaptersInBooksOfBible[currentBook - 1];
							}
							vibe.vibrate(mediumFeedback);
							break;
						case "Book":
							if (currentBook != 1) {
								currentBook = currentBook - 1;
								currentVerse = 1;
								currentChapter = 1;
							}
							vibe.vibrate(heavyFeedback);
							break;
					}
					shouldClearSelection = true;
					if (!online) {
						locate(false);
					}
					storeLocation();
				}
			}
		});
		right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contentMode.equals("Verses")) {
					switch (bottomNavigationMode) {
						case "Verse":
							if (currentVerse != versesInCurrentChapter.size()) {
								currentVerse = currentVerse + 1;
							}
							vibe.vibrate(lightFeedback);
							break;
						case "Chapter":
							shouldClearSelection = true;
							if (currentChapter != numberOfChaptersInBooksOfBible[currentBook - 1]) {
								currentChapter = currentChapter + 1;
								currentVerse = 1;
							} else {
								if (currentBook != booksOfBible.length) {
									currentBook = currentBook + 1;
									currentVerse = 1;
									currentChapter = 1;
								}
							}
							vibe.vibrate(mediumFeedback);
							break;
						case "Book":
							shouldClearSelection = true;
							if (currentBook != booksOfBible.length) {
								currentBook = currentBook + 1;
								currentVerse = 1;
								currentChapter = 1;
							}
							vibe.vibrate(heavyFeedback);
							break;
					}
					if (!online) {
						locate(false);
					}
					storeLocation();
				}
			}
		});
		bottomLabel.setOnTouchListener(new View.OnTouchListener() {
			int CLICK_ACTION_THRESHOLD = 200;
			float startX;
			float startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						vibe.vibrate(lightFeedback);
						verseFormer = currentVerse;
						chapterFormer = currentChapter;
						bookFormer = currentBook;
						touchBottomNavigationIsActive = true;
						startX = event.getX();
						startY = event.getY();
						xw = (startX / (v.getWidth()));
						if (xw <= 0) {
							xw = 0;
						} else if (xw > 1) {
							xw = 1;
						}
						break;
					case MotionEvent.ACTION_MOVE:
						float movingX = event.getX();
						if (startX != movingX) {
							touchBottomNavigationIsActive = false;
						}
						xw = (movingX / (v.getWidth()));
						if (xw <= 0) {
							xw = 0;
						} else if (xw > 1) {
							xw = 1;
						}
						if (contentMode.equals("Verses")) {
							switch (bottomNavigationMode) {
								case "Verse":
									shouldClearSelection = false;
									int currentVerseChoice = (int) (xw * (versesInCurrentChapter.size()));
									if (currentVerseChoice < 1) {
										currentVerseChoice = 1;
									}
									currentVerse = currentVerseChoice;
									break;
								case "Chapter":
									shouldClearSelection = true;
									int currentChapterChoice = (int) (xw * (numberOfChaptersInBooksOfBible[currentBook - 1]));
									if (currentChapterChoice < 1) {
										currentChapterChoice = 1;
									}
									currentChapter = currentChapterChoice;
									currentVerse = 1;
									break;
								case "Book":
									shouldClearSelection = true;
									int currentBookChoice = (int) (xw * (booksOfBible.length));
									if (currentBookChoice < 1) {
										currentBookChoice = 1;
									}
									currentBook = currentBookChoice;
									currentChapter = 1;
									currentVerse = 1;
									break;
							}
							shouldVibe = false;
							locate(false);
						} else {
							summonContent();
						}
						break;
					case MotionEvent.ACTION_UP:
						vibe.vibrate(mediumFeedback);
						float endX = event.getX();
						float endY = event.getY();
						if (isAClick(startX, endX, startY, endY)) {
							if (touchBottomNavigationIsActive) {
								if (contentMode.equals("Verses")) {
									if (endX < (v.getWidth() / 2.0)) {
										switch (bottomNavigationMode) {
											case "Verse":
												bottomNavigationMode = "Book";
												break;
											case "Book":
												bottomNavigationMode = "Chapter";
												break;
											case "Chapter":
												bottomNavigationMode = "Verse";
												break;
										}
									} else {
										switch (bottomNavigationMode) {
											case "Verse":
												bottomNavigationMode = "Chapter";
												break;
											case "Chapter":
												bottomNavigationMode = "Book";
												break;
											case "Book":
												bottomNavigationMode = "Verse";
												break;
										}
									}
									database.child("users").child(uid).child("bottomNavigationMode").setValue(bottomNavigationMode);
									currentVerse = verseFormer;
									currentChapter = chapterFormer;
									currentBook = bookFormer;
									if (!online) {
										locate(false);
									}
									storeLocation();
									touchBottomNavigationIsActive = false;
								}
							}
							vibe.vibrate(heavyFeedback);
						}
						break;
				}
				return true;
			}

			private boolean isAClick(float startX, float endX, float startY, float endY) {
				float differenceX = Math.abs(startX - endX);
				float differenceY = Math.abs(startY - endY);
				return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
			}
		});

		//MARK: Shake

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor accel = null;
		if (sensorManager != null) {
			accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		assert sensorManager != null;
		sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

		//MARK: Feedback

		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		menuBackground.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				contentOnTouchStartX = event.getX();
				contentOnTouchStartY = event.getY();
				contentOnTouchX = event.getX();
				contentOnTouchY = event.getY();
				return menuIsActive;
			}
		});

		//MARK: Check if Online

		DatabaseReference connected = FirebaseDatabase.getInstance().getReference(".info/connected");
		connected.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				online = snapshot.getValue(Boolean.class);
				setStatusBarColor();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				setStatusBarColor();
			}
		});
		if (!online) {
			setStatusBarColor();
			locate(false);
		}
	}

	//MARK: Top - Functions

	private void goToRandomVerse() {
		Random randomNumber = new Random();
		currentBook = randomNumber.nextInt(booksOfBible.length);
		int numberOfChaptersInBook = numberOfChaptersInBooksOfBible[currentBook - 1];
		currentChapter = randomNumber.nextInt(numberOfChaptersInBook) + 1;
		shouldClearSelection = true;
		locate(true);
	}

	//MARK: Bottom - Functions

	private void previousChapter() {
		if (contentMode.equals("Verses")) {
			if (currentChapter != 1) {
				currentChapter = currentChapter - 1;
				currentVerse = 1;
			} else if (currentBook != 1) {
				currentBook = currentBook - 1;
				currentVerse = 1;
				currentChapter = numberOfChaptersInBooksOfBible[currentBook - 1];
			}
			shouldClearSelection = true;
			if (!online) {
				locate(false);
			}
			storeLocation();
			vibe.vibrate(mediumFeedback);
		}
	}

	private void nextChapter() {
		if (contentMode.equals("Verses")) {
			if (currentChapter != numberOfChaptersInBooksOfBible[currentBook - 1]) {
				currentChapter = currentChapter + 1;
				currentVerse = 1;
			} else {
				if (currentBook != booksOfBible.length) {
					currentBook = currentBook + 1;
					currentVerse = 1;
					currentChapter = 1;
				}
			}
			shouldClearSelection = true;
			if (!online) {
				locate(false);
			}
			storeLocation();
			vibe.vibrate(mediumFeedback);
		}
	}

	//MARK: Menu

	@SuppressLint("ClickableViewAccessibility")
	private void menuEvents(TableRow row) {
		row.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					stopMenuTimer = false;
					if (!menuTimerIsActive) {
						menuTimer();
					}
					menuTimerIsActive = true;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (menuIsActive) {
						menuUp();
						menuIsActive = false;
						menuTimerIsActive = false;
						stopMenuTimer = true;
						touchPositionIsConstantEnough = true;
					} else {
						stopMenuTimer = true;
					}
				}
				return false;
			}
		});
	}

	private void menuTimer() {
		setScreenSize();
		setMenuIcons(screenWidth, screenHeight);
		final Handler timerHandler = new Handler();
		Thread task = new Thread() {
			@Override
			public void run() {
				if (stopMenuTimer) {
					return;
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!menuIsActive && touchPositionIsConstantEnough) {
							vibe.vibrate(heavyFeedback);
							menuIsActive = true;
						}
						menuMove();
					}
				});
			}
		};
		timerHandler.postDelayed(task, 1000);
	}

	private void menuDown() {
		ArrayList<String> list = new ArrayList<>();
		while (shouldCheckBook) {
			list.add(currentBook + "");
			if (list.size() > 10) {
				list.remove(0);
			}
			boolean flag = true;
			String comparator = list.get(0);
			for (String string : list) {
				if (comparator.equals(string)) {
					continue;
				}
				flag = false;
			}
			if (list.size() == 10 && flag && !bookFreeze) {
				bookFreeze = true;
				vibe.vibrate(lightFeedback);
			}
			try {
				Thread.sleep(100);
			} catch (Throwable ignored) {
			}
		}
	}

	private void menuMove() {
		setScreenSize();
		setMenuIcons(screenWidth, screenHeight);
		menuProfile.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuProfile.setY(0);
		menuProfile.setX(0);
		menuSettings.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuSettings.setY(0);
		menuSettings.setX((float) screenWidth / 3);
		menuVersions.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuVersions.setY(0);
		menuVersions.setX(((float) screenWidth / 3) * 2);
		menuHistory.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuHistory.setY((float) screenHeight / 3);
		menuHistory.setX(0);
		menuHighlights.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuHighlights.setY((float) screenHeight / 3);
		menuHighlights.setX((float) screenWidth / 3);
		menuReferences.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuReferences.setY((float) screenHeight / 3);
		menuReferences.setX(((float) screenWidth / 3) * 2);
		menuAudio.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuAudio.setY(((float) screenHeight / 3) * 2);
		menuAudio.setX(0);
		menuHighlight.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuHighlight.setY(((float) screenHeight / 3) * 2);
		menuHighlight.setX((float) screenWidth / 3);
		menuCopy.setLayoutParams(new Constraints.LayoutParams(screenWidth / 3, screenHeight / 3));
		menuCopy.setY(((float) screenHeight / 3) * 2);
		menuCopy.setX(((float) screenWidth / 3) * 2);
		float eventX = contentOnTouchX;
		float eventY = contentOnTouchY;
		if (menuIsActive) {
			menuProfile.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuSettings.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuVersions.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuHistory.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuHighlights.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuReferences.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuAudio.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuHighlight.setBackgroundColor(getResources().getColor(R.color.colorClear));
			menuCopy.setBackgroundColor(getResources().getColor(R.color.colorClear));
			if (night) {
				menuBackground.setBackgroundColor(getResources().getColor(R.color.colorBlack));
			} else {
				menuBackground.setBackgroundColor(getResources().getColor(R.color.colorWhite));
			}
			menuBackground.setAlpha((float) 0.95);
			String horizontal;
			String vertical;
			if (eventX < (float) screenWidth / 3) {
				horizontal = "left";
			} else if (eventX > (float) screenWidth * 2 / 3) {
				horizontal = "right";
			} else {
				horizontal = "center";
			}
			if (eventY < (float) screenHeight / 3) {
				vertical = "top";
			} else if (eventY > (float) screenHeight * 2 / 3) {
				vertical = "bottom";
			} else {
				vertical = "center";
			}
			if (vertical.equals("top") && horizontal.equals("left")) {
				menuProfile.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("top") && horizontal.equals("center")) {
				menuSettings.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("top") && horizontal.equals("right")) {
				menuVersions.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("center") && horizontal.equals("left")) {
				menuHistory.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("center") && horizontal.equals("center")) {
				menuHighlights.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("center") && horizontal.equals("right")) {
				menuReferences.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("bottom") && horizontal.equals("left")) {
				menuAudio.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("bottom") && horizontal.equals("center")) {
				menuHighlight.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			if (vertical.equals("bottom") && horizontal.equals("right")) {
				menuCopy.setBackgroundColor(getResources().getColor(R.color.colorMenuSelected));
			}
			menuProfileImage.setVisibility(View.VISIBLE);
			menuSettingsImage.setVisibility(View.VISIBLE);
			menuVersionsImage.setVisibility(View.VISIBLE);
			menuHistoryImage.setVisibility(View.VISIBLE);
			menuHighlightsImage.setVisibility(View.VISIBLE);
			menuReferencesImage.setVisibility(View.VISIBLE);
			menuAudioImage.setVisibility(View.VISIBLE);
			menuHighlightImage.setVisibility(View.VISIBLE);
			menuCopyImage.setVisibility(View.VISIBLE);
			menuProfileLabel.setVisibility(View.VISIBLE);
			menuSettingsLabel.setVisibility(View.VISIBLE);
			menuVersionsLabel.setVisibility(View.VISIBLE);
			menuHistoryLabel.setVisibility(View.VISIBLE);
			menuHighlightsLabel.setVisibility(View.VISIBLE);
			menuReferencesLabel.setVisibility(View.VISIBLE);
			menuAudioLabel.setVisibility(View.VISIBLE);
			menuHighlightLabel.setVisibility(View.VISIBLE);
			menuCopyLabel.setVisibility(View.VISIBLE);
		}
	}

	private void menuUp() {
		menuBackground.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuProfile.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuSettings.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuVersions.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuHistory.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuHighlights.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuReferences.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuAudio.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuHighlight.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuCopy.setBackgroundColor(getResources().getColor(R.color.colorClear));
		menuProfileImage.setVisibility(View.INVISIBLE);
		menuSettingsImage.setVisibility(View.INVISIBLE);
		menuVersionsImage.setVisibility(View.INVISIBLE);
		menuHistoryImage.setVisibility(View.INVISIBLE);
		menuHighlightsImage.setVisibility(View.INVISIBLE);
		menuReferencesImage.setVisibility(View.INVISIBLE);
		menuAudioImage.setVisibility(View.INVISIBLE);
		menuHighlightImage.setVisibility(View.INVISIBLE);
		menuCopyImage.setVisibility(View.INVISIBLE);
		menuProfileLabel.setVisibility(View.INVISIBLE);
		menuSettingsLabel.setVisibility(View.INVISIBLE);
		menuVersionsLabel.setVisibility(View.INVISIBLE);
		menuHistoryLabel.setVisibility(View.INVISIBLE);
		menuHighlightsLabel.setVisibility(View.INVISIBLE);
		menuReferencesLabel.setVisibility(View.INVISIBLE);
		menuAudioLabel.setVisibility(View.INVISIBLE);
		menuHighlightLabel.setVisibility(View.INVISIBLE);
		menuCopyLabel.setVisibility(View.INVISIBLE);
		setScreenSize();
		float eventX = contentOnTouchX;
		float eventY = contentOnTouchY;
		String horizontal;
		String vertical;
		if (eventX < (float) screenWidth / 3) {
			horizontal = "left";
		} else if (eventX > (float) screenWidth * 2 / 3) {
			horizontal = "right";
		} else {
			horizontal = "center";
		}
		if (eventY < (float) screenHeight / 3) {
			vertical = "top";
		} else if (eventY > (float) screenHeight * 2 / 3) {
			vertical = "bottom";
		} else {
			vertical = "center";
		}
		if (vertical.equals("top") && horizontal.equals("left")) {
			database.child("users").child(uid).child("currentPage").setValue("profile");
		}
		if (vertical.equals("top") && horizontal.equals("center")) {
			settings();
		}
		if (vertical.equals("top") && horizontal.equals("right")) {
			versions();
		}
		if (vertical.equals("center") && horizontal.equals("left")) {
			listHistory();
		}
		if (vertical.equals("center") && horizontal.equals("center")) {
			listHighlights();
		}
		if (vertical.equals("center") && horizontal.equals("right")) {
			listReferences();
		}
		if (vertical.equals("bottom") && horizontal.equals("left")) {
			playAudio();
		}
		if (vertical.equals("bottom") && horizontal.equals("center")) {
			highlightPassage();
		}
		if (vertical.equals("bottom") && horizontal.equals("right")) {
			copyPassage();
		}
		vibe.vibrate(lightFeedback);
	}

	//MARK: Menu - Top

	private void goToProfile() {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
		Intent intention = new Intent(this, ProfileActivity.class);
		startActivityForResult(intention, 6);
	}

	private void settings() {
		builder = new AlertDialog.Builder(context);
		builder.setTitle("Settings");
		builder.setItems(new CharSequence[]{"Fullscreen", "Font Family", "Font Size", "Clear History", "Clear Highlights", "Cancel"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								vibe.vibrate(lightFeedback);
								setFullscreen();
								break;
							case 1:
								vibe.vibrate(lightFeedback);
								fontFamily();
								break;
							case 2:
								vibe.vibrate(lightFeedback);
								fontSize();
								break;
							case 3:
								vibe.vibrate(lightFeedback);
								database.child("users").child(uid).child("history").removeValue();
								break;
							case 4:
								vibe.vibrate(lightFeedback);
								database.child("users").child(uid).child("highlights").removeValue();
								break;
							case 5:
								vibe.vibrate(lightFeedback);
								break;
						}
					}
				});
		builder.create().show();
	}

	private void versions() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://biblehub.com/" + booksOfBiblePaths[currentBook - 1].toLowerCase() + "/" + currentChapter + "-" + currentVerse + ".htm")));
	}

	//MARK: Menu - Middle

	private void listHistory() {
		DatabaseReference ref = database.child("users").child(user.getUid()).child("history");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				scriptureHistory.clear();
				historyTitles.clear();
				ArrayList<Scripture> historyScriptures = new ArrayList<>();
				try {
					Object o = dataSnapshot.getValue();
					String s = "";
					if (o != null) {
						s = o.toString();
					}
					s = s.replace("[", "");
					s = s.replace("]", "");
					s = s.replace("{", "");
					s = s.replace("}", "");
					ArrayList<String> timestamps = new ArrayList<>();
					ArrayList<String> historyLocations = new ArrayList<>();
					String[] histories = s.split("\\s*, \\s*");
					for (String string : histories) {
						timestamps.add(Arrays.asList(string.split("\\s*=\\s*")).get(0));
						historyLocations.add(Arrays.asList(string.split("\\s*=\\s*")).get(1));
					}
					int i = 0;
					for (String ignored : historyLocations) {
						List<String> items = Arrays.asList(historyLocations.get(i).split("\\."));
						Scripture scripture = new Scripture(parseInt(items.get(0)), parseInt(items.get(1)), parseInt(items.get(2)), parseInt(timestamps.get(i)));
						historyScriptures.add(scripture);
						i++;
					}
					Collections.sort(historyScriptures);
					for (Scripture scripture : historyScriptures) {
						historyTitles.add(booksOfBible[scripture.getBook() - 1] + " " + scripture.getChapter() + ":" + scripture.getVerse());
						scriptureHistory.add(scripture.getBook() + "." + scripture.getChapter() + "." + scripture.getVerse());
					}

					if (historyTitles.size() > 0) {
						content.removeAllViews();
						contentMode = "History";
						summonList(historyScriptures);
					}
				} catch (Throwable ignore) {
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}

	private void listHighlights() {
		DatabaseReference ref = database.child("users").child(user.getUid()).child("highlights/");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				HashMap<String, HashMap> map = (HashMap<String, HashMap>) dataSnapshot.getValue();
				ArrayList<String> locations = new ArrayList<>();
				ArrayList<String> books = new ArrayList<>();
				ArrayList<String> chapters = new ArrayList<>();
				ArrayList<String> verses = new ArrayList<>();
				ArrayList<String> orderValues = new ArrayList<>();
				ArrayList<String> chaptersWithVerses = new ArrayList<>();
				ArrayList<String> bookStrings = new ArrayList<>();
				if (map != null) {
					for (Map.Entry<String, HashMap> entry : map.entrySet()) {
						bookStrings.add(entry.getKey());
						chaptersWithVerses.add(entry.getValue().toString());
					}
					ArrayList<String> chaptersPerBooksBooks = new ArrayList<>();
					ArrayList<String> chaptersPerBooksChaptersAndVerses = new ArrayList<>();
					for (int i = 0; i < bookStrings.size(); i++) {
						String[] chaptersWithVersesSplit = chaptersWithVerses.get(i).split(", ");
						for (String s : chaptersWithVersesSplit) {
							String chaptersPerBooksBook = bookStrings.get(i);
							String chaptersPerBooksChaptersAndVerse = s;
							chaptersPerBooksChaptersAndVerse = chaptersPerBooksChaptersAndVerse.replaceAll("[{]", "");
							chaptersPerBooksChaptersAndVerse = chaptersPerBooksChaptersAndVerse.replaceAll("[}]", "");
							chaptersPerBooksChaptersAndVerse = chaptersPerBooksChaptersAndVerse.replaceAll("\"", "");
							chaptersPerBooksBooks.add(chaptersPerBooksBook);
							chaptersPerBooksChaptersAndVerses.add(chaptersPerBooksChaptersAndVerse);
						}
					}
					ArrayList<String> versesPerChaptersPerBooksBooks = new ArrayList<>();
					ArrayList<String> versesPerChaptersPerBooksChapters = new ArrayList<>();
					ArrayList<String> versesPerChaptersPerBooksVerses = new ArrayList<>();
					for (int i = 0; i < chaptersPerBooksBooks.size(); i++) {
						if (!chaptersPerBooksChaptersAndVerses.get(i).equals("=")) {
							String[] chaptersPerBooksChaptersAndVersesSplit = chaptersPerBooksChaptersAndVerses.get(i).split("=");
							if (chaptersPerBooksChaptersAndVersesSplit.length > 1) {
								versesPerChaptersPerBooksBooks.add(chaptersPerBooksBooks.get(i));
								versesPerChaptersPerBooksChapters.add(chaptersPerBooksChaptersAndVersesSplit[0]);
								versesPerChaptersPerBooksVerses.add(chaptersPerBooksChaptersAndVersesSplit[1]);
							}
						}
					}
					for (int i = 0; i < versesPerChaptersPerBooksBooks.size(); i++) {
						String[] versesPerChaptersPerBooksVersesSplit = versesPerChaptersPerBooksVerses.get(i).split(",");
						for (String s : versesPerChaptersPerBooksVersesSplit) {
							String book = versesPerChaptersPerBooksBooks.get(i);
							String chapter = versesPerChaptersPerBooksChapters.get(i);
							orderValues.add(("00" + book).substring(book.length()) + ("000" + chapter).substring(chapter.length()) + ("000" + s).substring(s.length()));
						}
					}
					HashSet<String> hashSet = new HashSet<>(orderValues);
					orderValues.clear();
					orderValues.addAll(hashSet);
					Collections.sort(orderValues);
					for (int i = 0; i < orderValues.size(); i++) {
						String book = orderValues.get(i).substring(0, 2).replaceFirst("^0+(?!$)", "");
						String chapter = orderValues.get(i).substring(2, 5).replaceFirst("^0+(?!$)", "");
						String verse = orderValues.get(i).substring(5, 8).replaceFirst("^0+(?!$)", "");
						if (!verse.equals("0")) {
							books.add(book);
							chapters.add(chapter);
							verses.add(verse);
							locations.add(book + "." + chapter + "." + verse);
						}
					}
					highlightsLocationsSize = locations.size();
					if (highlightsLocationsSize > 0) {
						contentMode = "Highlights";
						summonList(books, chapters, verses);
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}

	private void listReferences() {
		final ArrayList<Scripture> scriptures = new ArrayList<>();
		int i = 0;
		for (String referent : referent) {
			if (referent.equals(currentBook + "." + currentChapter + "." + currentVerse)) {
				List<String> items = Arrays.asList(crossReferences.get(i).split("\\."));
				Scripture scripture = new Scripture(parseInt(items.get(0)), parseInt(items.get(1)), parseInt(items.get(2)), parseInt(referenceRelevance.get(i)));
				scriptures.add(scripture);
			}
			i++;
		}
		Collections.sort(scriptures);
		ArrayList<String> referenceTitles = new ArrayList<>();
		for (Scripture scripture : scriptures) {
			referenceTitles.add(booksOfBible[scripture.getBook() - 1] + " " + scripture.getChapter() + ":" + scripture.getVerse());
		}
		content.removeAllViews();
		setScreenSize();
		referenceTitlesSize = referenceTitles.size();
		if (referenceTitles.size() > 0) {
			contentMode = "Cross References";
			summonList(scriptures);
		}
	}

	//MARK: Menu - Bottom

	private void playAudio() {
		String url = "https://audio.esv.org/hw/" + String.format(Locale.ENGLISH, "%02d", currentBook) + String.format(Locale.ENGLISH, "%03d", currentChapter) + String.format(Locale.ENGLISH, "%03d", currentVerse) + "-" + String.format(Locale.ENGLISH, "%02d", currentBook) + String.format(Locale.ENGLISH, "%03d", currentChapter) + String.format(Locale.ENGLISH, "%03d", versesInCurrentChapter.size());
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	private void highlightPassage() {
		if (selectedVerses.size() < 2 && selectedVerses.get(0).equals("x")) {
			if (!highlights.contains(currentVerse)) {
				highlights.add(currentVerse);
			}
		} else {
			for (String selectedVerse : selectedVerses) {
				if (!selectedVerse.equals("x")) {
					if (!highlights.contains(parseInt(selectedVerse) + 1)) {
						highlights.add(parseInt(selectedVerse) + 1);
					}
				}
			}
		}
		storeHighlights();
	}

	private void copyPassage() {
		String reference;
		StringBuilder copiedText = new StringBuilder();
		String lineBreak = "\n";
		boolean containsX = selectedVerses.contains("x");
		if ((selectedVerses.size() == 1 && !containsX) || (selectedVerses.size() == 2 && containsX)) {
			reference = booksOfBible[currentBook - 1] + " " + currentChapter + ":" + selectedReference();
			for (String selectedVerse : selectedVerses) {
				if (!selectedVerse.equals("x")) {
					copiedText = new StringBuilder(booksOfBible[currentBook - 1] + " " + currentChapter + ":" + versesInCurrentChapter.get(parseInt(selectedVerse)));
					String periodSpace = "\\. ";
					copiedText = new StringBuilder(copiedText.toString().replaceFirst(periodSpace, lineBreak));
				}
			}
		} else {
			if ((selectedVerses.contains("x") && selectedVerses.size() == 1) || selectedVerses.isEmpty()) {
				reference = booksOfBible[currentBook - 1] + " " + currentChapter;
				copiedText = new StringBuilder(reference);
				for (String verse : versesInCurrentChapter) {
					if (!verse.equals("x")) {
						copiedText.append(lineBreak).append(verse);
					}
				}
			} else {
				reference = booksOfBible[currentBook - 1] + " " + currentChapter + ":" + selectedReference();
				copiedText = new StringBuilder(reference);
				Collections.sort(selectedVerses, new NumericalStringComparator());
				for (String selectedVerse : selectedVerses) {
					if (!selectedVerse.equals("x")) {
						copiedText.append(lineBreak).append(versesInCurrentChapter.get(parseInt(selectedVerse)));
					}
				}
			}
		}
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("", copiedText.toString());
		if (clipboard != null) {
			clipboard.setPrimaryClip(clip);
		}
		toast(reference);
	}

	//MARK: Settings - Functions

	private void setFullscreen() {
		Intent intent = new Intent(this, ChapterActivity.class);
		intent.putExtra("CURRENT_BOOK", currentBook);
		intent.putExtra("CURRENT_CHAPTER", currentChapter);
		intent.putExtra("CURRENT_FONT_FAMILY_INT", currentFontFamilyInt);
		intent.putExtra("CURRENT_FONT_SIZE", currentFontSize);
		intent.putExtra("NIGHT", night);
		String[] versesInCurrentChapterArray = versesInCurrentChapter.toArray(new String[0]);
		intent.putExtra("VERSES_IN_CURRENT_CHAPTER", versesInCurrentChapterArray);
		startActivity(intent);
	}

	private void setFontFamily(String fontFamily) {
		vibe.vibrate(lightFeedback);
		currentFontFamily = fontFamily;
		setStyle();
		database.child("users").child(uid).child("currentFontFamily").setValue(currentFontFamily);
	}

	private void fontFamily() {
		vibe.vibrate(lightFeedback);
		builder.setTitle("Font Family");
		builder.setItems(new CharSequence[]{"Iceland", "Lora", "Oswald", "Playfair", "Poppins", "Raleway", "Roboto", "Satisfy", "Source Code", "Ubuntu", "Cancel"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								setFontFamily("Iceland");
								break;
							case 1:
								setFontFamily("Lora-Regular");
								break;
							case 2:
								setFontFamily("Oswald-Regular");
								break;
							case 3:
								setFontFamily("PlayfairDisplay-Regular");
								break;
							case 4:
								setFontFamily("Poppins-Regular");
								break;
							case 5:
								setFontFamily("Raleway-Regular");
								break;
							case 6:
								setFontFamily("Roboto-Regular");
								break;
							case 7:
								setFontFamily("Satisfy-Regular");
								break;
							case 8:
								setFontFamily("SourceCodePro-Regular");
								break;
							case 9:
								setFontFamily("Ubuntu-Regular");
								break;
							case 10:
								vibe.vibrate(lightFeedback);
								break;
						}
					}
				});
		builder.create().show();
	}

	private void setFontSize(Long fontSize) {
		vibe.vibrate(lightFeedback);
		currentFontSize = fontSize;
		setStyle();
		database.child("users").child(uid).child("currentFontSize").setValue(currentFontSize);
	}

	private void fontSize() {
		vibe.vibrate(lightFeedback);
		builder.setTitle("Font Size");
		builder.setItems(new CharSequence[]{"18", "20", "22", "24", "26", "28", "30", "32", "Cancel"},
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								setFontSize(18L);
								break;
							case 1:
								setFontSize(20L);
								break;
							case 2:
								setFontSize(22L);
								break;
							case 3:
								setFontSize(24L);
								break;
							case 4:
								setFontSize(26L);
								break;
							case 5:
								setFontSize(28L);
								break;
							case 6:
								setFontSize(30L);
								break;
							case 7:
								setFontSize(32L);
								break;
							case 8:
								vibe.vibrate(lightFeedback);
								break;
						}
					}
				});
		builder.create().show();
	}

	//MARK: Summon Content

	private void summonContent() {
		int denominator = 0;
		switch (contentMode) {
			case "Books":
				denominator = 66;
				break;
			case "History":
				denominator = historyTitles.size();
				break;
			case "Highlights":
				denominator = highlightsLocationsSize;
				break;
			case "Cross References":
				denominator = referenceTitlesSize;
				break;
		}
		int numerator = (int) (xw * denominator);
		if (numerator < 1) {
			numerator = 1;
		}
		String bottomLabelTextViewText = contentMode + " " + numerator + "/" + denominator;
		bottomLabel.setText(bottomLabelTextViewText);
		scrollTo(numerator - 1);
	}

	private void summonBookList() {
		contentMode = "Books";
		left.setVisibility(View.INVISIBLE);
		right.setVisibility(View.INVISIBLE);
		topLabel.setText(contentMode);
		content.removeAllViews();
		for (int i = 0; i < booksOfBible.length; i++) {
			final TableRow row = new TableRow(this);
			final TextView tv = new TextView(this);
			tv.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
			tv.setTextSize(currentFontSize);
			if (night) {
				tv.setTextColor(getResources().getColor(R.color.colorWhite));
			} else {
				tv.setTextColor(getResources().getColor(R.color.colorBlack));
			}
			row.setClickable(true);
			final int tag = i;
			row.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					vibe.vibrate(mediumFeedback);
					currentBook = tag + 1;
					currentChapter = 1;
					currentVerse = 1;
					if (!online) {
						locate(false);
					}
					storeLocation();
				}
			});
			TableRow.LayoutParams lp = new TableRow.LayoutParams(bounce.getWidth() - 64, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(32, 32, 32, 32);
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(lp);
			tv.setText(booksOfBible[i]);
			row.setId(i);
			row.addView(tv);
			menuEvents(row);
			content.addView(row, i);
			scrollTo(0);
			xw = 0;
			summonContent();
		}
	}

	private void summonList(ArrayList<Scripture> scriptures) {
		ArrayList<String> books = new ArrayList<>();
		ArrayList<String> chapters = new ArrayList<>();
		ArrayList<String> verses = new ArrayList<>();
		for (int i = 0; i < scriptures.size(); i++) {
			books.add(scriptures.get(i).getBook().toString());
			chapters.add(scriptures.get(i).getChapter().toString());
			verses.add(scriptures.get(i).getVerse().toString());
		}
		summonList(books, chapters, verses);
	}

	private void summonList(final ArrayList<String> books, final ArrayList<String> chapters, final ArrayList<String> verses) {
		left.setVisibility(View.INVISIBLE);
		right.setVisibility(View.INVISIBLE);
		topLabel.setText(contentMode);
		content.removeAllViews();
		for (int i = 0; i < books.size(); i++) {
			final TableRow row = new TableRow(this);
			final TextView tv = new TextView(this);
			tv.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
			tv.setTextSize(currentFontSize);
			if (night) {
				tv.setTextColor(getResources().getColor(R.color.colorWhite));
			} else {
				tv.setTextColor(getResources().getColor(R.color.colorBlack));
			}
			row.setClickable(true);
			final int tag = i;
			row.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					vibe.vibrate(mediumFeedback);
					currentBook = Integer.parseInt(books.get(tag));
					currentChapter = Integer.parseInt(chapters.get(tag));
					currentVerse = Integer.parseInt(verses.get(tag));
					if (!online) {
						locate(false);
					}
					storeLocation();
				}
			});
			TableRow.LayoutParams lp = new TableRow.LayoutParams(bounce.getWidth() - 64, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(32, 32, 32, 32);
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(lp);
			String tvText = booksOfBible[Integer.parseInt(books.get(i)) - 1] + " " + chapters.get(i) + ":" + verses.get(i);
			tv.setText(tvText);
			row.setId(i);
			row.addView(tv);
			menuEvents(row);
			content.addView(row, i);
		}
		scrollTo(currentVerse - 1);
		xw = 0;
		summonContent();
	}

	//MARK: Location

	private void locate(final boolean withRandomVerse) {
		if (formerBook != currentBook || formerChapter != currentChapter || formerVerse != currentVerse || formerHighlights != highlights || formerSelectedVerses != selectedVerses || !contentMode.equals("Verses")) {
			highlights.clear();
			DatabaseReference ref = database.child("users").child(user.getUid()).child("highlights/" + currentBook + "/" + currentChapter);
			if (online) {
				ref.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						try {
							String arrayAsString = dataSnapshot.getValue(String.class);
							String[] items = new String[0];
							if (arrayAsString != null) {
								items = arrayAsString.split(",");
							}
							for (String stringValue : items) {
								highlights.add(parseInt(stringValue));
							}
							Set<Integer> set = new HashSet<>(highlights);
							highlights.clear();
							highlights.addAll(set);
							formerHighlights = highlights;
							goTo(withRandomVerse);
						} catch (Exception e) {
							goTo(withRandomVerse);
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						goTo(withRandomVerse);
					}
				});
			} else {
				goTo(withRandomVerse);
			}
		}
	}

	private void goTo(boolean withRandomVerse) {
		contentMode = "Verses";
		left.setVisibility(View.VISIBLE);
		right.setVisibility(View.VISIBLE);
		String topLabelTextViewText = booksOfBible[currentBook - 1] + " " + currentChapter + ":" + currentVerse;
		topLabel.setText(topLabelTextViewText);
		versesInCurrentChapter.clear();
		if (shouldClearSelection) {
			selectedVerses.clear();
			selectedVerses.add("x");
			database.child("users").child(uid).child("selectedVerses").setValue(selectedVerses);
			if (shouldVibe) {
				vibe.vibrate(mediumFeedback);
				shouldVibe = true;
			}
			shouldClearSelection = false;
		}
		XmlPullParserFactory parserFactory;
		try {
			parserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			InputStream is = getAssets().open("web.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			Scripture scripture = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String eltName;
				if (eventType == XmlPullParser.START_TAG) {
					eltName = parser.getName();
					if ("book".equals(eltName)) {
						scripture = new Scripture();
					} else if (scripture != null) {
						if ("h".equals(eltName)) {
							String bookName = parser.nextText();
							for (int i = 0; i < booksOfBible.length; i++) {
								if (booksOfBible[i].equals(bookName)) {
									scripture.book = i;
								}
							}
						}
						if ("c".equals(eltName)) {
							scripture.chapter = Integer.parseInt(parser.nextText().split(":")[0]);
						} else if ("v".equals(eltName)) {
							String verseText = parser.nextText();
							if (scripture.book.equals(currentBook - 1)) {
								if (scripture.chapter.equals(currentChapter)) {
									versesInCurrentChapter.add(verseText);
								}
							}
						}
					}
				}
				eventType = parser.next();
			}
			content.removeAllViews();
			setScreenSize();
			for (int i = 0; i < versesInCurrentChapter.size(); i++) {
				final TableRow row = new TableRow(this);
				final TextView tv = new TextView(this);
				tv.setText(versesInCurrentChapter.get(i));
				tv.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
				tv.setTextSize(currentFontSize);
				if (night) {
					tv.setTextColor(getResources().getColor(R.color.colorWhite));
				} else {
					tv.setTextColor(getResources().getColor(R.color.colorBlack));
				}
				if (i == currentVerse - 1) {
					if (night) {
						row.setBackground(getResources().getDrawable(R.drawable.verse_night));
					} else {
						row.setBackground(getResources().getDrawable(R.drawable.verse_day));
					}
				}
				row.setClickable(true);
				final String tag = i + "";
				row.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						vibe.vibrate(mediumFeedback);
						formerSelectedVerses = selectedVerses;
						if (selectedVerses.contains(tag)) {
							selectedVerses.remove(tag);
							selectedVerses.add("x");
						} else {
							selectedVerses.add(tag);
							selectedVerses.remove("x");
						}
						if (selectedVerses != null) {
							HashSet<String> hashSet = new HashSet<>(selectedVerses);
							selectedVerses.clear();
							selectedVerses.addAll(hashSet);
						} else {
							selectedVerses = new ArrayList<>();
						}
						database.child("users").child(uid).child("selectedVerses").setValue(selectedVerses);
						vibe.vibrate(mediumFeedback);
					}
				});
				if (highlights != null) {
					if (highlights.contains(i + 1)) {
						if (night) {
							row.setBackground(getResources().getDrawable(R.drawable.highlighted_verse_night));
							if (currentVerse - 1 == i) {
								row.setBackground(getResources().getDrawable(R.drawable.current_highlighted_verse_night));
							}
						} else {
							row.setBackground(getResources().getDrawable(R.drawable.highlighted_verse_day));
							if (currentVerse - 1 == i) {
								row.setBackground(getResources().getDrawable(R.drawable.current_highlighted_verse_day));
							}
						}
					}
				}
				if (selectedVerses != null) {
					if (selectedVerses.contains(i + "") && currentVerse - 1 == i) {
						if (night) {
							row.setBackground(getResources().getDrawable(R.drawable.current_selected_verse_night));
						} else {
							row.setBackground(getResources().getDrawable(R.drawable.current_selected_verse_day));
						}
						if (highlights != null) {
							if (highlights.contains(i + 1)) {
								if (night) {
									row.setBackground(getResources().getDrawable(R.drawable.current_selected_highlighted_verse_night));
								} else {
									row.setBackground(getResources().getDrawable(R.drawable.current_selected_highlighted_verse_day));
								}
							}
						}
					} else if (selectedVerses.contains(i + "")) {
						if (night) {
							row.setBackground(getResources().getDrawable(R.drawable.selected_verse_night));
						} else {
							row.setBackground(getResources().getDrawable(R.drawable.selected_verse_day));
						}
						if (highlights != null) {
							if (highlights.contains(i + 1)) {
								if (night) {
									row.setBackground(getResources().getDrawable(R.drawable.selected_highlighted_verse_night));
								} else {
									row.setBackground(getResources().getDrawable(R.drawable.selected_highlighted_verse_day));
								}
							}
						}
					}
				}
				final Button eraseButton = new Button(this);
				eraseButton.setText("×");
				eraseButton.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
				eraseButton.setTextSize(currentFontSize);
				eraseButton.setTextColor(getResources().getColor(R.color.colorRed));
				eraseButton.setBackgroundColor(getResources().getColor(R.color.colorClear));
				TableRow.LayoutParams elp = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT);
				elp.gravity = Gravity.CENTER;
				eraseButton.setLayoutParams(elp);
				eraseButton.setId(i);
				TableRow.LayoutParams htlp = new TableRow.LayoutParams((bounce.getWidth() - 164), ViewGroup.LayoutParams.WRAP_CONTENT);
				htlp.setMargins(32, 32, 32, 32);
				tv.setLayoutParams(htlp);
				row.setId(i);
				row.addView(tv);
				if (highlights != null && highlights.contains(i + 1)) {
					eraseButton.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("ResourceType")
						@Override
						public void onClick(View v) {
							highlights.remove(Integer.valueOf(eraseButton.getId() + 1));
							storeHighlights();
						}
					});
					row.addView(eraseButton);
				}
				menuEvents(row);
				content.addView(row, i);
			}
			if (withRandomVerse) {
				Random randomNumber = new Random();
				currentVerse = randomNumber.nextInt(versesInCurrentChapter.size()) + 1;
				storeLocation();
			}
			scrollTo(currentVerse - 1);
		} catch (Throwable ignore) {
			scrollTo(currentVerse - 1);
		}
		int numerator = 0;
		int denominator = 0;
		switch (bottomNavigationMode) {
			case "Book":
				numerator = currentBook;
				denominator = 66;
				break;
			case "Chapter":
				numerator = currentChapter;
				denominator = numberOfChaptersInBooksOfBible[currentBook - 1];
				break;
			case "Verse":
				numerator = currentVerse;
				denominator = versesInCurrentChapter.size();
				break;
		}
		String bottomLabelTextViewText = bottomNavigationMode + " " + numerator + "/" + denominator;
		bottomLabel.setText(bottomLabelTextViewText);
		topLabelTextViewText = booksOfBible[currentBook - 1] + " " + currentChapter + ":" + currentVerse;
		topLabel.setText(topLabelTextViewText);
		formerBook = currentBook;
		formerChapter = currentChapter;
		formerVerse = currentVerse;
	}

	private void storeLocation() {
		database.child("users").child(uid).child("currentBook").setValue(currentBook);
		database.child("users").child(uid).child("currentVerse").setValue(currentVerse);
		database.child("users").child(uid).child("currentChapter").setValue(currentChapter);
		long timestampLong = System.currentTimeMillis() / 1000;
		final String timestamp = Long.toString(timestampLong);
		DatabaseReference ref = database.child("users").child(user.getUid()).child("history");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				scriptureHistory.clear();
				try {
					Object o = dataSnapshot.getValue();
					String s = "";
					if (o != null) {
						s = o.toString();
					}
					s = s.replace("[", "");
					s = s.replace("]", "");
					s = s.replace("{", "");
					s = s.replace("}", "");
					ArrayList<String> timestamps = new ArrayList<>();
					ArrayList<String> historyLocations = new ArrayList<>();
					String[] histories = s.split("\\s*, \\s*");
					for (String string : histories) {
						timestamps.add(Arrays.asList(string.split("\\s*=\\s*")).get(0));
						historyLocations.add(Arrays.asList(string.split("\\s*=\\s*")).get(1));
					}
					final ArrayList<Scripture> scriptures = new ArrayList<>();
					int i = 0;
					for (String referent : historyLocations) {
						List<String> items = Arrays.asList(referent.split("\\."));
						Scripture scripture = new Scripture(parseInt(items.get(0)), parseInt(items.get(1)), parseInt(items.get(2)), parseInt(timestamps.get(i)));
						scriptures.add(scripture);
						i++;
					}
					Collections.sort(scriptures);
					for (Scripture scripture : scriptures) {
						historyTitles.add(booksOfBible[scripture.getBook() - 1] + " " + scripture.getChapter() + ":" + scripture.getVerse());
						scriptureHistory.add(scripture.getBook() + "." + scripture.getChapter() + "." + scripture.getVerse());
					}
					if (!scriptureHistory.get(0).equals(currentBook + "." + currentChapter + "." + currentVerse)) {
						database.child("users").child(uid).child("history/" + timestamp).setValue((currentBook + "." + currentChapter + "." + currentVerse));
					}
					scrollTo(currentVerse - 1);
				} catch (Throwable t) {
					database.child("users").child(uid).child("history/" + timestamp).setValue((currentBook + "." + currentChapter + "." + currentVerse));
				}
				scrollTo(currentVerse - 1);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}

	//MARK: Highlights

	private void storeHighlights() {
		if (highlights.size() > 0) {
			StringBuilder arrayAsString = new StringBuilder();
			for (int highlightedVerse : highlights) {
				arrayAsString.append(highlightedVerse).append(",");
			}
			arrayAsString = new StringBuilder(arrayAsString.substring(0, arrayAsString.length() - 1));
			database.child("users").child(uid).child("highlights/" + currentBook + "/" + currentChapter).setValue(arrayAsString.toString());
			database.child("users").child(uid).child("highlights/" + currentBook + "/" + "\"\"").setValue("");
			database.child("users").child(uid).child("highlights/" + 66 + "/" + "\"\"").setValue("");
		} else {
			database.child("users").child(uid).child("highlights/" + currentBook + "/" + currentChapter).setValue("");
			database.child("users").child(uid).child("highlights/" + currentBook + "/" + "\"\"").setValue("");
			database.child("users").child(uid).child("highlights/" + 66 + "/" + "\"\"").setValue("");
		}
	}

	//MARK: Shake

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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		isShaking = true;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - lastUpdate) > 100) {
				long diffTime = (currentTime - lastUpdate);
				lastUpdate = currentTime;
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
				if (speed > 800 && isShaking && shouldShake) {
					goToRandomVerse();
					vibe.vibrate(heavyFeedback);
					shouldShake = false;
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							shouldShake = true;
						}
					}, 250);
				}
				lastX = x;
				lastY = y;
				lastZ = z;
			}
		}
		isShaking = false;
	}

	//MARK: Setters

	private void setScreenSize() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
	}

	private void setStyle() {
		setStatusBarColor();
		if (night) {
			nightMode.setImageDrawable(getResources().getDrawable(R.drawable.moon));
			layout.setBackgroundColor(getResources().getColor(R.color.colorBlack));
			content.setBackgroundColor(getResources().getColor(R.color.colorBlack));
			topLabel.setTextColor(getResources().getColor(R.color.colorWhite));
			bottomLabel.setTextColor(getResources().getColor(R.color.colorWhite));
		} else {
			nightMode.setImageDrawable(getResources().getDrawable(R.drawable.sun));
			layout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
			content.setBackgroundColor(getResources().getColor(R.color.colorWhite));
			topLabel.setTextColor(getResources().getColor(R.color.colorBlack));
			bottomLabel.setTextColor(getResources().getColor(R.color.colorBlack));
		}

		bounce.setMAX_Y_OVERSCROLL_DISTANCE((int) (currentFontSize * 4));
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
		topLabel.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
		bottomLabel.setTypeface(ResourcesCompat.getFont(this, currentFontFamilyInt));
		long fontSize = currentFontSize;
		if (fontSize > 24L) {
			fontSize = 24L;
		}
		topLabel.setTextSize(fontSize);
		bottomLabel.setTextSize(fontSize);
		topLabel.setSingleLine(true);
		if (!online) {
			locate(false);
		}
	}

	private void setStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (online) {
					if (night) {
						this.getWindow().setStatusBarColor(getColor(R.color.colorBlack));
					} else {
						this.getWindow().setStatusBarColor(getColor(R.color.colorWhite));
					}
				} else {
					this.getWindow().setStatusBarColor(getColor(R.color.colorRed));
				}
			}
		}
	}

	private void setMenuIcons(int width, int height) {
		int thirdWidth = (width / 3) / 3;
		int thirdHeight = (height / 3) / 3;
		menuProfileImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuProfileImage.setY(thirdHeight);
		menuProfileImage.setX(thirdWidth);
		menuSettingsImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuSettingsImage.setY(thirdHeight);
		menuSettingsImage.setX(((float) width / 3) + thirdWidth);
		menuVersionsImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuVersionsImage.setY(thirdHeight);
		menuVersionsImage.setX((((float) width / 3) * 2) + thirdWidth);
		menuHistoryImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuHistoryImage.setY(((float) height / 3) + thirdHeight);
		menuHistoryImage.setX(thirdWidth);
		menuHighlightsImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuHighlightsImage.setY(((float) height / 3) + thirdHeight);
		menuHighlightsImage.setX(((float) width / 3) + thirdWidth);
		menuReferencesImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuReferencesImage.setY(((float) height / 3) + thirdHeight);
		menuReferencesImage.setX((((float) width / 3) * 2) + thirdWidth);
		menuAudioImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuAudioImage.setY((((float) height / 3) * 2) + thirdHeight);
		menuAudioImage.setX(thirdWidth);
		menuHighlightImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuHighlightImage.setY((((float) height / 3) * 2) + thirdHeight);
		menuHighlightImage.setX(((float) width / 3) + thirdWidth);
		menuCopyImage.setLayoutParams(new Constraints.LayoutParams(thirdWidth, thirdHeight));
		menuCopyImage.setY((((float) height / 3) * 2) + thirdHeight);
		menuCopyImage.setX((((float) width / 3) * 2) + thirdWidth);
		menuProfileLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuProfileLabel.setY(0);
		menuProfileLabel.setX(0);
		menuSettingsLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuSettingsLabel.setY(0);
		menuSettingsLabel.setX((float) width / 3);
		menuVersionsLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuVersionsLabel.setY(0);
		menuVersionsLabel.setX(((float) width / 3) * 2);
		menuHistoryLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuHistoryLabel.setY((float) height / 3);
		menuHistoryLabel.setX(0);
		menuHighlightsLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuHighlightsLabel.setY((float) height / 3);
		menuHighlightsLabel.setX((float) width / 3);
		menuReferencesLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuReferencesLabel.setY((float) height / 3);
		menuReferencesLabel.setX(((float) width / 3) * 2);
		menuAudioLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuAudioLabel.setY(((float) height / 3) * 2);
		menuAudioLabel.setX(0);
		menuHighlightLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuHighlightLabel.setY(((float) height / 3) * 2);
		menuHighlightLabel.setX((float) width / 3);
		menuCopyLabel.setLayoutParams(new Constraints.LayoutParams(width / 3, height / 3));
		menuCopyLabel.setY(((float) height / 3) * 2);
		menuCopyLabel.setX(((float) width / 3) * 2);
		menuProfileLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuSettingsLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuVersionsLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuHistoryLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuHighlightsLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuReferencesLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuAudioLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuHighlightLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		menuCopyLabel.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
		int menuFontSizeMin = 10;
		int menuFontSizeMax = 20;
		int fontSize = (int) (currentFontSize * 0.5);
		if (fontSize < menuFontSizeMin) {
			fontSize = menuFontSizeMin;
		}
		if (fontSize > menuFontSizeMax) {
			fontSize = menuFontSizeMax;
		}
		menuProfileLabel.setTextSize(fontSize);
		menuSettingsLabel.setTextSize(fontSize);
		menuVersionsLabel.setTextSize(fontSize);
		menuHistoryLabel.setTextSize(fontSize);
		menuHighlightsLabel.setTextSize(fontSize);
		menuReferencesLabel.setTextSize(fontSize);
		menuAudioLabel.setTextSize(fontSize);
		menuHighlightLabel.setTextSize(fontSize);
		menuCopyLabel.setTextSize(fontSize);
		int color;
		if (night) {
			color = R.color.colorWhite;
		} else {
			color = R.color.colorBlack;
		}
		menuProfileLabel.setTextColor(getResources().getColor(color));
		menuSettingsLabel.setTextColor(getResources().getColor(color));
		menuVersionsLabel.setTextColor(getResources().getColor(color));
		menuHistoryLabel.setTextColor(getResources().getColor(color));
		menuHighlightsLabel.setTextColor(getResources().getColor(color));
		menuReferencesLabel.setTextColor(getResources().getColor(color));
		menuAudioLabel.setTextColor(getResources().getColor(color));
		menuHighlightLabel.setTextColor(getResources().getColor(color));
		menuCopyLabel.setTextColor(getResources().getColor(color));
	}

	//MARK: Helpers

	private String selectedReference() {
		ArrayList<String> workingArray = new ArrayList<>();
		for (int i = 0; i < versesInCurrentChapter.size(); i++) {
			boolean containsSelected = false;
			for (String verse : selectedVerses) {
				if (!verse.contains("x")) {
					if (versesInCurrentChapter.get(i).startsWith((parseInt(verse) + 1) + ".")) {
						containsSelected = true;
					}
				}
			}
			if (containsSelected) {
				String verseNumberString = (i + 1) + "";
				workingArray.add(verseNumberString);
			} else {
				workingArray.add(", ");
			}
		}
		ArrayList<ArrayList<String>> verseRange = new ArrayList<>();
		ArrayList<String> range = new ArrayList<>();
		for (int i = 0; i < workingArray.size(); i++) {
			if (!workingArray.get(i).equals(", ")) {
				range.add(workingArray.get(i));
			} else {
				if (range.size() != 0) {
					ArrayList<String> range2 = new ArrayList<>(range);
					verseRange.add(range2);
					range.clear();
				}
			}
		}
		if (range.size() != 0) {
			ArrayList<String> range2 = new ArrayList<>(range);
			verseRange.add(range2);
			range.clear();
		}
		StringBuilder verseRangesString = new StringBuilder();
		for (int i = 0; i < verseRange.size(); i++) {
			if (verseRange.get(i).size() > 1) {
				verseRangesString.append(verseRange.get(i).get(0)).append("-").append(verseRange.get(i).get(verseRange.get(i).size() - 1));
			} else if (verseRange.get(i).size() == 1) {
				verseRangesString.append(verseRange.get(i).get(0));
			}
			verseRangesString.append(", ");
		}
		verseRangesString = new StringBuilder(verseRangesString.substring(0, verseRangesString.length() - 2));
		if (selectedVerses.get(0).equals("x")) {
			return "x";
		} else {
			return verseRangesString.toString();
		}
	}

	private void toast(String string) {
		Toast toast = Toast.makeText(BibleActivity.this, string, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private static class NumericalStringComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			int i1 = parseInt(s1.split(" ")[0]);
			int i2 = parseInt(s2.split(" ")[0]);
			int cmp = 0;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				cmp = Integer.compare(i1, i2);
			}
			if (cmp != 0) {
				return cmp;
			}
			return s1.compareTo(s2);
		}
	}

	//MARK: Scroll

	private void scrollTo(int i) {
		final View child = content.getChildAt(i);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				try {
					if (child.isShown()) {
						bounce.scrollTo(0, child.getTop());
					}
				} catch (Throwable ignored) {
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
	}
}