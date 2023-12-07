package com.example.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CollectionsEditorFragment extends DialogFragment {

    /*
    This fragment is a dialog fragment, popping up when the user clicks
    a photo from their collections list. Presenting options to rename the
    photo or remove it from their collection.
     */

    private Integer editChoice;
    private TextView imageTitleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private String description;
    private String date;
    private EditText editTitle;
    private File internalFilename;

    public CollectionsEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        CollectionsFragment.getInstance().updateList();
        // Reset editchoice if fragment is dismissed
        editChoice = 0;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // LayoutInflater to inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_collections_editor, null);

        // Initialize edit choice integer;
        editChoice = 0;

        // Find and set the imageView
        ImageView editImage = view.findViewById(R.id.imageEdit);
        // Find and set the imageTitleTextView
        imageTitleTextView = view.findViewById(R.id.passedImageTitle);
        // Find and set inputText
        editTitle = view.findViewById(R.id.editTitle);
        // Find and set the buttons
        Button renameButton = view.findViewById(R.id.renameButton);
        Button removeButton = view.findViewById(R.id.removeButton);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            // Setting image title based on arguments passed
            String argsImageTitle = args.getString("imageTitle", getString((R.string.CFEdef1)));
            imageTitleTextView.setText(argsImageTitle);
            editTitle.setText(argsImageTitle);
            // Setting image based on image Title
            String filename = argsImageTitle.replace(" ", "") + ".jpg";

            // Pull date and description from shared preferences
            String dateKey = filename + "_date";
            String descriptionKey = filename + "_description";

            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            date = preferences.getString(dateKey, getString(R.string.CFEdef2));
            description = preferences.getString(descriptionKey, getString(R.string.CFEdef3));

            // Set details to corresponding textViews
            dateTextView = view.findViewById(R.id.passedImageDate);
            descriptionTextView = view.findViewById(R.id.passedImageDescription);
            dateTextView.setText(date);
            descriptionTextView.setText(description);

            descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
            descriptionTextView.setMaxLines(2);

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = getActivity().openFileInput(filename);
                if (fileInputStream != null) {
                    editImage.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                } else {
                    editImage.setImageResource(R.drawable.ic_launcher_background);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Click listeners for each button * description textView
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding and showing different edit tools
                imageTitleTextView.setVisibility(View.GONE);
                renameButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.GONE);
                dateTextView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);

                editTitle.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
                editChoice = 1;
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding and showing different edit tools
                renameButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.GONE);
                confirmButton.setVisibility(View.VISIBLE);
                editChoice = 2;
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks for what edit choice was made
                if (editChoice == 1) {
                    // Rename image
                    renameImage();
                    // Changing image title display
                    imageTitleTextView.setText(editTitle.getText());
                    // Hiding edit buttons
                    editTitle.setVisibility(View.INVISIBLE);
                    confirmButton.setVisibility(View.GONE);
                    // Showing main edit buttons
                    imageTitleTextView.setVisibility(View.VISIBLE);
                    renameButton.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.VISIBLE);
                    dateTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setVisibility(View.VISIBLE);

                    // Creating an InputMethodManager instance
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hiding the keyboard
                    imm.hideSoftInputFromWindow(editTitle.getWindowToken(), 0);
                } if (editChoice == 2 ) {
                    // Removing image and details from SharedPreferences
                    String filename = imageTitleTextView.getText().toString();
                    filename = filename.replace(" ", "");
                    filename = filename + ".jpg";

                    // Create a File object with the appropriate directory path
                    File internalStorageDir = getActivity().getFilesDir();
                    internalFilename = new File(internalStorageDir, filename);

                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(filename);
                    editor.remove(filename + "_title");
                    editor.remove(filename + "_description");
                    editor.remove(filename + "_date");
                    editor.apply();

                    // Delete the file
                    if (internalFilename.exists()) {

                        boolean isDeleted = internalFilename.delete();

                        if (isDeleted) {
                            Toast.makeText(getActivity(), getString(R.string.CFEdel1), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.CFEdel2), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.CFEdel3), Toast.LENGTH_SHORT).show();
                    }

                    dismiss();
                }
            }
        });

        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullDescription = description;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(fullDescription)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, close the dialog
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Use the AlertDialog.Builder to build your dialog
        return new AlertDialog.Builder(requireContext())
                .setView(view) // Set your custom layout here
                .create();
    }

    private void renameImage() {
        // Removing image and details from SharedPreferences
        String oldTitle = imageTitleTextView.getText().toString();
        String oldFilename = oldTitle.replace(" ", "") + ".jpg";

        String newTitle = String.valueOf(editTitle.getText());
        String newFilename = newTitle.replace(" ", "") + ".jpg";

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove old entries
        editor.remove(oldFilename);
        editor.remove(oldFilename + "_title");
        editor.remove(oldFilename + "_date");
        editor.remove(oldFilename + "_description");

        // Add new entries
        editor.putString(newFilename, newFilename);
        editor.putString(newFilename + "_title", newTitle);
        editor.putString(newFilename + "_date", date);
        editor.putString(newFilename + "_description", description);

        // Apply changes
        editor.apply();

        // Rename the file
        File originalFile = new File(getActivity().getFilesDir(), oldFilename);
        File renamedFile = new File(getActivity().getFilesDir(), newFilename);

        boolean isRenamed = originalFile.renameTo(renamedFile);

        if (isRenamed) {
            Toast.makeText(getActivity(), getString(R.string.CFEren1), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.CFEren2), Toast.LENGTH_SHORT).show();
        }
    }
}