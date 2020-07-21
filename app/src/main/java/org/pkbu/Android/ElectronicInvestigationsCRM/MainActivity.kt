package org.pkbu.Android.ElectronicInvestigationsCRM

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import org.pkbu.Android.ElectronicInvestigationsCRM.model.User
import org.pkbu.Android.ElectronicInvestigationsCRM.ui.SectionPagerAdapter
import org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel.MainViewModel

class MainActivity : CaseFileList.CaseFileListListener,
    UserEvidenceItemsList.UserEvidenceItemsListener, CaseEvidenceItemsList.CaseEvidenceItemsListener,
    CaseFileCreateDialog.CaseFileCreateDialogListener, CaseFileEdit.CaseFileEditListener,
    EvidenceItemEdit.EvidenceItemEditListener, EvidenceItemCreateDialog.EvidenceItemCreateDialogListener,
    UserInfoDialog.UserInfoDialogListener, EvidenceSearchDialog.EvidenceSearchDialogListener,
    EvidenceSearchResultsList.EvidenceSearchResultsListener,
    AppCompatActivity() {

    private lateinit var mViewModel: MainViewModel
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var fragManager: FragmentManager = supportFragmentManager
    private var pagerAdapter: SectionPagerAdapter? = null
    private var viewPager: ViewPager2? = null
    private var tabs: TabLayout? = null
    private var deleteIcon: MenuItem? = null

    private var drawerLayout: DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null

    private val caseFileList: CaseFileList = CaseFileList.newInstance()
    private val userEvidenceItemsList: UserEvidenceItemsList = UserEvidenceItemsList.newInstance()
    private val caseEvidenceItemsList: CaseEvidenceItemsList = CaseEvidenceItemsList.newInstance()
    private val evidenceSearchResultsList: EvidenceSearchResultsList = EvidenceSearchResultsList.newInstance()
    private val caseFileEdit: CaseFileEdit = CaseFileEdit.newInstance()
    private val evidenceItemEdit: EvidenceItemEdit = EvidenceItemEdit.newInstance()
    private val userInfoDialog: UserInfoDialog = UserInfoDialog.newInstance()
    private val evidenceSearchDialog: EvidenceSearchDialog = EvidenceSearchDialog.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel =
            ViewModelProviders.of(this).get(MainViewModel::class.java)

        // Firebase Authentication
        mAuth = mViewModel.mAuth
        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Setup fragments //////////////
        pagerAdapter = SectionPagerAdapter(fragManager, this)

        pagerAdapter!!.addScreen(caseFileList, CASE_FILE_LIST_TITLE, pagerAdapter!!.itemCount)
        pagerAdapter!!.addScreen(userEvidenceItemsList, USER_EVIDENCE_ITEM_LIST_TITLE,
            pagerAdapter!!.itemCount)

        viewPager = findViewById(R.id.view_pager)
        viewPager!!.adapter = pagerAdapter
        viewPager!!.offscreenPageLimit = 1
        viewPager!!.requestDisallowInterceptTouchEvent(true)

        tabs = findViewById(R.id.tabs)
        TabLayoutMediator(tabs!!, viewPager!!) { tab, position ->
            tab.text = pagerAdapter!!.getPageTitle(position)
        }.attach()

        // Setup Floating Action Button onClickListener to invoke proper methods: //////////////
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            var currentScreen = pagerAdapter!!.getPageTitle(tabs!!.selectedTabPosition)

            if (currentScreen == CASE_FILE_LIST_TITLE) {
                if (CASE_FILE_EDIT_TITLE in pagerAdapter!!.fragmentTitleList) {
                    Toast.makeText(this, "Finish editing Case File before " +
                            "beginning with another. ", Toast.LENGTH_SHORT).show()
                } else {
                    caseFileList.onClickAdd(fragManager)
                }
            } else if (currentScreen == CASE_EVIDENCE_ITEM_LIST_TITLE){
                if (EVIDENCE_ITEM_EDIT_TITLE in pagerAdapter!!.fragmentTitleList) {
                    Toast.makeText(this, "Finish editing Evidence Item before " +
                            "beginning with another. ", Toast.LENGTH_SHORT).show()
                } else {
                    caseEvidenceItemsList.onClickAdd(fragManager)
                }
            } else if (currentScreen == CASE_FILE_EDIT_TITLE) {
                // save case
                caseFileEdit.saveCaseData()
            } else if (currentScreen == EVIDENCE_ITEM_EDIT_TITLE) {
                // save evidence item
                evidenceItemEdit.saveEvidenceItemData()
            }
        }

        val bottomAppBar: BottomAppBar = findViewById(R.id.bottomAppBar)
        val bottomAppBarBehavior = bottomAppBar.behavior
        setSupportActionBar(bottomAppBar)

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, bottomAppBar,
            R.string.drawerOpen, R.string.drawerClose)
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle!!.drawerArrowDrawable.color = resources.getColor(R.color.colorWhite)
        actionBarDrawerToggle!!.syncState()

        // Listen to tabs and push bottom app bar back up if tab is changed //////////////
        // Listen to tabs and select appropriate FAB icon for selected screen //////////////
        tabs!!.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Pop up bottom app bar:
                bottomAppBarBehavior.slideUp(bottomAppBar)
                dismissKeyboard()

                var currentScreen = pagerAdapter!!.getPageTitle(tabs!!.selectedTabPosition)

                // Change FAB icon to appropriate logo descriptive of use:
                if (currentScreen == CASE_FILE_LIST_TITLE) {
                    deleteIcon!!.isVisible = false
                    fab.hide()
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_add));
                    fab.show()
                } else if (currentScreen == CASE_FILE_EDIT_TITLE){
                    deleteIcon!!.isVisible = true
                    fab.hide()
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_save));
                    fab.show()
                } else if (currentScreen == USER_EVIDENCE_ITEM_LIST_TITLE
                    || currentScreen == SEARCH_RESULTS_LIST_TITLE) {
                    deleteIcon!!.isVisible = false
                    fab.hide()
                } else if (currentScreen == CASE_EVIDENCE_ITEM_LIST_TITLE) {
                    deleteIcon!!.isVisible = false
                    fab.hide()
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_add));
                    fab.show()
                } else if (currentScreen == EVIDENCE_ITEM_EDIT_TITLE) {
                    deleteIcon!!.isVisible = true
                    fab.hide()
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_save));
                    fab.show()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onStart() {
        super.onStart()
        //is user already logged in?  (Firebase persistence across executions)
        checkAuthAndLog()
    }

    override fun onBackPressed() {
        val currentScreen = pagerAdapter!!.getPageTitle(tabs!!.selectedTabPosition)
        when (currentScreen) {
            CASE_FILE_EDIT_TITLE -> closeScreen(CASE_FILE_EDIT_TITLE, CLOSE_AND_GO_HOME)
            EVIDENCE_ITEM_EDIT_TITLE -> closeScreen(EVIDENCE_ITEM_EDIT_TITLE, JUST_CLOSE)
            CASE_EVIDENCE_ITEM_LIST_TITLE -> closeScreen(CASE_EVIDENCE_ITEM_LIST_TITLE,
                CLOSE_AND_GO_HOME)
            SEARCH_RESULTS_LIST_TITLE -> closeScreen(SEARCH_RESULTS_LIST_TITLE,
                CLOSE_AND_GO_HOME)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        deleteIcon = menu!!.findItem(R.id.menuDelete)
        deleteIcon!!.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                openUserInfoDialog()
            }
            R.id.menuDelete -> {
                var currentScreen = pagerAdapter!!.getPageTitle(tabs!!.selectedTabPosition)
                if (currentScreen == CASE_FILE_EDIT_TITLE) {
                    caseFileEdit.deleteCaseFileByCaseId()
                } else if (currentScreen == EVIDENCE_ITEM_EDIT_TITLE) {
                    evidenceItemEdit.deleteEvidenceItemByEvidenceId()
                }
            }
            R.id.menuSearch -> {
                openEvidenceSearchDialog()
            }
            R.id.menuHome -> {
                var newTab: TabLayout.Tab? = tabs!!.getTabAt(0)
                newTab!!.select()
            }
        }
        return true
    }

    private fun checkAuthAndLog() {
        var firestoreUser = mAuth!!.currentUser

        // If user is logged in, then log event in DB and set current user details
        if (firestoreUser != null) {
            mViewModel.getCurrentUser(mAuth!!.currentUser!!.uid)
            mViewModel.currentUserLiveData.observe(this, Observer<User?> {user ->
                mViewModel.logUser(user!!)
                // Refresh customized user evidence list once we've confirmed a user is logged in:
                userEvidenceItemsList.refreshQuery()
                // Make sure permissions are in place for picture selections:
            })
        } else {
            startLogin()
        }
    }

    private fun startLogin() {
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
            .setIsSmartLockEnabled(false)
            .build()
        startActivityForResult(intent, ACCOUNT_SETUP_RESULT)
    }

    private fun checkExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= 27) {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_IMAGE)
                return
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when(requestCode) {
            READ_IMAGE -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission for image uploads granted. ",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Cannot access local storage.  " +
                            "Please check permissions", Toast.LENGTH_SHORT).show()
                }
            } else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadImage(pickImageRequestor: Int) {

        checkExternalStoragePermission()

        var intent = Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*")

        startActivityForResult(intent, pickImageRequestor)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACCOUNT_SETUP_RESULT) {
            val result = IdpResponse.fromResultIntent(data)

            if (resultCode != Activity.RESULT_OK) {
                if (result == null) {
                    finish()
                } else if (result.error != null && result.error!!.errorCode ==
                    ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Error connecting to network",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unknown error creating account",
                        Toast.LENGTH_SHORT).show()
                }
            } else {

                // Save new user to database:
                mViewModel.addBasicUserInfo(User(mAuth!!.currentUser!!))
                checkAuthAndLog()
            }
        }
        if ((requestCode == PICK_IMAGE_FOR_CASE
                    || requestCode == PICK_IMAGE_FOR_EVIDENCE_FRONT
                    || requestCode == PICK_IMAGE_FOR_EVIDENCE_BACK
                    || requestCode == PICK_IMAGE_FOR_EVIDENCE_DETAIL1
                    || requestCode == PICK_IMAGE_FOR_EVIDENCE_DETAIL2
                    || requestCode == PICK_IMAGE_FOR_USER_INFO)
            && resultCode == Activity.RESULT_OK && data != null) {

            val pickedImage = data.data

            if (pickedImage != null) {
                when (requestCode) {
                    PICK_IMAGE_FOR_CASE -> caseFileEdit.setImageForCaseFile(pickedImage)
                    PICK_IMAGE_FOR_EVIDENCE_FRONT -> evidenceItemEdit
                        .setImageForEvidenceItem(pickedImage, PICK_IMAGE_FOR_EVIDENCE_FRONT)
                    PICK_IMAGE_FOR_EVIDENCE_BACK -> evidenceItemEdit
                        .setImageForEvidenceItem(pickedImage, PICK_IMAGE_FOR_EVIDENCE_BACK)
                    PICK_IMAGE_FOR_EVIDENCE_DETAIL1 -> evidenceItemEdit
                        .setImageForEvidenceItem(pickedImage, PICK_IMAGE_FOR_EVIDENCE_DETAIL1)
                    PICK_IMAGE_FOR_EVIDENCE_DETAIL2 -> evidenceItemEdit
                        .setImageForEvidenceItem(pickedImage, PICK_IMAGE_FOR_EVIDENCE_DETAIL2)
                    PICK_IMAGE_FOR_USER_INFO -> userInfoDialog.setImageForUser(pickedImage)
                }
            }
        }
    }

    private fun openUserInfoDialog() {
        val dialog: UserInfoDialog = userInfoDialog
        if (dialog.dialog == null && !dialog.isVisible) {
            dialog.show(supportFragmentManager, "UserInfoDialog")
        }
    }

    private fun openEvidenceSearchDialog() {
        val dialog: EvidenceSearchDialog = evidenceSearchDialog
        if (dialog.dialog == null && !dialog.isVisible) {
            dialog.show(supportFragmentManager, "EvidenceSearchDialog")
        }
    }

    fun dismissKeyboard() {
        val view: View? = this.currentFocus
        val mImm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mImm.hideSoftInputFromWindow(view?.windowToken, 0)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun closeScreen(screenTitle: String, target: String) {

        if (target == CLOSE_AND_GO_HOME) {
            val position = pagerAdapter!!.getPagePosition(screenTitle)
            val newTab: TabLayout.Tab? = tabs!!.getTabAt(0)
            newTab!!.select()
            pagerAdapter!!.removeScreen(position!!)

        } else if (target == JUST_CLOSE) {
            val position = pagerAdapter!!.getPagePosition(screenTitle)
            pagerAdapter!!.removeScreen(position!!)
        }
    }

    private fun openCaseEvidenceItems(caseId: String) {
        // set reference in viewmodel:
        mViewModel.setCaseIdForEvidence(caseId)

        // If case evidence item list already opened, close it before opening another
        if (CASE_EVIDENCE_ITEM_LIST_TITLE in pagerAdapter!!.fragmentTitleList) {
            caseEvidenceItemsList.refreshQuery()
            var position = pagerAdapter!!.getPagePosition(CASE_EVIDENCE_ITEM_LIST_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        } else {
            // create screens in page adapter:
            pagerAdapter!!.addScreen(caseEvidenceItemsList,
                CASE_EVIDENCE_ITEM_LIST_TITLE, pagerAdapter!!.itemCount)

            var position = pagerAdapter!!.getPagePosition(CASE_EVIDENCE_ITEM_LIST_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        }
    }

    private fun openCaseFileEdit(caseId: String) {

        if (CASE_FILE_EDIT_TITLE in pagerAdapter!!.fragmentTitleList) {
            Toast.makeText(this, "Finish editing Case File before " +
                    "beginning another. ", Toast.LENGTH_SHORT).show()
        } else {
            // set reference in viewmodel:
            mViewModel.setCaseIdForEdit(caseId)
            // create screens in page adapter:
            pagerAdapter!!.addScreen(caseFileEdit,
                CASE_FILE_EDIT_TITLE, pagerAdapter!!.itemCount)

            var position = pagerAdapter!!.getPagePosition(CASE_FILE_EDIT_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        }
    }

    // Open Evidence Item Edit screen: ////////////////
    private fun openEvidenceItemEdit(evidenceId: String) {

        if (EVIDENCE_ITEM_EDIT_TITLE in pagerAdapter!!.fragmentTitleList) {
            Toast.makeText(this, "Finish editing Evidence Item before " +
                    "beginning another. ", Toast.LENGTH_SHORT).show()
        } else {
            // set reference in viewmodel:
            mViewModel.setEvidenceId(evidenceId)
            // create screens in page adapter:
            pagerAdapter!!.addScreen(evidenceItemEdit,
                EVIDENCE_ITEM_EDIT_TITLE, pagerAdapter!!.itemCount)

            var position = pagerAdapter!!.getPagePosition(EVIDENCE_ITEM_EDIT_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        }
    }

    // Called when a case button is selected in recycler view:
    // if bool is true, then edit case was selected.
    // if bool is false, then a case evidence inventory was requested
    override fun onCaseFileListListener(caseId: String, editCase: Boolean) {
        if(editCase) {
            openCaseFileEdit(caseId)
        } else {
            openCaseEvidenceItems(caseId)
        }
    }

    // Called when case file edit screen is closed or a picture picker is requested
    override fun onCaseFileEditListener(purpose: String) {
        if (purpose == CLOSE_EDITING) {
            closeScreen(CASE_FILE_EDIT_TITLE, CLOSE_AND_GO_HOME)
        } else if (purpose == SELECT_PICTURE) {
            loadImage(PICK_IMAGE_FOR_CASE)
        }
    }

    // Called when evidence item edit screen is closed or a picture picker is requested
    override fun onEvidenceItemEditListener(purpose: String) {
        when (purpose) {
            CLOSE_EDITING -> closeScreen(EVIDENCE_ITEM_EDIT_TITLE, JUST_CLOSE)
            PICK_IMAGE_FOR_EVIDENCE_FRONT.toString() -> loadImage(PICK_IMAGE_FOR_EVIDENCE_FRONT)
            PICK_IMAGE_FOR_EVIDENCE_BACK.toString() -> loadImage(PICK_IMAGE_FOR_EVIDENCE_BACK)
            PICK_IMAGE_FOR_EVIDENCE_DETAIL1.toString() -> loadImage(PICK_IMAGE_FOR_EVIDENCE_DETAIL1)
            PICK_IMAGE_FOR_EVIDENCE_DETAIL2.toString() -> loadImage(PICK_IMAGE_FOR_EVIDENCE_DETAIL2)
        }
    }

    override fun onUserInfoDialogListener(purpose: String) {
        if (purpose == SELECT_PICTURE) {
            loadImage(PICK_IMAGE_FOR_USER_INFO)
        } else if (purpose == LOG_USER_OUT) {
            AuthUI.getInstance().signOut(this)
            startLogin()
        }
    }

    // Called if user invokes search function from EvidenceSearchDialog
    override fun onEvidenceSearchDialogListener() {
        openSearchResults()
    }

    private fun openSearchResults() {
        // If case search results list already opened, refresh
        if (SEARCH_RESULTS_LIST_TITLE in pagerAdapter!!.fragmentTitleList) {
            evidenceSearchResultsList.refreshQuery()
            var position = pagerAdapter!!.getPagePosition(SEARCH_RESULTS_LIST_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        } else {
            // create screens in page adapter:
            pagerAdapter!!.addScreen(evidenceSearchResultsList,
                SEARCH_RESULTS_LIST_TITLE, pagerAdapter!!.itemCount)

            var position = pagerAdapter!!.getPagePosition(SEARCH_RESULTS_LIST_TITLE)
            var newTab: TabLayout.Tab? = tabs!!.getTabAt(position!!)
            newTab!!.select()
        }
    }

    override fun onUserEvidenceItemsListener(evidenceId: String) {
        openEvidenceItemEdit(evidenceId)
    }
    override fun onCaseEvidenceItemsListener(evidenceId: String) {
        openEvidenceItemEdit(evidenceId)
    }

    override fun onEvidenceSearchResultsListener(evidenceId: String) {
        openEvidenceItemEdit(evidenceId)
    }

    // Called when Case File has been created:
    override fun onCaseFileCreateDialogListener(caseId: String?) {
        openCaseFileEdit(caseId!!)
    }

    // Called when Evidence Item as been created
    override fun onEvidenceItemCreateDialogListener(evidenceId: String?) {
        openEvidenceItemEdit(evidenceId!!)
    }

    companion object {
        private const val CASE_FILE_LIST_TITLE = "Case Files"
        private const val USER_EVIDENCE_ITEM_LIST_TITLE = "My Evidence Items"
        private const val CASE_EVIDENCE_ITEM_LIST_TITLE = "Selected Case Items"
        private const val SEARCH_RESULTS_LIST_TITLE = "Search Results"
        private const val CASE_FILE_EDIT_TITLE = "Edit Case File"
        private const val EVIDENCE_ITEM_EDIT_TITLE = "Edit Evidence Item"
        private const val JUST_CLOSE = "JustClose"
        private const val CLOSE_AND_GO_HOME = "CloseAndHome"
        private const val CLOSE_EDITING = "CloseEditing"
        private const val SELECT_PICTURE = "SelectPicture"
        private const val LOG_USER_OUT = "LogUserOut"
        private const val ACCOUNT_SETUP_RESULT = 3000
        private const val READ_IMAGE = 5001
        private const val PICK_IMAGE_FOR_CASE = 5501
        private const val PICK_IMAGE_FOR_EVIDENCE_FRONT = 5701
        private const val PICK_IMAGE_FOR_EVIDENCE_BACK = 5711
        private const val PICK_IMAGE_FOR_EVIDENCE_DETAIL1 = 5721
        private const val PICK_IMAGE_FOR_EVIDENCE_DETAIL2 = 5731
        private const val PICK_IMAGE_FOR_USER_INFO = 5901
        private const val TAG = "MainActivity"
    }
}
