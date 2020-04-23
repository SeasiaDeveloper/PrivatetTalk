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
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.objects.AttributesObject;
import com.privetalk.app.database.objects.CurrentUserDetails;
import com.privetalk.app.database.objects.LanguageObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.dialogs.PriveTalkPickerDialog;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.ViewpagerFragment;

import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ProfileEditComplexListFragment extends FragmentWithTitle implements ViewpagerFragment {

    private View rootView;
    private int fragmentPosition;
    private RecyclerView recyclerView;
    private ArrayList<AttributesObject> languageObjectArrayList;
    private ArrayList<AttributesObject> levelsArrayList;
    private String[] speakingLevels;
    private ComplexlistRecyclerAdapter adapter;
    private PriveTalkEditText priveTalkEditText;

    public static ProfileEditComplexListFragment newInstance(int position) {

        ProfileEditComplexListFragment fragmentFirst = new ProfileEditComplexListFragment();

        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        }

        languageObjectArrayList = new ArrayList<>();
        levelsArrayList = new ArrayList<>();
        languageObjectArrayList = AttributesDatasource.getInstance(getContext()).getAttributeObjectListAlphabetic("languages", "");
        levelsArrayList = AttributesDatasource.getInstance(getContext()).getAttributeObjectList("speaking_levels", "");

        speakingLevels = new String[levelsArrayList.size()];

        for (int i = 0; i < levelsArrayList.size(); i++) {
            speakingLevels[i] = levelsArrayList.get(i).display;
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
                languageObjectArrayList.clear();
                languageObjectArrayList.addAll(AttributesDatasource.getInstance(getContext()).getAttributeObjectList("languages", s.toString()));
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

        String databaseString = LanguageObject.getStringFromList(currentUserDetails.languageObjects);
        String currentString = LanguageObject.getStringFromList(PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects);

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

            holder.languageObject = null;
            holder.tickImageView.setBackgroundDrawable(null);
            holder.itemLevel.setText(getString(R.string.not_yet_set));

            for (LanguageObject languageObject : PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects) {
                if (languageObject.language.value.equals(languageObjectArrayList.get(position).value)) {
                    holder.tickImageView.setBackgroundResource(R.drawable.tick_selected);
                    holder.itemLevel.setText(languageObject.speakingLevel.display);
                    holder.languageObject = languageObject;
                    break;
                }
            }

            holder.itemName.setText(languageObjectArrayList.get(position).display);
            holder.spinnerView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    showLanguageLevePicker(holder, position);
                }
            });
            holder.itemView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {

                    if (holder.languageObject != null) {
                        PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects.remove(holder.languageObject);
                    } else {
                        showLanguageLevePicker(holder, position);
                    }

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return languageObjectArrayList.size();
        }
    }

    private void showLanguageLevePicker(final ComplexListViewHolder holder, final int languagePosition) {

        KeyboardUtilities.closeKeyboard(getActivity(), rootView);

        PriveTalkPickerDialog priveTalkPickerDialog = new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                    @Override
                    public void onDonePressed(int position) {

                        AttributesObject attributesObject = AttributesObject.getAttributeObject(position, PriveTalkTables.AttributesTables.getTableString("speaking_levels"));
                        if (holder.languageObject != null) {
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects.remove(holder.languageObject);
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects.add(new LanguageObject(languageObjectArrayList.get(languagePosition), attributesObject));
                        } else {
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.languageObjects.add(new LanguageObject(languageObjectArrayList.get(languagePosition), attributesObject));
                        }

                        adapter.notifyDataSetChanged();

                    }
                })
                .setTitle(getString(R.string.pick_something))
                .setSelectionArray(speakingLevels);
        for (int i = 0; i < speakingLevels.length; i++) {
            if (speakingLevels[i].equals(holder.itemLevel.getText().toString())) {
                priveTalkPickerDialog.numberPicker.setValue(i + 1);
                break;
            }
        }
        priveTalkPickerDialog.show();
    }

    private static class ComplexListViewHolder extends RecyclerView.ViewHolder {

        private ImageView tickImageView;
        private View spinnerView;
        private PriveTalkTextView itemName;
        private PriveTalkTextView itemLevel;
        private LanguageObject languageObject;

        public ComplexListViewHolder(View itemView) {
            super(itemView);
            tickImageView = (ImageView) itemView.findViewById(R.id.complex_list_checkbox_imageview);
            spinnerView = itemView.findViewById(R.id.complexListCustomSpinner);
            itemName = (PriveTalkTextView) itemView.findViewById(R.id.complexListItemName);
            itemLevel = (PriveTalkTextView) itemView.findViewById(R.id.complexListItemLevel);

        }
    }


}
