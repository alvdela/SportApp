package com.alvdela.sportapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.alvdela.sportapp.Utility.animateViewOfFloat
import com.alvdela.sportapp.Utility.animateViewOfInt
import com.alvdela.sportapp.Utility.getSecondsFromWatch
import com.alvdela.sportapp.Utility.setHeightLinearLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import me.tankery.lib.circularseekbar.CircularSeekBar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    private var challengeDistance: Float = 0f
    private var challengeDuration: Int = 0

    private lateinit var npChallengeDistance: NumberPicker
    private lateinit var npChallengeDurationHH: NumberPicker
    private lateinit var npChallengeDurationMM: NumberPicker
    private lateinit var npChallengeDurationSS: NumberPicker

    private lateinit var tvTimer: TextView

    private lateinit var swChallenge: Switch
    private lateinit var swVolume: Switch

    private lateinit var csbRunWalk: CircularSeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBar()
        initNavView()
        initObjects()
    }

    private fun initObjects() {

        tvTimer = findViewById(R.id.tvChrono)
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.white))
        initStopWatch()

        swChallenge = findViewById<Switch>(R.id.swChallenges)
        swVolume = findViewById<Switch>(R.id.swVolumes)

        csbRunWalk = findViewById(R.id.csbRunWalk)

        val lyMap = findViewById<LinearLayout>(R.id.lyMap)
        val lyFragmentApp = findViewById<LinearLayout>(R.id.lyFragmentMap)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengeSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenge = findViewById<LinearLayout>(R.id.lyChallenges)
        val lySettingsVolumeSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolume = findViewById<LinearLayout>(R.id.lySettingsVolumes)

        setHeightLinearLayout(lyMap, 0)
        setHeightLinearLayout(lyIntervalModeSpace, 0)
        setHeightLinearLayout(lyChallengeSpace, 0)
        setHeightLinearLayout(lySettingsVolumeSpace, 0)

        lyFragmentApp.translationY = -300f
        lyIntervalMode.translationY = -300f
        lyChallenge.translationY = -300f
        lySettingsVolume.translationY = -300f

        npChallengeDistance = findViewById(R.id.npChallengeDistance)
        npChallengeDurationHH = findViewById(R.id.npChallengeDurationHH)
        npChallengeDurationMM = findViewById(R.id.npChallengeDurationMM)
        npChallengeDurationSS = findViewById(R.id.npChallengeDurationSS)
        csbRunWalk.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {

            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean
            ) {
                var STEPS_UX: Int = 15
                var set: Int = 0
                var p = progress.toInt()

                if (p % STEPS_UX != 0) {
                    while (p >= 60) p -= 60
                    while (p >= STEPS_UX) p -= STEPS_UX
                    if (STEPS_UX - p > STEPS_UX / 2) set = -1 * p
                    else set = STEPS_UX - p

                    csbRunWalk.progress = csbRunWalk.progress + set
                }
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initStopWatch() {
        tvTimer.text = getString(R.string.init_stop_watch_value)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initNavView() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View =
            LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        /*val tvUser: TextView = findViewById(R.id.tvUser)
        tvUser.text = userEmail*/
    }

    private fun initToolBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title_terms,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_item_record -> callRecordActivity()
            R.id.nav_item_signout -> signOut()
        }

        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun callRecordActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }

    fun changeTypeMap(view: View) {}
    fun callCenterMap(view: View) {}
    fun callShowHideMap(view: View) {}
    fun startOrStopButtonClicked(view: View) {}

    fun takePicture(view: View) {}

    fun selectBike(view: View) {}
    fun selectRollerSkate(view: View) {}
    fun selectRunning(view: View) {}

    fun inflateIntervalMode(view: View) {
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        val lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)
        val tvRounds = findViewById<TextView>(R.id.tvRounds)
        val swIntervalMode = findViewById<Switch>(R.id.swIntervalMode)

        if (swIntervalMode.isChecked) {
            animateViewOfInt(
                lyIntervalMode,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )
            setHeightLinearLayout(lyIntervalModeSpace, 550)
            animateViewOfFloat(lyIntervalMode, "translationY", 0f, 500)

            animateViewOfFloat(tvTimer, "translationX", -110f, 500)
            tvRounds.setText(R.string.rounds)
            animateViewOfInt(
                tvRounds,
                "textColor",
                ContextCompat.getColor(this, R.color.white),
                500
            )

            setHeightLinearLayout(lySoftTrack, 120)
            setHeightLinearLayout(lySoftVolume, 200)
            if (swVolume.isChecked) {
                val lySettingsVolumeSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumeSpace, 600)
            }
        } else {
            animateViewOfInt(
                lyIntervalMode,
                "textColor",
                ContextCompat.getColor(this, R.color.white),
                300
            )
            setHeightLinearLayout(lyIntervalModeSpace, 0)
            lyIntervalMode.translationY = -300f

            animateViewOfFloat(tvTimer, "translationX", 0f, 500)
            tvRounds.setText(R.string.textNull)

            setHeightLinearLayout(lySoftTrack, 0)
            setHeightLinearLayout(lySoftVolume, 0)
        }

    }

    fun inflateChallenges(view: View) {
        val lyChallengeSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenge = findViewById<LinearLayout>(R.id.lyChallenges)
        if (swChallenge.isChecked) {
            animateViewOfInt(
                swChallenge,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )
            setHeightLinearLayout(lyChallengeSpace, 750)
            animateViewOfFloat(lyChallenge, "translationY", 0f, 500)
        } else {
            swChallenge.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyChallengeSpace, 0)
            lyChallenge.translationY = -300f

            challengeDistance = 0f
            challengeDuration = 0
        }
    }

    fun inflateVolumes(view: View) {
        val lySettingsVolumeSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolume = findViewById<LinearLayout>(R.id.lySettingsVolumes)

        if (swVolume.isChecked) {
            animateViewOfInt(
                swVolume,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )

            val swIntervalMode = findViewById<Switch>(R.id.swIntervalMode)
            var value = 400
            if (swIntervalMode.isChecked) value = 600

            setHeightLinearLayout(lySettingsVolumeSpace, value)
            animateViewOfFloat(lySettingsVolume, "translationY", 0f, 500)
        } else {
            swVolume.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lySettingsVolumeSpace, 0)
            lySettingsVolume.translationY = -300f

        }
    }

    fun showDuration(view: View) {
        showChallenge("duration")
    }

    fun showDistance(view: View) {
        showChallenge("distance")
    }

    private fun showChallenge(option: String) {
        val lyChallengeDuration = findViewById<LinearLayout>(R.id.lyChallengeDuration)
        val lyChallengeDistance = findViewById<LinearLayout>(R.id.lyChallengeDistance)
        val tvChallengeDuration = findViewById<TextView>(R.id.tvChallengeDuration)
        val tvChallengeDistance = findViewById<TextView>(R.id.tvChallengeDistance)

        when (option) {
            "duration" -> {
                lyChallengeDuration.translationZ = 5f
                lyChallengeDistance.translationZ = 0f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDuration.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_dark
                    )
                )

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDistance.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_medium
                    )
                )

                challengeDistance = 0f
                challengeDuration = getChallengeDuration(
                    npChallengeDurationHH.value,
                    npChallengeDurationMM.value, npChallengeDurationSS.value
                )

            }

            "distance" -> {
                lyChallengeDuration.translationZ = 0f
                lyChallengeDistance.translationZ = 5f

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDistance.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_dark
                    )
                )

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDuration.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_medium
                    )
                )

                challengeDuration = 0
                challengeDistance = npChallengeDistance.value.toFloat()
            }
        }
    }

    private fun getChallengeDuration(hh: Int, mm: Int, ss: Int): Int {
        var hours: String = hh.toString()
        if (hh < 10) hours = "0$hours"
        var minutes: String = mm.toString()
        if (mm < 10) minutes = "0$minutes"
        var seconds: String = ss.toString()
        if (ss < 10) seconds = "0$seconds"

        return getSecondsFromWatch("$hours:$minutes:$seconds")
    }

    fun closePopUp(view: View) {}
    fun deleteRun(view: View) {}
}