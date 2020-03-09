package com.schopfen.Booth.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.Ask_QuestionActivity;
import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.Adapters.CategoriesListAdapter;
import com.schopfen.Booth.Adapters.SearchBoothAdapter;
import com.schopfen.Booth.Adapters.SearchByDefaultAdapter;
import com.schopfen.Booth.Adapters.SearchExploreAdapter;
import com.schopfen.Booth.Adapters.SearchPeopleAdapter;
import com.schopfen.Booth.Adapters.SearchProductsAdapter;
import com.schopfen.Booth.Adapters.SearchQuestionsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.HomeCateDataModel;
import com.schopfen.Booth.DataClasses.SearchExploreArrayData;
import com.schopfen.Booth.DataClasses.SearchProductsArrayData;
import com.schopfen.Booth.DataClasses.SearchQuestionsArrayData;
import com.schopfen.Booth.Models.AccountsData;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.Models.SearchQuestionsData;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";
    ImageView imageView_filters;

    RecyclerView products_recyclerView, questions_recyclerView, booths_recyclerView, people_recyclerview, grid_recycler;
    ArrayList<SearchProductsArrayData> search_products_list;
    ArrayList<SearchQuestionsArrayData> questions_list;
    ArrayList<SearchExploreArrayData> explore_list;
    SearchProductsAdapter searchProductsAdapter;
    SearchQuestionsAdapter searchQuestionsAdapter;
    SearchExploreAdapter searchExploreAdapter;
    AutoCompleteTextView searchAutoComplete;
    RelativeLayout products_layout, questions_layout, back, img_list;
    LinearLayout booths_layout, people_layout, grid_layout;
    RelativeLayout header_comment, header_cart;
    ImageView header_more, inbox_img;
    LinearLayout search_field_selector, search_field;
    ArrayList<ProductsData> homeCateData = new ArrayList<>();
    String items[] = {"0", "1", "1", "1", "0", "1"};
    ArrayList<String> searchTypes = new ArrayList<>();
    HomeCateDataModel model;
    String searchType = "Products";
    EditText searchEdit;
    ImageView searchBtn;
    ArrayList<ProductsData> productsData = new ArrayList<>();
    ArrayList<ProductImagesData> productImagesData;
    LinearLayoutManager layoutManager;
    ArrayList<ProductImagesData> questionImages;
    ArrayList<SearchQuestionsData> searchQuestionsData = new ArrayList<>();
    ArrayList<AccountsData> accountsData = new ArrayList<>();
    TextView searchCount, questionsCount, boothCount, peopleCount, noProductsText, noQuestionsText, noBoothsText, noUsersText, ask_question;
    ArrayList<ProductsData> productsDataGrid = new ArrayList<>();
    AutoCompleteTextView category, subcategory, subsubcategory, sortby, city;
    ArrayList<MainCategoriesData> mainCategoryData;
    ArrayList<SubCategoriesData> subCategoriesData;
    ArrayList<SubSubCategoriesData> subSubCategoriesData;
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    HashMap<String, String> headerParams;
    TextView cart_count;
    TextView msg_count;
    ArrayList<String> mainCategoriesNames = new ArrayList<>();
    ArrayList<String> subCategoriesNames = new ArrayList<>();
    ArrayList<String> subSubCategoriesNames = new ArrayList<>();
    ArrayList<String> citiesNames = new ArrayList<>();

    String key_value_for_order = "";
    Dialog filtersDialog;

    int mainPosition;
    int subPosition;
    String subsubCategoryID;
    String CityID = "";
    String SORTBY = "";
    String CategoryID = "";

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Initializations(view);

        searchTypes.add(getResources().getString(R.string.products));
        searchTypes.add(getResources().getString(R.string.question));
        searchTypes.add(getResources().getString(R.string.booths));
        searchTypes.add(getResources().getString(R.string.people));

        searchAutoComplete.setHint(getResources().getString(R.string.products));

        getUserDetails();

        productsForGrid();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.searchdropdown, searchTypes);
        adapter.setDropDownViewResource(R.layout.searchdropdown);
        searchAutoComplete.setAdapter(adapter);
        searchAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAutoComplete.showDropDown();
            }
        });
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        searchType = "Products";
                        img_list.setVisibility(View.VISIBLE);
                        key_value_for_order = "products.ProductPrice";
                        products_layout.setVisibility(View.VISIBLE);
                        questions_layout.setVisibility(View.GONE);
                        booths_layout.setVisibility(View.GONE);
                        people_layout.setVisibility(View.GONE);
                        if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                            searchProductsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1:
                        searchType = "Questions";
                        img_list.setVisibility(View.GONE);
                        products_layout.setVisibility(View.GONE);
                        questions_layout.setVisibility(View.VISIBLE);
                        booths_layout.setVisibility(View.GONE);
                        people_layout.setVisibility(View.GONE);
                        if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                            searchQuestionsFunction(searchType, searchEdit.getText().toString());
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 2:
                        searchType = "Booths";
                        img_list.setVisibility(View.VISIBLE);
                        key_value_for_order = "users_text.BoothName";
                        products_layout.setVisibility(View.GONE);
                        questions_layout.setVisibility(View.GONE);
                        booths_layout.setVisibility(View.VISIBLE);
                        people_layout.setVisibility(View.GONE);
                        if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                            searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 3:
                        searchType = "People";
                        img_list.setVisibility(View.VISIBLE);
                        key_value_for_order = "users_text.FullName";
                        products_layout.setVisibility(View.GONE);
                        questions_layout.setVisibility(View.GONE);
                        booths_layout.setVisibility(View.GONE);
                        people_layout.setVisibility(View.VISIBLE);
                        if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                            searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        });

        search_field_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_field_selector.setVisibility(View.GONE);
                grid_layout.setVisibility(View.GONE);
                search_field.setVisibility(View.VISIBLE);
                products_layout.setVisibility(View.VISIBLE);
                questions_layout.setVisibility(View.GONE);
                booths_layout.setVisibility(View.GONE);
                people_layout.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_field_selector.setVisibility(View.VISIBLE);
                search_field.setVisibility(View.GONE);
                products_layout.setVisibility(View.GONE);
                questions_layout.setVisibility(View.GONE);
                booths_layout.setVisibility(View.GONE);
                people_layout.setVisibility(View.GONE);
                back.setVisibility(View.GONE);

                grid_layout.setVisibility(View.VISIBLE);

            }
        });

        ask_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Ask_QuestionActivity.class));
            }
        });

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                        switch (searchType) {
                            case "Products":
                                key_value_for_order = "products.ProductPrice";
                                searchProductsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                                break;
                            case "Questions":
                                searchQuestionsFunction(searchType, searchEdit.getText().toString());
                                break;
                            case "Booths":
                                key_value_for_order = "users_text.BoothName";
                                searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                                break;
                            case "People":
                                key_value_for_order = "users_text.FullName";
                                searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                                break;
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

//        products_aray_data();
//        searchProductsAdapter = new SearchProductsAdapter(getContext(), search_products_list);
//        LinearLayoutManager productsLayoutManager = new LinearLayoutManager(getContext());
//        products_recyclerView.setLayoutManager(productsLayoutManager);
//        products_recyclerView.setAdapter(searchProductsAdapter);

    }

    private void getUserDetails() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedPreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String CartCount = user_info.getString("CartCount");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            editor.putString("UserOrderCount", UserOrderCount).commit();
                            editor.putString("BoothOrderCount", BoothOrderCount).commit();
                            if (UnreadMessageCount.equals("0")) {
                                msg_count.setVisibility(View.GONE);
                            } else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(UnreadMessageCount);
                            }

