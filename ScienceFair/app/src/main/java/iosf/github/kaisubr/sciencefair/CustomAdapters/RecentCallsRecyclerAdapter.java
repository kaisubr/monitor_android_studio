package iosf.github.kaisubr.sciencefair.CustomAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iosf.github.kaisubr.sciencefair.ContactInformation;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.Dim;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;
import iosf.github.kaisubr.sciencefair.MainActivity;
import iosf.github.kaisubr.sciencefair.R;

import static iosf.github.kaisubr.sciencefair.MainActivity.*;

/**
 * Created at 9:16 AM for Science Fair.
 */
public class RecentCallsRecyclerAdapter extends RecyclerView.Adapter<RecentCallsRecyclerAdapter.ViewHolder>{
    //Same as ListView adapter but with additional functionalities --
    // onCreateViewHolder() & onBindViewHolder()
    private static final String TAG = "RCRA";
//    public static String phNumberPublic;
    int lastItemAnimated;
    private List<String> phoneNumberList;
    private List<Integer> imageIncomingOutgoingList;
    private Context mContext;

    static final UserPreferences up = new UserPreferences();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public CardView callItemWithinRecyclerView;
        public CardView parentCardViewBlockItemWithinRecyclerView;
        public ImageView blockItemWithinRecyclerView;
        public TextView contactNameTextView;
        public CardView parentCardViewRCRA;

