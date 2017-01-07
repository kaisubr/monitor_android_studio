package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import iosf.github.kaisubr.sciencefair.CustomAdapters.OnboardingRecyclerViewAdapter1;
import iosf.github.kaisubr.sciencefair.CustomAdapters.OnboardingRecyclerViewAdapter2;
import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.FallbackActivities.PermDisabled;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

public class OnboardingActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<Integer> mPageHistory = new ArrayList<>();
    private static final String TAG = "Onboarding";
    private static final String TAG_PERM = "Permissions";

    FragmentManager fragmentManager;

    final int PERMISSIONS_REQUEST_READ_CALL_LOG = 9876;
    final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1435;
    final int PERMISSIONS_REQUEST_CALL_PHONE = 5342;
    final int PERMISSIONS_REQUEST_READ_CONTACTS = 8675;
    final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1111;
    boolean PERMISSIONS_RECORD_AUDIO_IS_ENABLED;
    boolean PERMISSIONS_READ_CALL_LOG_IS_ENABLED;
    boolean PERMISSIONS_READ_PHONE_STATE_IS_ENABLED;
    boolean PERMISSIONS_CALL_PHONE_IS_ENABLED;
    boolean PERMISSIONS_READ_CONTACTS_IS_ENABLED;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        final Intent restart = new Intent(OnboardingActivity.this, MainActivity.class);

        final ImageButton leftButton = (ImageButton) findViewById(R.id.buttonBack);
        final ImageButton rightButton = (ImageButton) findViewById(R.id.buttonForward);
        final ImageView navCirc1 = (ImageView) findViewById(R.id.navigationCircle1);
        final ImageView navCirc2 = (ImageView) findViewById(R.id.navigationCircle2);
        final ImageView navCirc3 = (ImageView) findViewById(R.id.navigationCircle3);

        /**
         * Quickly save the user preferences just in case the user decides
         * not to change anything or if he/she exits voluntarily
         */
        UserPreferences UPDefault = new UserPreferences();
        UPDefault.setContext(OnboardingActivity.this);
        UPDefault.setUserNumCUSTOM("");
        UPDefault.setDefaultNumCUSTOM("true true true true true true true true ");


        if (navCirc1 != null && navCirc2 != null && navCirc3 !=null) {
            navCirc1.animate()
                    .alpha(1f)
                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);
            navCirc2.setAlpha(0.4f);
            navCirc3.setAlpha(0.4f);
        }

        if (leftButton != null && rightButton != null) {
            leftButton.setImageResource(R.drawable.ic_close_black_24dp);
            rightButton.setImageResource(R.drawable.ic_arrow_forward_black_24dp);

            leftButton.setOnClickListener(new ImageButton.OnClickListener(){
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Exit?")
                            .setMessage("If you exit the customization process, you will use the default phone number list to monitor.")
                            .setPositiveButton("Return to customization", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "return");
                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "end");
                                    UserPreferences UPEXIT = new UserPreferences();
                                    UPEXIT.setContext(OnboardingActivity.this);
                                    UPEXIT.setUserNumCUSTOM("");
                                    UPEXIT.setDefaultNumCUSTOM("true true true true true true true true ");
                                    finish();
                                }
                            }).show();
                }
            });

            rightButton.setOnClickListener(new ImageButton.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(1, true);
                }
            });


            boolean granted = checkPermissionsGranted();

            if (granted) {

            } else {
                new AlertDialog.Builder(OnboardingActivity.this)
                        .setTitle("Enable permissions")
                        .setMessage("Monitor needs access to your call log, your phone state, your contacts, and requires the ability to make and manage calls. These four permissions are critical for running the app.")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setPositiveButton("Enable now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                enablePermissions();
                            }
                        }).show();
            }
        }



        fragmentManager = getSupportFragmentManager();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageHistory.add(position); //Add to the end of array
                updateIndicators(position);
                updateButtons(position);
            }

            public void updateIndicators(int position) {
                switch (position) {
                    case 0:
                        //Page 1
                        if (navCirc1 != null && navCirc2 != null && navCirc3 !=null) {
                            navCirc1.animate()
                                    .alpha(1f)
                                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                                    .setListener(null);
                            navCirc2.setAlpha(0.4f);
                            navCirc3.setAlpha(0.4f);
                        }
                        break;
                    case 1:
                        //Page 2
                        if (navCirc1 != null && navCirc2 != null && navCirc3 !=null) {
                            navCirc2.animate()
                                    .alpha(1f)
                                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                                    .setListener(null);
                            navCirc1.setAlpha(0.4f);
                            navCirc3.setAlpha(0.4f);
                        }
                        break;
                    case 2:
                        if (navCirc1 != null && navCirc2 != null && navCirc3 !=null) {
                            navCirc3.animate()
                                    .alpha(1f)
                                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                                    .setListener(null);
                            navCirc2.setAlpha(0.4f);
                            navCirc1.setAlpha(0.4f);
                        }
                        break;
                    default:
                        break;
                }
            }

            public void updateButtons(final int position) {
                int lastPage = -1; //-1 means that it is not initialized yet.

                try {
                    lastPage = mPageHistory.get(mPageHistory.size() - 2);
                } catch (ArrayIndexOutOfBoundsException e) {
                    lastPage = 0;
                } finally {
                    Log.d(TAG, "mLastPage: " + lastPage); //Get last page
                }

                switch (position) {
                    case 0:
                        if (leftButton != null && rightButton != null) {
                            leftButton.setImageResource(R.drawable.ic_close_black_24dp);
                            rightButton.setImageResource(R.drawable.ic_arrow_forward_black_24dp);

                            leftButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(view.getContext())
                                            .setTitle("Exit?")
                                            .setMessage("If you exit the customization process, you will use the default phone number list to monitor.")
                                            .setPositiveButton("Return to customization", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.d(TAG, "return");
                                                }
                                            })
                                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.d(TAG, "end");
                                                    UserPreferences UPEXIT = new UserPreferences();
                                                    UPEXIT.setContext(OnboardingActivity.this);
                                                    UPEXIT.setFirstTimeUser("false");
                                                    finish();
                                                }
                                            }).show();
                                }
                            });

                            rightButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    mViewPager.setCurrentItem(position + 1, true);
                                }
                            });
                        }
                        break;
                    case 1:
                        if (leftButton != null && rightButton !=null) {
//                            leftButton.setImageResource(R.drawable.ic_arrow_back_black_24dp);

                            if (lastPage == 0) {
                                AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_vector_close);
                                leftButton.setImageDrawable(avd);
                                avd.start();
                            } else {
                                //DON'T ANIMATE!!! KEEP IT AT A BACK ARROW.
                            }
                            rightButton.setImageResource(R.drawable.ic_arrow_forward_black_24dp);

                            leftButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    mViewPager.setCurrentItem(position - 1, true);
                                }
                            });

                            rightButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    UserPreferences up1 = new UserPreferences();
                                    up1.setContext(OnboardingActivity.this);
                                    Log.d(TAG, up1.getDefaultNumCUSTOM());
                                    mViewPager.setCurrentItem(position + 1, true);
                                }
                            });
                        }
                        break;
                    case 2:
                        if (leftButton != null && rightButton !=null) {
                            leftButton.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                            rightButton.setImageResource(R.drawable.ic_check_black_24dp);

                            leftButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    mViewPager.setCurrentItem(position - 1, true);
                                }
                            });

                            rightButton.setOnClickListener(new ImageButton.OnClickListener(){
                                @Override
                                public void onClick(View view) {

                                    boolean granted = checkPermissionsGranted();

                                    if (granted) {
                                        final UserPreferences up2 = new UserPreferences();
                                        up2.setContext(OnboardingActivity.this);
                                        Log.d("test", up2.getUserNumCUSTOM());
                                        Log.d("test", up2.getDefaultNumCUSTOM());
                                        new AlertDialog.Builder(view.getContext())
                                                .setTitle("You're all set!")
                                                .setMessage("You can change the numbers to monitor in the app settings.")
                                                .setIcon(R.drawable.ic_check_black_24dp)
                                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.d(TAG, "end");
                                                        up2.setFirstTimeUser("false");
                                                        startActivity(restart);
                                                    }
                                                })
                                                .setNegativeButton("Return to customization", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        mViewPager.setCurrentItem(1, true);
                                                    }
                                                }).show();
                                    } else {

                                        startActivity(new Intent(OnboardingActivity.this, PermDisabled.class));
                                    }
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private boolean checkPermissionsGranted() {
        //Check if call phone permission is true
        int permissionsForCALL_PHONE = ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.CALL_PHONE);
        if (permissionsForCALL_PHONE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = true;
        } else {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = false;
        }

        //Check phone state
        int permissionsForPHONE_STATE = ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (permissionsForPHONE_STATE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = true;
        } else {
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = false;
        }

        int permissionsForCALL_LOG = ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_CALL_LOG);
        if (permissionsForCALL_LOG == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = true;
        } else {
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = false;
        }

        int permissionsForREAD_CONTACTS = ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_CONTACTS); //edit02
        if (permissionsForREAD_CONTACTS == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_READ_CONTACTS_IS_ENABLED = true;
        } else {
            PERMISSIONS_READ_CONTACTS_IS_ENABLED = false;
        }

        int permissionsForRECORD_AUDIO = ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.RECORD_AUDIO); //edit03
        if (permissionsForRECORD_AUDIO == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_RECORD_AUDIO_IS_ENABLED = true;
        } else {
            PERMISSIONS_RECORD_AUDIO_IS_ENABLED = false;
        }

        if (PERMISSIONS_CALL_PHONE_IS_ENABLED && PERMISSIONS_READ_CALL_LOG_IS_ENABLED && PERMISSIONS_READ_PHONE_STATE_IS_ENABLED && PERMISSIONS_READ_CONTACTS_IS_ENABLED && PERMISSIONS_RECORD_AUDIO_IS_ENABLED) {
            return true;
        } else {
            return false;
        }
    }

    private void enablePermissions() {
        //TODO: Put all of this in a different method. Gets too messy inside onCreate().
        //Check for permissions.
        if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission isn't granted yet.
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnboardingActivity.this,
                    Manifest.permission.READ_CALL_LOG)) {
                //We need to show an explanation.
                Log.d(TAG_PERM, "Explanation required!");
            } else {
                Log.d(TAG_PERM, "Explanation not required!");
                //We don't need to show an explanation.
                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                Log.d(TAG_PERM, "Showing dialog!");

                ActivityCompat.requestPermissions(
                        OnboardingActivity.this,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        PERMISSIONS_REQUEST_READ_CALL_LOG
                );
                Log.d(TAG_PERM, "Dialog shown!");
                //After user responds, system invokes onRequestPermissionsResult()
            }
        }

        //record audio perm
        if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission isn't granted yet.
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnboardingActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                //We need to show an explanation.
                Log.d(TAG_PERM, "Explanation required!");
            } else {
                Log.d(TAG_PERM, "Explanation not required!");
                //We don't need to show an explanation.
                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                Log.d(TAG_PERM, "Showing dialog!");

                ActivityCompat.requestPermissions(
                        OnboardingActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_RECORD_AUDIO
                );
                Log.d(TAG_PERM, "Dialog shown!");
                //After user responds, system invokes onRequestPermissionsResult()
            }
        }

        //Check if read phone state permission is granted
        if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission isn't granted yet.
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnboardingActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                //We need to show an explanation.
                Log.d(TAG_PERM, "Explanation required READ_PHONE_STATE!");
            } else {
                Log.d(TAG_PERM, "Explanation not required READ_PHONE_STATE!");
                //We don't need to show an explanation.
                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                Log.d(TAG_PERM, "Showing dialog READ_PHONE_STATE!");


                ActivityCompat.requestPermissions(
                        OnboardingActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE
                );
                Log.d(TAG_PERM, "Dialog shown READ_PHONE_STATE!");
                //After user responds, system invokes onRequestPermissionsResult()
            }
        }

        //Check if call phone permission is granted
        if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission isn't granted yet.
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnboardingActivity.this,
                    Manifest.permission.CALL_PHONE)) {
                //We need to show an explanation.
                Log.d(TAG_PERM, "Explanation required CALL_PHONE!");
            } else {
                Log.d(TAG_PERM, "Explanation not required CALL_PHONE!");
                //We don't need to show an explanation.
                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                Log.d(TAG_PERM, "Showing dialog CALL_PHONE!");

                ActivityCompat.requestPermissions(
                        OnboardingActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        PERMISSIONS_REQUEST_CALL_PHONE
                );
                Log.d(TAG_PERM, "Dialog shown CALL_PHONE!");
                //After user responds, system invokes onRequestPermissionsResult()
            }
        }

        //Check contacts permission
        if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_CONTACTS) //edit02
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnboardingActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                //We need to show an explanation.
                Log.d(TAG_PERM, "Explanation required READ_CONTACTS!");
            } else {
                Log.d(TAG_PERM, "Explanation not required READ_CONTACTS!");
                //We don't need to show an explanation.
                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                Log.d(TAG_PERM, "Showing dialog READ_CONTACTS!");

                ActivityCompat.requestPermissions(
                        OnboardingActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS
                );
                Log.d(TAG_PERM, "Dialog shown READ_CONTACTS!");
                //After user responds, system invokes onRequestPermissionsResult()
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class OnboardingFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView recyclerViewFragment1;
        private RecyclerView recyclerViewFragment2;

        public OnboardingFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static OnboardingFragment newInstance(int sectionNumber) {
            OnboardingFragment fragment = new OnboardingFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            int position = getArguments().getInt(ARG_SECTION_NUMBER);

            Log.d(TAG, "POSITION : " + position);

            if (position == 0) {
                //It is difficult to set image resource for buttons here, so I created a listener above.
                rootView = inflater.inflate(R.layout.fragment_onboarding_1, container, false);
//                Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
//
//                UserPreferences up = new UserPreferences();
//                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, up.getStatesArray());
//                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(spinnerAdapter);
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        Log.d(TAG, parent.getItemAtPosition(position).toString());
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
            } else if (position == 1) {
                rootView = inflater.inflate(R.layout.fragment_onboarding_2, container, false);

                List<Integer> imageViews = new ArrayList<>();
                List<String> phNumbers = new ArrayList<>();
                List<String> typeOfNumber = new ArrayList<>();
                List<Boolean> checked = new ArrayList<>();

                imageViews.add(0, R.drawable.ic_help_black_24dp);
                phNumbers.add(0, "18777348472");
                typeOfNumber.add(0, "The White House");
                checked.add(0, true);

                imageViews.add(1, R.drawable.ic_help_black_24dp);
                phNumbers.add(1, "18004321000");
                typeOfNumber.add(1, "Bank of America");
                checked.add(1, true);

                imageViews.add(2, R.drawable.ic_help_black_24dp);
                phNumbers.add(2, "18004323117");
                typeOfNumber.add(2, "Chase Bank");
                checked.add(2, true);

                imageViews.add(3, R.drawable.ic_help_black_24dp);
                phNumbers.add(3, "18009505114");
                typeOfNumber.add(3, "Citibank");
                checked.add(3, true);

                imageViews.add(4, R.drawable.ic_help_black_24dp);
                phNumbers.add(4, "18005284800");
                typeOfNumber.add(4, "American Express");
                checked.add(4, true);

                imageViews.add(5, R.drawable.ic_help_black_24dp);
                phNumbers.add(5, "18776696877");
                typeOfNumber.add(5, "Nationwide Insurance");
                checked.add(5, true);

                imageViews.add(6, R.drawable.ic_help_black_24dp);
                phNumbers.add(6, "18883988924");
                typeOfNumber.add(6, "Liberty Mutual");
                checked.add(6, true);

                imageViews.add(7, R.drawable.ic_help_black_24dp);
                phNumbers.add(7, "18777348472");
                typeOfNumber.add(7, "State Farm Insurance");
                checked.add(7, true);

                recyclerViewFragment1 = (RecyclerView) rootView.findViewById(R.id.onboardingRecyclerView1);
                recyclerViewFragment1.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                final OnboardingRecyclerViewAdapter1 rVAdapter1 = new OnboardingRecyclerViewAdapter1(phNumbers, typeOfNumber, imageViews, checked, rootView.getContext());
                recyclerViewFragment1.setAdapter(rVAdapter1);

            } else if (position == 2){

                rootView = inflater.inflate(R.layout.fragment_onboarding_3, container, false);
                final List<String> phNumbers2 = new ArrayList<>();

                recyclerViewFragment2 = (RecyclerView) rootView.findViewById(R.id.onboardingRecyclerView2);
                recyclerViewFragment2.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                final OnboardingRecyclerViewAdapter2 rVAdapter2 = new OnboardingRecyclerViewAdapter2(phNumbers2, rootView.getContext());
                recyclerViewFragment2.setAdapter(rVAdapter2);


                FloatingActionButton fab;
                fab = (FloatingActionButton) rootView.findViewById(R.id.FABaddNew);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editTextInput = new EditText(getContext());

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        editTextInput.setLayoutParams(layoutParams);
                        editTextInput.setTextColor(getResources().getColor(R.color.colorAccent, null));


                        new AlertDialog.Builder(getContext())
                                .setTitle("Add a phone number")
                                .setMessage("Add a number to monitor below (do not enter any parentheses, dashes, or spaces):")
                                .setPositiveButton("Add",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FormatNumber fn = new FormatNumber();
                                                String phNumberChosen = fn.toPhoneNumber(editTextInput.getText().toString());
                                                phNumbers2.add(phNumberChosen);
                                                //rVAdapter2.notifyDataSetChanged();
                                                rVAdapter2.notifyItemInserted(phNumbers2.size() - 1);
                                                Log.d(TAG, phNumberChosen);
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
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(
//                    getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER))
//            );

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a OnboardingFragment (defined as a static inner class below).
            return OnboardingFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "START";
                case 1:
                    return "EDIT DEFAULTS";
                case 2:
                    return "ADD CUSTOM NUMBERS";
            }
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CALL_LOG: {
                //If request was cancelled, our result array (grantResults array) is empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The permission was granted!
                    Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_READ_CALL_LOG granted.");
                    //Now refresh the activity.
                    //TODO: Ask user to refresh for changes to take effect --> +: Refresh now. -: Refresh Later.
                    finish();
                    startActivity(getIntent());
                } else {
                    //The permission was not granted.
                    Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_READ_CALL_LOG not granted!");
                }

                return;
            }

            //other case statements to check for other permissions
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                //If request was cancelled, our result array (grantResults array) is empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The permission was granted!
                    Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_READ_PHONE_STATE granted.");
                    //Now refresh the activity.
                    //TODO: Ask user to refresh for changes to take effect --> +: Refresh now. -: Refresh Later.
                    finish();
                    startActivity(getIntent());
                } else {
                    //The permission was not granted.
                    Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_READ_PHONE_STATE not granted!");
                }

                return;
            }

            case PERMISSIONS_REQUEST_CALL_PHONE: {
                //If request was cancelled, our result array (grantResults array) is empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The permission was granted!
                    Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_CALL_PHONE granted.");
                    //Now refresh the activity.
                    //TODO: Ask user to refresh for changes to take effect --> +: Refresh now. -: Refresh Later.
                    finish();
                    startActivity(getIntent());
                } else {
                    //The permission was not granted.
                    Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_CALL_PHONE not granted!");
                }

                return;
            }

            case PERMISSIONS_REQUEST_RECORD_AUDIO: {
                //If request was cancelled, our result array (grantResults array) is empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The permission was granted!
                    Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_RECORD_AUDIO granted.");
                    //Now refresh the activity.
                    //TODO: Ask user to refresh for changes to take effect --> +: Refresh now. -: Refresh Later.
                    finish();
                    startActivity(getIntent());
                } else {
                    //The permission was not granted.
                    Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_RECORD_AUDIO not granted!");
                }

                return;
            }

            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The permission was granted!
                    Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_READ_CONTACTS granted.");
                    //Now refresh the activity.
                    //TODO: Ask user to refresh for changes to take effect --> +: Refresh now. -: Refresh Later.
                    finish();
                    startActivity(getIntent());
                } else {
                    //The permission was not granted.
                    Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_READ_CONTACTS not granted!");
                }

                return;
            }
        }
    }
}