//                            if (HasUnreadMessage.equals("yes")) {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                            } else {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                            }
                            if (CartCount.equals("0")) {
                                cart_count.setVisibility(View.GONE);
                            } else {
                                cart_count.setVisibility(View.VISIBLE);
                                cart_count.setText(CartCount);
                            }

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                editor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Initializations(View view) {
        search_products_list = new ArrayList<>();
        questions_list = new ArrayList<>();
        explore_list = new ArrayList<>();
        products_recyclerView = view.findViewById(R.id.products_recycleView);
        questions_recyclerView = view.findViewById(R.id.questions_recycleView);
        booths_recyclerView = view.findViewById(R.id.booths_recycler);
        people_recyclerview = view.findViewById(R.id.people_recycler);
        grid_recycler = view.findViewById(R.id.grid_recycler);
        searchAutoComplete = view.findViewById(R.id.searchAutoComplete);
        products_layout = view.findViewById(R.id.products_layout);
        questions_layout = view.findViewById(R.id.questions_layout);
        booths_layout = view.findViewById(R.id.booths_layout);
        people_layout = view.findViewById(R.id.people_layout);
        grid_layout = view.findViewById(R.id.grid_layout);
        header_comment = view.findViewById(R.id.header_comment);
        inbox_img = view.findViewById(R.id.inbox_img);
        cart_count = view.findViewById(R.id.cart_count);
        msg_count = view.findViewById(R.id.msg_count);
        header_cart = view.findViewById(R.id.header_cart);
        header_more = view.findViewById(R.id.header_more);
        search_field_selector = view.findViewById(R.id.search_field_selector);
        search_field = view.findViewById(R.id.search_field);
        back = view.findViewById(R.id.back);
        imageView_filters = view.findViewById(R.id.iv_filters);
        img_list = view.findViewById(R.id.list);
        header_comment.setOnClickListener(this);
        header_cart.setOnClickListener(this);
        header_more.setOnClickListener(this);
        imageView_filters.setOnClickListener(this);

        searchBtn = view.findViewById(R.id.searchBtn);
        searchEdit = view.findViewById(R.id.searchEdit);
        searchCount = view.findViewById(R.id.products_search_count);
        questionsCount = view.findViewById(R.id.questionsCount);
        boothCount = view.findViewById(R.id.booths_Count);
        peopleCount = view.findViewById(R.id.peopleCount);
        noProductsText = view.findViewById(R.id.noProductsText);
        noQuestionsText = view.findViewById(R.id.noQuestionsText);
        noBoothsText = view.findViewById(R.id.noBoothsText);
        noUsersText = view.findViewById(R.id.noUsersText);
        ask_question = view.findViewById(R.id.ask_question);
        searchBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_comment:
                startActivity(new Intent(getActivity(), InboxActivity.class));
                break;
            case R.id.header_cart:
                startActivity(new Intent(getActivity(), ShoppingCartActivity.class));
                break;
            case R.id.header_more:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.searchBtn:
                if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                    switch (searchType) {
                        case "Products":
                            key_value_for_order = "products.ProductPrice";
                            searchProductsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                            break;
                        case "Questions":
                            searchQuestionsFunction(searchType, searchEdit.getText().toString());
                            break;
                        case "Booths":
                            key_value_for_order = "users_text.BoothName";
                            searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                            break;
                        case "People":
                            key_value_for_order = "users_text.FullName";
                            searchAccountsFunction(searchType, searchEdit.getText().toString(), key_value_for_order);
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_filters:
                if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                    showFiltersDialoge();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showFiltersDialoge() {
        filtersDialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
        filtersDialog.setContentView(R.layout.search_filter_dialog);
        Button cancel, apply;
        category = filtersDialog.findViewById(R.id.category);
        subcategory = filtersDialog.findViewById(R.id.subcategory);
        subsubcategory = filtersDialog.findViewById(R.id.item);
        sortby = filtersDialog.findViewById(R.id.sortby);
        city = filtersDialog.findViewById(R.id.city);
        cancel = filtersDialog.findViewById(R.id.btn_cancel);
        apply = filtersDialog.findViewById(R.id.btn_apply);

        switch (searchType) {
            case "Products":
                category.setVisibility(View.VISIBLE);
                subcategory.setVisibility(View.VISIBLE);
                subsubcategory.setVisibility(View.VISIBLE);
                sortby.setVisibility(View.VISIBLE);
                city.setVisibility(View.GONE);
                break;
            case "Booths":
                category.setVisibility(View.VISIBLE);
                subcategory.setVisibility(View.VISIBLE);
                subsubcategory.setVisibility(View.VISIBLE);
                sortby.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);
                break;
            case "People":
                category.setVisibility(View.VISIBLE);
                subcategory.setVisibility(View.VISIBLE);
                subsubcategory.setVisibility(View.GONE);
                sortby.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);
                category.setHint(getResources().getString(R.string.first_interests));
                subcategory.setHint(getResources().getString(R.string.second_interests));
                break;
        }

        filtersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        filtersDialog.show();
        CategoryID = "";
        CityID = "";

        String ORDER[] = {getResources().getString(R.string.ascending), getResources().getString(R.string.descending)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, ORDER);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sortby.setAdapter(adapter);

        getGoriesList();
      //  getCategoriesApi();
        getCities();
        selectsubsubcategoriesClickListener();
        selectsortbyClickListener();
        selectcityClickListener();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtersDialog.dismiss();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "mmm, working on it!", Toast.LENGTH_SHORT).show();
                String sorteas = "DESC";
                if (sortby.getText().toString().equals("Descending")) {
                    sorteas = "DESC";
                } else if (sortby.getText().toString().equals("Ascending")) {
                    sorteas = "ASC";
                }
                Log.e("sort", searchType + " , " + searchEdit.getText().toString() + " , " + key_value_for_order + " , " + sorteas + " , " + CategoryID + " , " + CityID);
                if (searchType.equals("Booths")) {
                    key_value_for_order = "users_text.BoothName";
                    img_list.setVisibility(View.VISIBLE);
                    products_layout.setVisibility(View.GONE);
                    questions_layout.setVisibility(View.GONE);
                    booths_layout.setVisibility(View.VISIBLE);
                    people_layout.setVisibility(View.GONE);
                    if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                        getFiltersResultApiCall(searchType, searchEdit.getText().toString(), key_value_for_order, sorteas, CategoryID, CityID);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        filtersDialog.dismiss();
                    }
                } else if (searchType.equals("People")) {
                    key_value_for_order = "users_text.FullName";
                    img_list.setVisibility(View.VISIBLE);
                    products_layout.setVisibility(View.GONE);
                    questions_layout.setVisibility(View.GONE);
                    booths_layout.setVisibility(View.GONE);
                    people_layout.setVisibility(View.VISIBLE);

                    if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                        getFiltersResultApiCall(searchType, searchEdit.getText().toString(), key_value_for_order, sorteas, CategoryID, CityID);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        filtersDialog.dismiss();
                    }

                } else if (searchType.equals("Products")) {
                    key_value_for_order = "products.ProductPrice";
                    img_list.setVisibility(View.VISIBLE);
                    products_layout.setVisibility(View.VISIBLE);
                    questions_layout.setVisibility(View.GONE);
                    booths_layout.setVisibility(View.GONE);
                    people_layout.setVisibility(View.GONE);

                    if (!searchEdit.getText().toString().isEmpty() && !searchEdit.getText().toString().trim().equals("")) {
                        getFiltersResultApiCallForProducts(searchType, searchEdit.getText().toString(), key_value_for_order, sorteas, CategoryID, CityID);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.writsomething), Toast.LENGTH_SHORT).show();
                        filtersDialog.dismiss();
                    }
                }
            }
        });
    }

    // complete categories dropdown
    private void selectcategoriesClickListener() {

        ArrayAdapter<String> mainCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mainCategoriesNames);
        mainCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        category.setAdapter(mainCateAutoCompAdapter);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