        public ViewHolder (final View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on click
                    Log.d(TAG, "Element " + getLayoutPosition() + " clicked.");

                    Log.d(TAG, String.valueOf(v.getClass()));

                    final List<String> detailedData = new ArrayList<String>();
                    int callIconTypeOfNumber = R.drawable.ic_help_black_24dp;
                    Uri calls = Uri.parse("content://call_log/calls");
                    Cursor callsCursor = (v.getContext()).getContentResolver().query(calls, null, null, null, null);

                    int number = callsCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int date = callsCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = callsCursor.getColumnIndex(CallLog.Calls.DURATION);
                    int type = callsCursor.getColumnIndex(CallLog.Calls.TYPE);

                    detailedData.add(0, "Unknown");
                    detailedData.add(1, "Unknown");
                    detailedData.add(2, "Unknown");
                    detailedData.add(3, "Unknown");
                    detailedData.add(4, "Unknown");

                    int location = (callsCursor.getCount() - getLayoutPosition()) - 1;
                    Log.d(TAG, String.valueOf(location));
                    while(callsCursor.moveToPosition(location)) {
                        String phoneNumber = callsCursor.getString(number);
                        String unformattedPhoneNumber = callsCursor.getString(number);
//                        Log.d(TAG, String.valueOf(phoneNumber));
                        String callDate = callsCursor.getString(date);

                        Date readableCallDate = new Date(Long.valueOf(callDate));
                        SimpleDateFormat customDateFormat = new SimpleDateFormat("M/d/yyyy, h:mm a", Locale.ENGLISH);
                        String formattedDate = customDateFormat.format(readableCallDate);

                        String callDuration = callsCursor.getString(duration);

                        String typeOfNumberString;

                        int typeOfNumber = Integer.parseInt(callsCursor.getString(type));

                        int icon;

                        //Format number, if possible
                        FormatNumber formatNumberDetailedCall = new FormatNumber();
                        detailedData.set(
                                0,
                                formatNumberDetailedCall.toPhoneNumber(phoneNumber)
                        );

                        switch (typeOfNumber) {
                            case CallLog.Calls.OUTGOING_TYPE:
                                icon = R.drawable.ic_call_made_black_24dp;
                                typeOfNumberString = "Outgoing";
                                break;
                            case CallLog.Calls.MISSED_TYPE:
                                icon = R.drawable.ic_call_missed_black_24dp;
                                typeOfNumberString = "Missed";
                                break;
                            case CallLog.Calls.INCOMING_TYPE:
                                icon = R.drawable.ic_call_received_black_24dp;
                                typeOfNumberString = "Incoming";
                                break;
                            default:
                                icon = R.drawable.ic_help_black_24dp;
                                typeOfNumberString = "Unknown";
                                break;
                        }

                        //0 phoneNumber (already formatted, if possible, above)
                        //1 callDate
                        //2 callDuration
                        //3 typeOfNumber, as a String
                        //  callIconTypeOfNumber = icon
                        //4 phoneNumber (unformatted)

                        detailedData.set(1, String.valueOf(formattedDate));
                        detailedData.set(2, callDuration);
                        detailedData.set(3, typeOfNumberString);
                        detailedData.set(4, unformattedPhoneNumber);

                        callIconTypeOfNumber = icon;

                        callsCursor.close();
                        break;
                    }

                    //Display alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(v.getContext())
                            .setTitle("Detailed information")
                            .setMessage("" +
                                    "Phone Number: " + detailedData.get(0) + "\n" +
                                    "Date Called: " + detailedData.get(1) + "\n" +
                                    "Call Duration: About " + Math.round((Integer.parseInt(detailedData.get(2)))/60) + " minutes (" + detailedData.get(2) +  " second(s)) \n" +
                                    "Type of number: " + detailedData.get(3) + "\n"
                            )
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //close dialog
                                }
                            })

                            .setIcon(ResourcesCompat.getDrawable(v.getResources(), callIconTypeOfNumber, null))
                            ;
                    if (up.shouldMonitor(FormatNumber.unFormat(textView.getText().toString()), up.getContext())) {
                        b.setNeutralButton("Unblock This Number", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Dialog dialogInterface = (Dialog) dialog;
                                Context context = dialogInterface.getContext();
                                up.setContext(context);
                                up.removeUserNumCUSTOM(detailedData.get(4));


//                                    Toast.makeText(
//                                            context,
//                                            "The number " + detailedData.get(0) + " has been blocked. You can undo this in Settings.",
//                                            Toast.LENGTH_LONG
//                                    ).show();

                                Snackbar.make(
                                        view.getRootView().findViewById(R.id.relativeLayoutMainActivity),
                                        "Please refresh for complete changes to apply.",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("Refresh", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle b = new Bundle();
                                                b.putString("RCRA_Refresh_Key", "just refreshed");
                                                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtras(b));
                                            }
                                        })
                                        .show();
                            }
                        });
                    } else {

                        b.setNeutralButton("Block This Number", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Dialog dialogInterface = (Dialog) dialog;
                                Context context = dialogInterface.getContext();
                                up.setContext(context);
                                up.insertUserNumCUSTOM(detailedData.get(4));


//                                    Toast.makeText(
//                                            context,
//                                            "The number " + detailedData.get(0) + " has been blocked. You can undo this in Settings.",
//                                            Toast.LENGTH_LONG
//                                    ).show();

                                Snackbar.make(
                                        view.getRootView().findViewById(R.id.relativeLayoutMainActivity),
                                        "Please refresh for complete changes to apply.",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("Refresh", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle b = new Bundle();
                                                b.putString("RCRA_Refresh_Key", "just refreshed");
                                                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtras(b));
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                    b.show();
                }
            });
            textView = (TextView) view.findViewById(R.id.textViewItemForRecyclerView); //this is from the recent_calls_list_row.xml file.
            imageView = (ImageView) view.findViewById(R.id.imageViewItemForRecyclerView); //this is the image view
            callItemWithinRecyclerView = (CardView) view.findViewById(R.id.callItemWithinRecyclerView); //This is a card view
            blockItemWithinRecyclerView = (ImageView) view.findViewById(R.id.blockItemWithinRecyclerView); //This is an image view
            parentCardViewBlockItemWithinRecyclerView = (CardView) view.findViewById(R.id.parentCardViewBlockItemWithinRecyclerView);
            contactNameTextView = (TextView) view.findViewById(R.id.contactNameForRecyclerView);
            parentCardViewRCRA = (CardView) view.findViewById(R.id.parentCardViewRCRA);

            up.setContext(view.getContext());

            parentCardViewBlockItemWithinRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean currentBlockStatus = up.shouldMonitor(FormatNumber.unFormat(textView.getText().toString()), up.getContext());
                    Log.d(TAG, String.valueOf(currentBlockStatus));
                    if (currentBlockStatus) {
                        //If currently true
                        blockItemWithinRecyclerView.setAlpha(0.5f);
                        blockItemWithinRecyclerView.setColorFilter(Color.parseColor("#727272")); //Make it grey
                        up.removeUserNumCUSTOM(FormatNumber.unFormat(textView.getText().toString()));

                        Snackbar.make(
                                view.getRootView().findViewById(R.id.relativeLayoutMainActivity),
                                "Please refresh for complete changes to apply.",
                                Snackbar.LENGTH_LONG)
                                .setAction("Refresh", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle b = new Bundle();
                                        b.putString("RCRA_Refresh_Key", "just refreshed");
                                        view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtras(b));
                                    }
                                })
                                .show()
                        ;
                    } else {
                        blockItemWithinRecyclerView.setAlpha(1f);
                        blockItemWithinRecyclerView.setColorFilter(Color.parseColor("#00796B"));
                        up.insertUserNumCUSTOM(FormatNumber.unFormat(textView.getText().toString()));

                        Snackbar.make(
                                view.getRootView().findViewById(R.id.relativeLayoutMainActivity),
                                "Please refresh for complete changes to apply.",
                                Snackbar.LENGTH_LONG)
                                .setAction("Refresh", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle b = new Bundle();
                                        b.putString("RCRA_Refresh_Key", "just refreshed");
                                        view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtras(b));
                                    }
                                })
                                .show()
                        ;
                    }
                }
            });

            final float[] x = {0};
            final float[] y = {0};

            parentCardViewBlockItemWithinRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    x[0] = (int) event.getRawX();
                    y[0] = (int) event.getRawY();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        blockItemWithinRecyclerView.animate().scaleX(1.75f).scaleY(1.75f).setDuration(250).setInterpolator(new FastOutLinearInInterpolator()).start();
                        blockItemWithinRecyclerView.setElevation(5f);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        blockItemWithinRecyclerView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(250).setInterpolator(new FastOutLinearInInterpolator()).start();
                        blockItemWithinRecyclerView.setElevation(0f);
                    }
                    return false;
                }
            });

            parentCardViewBlockItemWithinRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast t = Toast.makeText(v.getContext(), "Block or unblock caller", Toast.LENGTH_SHORT);
                    Log.d(TAG, "X: " + x[0] + ", Y: " + y[0]);
                    t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                    t.show();
                    return false;
                }
            });

            callItemWithinRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callItemWithinRecyclerView.animate()
                            .rotation(360 + callItemWithinRecyclerView.getRotation()) //so that it rotates every time it is clicked
                            .setDuration(1000)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + textView.getText())); //The textview has the number! :) Not so hard after all.
                    Log.d(TAG, "Calling " + textView.getText());
                    try {
                        v.getContext().startActivity(call);
                    } catch (SecurityException e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(v.getContext(), "Could not make call. Please check if permissions are enabled.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            callItemWithinRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    x[0] = (int) event.getRawX();
                    y[0] = (int) event.getRawY();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        callItemWithinRecyclerView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(250).setInterpolator(new FastOutLinearInInterpolator()).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        callItemWithinRecyclerView.animate().scaleX(1f).scaleY(1f).setDuration(250).setInterpolator(new FastOutLinearInInterpolator()).start();
                    }
                    return false;
                }
            });

            callItemWithinRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast t = Toast.makeText(v.getContext(), "Call this number", Toast.LENGTH_SHORT);
                    Log.d(TAG, "X: " + x[0] + ", Y: " + y[0]);
                    t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                    t.show();
                    return false;
                }
            });

            parentCardViewRCRA.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    x[0] = (int) event.getRawX();
                    y[0] = (int) event.getRawY();
                    return false;
                }
            });

            parentCardViewRCRA.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast t = Toast.makeText(v.getContext(), "View more details", Toast.LENGTH_SHORT);
                    Log.d(TAG, "X: " + x[0] + ", Y: " + y[0]);
                    t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                    t.show();
                    return false;
                }
            });

        }

            public TextView getTextView() {
                return textView;
            }
            public ImageView getImageView(){
                return imageView;
            }
            public CardView getCallItemWithinRecyclerView(){
                return callItemWithinRecyclerView;
            }
            public ImageView getBlockItemWithinRecyclerView() {
                return blockItemWithinRecyclerView;
            }
            public TextView getContactNameTextView() {
                return contactNameTextView;
            }
            public CardView getContainer() {
                return parentCardViewRCRA;
            }
        }

    public RecentCallsRecyclerAdapter(List<String> my_phoneNumberList, List<Integer> my_imageViewList, Context context) {
        this.mContext = context;
        phoneNumberList = my_phoneNumberList;
        imageIncomingOutgoingList = my_imageViewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_calls_list_row, viewGroup, false);
        //RecentCallsListHolder listHolder = new RecentCallsListHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder ViewHolder, int position) {
        ViewHolder.getTextView().setText(phoneNumberList.get(position)); //Do not add unknown; needed for phne calling item
