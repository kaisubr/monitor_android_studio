package iosf.github.kaisubr.sciencefair.CustomAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
 * Created at 9:11 AM for Science Fair.
 */
public class EditListCompleteAdapter extends RecyclerView.Adapter<EditListCompleteAdapter.ViewHolder>{
    private final static String TAG = "EditListCompleteAdapter";
    List<String> phNumbers;
    final static UserPreferences up = new UserPreferences();
    final static FormatNumber fn = new FormatNumber();
    private Context context;
    private String phNumbersAsString;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPhNumber;

        public ViewHolder(final View itemView) {
            super(itemView);

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

    public EditListCompleteAdapter(List<String> mTextViewPhNumber, Context mContext) {
        this.context = mContext;
        phNumbers = mTextViewPhNumber;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_calls_of2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        up.setContext(context);
        Log.d(TAG, "Context set. Current position " + position + ", and the value at the position is " + fn.toPhoneNumber(phNumbers.get(position)));
        holder.getTextViewPhNumber().setText(fn.toPhoneNumber(phNumbers.get(position)));

        final int pos = position;

        holder.itemView.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos >= 1) {
                    //FIXME: No animation due to usage of notifyDataSetChanged().
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                    Log.d(TAG, "Removed #" + pos + ", and the old value at the position was " + fn.toPhoneNumber(phNumbers.get(pos)));
                    if (pos == phNumbers.size() - 1) {
                        //If it's the last one
                        phNumbers.remove(phNumbers.size() - 1);
                    } else {
                        phNumbers.remove(pos);
                        Log.d(TAG, "Removed #" + pos + ", and the new value at the position is " + fn.toPhoneNumber(phNumbers.get(pos)));
                    }
                } else if (pos == 0) {
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                    if (phNumbers.size() == 1) {
                        //If the top one is the only one remaining
                        phNumbers.clear();
                    } else {
                        //Remove if top one is not the only one
                        phNumbers.remove(pos);
                    }
                } else {
                    //What is this number??
                    Log.d(TAG, "Unknown number! "+ pos);
                }
            }
        });

        up.setContext(context);
        up.setUltimatePhoneList(phNumbers);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("Ultimate Changed!", sharedPreferences.getString("pref_complete_list", ""));
    }

    @Override
    public int getItemCount() {
        return phNumbers.size();
    }
}