//                hideKeyboard(category);
                category.showDropDown();
            }
        });

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CategoryID = mainCategoryData.get(position).getCategoryID();
                    subcategory.setText("");
                    subsubcategory.setText("");
                    mainPosition = position;
                    subCategoriesNames.clear();
                    for (int i = 0; i < mainCategoryData.get(position).getSubCategoriesData().size(); i++) {
                        subCategoriesNames.add(mainCategoryData.get(position).getSubCategoriesData().get(i).getTitle());
                    }

                    selectsubcategoriesClickListener();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // complete subcategories dropdown
    private void selectsubcategoriesClickListener() {

        ArrayAdapter<String> subCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, subCategoriesNames);
        subCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subcategory.setAdapter(subCateAutoCompAdapter);

        subcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
//                hideKeyboard(subcategory);
                subcategory.showDropDown();
            }
        });

        subcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CategoryID = mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getCategoryID();
                    subsubcategory.setText("");
                    subSubCategoriesNames.clear();
                    for (int i = 0; i < mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().size(); i++) {
                        subSubCategoriesNames.add(mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().get(i).getTitle());
                    }

                    subPosition = position;

                    selectsubsubcategoriesClickListener();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // complete subsubcategories dropdown
    private void selectsubsubcategoriesClickListener() {

        ArrayAdapter<String> subSubCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, subSubCategoriesNames);
        subSubCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subsubcategory.setAdapter(subSubCateAutoCompAdapter);

        subsubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
