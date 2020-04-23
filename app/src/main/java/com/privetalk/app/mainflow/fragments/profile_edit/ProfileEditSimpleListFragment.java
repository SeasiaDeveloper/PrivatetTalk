package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.objects.AttributesObject;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.TintImageView;
import com.privetalk.app.utilities.ViewpagerFragment;

import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ProfileEditSimpleListFragment extends FragmentWithTitle implements ViewpagerFragment {

    private View rootView;
    private RecyclerView simpleListRecyclerView;
    private int fragmentPosition;
    private int fragmentType;
    private LayoutInflater layoutInflater;
    private AttributesObject attributesObject;

    private ArrayList<AttributesObject> attributesObjectArrayList;
    private PriveTalkEditText priveTalkEditText;
    private SimpleListRecyclerAdapter adapter;

    public static ProfileEditSimpleListFragment newInstance(int position, int fragmentType) {

        ProfileEditSimpleListFragment fragmentFirst = new ProfileEditSimpleListFragment();

        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        args.putInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE, fragmentType);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
            fragmentType = getArguments().getInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE);
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
            fragmentType = getArguments().getInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE);
        }

        switch (fragmentPosition) {
            case PersonalInfoEditPagerFragment.RELATIONSHIP:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.RELATIONSHIP_STATUSES], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.relationship_status;
                break;
            case PersonalInfoEditPagerFragment.SEXUALITY:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.SEXUALITY_STATUSES], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.sexuality_status;
                break;
            case PersonalInfoEditPagerFragment.BODY_TYPE:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.BODY_TYPES], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.body_type;
                break;
            case PersonalInfoEditPagerFragment.HAIR:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.HAIR_COLORS], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.hair_color;
                break;
            case PersonalInfoEditPagerFragment.EYES:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EYE_COLOR], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.eyes_color;
                break;
            case PersonalInfoEditPagerFragment.SMOKING:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.SMOKING_HABITS], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.smoking_status;
                break;
            case PersonalInfoEditPagerFragment.DRINKING:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.DRINKING_HABITS], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.drinking_status;
                break;
            case PersonalInfoEditPagerFragment.EDUCATION:
                attributesObjectArrayList = AttributesDatasource.
                        getInstance(getContext()).
                        getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EDUCATION_LEVEL], "");
                attributesObject = PersonalInfoEditParentFragment.currentUser.currentUserDetails.education_status;
                break;
            default:
                attributesObjectArrayList = new ArrayList<>();

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layoutInflater = inflater;

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_edit_simple_list, container, false);

        simpleListRecyclerView = (RecyclerView) rootView.findViewById(R.id.simpleListRecyclerView);
        simpleListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleListRecyclerView.setAdapter(adapter = new SimpleListRecyclerAdapter());

        priveTalkEditText = (PriveTalkEditText) rootView.findViewById(R.id.searchContext);
        priveTalkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                attributesObjectArrayList.clear();

                switch (fragmentPosition) {
                    case PersonalInfoEditPagerFragment.RELATIONSHIP:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.RELATIONSHIP_STATUSES], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.SEXUALITY:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.SEXUALITY_STATUSES], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.BODY_TYPE:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.BODY_TYPES], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.HAIR:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.HAIR_COLORS], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.EYES:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EYE_COLOR], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.SMOKING:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.SMOKING_HABITS], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.DRINKING:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.DRINKING_HABITS], s.toString()));
                        break;
                    case PersonalInfoEditPagerFragment.EDUCATION:
                        attributesObjectArrayList.addAll(AttributesDatasource.
                                getInstance(getContext()).getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EDUCATION_LEVEL], s.toString()));
                        break;
                    default:
                        attributesObjectArrayList = new ArrayList<>();

                }


                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE, fragmentType);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
    }

    @Override
    protected String getActionBarTitle() {
        return fragmentType == PersonalInfoEditParentFragment.PARENT_TYPE_PROFILE_EDIT ?
                PriveTalkApplication.getInstance().getResources().getStringArray(R.array.personalInfoEditArray)[fragmentPosition].replace(":", "") :
                PriveTalkApplication.getInstance().getResources().getStringArray(R.array.activities_array)[fragmentPosition].replace(":", "");
    }



    @Override
    public void onPauseFragment() {

        if (attributesObject == null)
            return;

        switch (fragmentPosition) {
            case PersonalInfoEditPagerFragment.RELATIONSHIP:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.relationship_status.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.relationship_status = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.SEXUALITY:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.sexuality_status.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.sexuality_status = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.BODY_TYPE:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.body_type.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.body_type = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.HAIR:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.hair_color.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.hair_color = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.EYES:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.eyes_color.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.eyes_color = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.SMOKING:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.smoking_status.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.smoking_status = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.DRINKING:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.drinking_status.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.drinking_status = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            case PersonalInfoEditPagerFragment.EDUCATION:
                if (!PersonalInfoEditParentFragment.currentUser.currentUserDetails.education_status.value.equals(attributesObject.value)) {
                    PersonalInfoEditParentFragment.currentUser.currentUserDetails.education_status = attributesObject;
                    PersonalInfoEditParentFragment.infoChanged = true;
                }
                break;
            default:

        }

    }

    @Override
    public void onResumeFragment() {

    }

    private class SimpleListRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(layoutInflater.inflate(R.layout.row_edit_simple_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.titleTextview.setText(attributesObjectArrayList.get(position).display);

            if (attributesObjectArrayList.get(position).value.equals(attributesObject.value))
                holder.imageView.setImageResource(R.drawable.tick_selected);
            else
                holder.imageView.setImageResource(R.drawable.circle_blue_stroke);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attributesObject = attributesObjectArrayList.get(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return attributesObjectArrayList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextview;
        private TintImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = (TextView) itemView.findViewById(R.id.rowEditTitle);
            imageView = (TintImageView) itemView.findViewById(R.id.tickImageView);
        }
    }
}
