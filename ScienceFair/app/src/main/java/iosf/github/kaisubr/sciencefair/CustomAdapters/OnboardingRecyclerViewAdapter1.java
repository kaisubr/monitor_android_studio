package iosf.github.kaisubr.sciencefair.CustomAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;
import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 7:17 AM for Science Fair.
 */
public class OnboardingRecyclerViewAdapter1 extends RecyclerView.Adapter<OnboardingRecyclerViewAdapter1.ViewHolder> {
    private final static String TAG = "OnboardingRVAdapter";
    private static String listOfBooleans = "true true true true true true true true ";
    private List<Integer> imageViews;
    private static List<String> phNumbers;
    private List<String> typeOfNumber;
    private List<Boolean> checked;
    final static UserPreferences up = new UserPreferences();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPhNumber;
        public TextView textViewTypeOfNumber;
        public ImageView imageViewIcon;
        public CheckBox checkBox;

        public ViewHolder(final View itemView) {
            super(itemView);

            up.setContext(itemView.getContext());
            if (!up.getDefaultNumCUSTOM().equals("notSetYet")) {
                listOfBooleans = up.getDefaultNumCUSTOM(); //In case the user rotated the screen, this is the most updated version of the default numbers
            }

            //Happens in background - this is a list.
            up.makeAll(true, phNumbers.size());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Item " + getLayoutPosition() + " clicked.");
                    if(up.userFinalChoices.get(getLayoutPosition())) { //If the current value is true, set to false.
                        up.userFinalChoices.set(getLayoutPosition(), false);
                        getCheckBox().setChecked(false);
                    } else { //Else set to true
                        up.userFinalChoices.set(getLayoutPosition(), true);
                        getCheckBox().setChecked(true);
                    }

                    //Log.d(TAG, "Item set to " + up.userFinalChoices.get(getLayoutPosition()));

                    StringBuffer sb = new StringBuffer("");
                    for (int i = 0; i < phNumbers.size(); i++) {
                        sb.append(up.userFinalChoices.get(i)); //Get the boolean, convert to string, append to StringBuffer
                        sb.append(" ");
                    }

                    listOfBooleans = sb.toString();
                    Log.d(TAG, listOfBooleans);
                    Log.d(TAG, String.valueOf(up.userFinalChoices.size()));

                    UserPreferences up = new UserPreferences();
                    up.setContext(v.getContext());
                    up.setDefaultNumCUSTOM(listOfBooleans);

//                    Context contextV = itemView.getContext();
//
//                    SharedPreferences sharedPreferences = contextV.getSharedPreferences("pref_default_numbers", Context.MODE_PRIVATE);
//                    sharedPreferences.edit().putString(listOfBooleans, "true true true true true true true true ").apply();
                }
            });

            textViewPhNumber = (TextView) itemView.findViewById(R.id.textViewPhNumber1);
            textViewTypeOfNumber = (TextView) itemView.findViewById(R.id.textViewTypeOfNumber1);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon1);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox1);
        }

        public TextView getTextViewPhNumber() {
            return textViewPhNumber;
        }

        public TextView getTextViewTypeOfNumber() {
            return textViewTypeOfNumber;
        }

        public ImageView getImageViewIcon() {
            return imageViewIcon;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

    public OnboardingRecyclerViewAdapter1(List<String> mTextViewPhNumber, List<String> mTextViewType, List<Integer> mImageViewIcon, List<Boolean> mCheckBox, Context mContext) {
        this.context = mContext;
        phNumbers = mTextViewPhNumber;
        typeOfNumber = mTextViewType;
        checked = mCheckBox;
        imageViews = mImageViewIcon;
    }

    @Override
    public OnboardingRecyclerViewAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_f1, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OnboardingRecyclerViewAdapter1.ViewHolder holder, int position) {
        FormatNumber fn = new FormatNumber();
        holder.getTextViewPhNumber().setText(fn.toPhoneNumber(phNumbers.get(position)));
        Log.d(TAG, "Set " + phNumbers.get(position));
        holder.getTextViewTypeOfNumber().setText(typeOfNumber.get(position));
        Log.d(TAG, "Set " + typeOfNumber.get(position));
        holder.getImageViewIcon().setImageResource(imageViews.get(position));
        Log.d(TAG, "Set " + imageViews.get(position));

        holder.getCheckBox().setChecked(Boolean.valueOf(listOfBooleans.split(" ")[position]));

//
//        Boolean bool;
//        //Set vals
//        try {
//            bool = up.userFinalChoices.get(position);
//        } catch (Exception e) {
//            bool = checked.get(position);
//            Log.d(TAG, e.toString());
//        }
//
//        holder.getCheckBox().setChecked(bool);
//
//        final int pos = position;
//
//        holder.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                //Log.d(TAG, Boolean.toString(isChecked));
//                up.userFinalChoices.set(pos, isChecked);
//                StringBuffer sb = new StringBuffer("");
//                for (int i = 0; i < phNumbers.size(); i++) {
//                    sb.append(up.userFinalChoices.get(i));
//                    sb.append(" ");
//                }
//                listOfBooleans = sb.toString();
//                Log.d(TAG, listOfBooleans);
//                //holder.itemView.performClick();
//
//                UserPreferences up = new UserPreferences();
//                up.setContext(context);
//                up.setDefaultNumCUSTOM(listOfBooleans);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return phNumbers.size();
    }
}
