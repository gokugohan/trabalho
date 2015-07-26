package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentDespesa;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentFecharContas;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentGrupo;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentPessoa;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentSimular;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private int mMenu = 0;
    private SharedPreferences mSharedpreferences;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        // Check if the user is not anonymous user
        // if is the case redirect to the login view
        if(!ParseUtils.isRegisteredUser()){
            Util.Utilities.startActivity(this, ActivityLogin.class,null);
            finish();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        TextView userStatus = (TextView) findViewById(R.id.id_user_status);
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            ParseUser user = ParseUser.getCurrentUser();
            userStatus.setText(user.getUsername().toString());
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {

            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        insertFragmentToViewPager();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveMenuValue(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        saveMenuValue(mMenu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMenu = getMenuValue();
    }

    private void saveMenuValue(int menu_val) {
        mSharedpreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedpreferences.edit();
        editor.putInt("menu", menu_val);
        editor.commit();
    }

    private int getMenuValue() {
        mSharedpreferences = getPreferences(Context.MODE_PRIVATE);
        return mSharedpreferences.getInt("menu", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
            menu.add(0,Menu.FIRST,Menu.NONE,R.string.LogoutBtn);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case Menu.FIRST:
                ParseUser.logOut();
                Util.Utilities.startActivity(this, ActivityLogin.class,null);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertFragmentToViewPager() {
        if (viewPager != null) {
            setupViewPager();
        }

    }


    private void setupViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        int m = getMenuValue();
        Bundle bundle;
        switch (m) {
            case 0://Home
                initFragmentHome(adapter);
                mToolbar.setTitle(getString(R.string.home));
                break;
            case 1://Consultar

                initFragmentConsultar(adapter);
                mToolbar.setTitle(getString(R.string.app_name_consultar_despesa));
                break;
            case 2://Alterar
                initFragmentAlterar(adapter);
                mToolbar.setTitle(getString(R.string.app_name_alterar));

                break;
            case 3:
                initFragmentSimular(adapter);
                mToolbar.setTitle(getString(R.string.app_name_simular_contas));
                break;
            case 4:
                initFragmentFecharContas(adapter);
                mToolbar.setTitle(getString(R.string.app_name_fechar_contas));

                break;
        }


        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragmentFecharContas(Adapter adapter) {
        Bundle bundle;
        bundle = new Bundle();
        bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.FECHAR);
        Fragment fragment = new FragmentFecharContas();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_name_fechar_contas), 4);
    }

    private void initFragmentSimular(Adapter adapter) {
        Bundle bundle;
        bundle = new Bundle();
        bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.SIMULAR);
        Fragment fragment = new FragmentSimular();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_name_simular_contas), 3);
    }

    private void initFragmentAlterar(Adapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.ALTERAR);
        Fragment fragment = new FragmentDespesa();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_fragment_title_despesas), 2);

        fragment = new FragmentGrupo();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_fragment_title_grupos), 2);

        fragment = new FragmentPessoa();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_fragment_title_pessoas), 2);
    }

    private void initFragmentConsultar(Adapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.CONSULTAR);
        Fragment fragment = new FragmentDespesa();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_name_consultar_despesa_todas), 1);

        fragment = new FragmentGrupo();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_name_consultar_despesa_por_grupo), 1);

        fragment = new FragmentPessoa();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, getString(R.string.app_name_consultar_despesa_por_pessoas), 1);

    }

    private void initFragmentHome(Adapter adapter) {
        Fragment fragment;
        fragment = new FragmentGrupo();
        adapter.addFragment(fragment, getString(R.string.app_fragment_title_grupos), 0);
        fragment = new FragmentPessoa();
        adapter.addFragment(fragment, getString(R.string.app_fragment_title_pessoas), 0);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //boolean isChecked = menuItem.isChecked();
                        //menuItem.setChecked(!isChecked);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                saveMenuValue(0);
                                insertFragmentToViewPager();
                                break;
                            case R.id.nav_recolha_despesa:
                                //Util.Utilities.startActivity(MainActivity.this, ActivityDespesaNovo.class, null);
                                Util.Utilities.startActivityForResult(MainActivity.this,ActivityDespesaNovo.class,123,null);
                                break;
                            case R.id.nav_consultar_despesa:
                                saveMenuValue(1);
                                insertFragmentToViewPager();
                                break;
                            case R.id.nav_alterar_despesa:
                                saveMenuValue(2);
                                insertFragmentToViewPager();
                                break;
                            case R.id.nav_simular:
                                saveMenuValue(3);
                                insertFragmentToViewPager();
                                break;
                            case R.id.nav_fechar_contas:
                                saveMenuValue(4);
                                insertFragmentToViewPager();
                        }
                        return true;
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public static class Adapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;
        private List<String> mFragmentTitles;
        private FragmentManager fm;

        public Adapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
            mFragments = new ArrayList<>();
            mFragmentTitles = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title, int option) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (position <= getCount()) {
                FragmentTransaction trans = fm.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }
        }
    }

}
