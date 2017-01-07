package iosf.github.kaisubr.sciencefair;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iosf.github.kaisubr.sciencefair.CustomAdapters.EditListCompleteAdapter;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

public class EditListActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "EditListActivity";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_ELA);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEditListNewNumber);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Phone Numbers");

        String finalList = PreferenceManager.getDefaultSharedPreferences(EditListActivity.this).getString("pref_complete_list", "");
        String[] finalArray = finalList.split(",");
        final List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(finalArray));

        //It would be best to create a new adapter, but keep the same layout as the Onboarding Recycler View Adapter 2.
        recyclerView = (RecyclerView) findViewById(R.id.editListRecyclerView); //Keep this
        recyclerView.setLayoutManager(new LinearLayoutManager(EditListActivity.this)); //Keep this
        final EditListCompleteAdapter rvAdapter = new EditListCompleteAdapter(list, EditListActivity.this); //Change this
        recyclerView.setAdapter(rvAdapter); //Keep this

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final EditText editTextInput = new EditText(EditListActivity.this);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );

                    editTextInput.setLayoutParams(layoutParams);
                    editTextInput.setTextColor(getResources().getColor(R.color.colorAccent, null));


                    new AlertDialog.Builder(EditListActivity.this)
                            .setTitle("Add a phone number")
                            .setMessage("Add a number to monitor below (do not enter any parentheses, dashes, or spaces):")
                            .setPositiveButton("Add",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FormatNumber fn = new FormatNumber();
                                            String phNumberChosen = fn.toPhoneNumber(editTextInput.getText().toString());
                                            list.add(phNumberChosen);
                                            //rVAdapter2.notifyDataSetChanged();
                                            rvAdapter.notifyItemInserted(list.size() - 1);
                                            Log.d(TAG, phNumberChosen);
                                            Snackbar.make(view, "Phone number added!", Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                            .setView(editTextInput, 48, 20, 48, 24) //padding
                            .show();
                }
            });
        }

        //TODO: Nudge user to add a phone number if list is empty.
    }

}
