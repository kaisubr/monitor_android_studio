package iosf.github.kaisubr.sciencefair.CustomAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;
import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 7:17 AM for Science Fair.
 */
public class OnboardingRecyclerViewAdapter2 extends RecyclerView.Adapter<OnboardingRecyclerViewAdapter2.ViewHolder> {
    private final static String TAG = "OnboardingRVAdapter2";
    private static List<String> phNumbers;
    private static String phNumbersAsString; //SharedPreference doesn't allow List<String> as a value to store
    final static UserPreferences up = new UserPreferences();
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPhNumber;
        View rootView;

        public ViewHolder(final View itemView) {
            super(itemView);
            rootView = itemView.getRootView();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Item " + getLayoutPosition() + " clicked.");
                }
            });


            textViewPhNumber = (TextView) itemView.findViewById(R.id.textViewPhNumber2);
        }

        public TextView getTextViewPhNumber() {
            return textViewPhNumber;
        }
    }

    public OnboardingRecyclerViewAdapter2(List<String> mTextViewPhNumber, Context mContext) {
        this.context = mContext;
        phNumbers = mTextViewPhNumber;
    }

    @Override
    public OnboardingRecyclerViewAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_calls_of2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OnboardingRecyclerViewAdapter2.ViewHolder holder, int position) {
        FormatNumber fn = new FormatNumber();
        holder.getTextViewPhNumber().setText(fn.toPhoneNumber(phNumbers.get(position)));
        Log.d(TAG, "Set " + phNumbers.get(position));
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i <= phNumbers.size() - 1; i++) {
            sb.append(FormatNumber.unFormat(phNumbers.get(i)));
            sb.append(" ");
        }
        phNumbersAsString = sb.toString();
        Log.d("STRING", phNumbersAsString);
        UserPreferences UP1 = new UserPreferences();
        UP1.setContext(context);
        UP1.setUserNumCUSTOM(phNumbersAsString);

        final int pos = position;

        holder.itemView.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    //notifyDataSetChanged();
                    try {
                        phNumbers.remove(pos);
                        notifyItemRemoved(pos);
                    } catch(Exception e) {
                        phNumbers.remove(phNumbers.size()-1);
                        notifyItemRemoved(phNumbers.size()-1);
                        Log.d("Exception", e.toString());
                    } finally {
                        Log.d(TAG, "closing...");

                        up.userSelectedNumbers = phNumbers;
                        Log.d(TAG, up.userSelectedNumbers.toString());
                    }

                } else {
                    phNumbers.remove(pos);
                    notifyDataSetChanged();
                    Log.d(TAG, "closing...");

                    up.userSelectedNumbers = phNumbers;
                    Log.d(TAG, up.userSelectedNumbers.toString());
                }

                StringBuilder sbDel = new StringBuilder("");

                for (int i = 0; i <= phNumbers.size() - 1; i++) {
                    sbDel.append(FormatNumber.unFormat(phNumbers.get(i)));
                    sbDel.append(" ");
                }
                phNumbersAsString = sbDel.toString();
                Log.d("STRING", phNumbersAsString);
                UserPreferences UP2 = new UserPreferences();
                UP2.setContext(context);
                UP2.setUserNumCUSTOM(phNumbersAsString);
            }
        });

        up.userSelectedNumbers = phNumbers;
        Log.d(TAG, up.userSelectedNumbers.toString());


    }

    @Override
    public int getItemCount() {
        return phNumbers.size();
    }
}
