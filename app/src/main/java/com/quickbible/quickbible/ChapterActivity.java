package com.quickbible.quickbible;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
public class ChapterActivity extends AppCompatActivity {
    Activity activity;
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
        setContentView(R.layout.activity_chapter);
        getSupportActionBar().hide();
        activity = this;
        Context context = this;
        String[] booksOfBible = new String[]{"Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", "1 Kings", "2 Kings", "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", "3 John", "Jude", "Revelation"};
        int currentBook = getIntent().getIntExtra("CURRENT_BOOK",0);
        int currentChapter = getIntent().getIntExtra("CURRENT_CHAPTER",0);
        int currentFontFamilyInt = getIntent().getIntExtra("CURRENT_FONT_FAMILY_INT",0);
        long currentFontSize = getIntent().getLongExtra("CURRENT_FONT_SIZE",0L);
        boolean night = getIntent().getBooleanExtra("NIGHT",false);
        String[] versesInCurrentChapter = getIntent().getStringArrayExtra("VERSES_IN_CURRENT_CHAPTER");
        View fullscreenBackgroundView = findViewById(R.id.fullscreenBackgroundView);
        ImageButton fullscreenBackButton = findViewById(R.id.fullscreenBackButton);
        TextView fullscreenLabelTextView = findViewById(R.id.fullscreenLabelTextView);
        TextView fullscreenChapterTextView = findViewById(R.id.fullscreenChapterTextView);
        fullscreenLabelTextView.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
        fullscreenChapterTextView.setTypeface(ResourcesCompat.getFont(context, currentFontFamilyInt));
        fullscreenLabelTextView.setTextSize(currentFontSize);
        fullscreenChapterTextView.setTextSize(currentFontSize);
        if (night) {
            fullscreenLabelTextView.setTextColor(getResources().getColor(R.color.colorWhite));
            fullscreenChapterTextView.setTextColor(getResources().getColor(R.color.colorWhite));
            fullscreenBackgroundView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        } else {
            fullscreenLabelTextView.setTextColor(getResources().getColor(R.color.colorBlack));
            fullscreenChapterTextView.setTextColor(getResources().getColor(R.color.colorBlack));
            fullscreenBackgroundView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
        String titleText = booksOfBible[currentBook - 1] + " " + currentChapter;
        StringBuilder chapterText = new StringBuilder();
        String lineBreak = "\n";
        if (versesInCurrentChapter != null) {
            for (String verse : versesInCurrentChapter) {
                if (!verse.equals("x")) {
                    chapterText.append(lineBreak).append(verse);
                }
            }
        }
        chapterText = new StringBuilder(chapterText.substring(1) + lineBreak);
        fullscreenLabelTextView.setText(titleText);
        fullscreenChapterTextView.setText(chapterText.toString());
        fullscreenBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}