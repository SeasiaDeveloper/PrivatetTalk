package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.objects.AttributesObject;
import com.privetalk.app.database.objects.CurrentUserDetails;
import com.privetalk.app.database.objects.FaithObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.ViewpagerFragment;
import com.privetalk.app.utilities.dialogs.PriveTalkPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ReligionComplexList extends FragmentWithTitle implements ViewpagerFragment {

    private static final String PREFER_NOT_TO_SAY = "17";

    private View rootView;
    private int fragmentPosition;
    private RecyclerView recyclerView;
    private ArrayList<AttributesObject> religionsArrayList;
    private ArrayList<AttributesObject> levelsArrayList;
    private String[] religionLevels;
    private ComplexlistRecyclerAdapter adapter;
    private PriveTalkEditText priveTalkEditText;

    public static ReligionComplexList newInstance(int position) {

        ReligionComplexList fragmentFirst = new ReligionComplexList();

        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        }

        religionsArrayList = AttributesDatasource.getInstance(getContext()).getAttributeObjectListAlphabetic("religions", "");
        levelsArrayList = AttributesDatasource.getInstance(getContext()).getAttributeObjectList("faith_levels", "");

        religionLevels = new String[levelsArrayList.size()];

        Collections.reverse(levelsArrayList);

//        for (int i = 0; i < levelsArrayList.size(); i++) {
//            religionLevels[i] = levelsArrayList.get(levelsArrayList.size() - i - 1).display;
//        }
        for (int i = 0; i < levelsArrayList.size(); i++) {
            religionLevels[i] = levelsArrayList.get(i).display;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_edit_complex_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.complexListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new ComplexlistRecyclerAdapter());

        priveTalkEditText = (PriveTalkEditText) rootView.findViewById(R.id.searchContext);
        priveTalkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                religionsArrayList.clear();
                religionsArrayList.addAll(AttributesDatasource.getInstance(getContext()).getAttributeObjectList("religions", s.toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    @Override
    protected String getActionBarTitle() {
        return PriveTalkApplication.getInstance().getResources().getStringArray(R.array.personalInfoEditArray)[fragmentPosition].replace(":", "");
    }

    private boolean haveDataChanged() {

        CurrentUserDetails currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();

        String databaseString = currentUserDetails.faith.getJSONObject().toString();
        String currentString = PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith.getJSONObject().toString();

        return !databaseString.equals(currentString);
    }

    @Override
    public void onPauseFragment() {
        PersonalInfoEditParentFragment.infoChanged = haveDataChanged() || PersonalInfoEditParentFragment.infoChanged;
    }

    @Override
    public void onResumeFragment() {

    }

    public class ComplexlistRecyclerAdapter extends RecyclerView.Adapter<ComplexListViewHolder> {

        @Override
        public ComplexListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_complex_list, parent, false);
            return new ComplexListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ComplexListViewHolder holder, final int position) {

            holder.faithObject = null;
            holder.tickImageView.setBackgroundDrawable(null);
            holder.itemLevel.setText(getString(R.string.not_yet_set));

            if (PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith.religion.value.equals(religionsArrayList.get(position).value)) {
                holder.tickImageView.setBackgroundResource(R.drawable.tick_selected);
                if (!religionsArrayList.get(position).value.equals(PREFER_NOT_TO_SAY))
                    holder.itemLevel.setText(PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith.faithLevel.display);
                holder.faithObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith;
            }

            holder.itemName.setText(religionsArrayList.get(position).display);


            holder.spinnerView.setEnabled(!religionsArrayList.get(position).value.equals(PREFER_NOT_TO_SAY));

            holder.spinnerView.setOnTouchListener(religionsArrayList.get(position).value.equals(PREFER_NOT_TO_SAY) ? null : new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {

                    showLanguageLevePicker(holder, position);
                }
            });

            holder.itemView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {

                    if (holder.faithObject != null) {
                        PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith = new FaithObject(new JSONObject());
                    } else {
                        if (religionsArrayList.get(position).value.equals(PREFER_NOT_TO_SAY))
                            setPreferNotToSay(position);
                        else
                            showLanguageLevePicker(holder, position);
                    }

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return religionsArrayList.size();
        }
    }

    private void showLanguageLevePicker(final ComplexListViewHolder holder, final int religionPosition) {

        KeyboardUtilities.closeKeyboard(getActivity(), rootView);

        PriveTalkPickerDialog priveTalkPickerDialog = new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                    @Override
                    public void onDonePressed(int position) {

                        PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith = new FaithObject(new JSONObject());
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("religion", religionsArrayList.get(religionPosition).value);
                            jsonObject.put("level", levelsArrayList.get(position - 1).value);
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith = new FaithObject(jsonObject);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).setTitle(getString(R.string.pick_something))
                .setSelectionArray(religionLevels);

        for (int i = 0; i < religionLevels.length; i++) {
            if (religionLevels[i].equals(holder.itemLevel.getText().toString())) {
                priveTalkPickerDialog.numberPicker.setValue(i + 1);
                break;
            }
        }
        priveTalkPickerDialog.show();
    }

    private void setPreferNotToSay(int religionPosition) {
        PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith = new FaithObject(new JSONObject());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("religion", religionsArrayList.get(religionPosition).value);
            jsonObject.put("level", "1");
            PersonalInfoEditParentFragment.currentUser.currentUserDetails.faith = new FaithObject(jsonObject);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class ComplexListViewHolder extends RecyclerView.ViewHolder {

        private ImageView tickImageView;
        private View spinnerView;
        private PriveTalkTextView itemName;
        private PriveTalkTextView itemLevel;
        private FaithObject faithObject;

        public ComplexListViewHolder(View itemView) {
            super(itemView);
            tickImageView = (ImageView) itemView.findViewById(R.id.complex_list_checkbox_imageview);
            spinnerView = itemView.findViewById(R.id.complexListCustomSpinner);
            itemName = (PriveTalkTextView) itemView.findViewById(R.id.complexListItemName);
            itemLevel = (PriveTalkTextView) itemView.findViewById(R.id.complexListItemLevel);

        }
    }


}