//                hideKeyboard(subsubcategory);
                subsubcategory.showDropDown();
            }
        });

        subsubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                subsubCategoryID = mainCategoryData.get(mainPosition).getSubCategoriesData().get(subPosition).getSubSubCategoriesData().get(position).getCategoryID();
                CategoryID = mainCategoryData.get(mainPosition).getSubCategoriesData().get(subPosition).getSubSubCategoriesData().get(position).getCategoryID();
                Log.e("categoryID", subsubCategoryID);
            }
        });
    }

    // complete sortby dropdown
    private void selectsortbyClickListener() {
        sortby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
//                hideKeyboard(sortby);
                sortby.showDropDown();
            }
        });

        sortby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    Log.e("sort", SORTBY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // complete cities dropdown
    private void selectcityClickListener() {
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
//                hideKeyboard(city);
                city.showDropDown();
            }
        });

        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CityID = citiesData.get(position).getCityID();
//                    mEditor.putString("CityID", CityID).commit();
//                    mEditor.putString("City", citiesDataArrayList.get(position).getTitle()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void getFiltersResultApiCallForProducts(String type, String keyword, String sorting, String sortas, String catid, String citid) {
        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("Type", type);
        body.put("Keyword", keyword);
        body.put("SortBy", sorting);
        body.put("SortAs", sortas);
        if (!catid.isEmpty()) {
            body.put("CategoryID", catid);
        }
        if (!citid.isEmpty()) {
            body.put("CityID", citid);
        }
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("sort", body.toString());

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        Log.e("sort", header.toString());

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.search, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("SearchProdResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            productsData.clear();
                            CustomLoader.dialog.dismiss();
                            filtersDialog.dismiss();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String CategoryID = jsonObject1.getString("CategoryID");
                                String CategoryName = jsonObject1.getString("CategoryName");
                                String CityName = jsonObject1.getString("CityName");
//                                String CommentCount = jsonObject1.getString("CommentCount");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String Currency = jsonObject1.getString("Currency");
                                String CurrencySymbol = jsonObject1.getString("CurrencySymbol");
                                String DeliveryTime = jsonObject1.getString("DeliveryTime");
//                                String LikesCount = jsonObject1.getString("LikesCount");
                                String OutOfStock = jsonObject1.getString("OutOfStock");
                                String ProductDescription = jsonObject1.getString("ProductDescription");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductPrice = jsonObject1.getString("ProductPrice");
                                String ProductType = jsonObject1.getString("ProductType");
                                String ProductVideo = jsonObject1.getString("ProductVideo");
                                String ProductVideoThumbnail = jsonObject1.getString("ProductVideoThumbnail");
                                String Title = jsonObject1.getString("Title");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String SubCategoryName = jsonObject1.getString("SubCategoryName");
                                String SubSubCategoryName = jsonObject1.getString("SubSubCategoryName");
                                String ProductBrandName = jsonObject1.getString("ProductBrandName");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
//                                String IsLiked = jsonObject1.getString("IsLiked");

//                                mEditor.putString("ProductID",ProductID).commit();

                                productImagesData = new ArrayList<>();
                                JSONArray productImages = jsonObject1.getJSONArray("ProductImages");
                                for (int j = 0; j < productImages.length(); j++) {
                                    JSONObject imagesObj = productImages.getJSONObject(j);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                productsData.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, "", CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        "", OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, "", BoothUserName, ""));
                            }

                            searchCount.setText(productsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                            if (productsData.size() == 0) {
                                noProductsText.setVisibility(View.VISIBLE);
                            } else {
                                noProductsText.setVisibility(View.GONE);
                            }

                            layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            products_recyclerView.setLayoutManager(layoutManager);
                            SearchProductsAdapter searchProductsAdapter = new SearchProductsAdapter(getActivity(), productsData, new SearchProductsAdapter.ItemClick() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            products_recyclerView.setAdapter(searchProductsAdapter);

                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFiltersResultApiCall(String type, String keyword, String sorting, String sortas, String catid, String citid) {

        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("Type", type);
        body.put("Keyword", keyword);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("SortBy", sorting);
        body.put("SortAs", sortas);
        if (!catid.isEmpty()) {
            body.put("CategoryID", catid);
        }
        if (!citid.isEmpty()) {
            body.put("CityID", citid);
        }

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.search, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("SearchAccountsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            accountsData.clear();
                            CustomLoader.dialog.dismiss();
                            filtersDialog.dismiss();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String About = jsonObject1.getString("About");
                                String AppStatus = jsonObject1.getString("AppStatus");
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CityTitle = jsonObject1.getString("CityTitle");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String CountryTitle = jsonObject1.getString("CountryTitle");
                                String Email = jsonObject1.getString("Email");
                                String FullName = jsonObject1.getString("FullName");
                                String Gender = jsonObject1.getString("Gender");
                                String Image = jsonObject1.getString("Image");
                                String IsEmailVerified = jsonObject1.getString("IsEmailVerified");
                                String IsMobileVerified = jsonObject1.getString("IsMobileVerified");
                                String IsProfileCustomized = jsonObject1.getString("IsProfileCustomized");
                                String Mobile = jsonObject1.getString("Mobile");
                                String OnlineStatus = jsonObject1.getString("OnlineStatus");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String SelectedCategories = jsonObject1.getString("SelectedCategories");

//
                                accountsData.add(new AccountsData(About, AppStatus, BoothImage, BoothName, BoothType, BoothUserName, CityTitle, CompressedBoothImage, CompressedImage,
                                        CountryTitle, Email, FullName, Gender, Image, IsEmailVerified, IsMobileVerified, IsProfileCustomized, Mobile, OnlineStatus, UserID, UserName, SelectedCategories));
                            }

                            if (searchType.equals("Booths")) {
                                boothCount.setText(accountsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                                if (accountsData.size() == 0) {
                                    noBoothsText.setVisibility(View.VISIBLE);
                                } else {
                                    noBoothsText.setVisibility(View.GONE);
                                }
                                layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                booths_recyclerView.setLayoutManager(layoutManager);
                                SearchBoothAdapter searchBoothAdapter = new SearchBoothAdapter(getActivity(), accountsData, new SearchBoothAdapter.ItemClick() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                    }
                                });
                                booths_recyclerView.setAdapter(searchBoothAdapter);
                            } else if (searchType.equals("People")) {
                                peopleCount.setText(accountsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                                if (accountsData.size() == 0) {
                                    noUsersText.setVisibility(View.VISIBLE);
                                } else {
                                    noUsersText.setVisibility(View.GONE);
                                }
                                layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                people_recyclerview.setLayoutManager(layoutManager);
                                SearchPeopleAdapter searchPeopleAdapter = new SearchPeopleAdapter(getActivity(), accountsData, new SearchPeopleAdapter.ItemClick() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                    }
                                });
                                people_recyclerview.setAdapter(searchPeopleAdapter);
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCitiesResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            citiesNames.clear();
                            citiesData.clear();
                            JSONArray citiesArray = jsonObject.getJSONArray("cities");
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject jsonObject1 = citiesArray.getJSONObject(i);

                                String CityID = jsonObject1.getString("CityID");
                                String CityLat = jsonObject1.getString("CityLat");
                                String CityLong = jsonObject1.getString("CityLong");
                                String CityPlaceID = jsonObject1.getString("CityPlaceID");
                                String Title = jsonObject1.getString("Title");
                                citiesNames.add(Title);
                                citiesData.add(new CitiesData(CityID, CityLat, CityLong, CityPlaceID, Title));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_list_item_1, citiesNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            city.setAdapter(adapter);


                        } else {
                            Toast.makeText(getActivity(), String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void searchProductsFunction(String type, String keyword, String sorting) {
        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("Type", type);
        body.put("Keyword", keyword);
        body.put("SortBy", sorting);

        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.search, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("SearchProdResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            productsData.clear();
                            CustomLoader.dialog.dismiss();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String CategoryID = jsonObject1.getString("CategoryID");
                                String CategoryName = jsonObject1.getString("CategoryName");
                                String CityName = jsonObject1.getString("CityName");
//                                String CommentCount = jsonObject1.getString("CommentCount");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String Currency = jsonObject1.getString("Currency");
                                String CurrencySymbol = jsonObject1.getString("CurrencySymbol");
                                String DeliveryTime = jsonObject1.getString("DeliveryTime");
//                                String LikesCount = jsonObject1.getString("LikesCount");
                                String OutOfStock = jsonObject1.getString("OutOfStock");
                                String ProductDescription = jsonObject1.getString("ProductDescription");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductPrice = jsonObject1.getString("ProductPrice");
                                String ProductType = jsonObject1.getString("ProductType");
                                String ProductVideo = jsonObject1.getString("ProductVideo");
                                String ProductVideoThumbnail = jsonObject1.getString("ProductVideoThumbnail");
                                String Title = jsonObject1.getString("Title");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String SubCategoryName = jsonObject1.getString("SubCategoryName");
                                String SubSubCategoryName = jsonObject1.getString("SubSubCategoryName");
                                String ProductBrandName = jsonObject1.getString("ProductBrandName");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
//                                String IsLiked = jsonObject1.getString("IsLiked");

//                                mEditor.putString("ProductID",ProductID).commit();

                                productImagesData = new ArrayList<>();
                                JSONArray productImages = jsonObject1.getJSONArray("ProductImages");
                                for (int j = 0; j < productImages.length(); j++) {
                                    JSONObject imagesObj = productImages.getJSONObject(j);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                productsData.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, "", CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        "", OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, "", BoothUserName, ""));
                            }

                            searchCount.setText(productsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                            if (productsData.size() == 0) {
                                noProductsText.setVisibility(View.VISIBLE);
                            } else {
                                noProductsText.setVisibility(View.GONE);
                            }

                            layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            products_recyclerView.setLayoutManager(layoutManager);
                            SearchProductsAdapter searchProductsAdapter = new SearchProductsAdapter(getActivity(), productsData, new SearchProductsAdapter.ItemClick() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            products_recyclerView.setAdapter(searchProductsAdapter);

                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchQuestionsFunction(String type, String keyword) {
        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("Type", type);
        body.put("Keyword", keyword);

        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.search, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("SearchQuesResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            searchQuestionsData.clear();
                            CustomLoader.dialog.dismiss();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CategoryID = jsonObject1.getString("CategoryID");
                                String CategoryName = jsonObject1.getString("CategoryName");
                                String FullName = jsonObject1.getString("FullName");
                                String QuestionAskedAt = jsonObject1.getString("QuestionAskedAt");
                                String QuestionDescription = jsonObject1.getString("QuestionDescription");
                                String QuestionID = jsonObject1.getString("QuestionID");
                                String SubCategoryName = jsonObject1.getString("SubCategoryName");
                                String UserCityName = jsonObject1.getString("UserCityName");
                                String UserID = jsonObject1.getString("UserID");
                                String UserImage = jsonObject1.getString("UserImage");
                                String UserName = jsonObject1.getString("UserName");
                                String CommentCount = jsonObject1.getString("CommentCount");
//
                                questionImages = new ArrayList<>();
                                JSONArray questionImage = jsonObject1.getJSONArray("QuestionImages");
                                for (int j = 0; j < questionImage.length(); j++) {
                                    JSONObject imagesObj = questionImage.getJSONObject(j);
                                    String CompressedImage = imagesObj.getString("CompressedImage");
                                    String Image = imagesObj.getString("Image");

                                    questionImages.add(new ProductImagesData(CompressedImage, Image));
                                }

                                searchQuestionsData.add(new SearchQuestionsData(BoothName, BoothUserName, CategoryID, CategoryName, FullName, QuestionAskedAt, QuestionDescription, QuestionID, SubCategoryName, UserCityName,
                                        UserID, UserImage, UserName, CommentCount, questionImages));
                            }

                            questionsCount.setText(searchQuestionsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                            if (searchQuestionsData.size() == 0) {
                                noQuestionsText.setVisibility(View.VISIBLE);
                            } else {
                                noQuestionsText.setVisibility(View.GONE);
                            }

                            layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            questions_recyclerView.setLayoutManager(layoutManager);
                            SearchQuestionsAdapter searchQuestionsAdapter = new SearchQuestionsAdapter(getActivity(), searchQuestionsData, new SearchQuestionsAdapter.ItemClick() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            questions_recyclerView.setAdapter(searchQuestionsAdapter);

                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchAccountsFunction(String type, String keyword, String sorting) {
        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("Type", type);
        body.put("Keyword", keyword);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("SortBy", sorting);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.search, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("SearchAccountsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            accountsData.clear();
                            CustomLoader.dialog.dismiss();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String About = jsonObject1.getString("About");
                                String AppStatus = jsonObject1.getString("AppStatus");
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CityTitle = jsonObject1.getString("CityTitle");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String CountryTitle = jsonObject1.getString("CountryTitle");
                                String Email = jsonObject1.getString("Email");
                                String FullName = jsonObject1.getString("FullName");
                                String Gender = jsonObject1.getString("Gender");
                                String Image = jsonObject1.getString("Image");
                                String IsEmailVerified = jsonObject1.getString("IsEmailVerified");
                                String IsMobileVerified = jsonObject1.getString("IsMobileVerified");
                                String IsProfileCustomized = jsonObject1.getString("IsProfileCustomized");
                                String Mobile = jsonObject1.getString("Mobile");
                                String OnlineStatus = jsonObject1.getString("OnlineStatus");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String SelectedCategories = jsonObject1.getString("SelectedCategories");

//
                                accountsData.add(new AccountsData(About, AppStatus, BoothImage, BoothName, BoothType, BoothUserName, CityTitle, CompressedBoothImage, CompressedImage,
                                        CountryTitle, Email, FullName, Gender, Image, IsEmailVerified, IsMobileVerified, IsProfileCustomized, Mobile, OnlineStatus, UserID, UserName, SelectedCategories));
                            }

                            if (searchType.equals("Booths")) {
                                boothCount.setText(accountsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                                if (accountsData.size() == 0) {
                                    noBoothsText.setVisibility(View.VISIBLE);
                                } else {
                                    noBoothsText.setVisibility(View.GONE);
                                }
                                layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                booths_recyclerView.setLayoutManager(layoutManager);
                                SearchBoothAdapter searchBoothAdapter = new SearchBoothAdapter(getActivity(), accountsData, new SearchBoothAdapter.ItemClick() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                    }
                                });
                                booths_recyclerView.setAdapter(searchBoothAdapter);
                            } else if (searchType.equals("People")) {
                                peopleCount.setText(accountsData.size() + " " + getActivity().getResources().getString(R.string.searchResults));
                                if (accountsData.size() == 0) {
                                    noUsersText.setVisibility(View.VISIBLE);
                                } else {
                                    noUsersText.setVisibility(View.GONE);
                                }
                                layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                people_recyclerview.setLayoutManager(layoutManager);
                                SearchPeopleAdapter searchPeopleAdapter = new SearchPeopleAdapter(getActivity(), accountsData, new SearchPeopleAdapter.ItemClick() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                    }
                                });
                                people_recyclerview.setAdapter(searchPeopleAdapter);
                            }


                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void productsForGrid() {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getProductsForSearchGrid, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GridProductsResp", result);
                    Log.e("GridProductsURL", Constants.URL.getProductsForSearchGrid);
                    Log.e("GridProductsBody", body + "    " + header);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            productsDataGrid.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
                                String ItemType = productObj.getString("ItemType");
                                if (ItemType.equals("Product")) {
                                    String BoothImage = productObj.getString("BoothImage");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CityName = productObj.getString("CityName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                    String Currency = productObj.getString("Currency");
                                    String CurrencySymbol = productObj.getString("CurrencySymbol");
                                    String DeliveryTime = productObj.getString("DeliveryTime");
                                    String LikesCount = productObj.getString("LikesCount");
                                    String OutOfStock = productObj.getString("OutOfStock");
                                    String ProductDescription = productObj.getString("ProductDescription");
                                    String ProductID = productObj.getString("ProductID");
                                    String ProductPrice = productObj.getString("ProductPrice");
                                    String ProductType = productObj.getString("ProductType");
                                    String ProductVideo = productObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                    String Title = productObj.getString("Title");
                                    String UserID = productObj.getString("UserID");
                                    String UserName = productObj.getString("UserName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                    String ProductBrandName = productObj.getString("ProductBrandName");
                                    String CreatedAt = productObj.getString("CreatedAt");
                                    String IsLiked = productObj.getString("IsLiked");
                                    String BoothUserName = productObj.getString("BoothUserName");

                                    productImagesData = new ArrayList<>();
                                    JSONArray productImages = productObj.getJSONArray("ProductImages");
                                    for (int j = 0; j < productImages.length(); j++) {
                                        JSONObject imagesObj = productImages.getJSONObject(j);
                                        String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                        String ProductImage = imagesObj.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    productsDataGrid.add(new ProductsData("", ItemType, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                            LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, ""));

                                }
                            }

                            grid_recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            SearchByDefaultAdapter searchByDefaultAdapter = new SearchByDefaultAdapter(getContext(), productsDataGrid, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                }
                            });
                            grid_recycler.setAdapter(searchByDefaultAdapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCategoriesApi() {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("UserID", sharedPreferences.getString("UserID", ""));
        postParams.put("Type", sharedPreferences.getString("LastState", ""));
        postParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        postParams.put("OS", String.valueOf(Build.VERSION.RELEASE));

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETCATEGORIES, getActivity(), postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            mainCategoriesNames.clear();

                            JSONArray categories = jsonObject.getJSONArray("user_categories");

                            mainCategoryData = new ArrayList<>();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject mainCateObject = categories.getJSONObject(i);
                                String CategoryID = mainCateObject.getString("CategoryID");
                                String Image = mainCateObject.getString("Image");
                                String Title = mainCateObject.getString("Title");

                                JSONArray subCateArray = mainCateObject.getJSONArray("SubCategories");
                                subCategoriesData = new ArrayList<>();
                                for (int j = 0; j < subCateArray.length(); j++) {
                                    JSONObject subCateObject = subCateArray.getJSONObject(j);
                                    String CategoryID2 = subCateObject.getString("CategoryID");
                                    String Image2 = subCateObject.getString("Image");
                                    String Title2 = subCateObject.getString("Title");

                                    JSONArray subSubCateArray = subCateObject.getJSONArray("SubSubCategories");
                                    subSubCategoriesData = new ArrayList<>();
                                    for (int k = 0; k < subSubCateArray.length(); k++) {
                                        JSONObject subSubCateObject = subSubCateArray.getJSONObject(k);
                                        String CategoryID3 = subSubCateObject.getString("CategoryID");
                                        String Image3 = subSubCateObject.getString("Image");
                                        String Title3 = subSubCateObject.getString("Title");
                                        subSubCategoriesData.add(new SubSubCategoriesData(CategoryID3, Image3, Title3));
                                    }
                                    subCategoriesData.add(new SubCategoriesData(CategoryID2, Image2, Title2, subSubCategoriesData));
                                }
                                mainCategoryData.add(new MainCategoriesData(CategoryID, Image, Title, subCategoriesData));
                                mainCategoriesNames.add(Title);
                            }

                            selectcategoriesClickListener();

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                editor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getGoriesList() {

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        Map<String, String> postParams = new HashMap<>();

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.categories + "AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            mainCategoriesNames.clear();

                            JSONArray categories = jsonObject.getJSONArray("categories");

                            mainCategoryData = new ArrayList<>();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject mainCateObject = categories.getJSONObject(i);
                                String CategoryID = mainCateObject.getString("CategoryID");
                                String Image = mainCateObject.getString("Image");
                                String Title = mainCateObject.getString("Title");

                                JSONArray subCateArray = mainCateObject.getJSONArray("SubCategories");
                                subCategoriesData = new ArrayList<>();
                                for (int j = 0; j < subCateArray.length(); j++) {
                                    JSONObject subCateObject = subCateArray.getJSONObject(j);
                                    String CategoryID2 = subCateObject.getString("CategoryID");
                                    String Image2 = subCateObject.getString("Image");
                                    String Title2 = subCateObject.getString("Title");

                                    JSONArray subSubCateArray = subCateObject.getJSONArray("SubSubCategories");
                                    subSubCategoriesData = new ArrayList<>();
                                    for (int k = 0; k < subSubCateArray.length(); k++) {
                                        JSONObject subSubCateObject = subSubCateArray.getJSONObject(k);
                                        String CategoryID3 = subSubCateObject.getString("CategoryID");
                                        String Image3 = subSubCateObject.getString("Image");
                                        String Title3 = subSubCateObject.getString("Title");
                                        subSubCategoriesData.add(new SubSubCategoriesData(CategoryID3, Image3, Title3));
                                    }
                                    subCategoriesData.add(new SubCategoriesData(CategoryID2, Image2, Title2, subSubCategoriesData));
                                }
                                mainCategoryData.add(new MainCategoriesData(CategoryID, Image, Title, subCategoriesData));
                                mainCategoriesNames.add(Title);
                            }

                            selectcategoriesClickListener();

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                editor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