//        phNumberPublic = phoneNumberList.get(position);
        Log.d(TAG, "Element " + phoneNumberList.get(position) + " set.");

        ViewHolder.getImageView().setImageResource(imageIncomingOutgoingList.get(position));
        Log.d(TAG, "Corresponding image of " + phoneNumberList.get(position) + " set.");
        Log.d(TAG, String.valueOf(imageIncomingOutgoingList.get(position)));

        ContactInformation ci = new ContactInformation(ViewHolder.getTextView().getContext());
        if (ci.getContactID(phoneNumberList.get(position)) != null && ci.getContactImageAsUriWithoutDefaults(phoneNumberList.get(position)) != null) {
            Log.d(TAG, "For " + phoneNumberList.get(position) + ", " + ci.getContactImageAsUriWithoutDefaults(phoneNumberList.get(position)));
            RoundedBitmapDrawable rBmpDr = null;
            //Only if the file is found AND a contact image exists
            try {
                rBmpDr = RoundedBitmapDrawableFactory.create(mContext.getResources(),
                        MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(ci.getContactImageAsUri(phoneNumberList.get(position)).toString())
                ));
                rBmpDr.setCornerRadius(50f);
                ViewHolder.getImageView().setImageDrawable(rBmpDr);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ViewHolder.getImageView().setMinimumWidth((int) Dim.convertToPixels(ViewHolder.getTextView().getContext(), 48));
            ViewHolder.getImageView().setMinimumHeight((int) Dim.convertToPixels(ViewHolder.getTextView().getContext(), 48));
        } else if (ci.getContactID(phoneNumberList.get(position)) != null && ci.getContactImageAsUriWithoutDefaults(phoneNumberList.get(position)) == null) {
            //Existing contact, no image found
            ViewHolder.getImageView().setImageResource(R.drawable.ic_person_black_24dp);
            ViewHolder.getImageView().setMinimumWidth((int) Dim.convertToPixels(ViewHolder.getTextView().getContext(), 48));
            ViewHolder.getImageView().setMinimumHeight((int) Dim.convertToPixels(ViewHolder.getTextView().getContext(), 48));
        }



        UserPreferences up = new UserPreferences();
        up.setContext(ViewHolder.getTextView().getContext());

        String unformattedNumber = FormatNumber.unFormat(phoneNumberList.get(position));

        if (up.shouldMonitor(unformattedNumber, ViewHolder.getTextView().getContext())) {
            ViewHolder.getBlockItemWithinRecyclerView().setAlpha(1f);
            ViewHolder.getBlockItemWithinRecyclerView().setColorFilter(Color.parseColor("#00796B"));
        } else {
            ViewHolder.getBlockItemWithinRecyclerView().setAlpha(0.5f);
            ViewHolder.getBlockItemWithinRecyclerView().setColorFilter(Color.parseColor("#727272"));
        }

        ContactInformation contactInformation = new ContactInformation(up.getContext());
        if (contactInformation.getContactName(unformattedNumber, PERMISSIONS_READ_CONTACTS_IS_ENABLED) != null) {
            ViewHolder.getContactNameTextView().setText(contactInformation.getContactName(unformattedNumber, PERMISSIONS_READ_CONTACTS_IS_ENABLED));
        } else {
            //Let it say "Unknown Contact"
        }

        animate(ViewHolder.getContainer(), position);
//        enterAnimation(ViewHolder.itemView);
//        View root = ( (Activity)mContext )
//                .getWindow()
//                .getDecorView()
//                .findViewById(android.R.id.content);
//        final View buttonLoadCalls = root.findViewById(R.id.buttonLoadAllCalls);
//
//        if(position > phoneNumberList.size()-3) {
//            Log.d(TAG, "setting alpha...");
//            buttonLoadCalls.setAlpha(1);
//            Log.d(TAG, "alpha set");
//        } else {
//            Log.d(TAG, "removing alpha...");
//            buttonLoadCalls.setAlpha(0.5f);
//            Log.d(TAG, "alpha removed");
//        }
    }

    private void animate(CardView container, int position) {
        if (position > lastItemAnimated) {
            lastItemAnimated = position;

            Animation a = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_quick);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            a.setDuration(500);
            a.setStartOffset(250);
            container.startAnimation(a);
        }

        Animation a2 = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        a2.setStartOffset(500);
        container.findViewById(R.id.imageViewItemForRecyclerView).startAnimation(a2);
    }




    @Override
    public int getItemCount(){
        return phoneNumberList.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((RecentCallsRecyclerAdapter.ViewHolder)holder).getContainer().clearAnimation();
    }


}
